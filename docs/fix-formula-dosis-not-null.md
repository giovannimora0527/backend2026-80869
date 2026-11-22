# Correccion: error al crear una formula medica (Column 'dosis' cannot be null)

## Sintoma

Al crear una formula medica nueva (pantalla "Gestion de Formulas" -> "Nueva
formula"), aun seleccionando un medicamento con su dosis, el backend respondia
con un error de persistencia:

```
could not execute statement [Column 'dosis' cannot be null]
insert into formula_medica (cita_id, dosis, duracion, fecha_actualizacion_registro,
  fecha_creacion, fecha_creacion_registro, frecuencia, indicaciones,
  medicamento_id, medico_id) values (...); constraint [dosis]
```

## Causa

El modulo de formulas evoluciono a un modelo de "varios medicamentos por
formula": la cabecera es `formula_medica` y cada medicamento prescrito se guarda
como una fila en la tabla de detalle `formula_medicamento` (con su dosis,
frecuencia, duracion, cantidad y via). Es decir, en el modelo nuevo la dosis
pertenece al detalle, no a la cabecera.

En la fase 3B se hizo opcional la columna `formula_medica.medicamento_id`, pero
no se hizo lo mismo con `formula_medica.dosis`, que quedo como `TEXT NOT NULL`
(herencia del modelo antiguo de un solo medicamento).

Al crear una formula nueva, el servicio `FormulaNuevaServiceImpl.crear` guarda la
cabecera sin asignar `dosis` (correcto, porque esa informacion va en el detalle).
Hibernate genera el INSERT con `dosis = NULL`, y MySQL lo rechaza por la
restriccion `NOT NULL`, produciendo el error.

## Solucion

Se hace opcional (`NULL`) la columna `dosis` de la cabecera `formula_medica`,
igual que ya se habia hecho con `medicamento_id`. La dosis real sigue siendo
obligatoria en el detalle (`formula_medicamento.dosis` permanece `NOT NULL`), de
modo que no se pierde ninguna validacion clinica: el servicio sigue exigiendo
dosis por cada medicamento de la formula.

Cambios realizados:

1. `docker/mysql-init/01-clinica-dump.sql`: la columna `dosis` de
   `formula_medica` pasa de `text NOT NULL` a `text DEFAULT NULL`. Asi, cuando
   la base se crea desde cero con Docker, ya queda correcta.
2. `docs/sql/fase-3b-formula-detalle.sql`: se agrega el `ALTER TABLE` que relaja
   `formula_medica.dosis` a `NULL`, para bases de datos que ya estaban creadas.

No se modifico codigo Java: el servicio ya operaba de forma correcta; el
problema estaba unicamente en la definicion de la tabla.

Las formulas antiguas que tuvieran un valor en `dosis` lo conservan; las nuevas
pueden dejar esa columna de cabecera vacia.

## Como aplicar el cambio

- Entorno Docker desde cero (recomendado): al recrear la base, el dump ya trae la
  columna corregida. Si el volumen de MySQL ya existia, recrearlo para que se
  vuelvan a ejecutar los scripts de `mysql-init`:
  `docker compose down -v` y luego `docker compose up -d --build`.
  Advertencia: `-v` borra los datos y recarga los de prueba.
- Base de datos existente que se quiere conservar: ejecutar unicamente la
  sentencia agregada en `docs/sql/fase-3b-formula-detalle.sql`:
  `ALTER TABLE formula_medica MODIFY COLUMN dosis TEXT NULL;`

## Verificacion

- Compilacion del backend correcta:
  `mvn -q -Dmaven.repo.local=/projects/sandbox/.m2repo compile`.
- Prueba funcional recomendada (requiere la base de datos actualizada): crear una
  formula seleccionando una cita y uno o varios medicamentos con su dosis. La
  respuesta esperada es estado 200 con el mensaje "Formula medica creada
  correctamente", y la formula debe aparecer en el listado y permitir generar su
  PDF.

Nota: el entorno de desarrollo usado para este cambio no cuenta con MySQL en
ejecucion, por lo que la verificacion automatizada se limita a la compilacion y a
la revision del esquema. La prueba funcional end-to-end debe ejecutarse con la
base de datos actualizada.

## Archivos modificados

- `docker/mysql-init/01-clinica-dump.sql`
- `docs/sql/fase-3b-formula-detalle.sql`
- `docs/fix-formula-dosis-not-null.md` (este documento)

# Estabilizacion de modulos

Fase intermedia para dejar todos los modulos existentes navegables y estables
antes de continuar con los modulos grandes del examen.

## Correccion de modelo de datos

Se detectaron y corrigieron columnas que las entidades esperaban y no existian
en la base de datos (ver `docs/sql/fase-2-fix-columnas-faltantes.sql`):

- medico.estado, mascota.activo, formula_medica.frecuencia, formula_medica.duracion,
  anotacion_historia.diagnostico, anotacion_historia.observaciones,
  anotacion_historia.cita_id.

Estas columnas provocaban errores 500 ("Unknown column") al listar o guardar.

## Modulo de clientes

Se completo el CRUD de clientes, que antes solo tenia listar y buscar:

- `POST /cliente/guardar` : crea un cliente.
- `POST /cliente/actualizar` : actualiza un cliente.
- Validaciones: documento unico, campos obligatorios (tipo y numero de
  documento, nombres, apellidos, fecha de nacimiento).

## Estado de los modulos

- Estables (listan y operan): Clientes, Especializaciones, Mascotas, Historias,
  Medicos, Inventario, Usuarios, Citas (lista y calendario).
- Formulas: funcional con un medicamento; se reconstruye en la fase de atencion
  medica para soportar varios medicamentos.
- Medicamentos (modulo antiguo): retirado del menu, su funcionalidad la cubre el
  modulo de Inventario.

## Nota (frontend)

En el frontend, el modulo de citas se unifico en una sola pantalla con dos
pestanas (Lista y Calendario), evitando menus duplicados. Ademas se aumento el
numero de filas por pagina en los listados.

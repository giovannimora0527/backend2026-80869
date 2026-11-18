# Correccion: error 500 al crear una mascota

## Sintoma

Al registrar una mascota nueva desde el frontend (pantalla "Gestion de
Mascotas" -> "Nuevo"), el backend respondia con error:

```
Http failure response for http://localhost:8080/clinica/v1/mascota/guardar: 500 OK
```

El cliente si se creaba correctamente, pero no era posible crear la mascota ni
asociarla a ese cliente.

## Causa

La tabla `mascota` define la columna `activo` como obligatoria:

```sql
`activo` tinyint(1) NOT NULL DEFAULT 1,
```

El metodo `guardarMascota` del servicio `MascotaServiceImpl` construia la nueva
mascota con nombre, edad, cliente, raza y fecha de registro, pero no asignaba el
campo `activo`. Por lo tanto quedaba en `null`.

Al guardar, Hibernate genera un INSERT que incluye la columna `activo` con valor
`NULL` de forma explicita. Un valor `NULL` explicito no activa el `DEFAULT 1` de
la tabla, de modo que MySQL rechaza la insercion por violar la restriccion
`NOT NULL`. Esa excepcion de persistencia se traduce en una respuesta HTTP 500.

## Solucion

Se asigna el estado activo al crear la mascota, siguiendo el mismo patron que ya
usaba el modulo de clientes (una entidad nueva nace activa):

```java
nueva.setActivo(Boolean.TRUE);
```

Ubicacion: `MascotaServiceImpl.guardarMascota`, justo despues de asignar la fecha
de registro.

El metodo `actualizarMascota` no requiere cambios: al actualizar se carga la
mascota existente desde la base de datos, que ya conserva su valor de `activo`.

## Verificacion

- Compilacion del backend correcta:
  `mvn -q -Dmaven.repo.local=/projects/sandbox/.m2repo compile`.
- Prueba funcional recomendada (requiere la base de datos en ejecucion): crear un
  cliente, luego crear una mascota y asignarsela. La respuesta esperada es
  estado 200 con el mensaje "Mascota guardada correctamente", y la mascota debe
  aparecer en el listado con estado "Activo".

Nota: el entorno de desarrollo usado para este cambio no cuenta con MySQL en
ejecucion, por lo que la verificacion automatizada se limita a la compilacion.
La prueba funcional end-to-end debe ejecutarse con el entorno Docker levantado.

## Archivos modificados

- `src/main/java/com/uniminuto/clinica/serviceimpl/MascotaServiceImpl.java`
- `docs/fix-crear-mascota-activo.md` (este documento)

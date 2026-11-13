# Scripts de inicializacion de MySQL

Los archivos `.sql` que se coloquen en esta carpeta se ejecutan
**automaticamente** la primera vez que el contenedor de MySQL crea la base de
datos (en orden alfabetico). Asi, al hacer `docker compose up`, la base queda
poblada sin pasos manuales.

## Que archivo colocar aqui

Coloque aqui el **dump completo** de la base `clinica` exportado desde MySQL
Workbench, con un nombre que empiece por un numero para fijar el orden, por
ejemplo:

```
01-clinica-dump.sql
```

Ese dump debe contener:

1. La estructura y los datos base del proyecto (tablas usuario, perfil, menu,
   cliente, mascota, raza, medico, especializacion, cita, etc.).
2. Los cambios de todas las fases (equivalente a ejecutar en orden los scripts
   de `docs/sql/`: fase-0.1, fase-1, fase-2, fase-2-fix, gestion-roles,
   fase-3a, fase-3a-datos, fase-3b, fase-3d, fase-4, fase-5).
3. Datos de prueba (medicamentos, servicios, citas, etc.).

La forma mas simple de obtenerlo es exportar la base `clinica` que ya tiene
funcionando en local:

- MySQL Workbench: menu `Server` -> `Data Export` -> seleccione el esquema
  `clinica` -> marque "Dump Structure and Data" -> "Export to Self-Contained
  File" -> guarde el archivo y copielo aqui como `01-clinica-dump.sql`.

## Nota

No es necesario crear la base `clinica`: el contenedor ya la crea segun la
variable `MYSQL_DATABASE` del `docker-compose.yml`. El dump solo debe crear las
tablas e insertar los datos dentro de esa base.

Si el dump incluye una linea `CREATE DATABASE` o `USE otra_base`, verifique que
el nombre sea `clinica` para que todo quede en la base correcta.

## Archivos incluidos

- `01-clinica-dump.sql`: estructura completa y datos base (clientes, mascotas,
  medicos, especializaciones, servicios, usuarios, perfiles y menus). Las tablas
  transaccionales (citas, formulas, facturas, inventario) inician vacias.
- `02-datos-prueba.sql`: datos de prueba para la demostracion (medicamentos con
  lotes que disparan las alertas, citas de la semana de entrega en varios
  estados, historias y anotaciones, y formulas pendientes de despacho). Se
  ejecuta despues del dump por el orden del nombre. Las facturas y los despachos
  se dejan para realizarlos en vivo durante la sustentacion.

Los scripts se ejecutan en orden alfabetico, por eso el prefijo numerico.

# Modulo de servicios y tarifas (Fase 3A)

Modulo administrativo para gestionar los servicios de la clinica (consultas y
procedimientos) y sus tarifas, con IVA configurable e historial de cambios.

## Modelo de datos

- `servicio`: catalogo de servicios. Campos: nombre (unico), descripcion,
  especializacion asociada (opcional), precio, porcentaje de IVA y estado
  activo/inactivo.
- `log_tarifa`: historial de cambios de tarifa. Cada vez que cambia el precio o
  el IVA de un servicio, se guarda el valor anterior, el nuevo, la fecha y el
  usuario responsable.

Scripts: `docs/sql/fase-3a-servicios-tarifas.sql` (estructura y menu) y
`docs/sql/fase-3a-datos-servicios.sql` (30 servicios de ejemplo).

## Reglas de negocio

- El nombre del servicio es unico.
- El precio no puede ser negativo.
- El IVA debe estar entre 0 y 100.
- La especializacion es opcional: un servicio puede ser general (por ejemplo
  consulta basica) o requerir una especialidad (por ejemplo electrocardiograma
  asociado a cardiologia).
- Las tarifas se configuran desde la aplicacion, sin tocar el codigo fuente.
- Todo cambio de precio o IVA queda registrado en el historial.

## Endpoints (/servicio)

- `GET /servicio/listar` : lista todos los servicios.
- `GET /servicio/listar-activos` : lista solo los activos (para atencion y
  facturacion en fases siguientes).
- `POST /servicio/guardar` : crea un servicio.
- `POST /servicio/actualizar` : actualiza un servicio; registra el cambio de
  tarifa si cambia el precio o el IVA.
- `GET /servicio/historial-tarifas?servicioId=` : historial de cambios de
  tarifa del servicio.

## Relacion con fases siguientes

Los servicios son la base de la facturacion. Cuando se atienda una cita y se
genere su factura, se tomaran los precios de los servicios prestados, y el valor
cobrado se conservara historicamente en la factura (aunque luego cambie la
tarifa del servicio).

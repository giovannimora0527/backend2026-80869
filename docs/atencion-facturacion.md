# Atencion de la cita y facturacion (Fases 3C y 3D)

## Fase 3C - Atencion de la cita (historia clinica)

El registro de la atencion medica se realiza mediante el modulo de historias y
anotaciones ya existente. Cada atencion se guarda como una anotacion de la
historia clinica de la mascota e incluye:

- Descripcion (motivo de consulta y sintomas).
- Diagnostico.
- Observaciones (observaciones y recomendaciones).
- Vinculo con la cita atendida y el medico responsable.

La historia clinica se crea automaticamente para la mascota si aun no existe.
El estado de la cita se gestiona desde el calendario (confirmar, atender,
finalizar), de modo que una cita atendida queda registrada como tal.

## Fase 3D - Facturacion

Permite generar facturas a partir de la atencion de una cita.

### Modelo de datos

- `datos_clinica`: datos de la clinica para la cabecera de la factura.
- `factura`: cabecera con cita, cliente, mascota, medico, subtotal, IVA,
  descuento, total y estado de pago.
- `factura_detalle`: items (servicios o medicamentos) con cantidad, precio
  unitario, IVA e importe. El valor se conserva historicamente en el detalle.
- `pago`: pagos registrados sobre la factura.

Script: `docs/sql/fase-3d-facturacion.sql`.

### Reglas de negocio

- Una cita no se factura dos veces (salvo que la factura anterior este anulada).
- El subtotal, IVA y total se calculan a partir de las tarifas vigentes y se
  guardan en la factura (valor historico).
- Estados de pago: PENDIENTE, PAGO_PARCIAL, PAGADA, ANULADA.
- Al registrar un pago, el estado se actualiza automaticamente segun el total
  pagado. Un pago no puede exceder el saldo pendiente.
- La anulacion no elimina registros: la factura se marca como ANULADA.
- Los medicamentos facturados corresponden a los realmente prescritos o
  entregados (la dispensacion efectiva se controla en el modulo de despacho).

### Endpoints (/factura)

- `GET /factura/listar` : lista todas las facturas.
- `GET /factura/por-cliente?clienteId=` : historial de facturacion de un cliente.
- `GET /factura/obtener?id=` : una factura con su detalle y estado de pago.
- `POST /factura/generar` : genera una factura a partir de una cita y sus items.
- `POST /factura/registrar-pago` : registra un pago y actualiza el estado.
- `POST /factura/anular?id=` : anula la factura (sin eliminar registros).
- `GET /factura/pdf?id=` : genera el PDF de la factura (OpenPDF).

### PDF

El PDF incluye los datos de la clinica, el numero y fecha de la factura, el
propietario, la mascota, el medico, el detalle de items (descripcion, cantidad,
precio unitario, IVA y subtotal) y los totales (subtotal, IVA, descuento, total,
pagado y saldo).

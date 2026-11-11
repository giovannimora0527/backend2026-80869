# Guia de estudio 2 - Atencion, formulas y facturacion (Fase 3)

Esta guia cubre la Fase 3 del sistema de la clinica veterinaria: el flujo
clinico y comercial que va desde la definicion de servicios y tarifas, pasando
por la formulacion de medicamentos y la atencion de la cita, hasta la
generacion de la factura y el registro de pagos. Esta escrita en lenguaje claro
para repasar y para generar un video de estudio.

---

## 1. Vision general de la Fase 3

La Fase 3 integra varias piezas en un mismo flujo:

1. Se definen los servicios de la clinica y sus tarifas (precio e IVA).
2. El veterinario atiende la cita y registra la atencion en la historia clinica.
3. El veterinario formula medicamentos (una formula puede tener varios).
4. Se genera la factura con los servicios y medicamentos, se calculan los
   totales y se registran los pagos.

Se divide en cuatro sub-entregas: 3A servicios y tarifas, 3B formula con varios
medicamentos, 3C atencion de la cita y 3D facturacion.

---

## 2. Fase 3A - Servicios y tarifas

Modulo administrativo para gestionar los servicios (consultas y procedimientos)
y sus tarifas.

### Modelo de datos

- `servicio`: catalogo de servicios. Cada uno tiene nombre, descripcion, una
  especializacion opcional, precio, porcentaje de IVA y estado activo/inactivo.
- `log_tarifa`: historial de cambios de tarifa. Cada vez que cambia el precio o
  el IVA de un servicio, se guarda el valor anterior, el nuevo, la fecha y el
  usuario responsable.

### Ideas clave

- Las tarifas se configuran desde la aplicacion, sin tocar el codigo fuente.
- Un servicio puede ser general (consulta basica) o requerir una especialidad
  (por ejemplo, un electrocardiograma asociado a cardiologia), por eso la
  especializacion es opcional.
- Todo cambio de precio o IVA queda registrado para dar trazabilidad. Esto
  permite que, aunque la tarifa cambie en el futuro, la factura conserve el
  valor que se cobro en su momento.

---

## 3. Fase 3B - Formula con varios medicamentos

Antes una formula referenciaba un solo medicamento. Ahora puede contener varios.

### Modelo de datos

- `formula_medica` es la cabecera (cita, medico, fecha, indicaciones
  generales).
- `formula_medicamento` es el detalle: cada fila es un medicamento prescrito con
  su dosis, frecuencia, duracion, cantidad y via de administracion.

### Ideas clave

- Una formula se asocia a una cita y debe tener al menos un medicamento, y cada
  medicamento requiere, como minimo, su dosis.
- Crear la formula NO descuenta el inventario. El descuento ocurre al confirmar
  la dispensacion en el modulo de despacho. Esta separacion entre formular y
  dispensar es una regla central del enunciado.
- La formula se puede generar e imprimir en PDF, con los datos de la clinica, la
  mascota, el propietario, el medico y la tabla de medicamentos.

---

## 4. Fase 3C - Atencion de la cita

Cuando el veterinario atiende una cita, registra la atencion como una anotacion
de la historia clinica de la mascota. La atencion incluye el motivo de consulta
y los sintomas (descripcion), el diagnostico y las observaciones y
recomendaciones. Queda vinculada a la cita atendida y al medico responsable, y
la historia clinica de la mascota se crea automaticamente si aun no existe. El
estado de la cita (atendida, finalizada) se gestiona desde el calendario.

---

## 5. Fase 3D - Facturacion

Permite generar la factura a partir de la atencion de una cita.

### Modelo de datos

- `datos_clinica`: datos de la clinica (nombre, NIT, telefono, direccion) para
  la cabecera de la factura.
- `factura`: cabecera con la cita, el cliente, la mascota, el medico, el
  subtotal, el IVA, el descuento, el total y el estado de pago.
- `factura_detalle`: los items de la factura (servicios y medicamentos), con
  cantidad, precio unitario, IVA e importe. El valor se guarda en el detalle
  para conservarlo historicamente.
- `pago`: los pagos registrados sobre la factura.

### Como se calcula la factura

Al generar la factura se toman los items (servicios y medicamentos). Por cada
item se calcula el subtotal (precio por cantidad) y el IVA segun el porcentaje
del servicio. La factura suma los subtotales y los IVA, resta el descuento y
obtiene el total. Todos esos valores quedan guardados en la factura.

### Estados de pago

- PENDIENTE: no se ha registrado ningun pago.
- PAGO_PARCIAL: se ha pagado una parte.
- PAGADA: el total fue cubierto.
- ANULADA: la factura fue anulada (sin eliminar registros).

Cuando se registra un pago, el estado se actualiza automaticamente segun el
total pagado. Un pago no puede exceder el saldo pendiente.

### Reglas de negocio

- Una cita no se factura dos veces (salvo que la factura anterior este anulada).
- Las tarifas se conservan historicamente en la factura.
- La anulacion no elimina registros; la factura solo cambia de estado.
- La factura se puede generar en PDF, con los datos de la clinica, el
  propietario, la mascota, el medico, el detalle de items y los totales
  (subtotal, IVA, descuento, total, pagado y saldo).

---

## 6. El flujo completo (como contarlo en la sustentacion)

1. El administrador define los servicios y sus tarifas (con IVA).
2. Se agenda y atiende una cita; el medico registra la atencion en la historia.
3. El medico formula uno o varios medicamentos y puede imprimir la formula.
4. Recepcion (o el administrador) genera la factura con los servicios prestados
   y los medicamentos, el sistema calcula subtotal, IVA y total, y se registran
   los pagos hasta dejar la factura pagada. Todo se puede imprimir en PDF.

La separacion entre formular (no afecta inventario) y dispensar (si lo afecta,
en el modulo de despacho) es la idea que conecta esta fase con el inventario.

---

## 7. Como usar cada modulo

### Gestion de servicios
- Boton "Nuevo servicio" o "Editar": nombre, descripcion, especializacion
  (opcional), precio, IVA y estado. La tabla muestra el precio, el IVA y el
  precio con IVA.
- Boton del historial (icono de reloj): muestra los cambios de tarifa del
  servicio (precio e IVA anterior y nuevo, fecha y responsable).
- Para ver el historial, primero editar un servicio cambiando su precio o IVA y
  guardar; el cambio queda registrado.

### Formulas medicas
- Boton "Nueva formula": seleccionar la cita, escribir indicaciones generales y
  agregar uno o varios medicamentos (cada uno con dosis, frecuencia, duracion,
  cantidad y via). Boton "Agregar medicamento" para sumar filas.
- En la tabla de formulas, el boton PDF genera e imprime la formula.

### Atencion de la cita
- Se registra desde el modulo de historias/anotaciones: motivo y sintomas,
  diagnostico y observaciones, asociados a la cita y al medico. La historia de la
  mascota se crea sola si no existe.

### Facturacion
- Boton "Nueva factura": seleccionar la cita, un descuento opcional y agregar
  items. Cada item es un servicio o un medicamento con su cantidad. El sistema
  calcula subtotal, IVA y total.
- En la tabla de facturas: boton PDF (descarga la factura), boton de pago
  (registrar un pago; el estado cambia a parcial o pagada) y boton anular
  (marca la factura como anulada sin borrarla).

---

## 8. Flujo de prueba sugerido para la sustentacion

Este recorrido demuestra el flujo clinico y comercial completo:

1. En Gestion de Servicios, mostrar el catalogo (mas de 30 servicios), crear uno
   nuevo y, en otro, cambiar el precio para mostrar el historial de tarifas.
2. En Formulas, crear una formula para una cita con dos o tres medicamentos
   (con sus dosis y frecuencias) y descargar el PDF de la formula.
3. Mostrar la atencion de la cita registrada en la historia clinica de la
   mascota (motivo, diagnostico, observaciones).
4. En Facturacion, generar una factura para esa cita agregando un servicio y un
   medicamento. Mostrar como el sistema calcula subtotal, IVA y total.
5. Registrar un pago parcial (el estado pasa a pago parcial) y luego el resto
   (el estado pasa a pagada). Descargar el PDF de la factura.
6. En otra factura, mostrar la anulacion (el registro se conserva).
7. Explicar la regla clave: crear la formula no descuenta inventario; el
   descuento ocurrira en el modulo de despacho.

---

## 9. Glosario

- Servicio: consulta o procedimiento con un precio y un IVA.
- Tarifa: el precio e IVA de un servicio; sus cambios quedan en un historial.
- Formula: prescripcion de uno o varios medicamentos para una cita.
- Factura: documento con los servicios y medicamentos cobrados, sus totales y su
  estado de pago.
- IVA: impuesto que se calcula sobre el valor de un servicio.

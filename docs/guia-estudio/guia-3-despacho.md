# Guia de estudio 3 - Despacho y dispensacion (Fase 5)

Esta guia cubre la Fase 5 del sistema de la clinica veterinaria: el modulo de
despacho, que gestiona la entrega real de los medicamentos formulados y es el
unico punto donde se descuenta el inventario. Esta escrita en lenguaje claro
para repasar y para generar un video de estudio.

---

## 1. Vision general de la Fase 5

El objetivo del modulo es separar dos momentos que antes se confundian:

1. Formular (Fase 3): el veterinario prescribe medicamentos en una formula. Esto
   NO afecta el inventario.
2. Dispensar (Fase 5): el personal de despacho entrega fisicamente los
   medicamentos. Solo aqui se descuenta el inventario.

Esta separacion garantiza que el inventario unicamente se afecte cuando existe
una entrega efectivamente realizada, que es una regla central del enunciado.

El modulo lo opera un perfil especifico: DESPACHO_MEDICO.

---

## 2. El perfil DESPACHO_MEDICO

Es un rol con acceso restringido. Solo puede:

- Consultar el inventario (medicamentos, existencias, lotes, vencimientos), sin
  crear, editar ni modificar cantidades manualmente.
- Operar el modulo de despacho (consultar formulas, validar y confirmar
  entregas).

No puede acceder a citas, atencion medica, historias, creacion de formulas,
facturacion, tarifas, reportes ni administracion de usuarios y roles.

Idea clave: la restriccion se valida en el backend (en el filtro de seguridad),
no solo ocultando los menus en el frontend. Aunque alguien manipule la interfaz,
el servidor rechaza las peticiones no autorizadas con un error de permisos.

---

## 3. Modelo de datos

- `despacho`: cabecera de la dispensacion de una formula. Guarda su estado, la
  fecha, el usuario responsable y una observacion.
- `despacho_detalle`: por cada medicamento, la cantidad solicitada, la cantidad
  efectivamente entregada y el lote del que se descuenta.
- `log_despacho`: historial de cambios de estado del despacho, para
  trazabilidad.

Estas tablas se relacionan con las ya existentes: la formula (`formula_medica` y
`formula_medicamento`), los lotes (`lote_medicamento`) y los movimientos de
inventario (`movimiento_inventario`).

---

## 4. La politica FEFO (First Expired, First Out)

Al confirmar un despacho, el sistema descuenta de los lotes empezando por el que
vence primero (siempre que no este vencido). "Primero el que expira antes."

Esto evita que queden lotes viejos sin usar que luego se venzan, y es la forma
correcta de manejar inventario de medicamentos. Por eso los lotes se consultan
ordenados por fecha de vencimiento ascendente.

---

## 5. El proceso de despacho paso a paso

1. El personal de despacho consulta las formulas pendientes de dispensacion.
2. Al seleccionar una formula, ve sus medicamentos con la cantidad prescrita y
   la existencia disponible (solo de lotes vigentes).
3. Registra cuanto entrega de cada medicamento.
4. Al confirmar, el sistema, de forma transaccional:
   - Descuenta las cantidades de los lotes segun FEFO (sin tocar vencidos).
   - Genera un movimiento de salida por cada lote afectado, ligado a la formula
     y al despacho.
   - Guarda el detalle (solicitado, entregado y lote).
   - Actualiza el estado del despacho y registra el cambio en el log.
   - Genera un comprobante de despacho en PDF.

Transaccional significa que, si algo falla a mitad del proceso, no se aplica
ningun descuento: o se hace todo o no se hace nada. Asi el inventario nunca
queda a medias.

---

## 6. Estados del despacho

- PENDIENTE: la formula fue generada pero aun no se procesa.
- EN_PREPARACION: se estan verificando los medicamentos y cantidades.
- DESPACHO_PARCIAL: se entrego solo una parte de lo formulado.
- DESPACHADO: se entrego todo lo formulado.
- PENDIENTE_DISPONIBILIDAD: hay medicamentos que no se pueden entregar por falta
  de existencias.
- CANCELADO: el despacho se cancelo mediante un procedimiento autorizado.

---

## 7. Reglas de negocio clave

- Solo DESPACHO_MEDICO (y el administrador) operan el modulo.
- El personal de despacho no modifica las dosis ni cantidades prescritas por el
  medico.
- El inventario se descuenta solo al confirmar la dispensacion.
- No se dispensan medicamentos vencidos.
- No se puede entregar mas de lo disponible; si no hay suficiente, se permite una
  entrega parcial.
- Cada salida de inventario queda ligada a un despacho y a una formula.
- No se descuenta dos veces por una misma dispensacion (una formula ya
  despachada por completo no se vuelve a despachar).
- Toda operacion registra el usuario responsable, la fecha y la hora.
- Las dispensaciones confirmadas no se eliminan; se conserva el historial.

---

## 8. Como usar el modulo

### Pestana Pendientes
- Lista las formulas pendientes de dispensacion, con propietario, mascota,
  medico y numero de medicamentos.
- Boton "Preparar": abre el detalle de la formula con cada medicamento, su
  cantidad prescrita y la existencia disponible. Se indica cuanto entregar de
  cada uno (por defecto, lo prescrito limitado por lo disponible) y se confirma.
- Si se entrega menos de lo prescrito, el despacho queda como parcial.

### Pestana Historial
- Lista los despachos realizados con su fecha, estado y responsable.
- Boton PDF: descarga el comprobante de despacho.

---

## 9. Flujo de prueba sugerido para la sustentacion

Este recorrido demuestra el descuento de inventario mediante dispensacion:

1. Antes de despachar, mostrar en Inventario la existencia de un medicamento
   (por ejemplo, 50 unidades repartidas en lotes con distinta fecha de
   vencimiento).
2. Entrar con el usuario de perfil DESPACHO_MEDICO y mostrar que solo ve
   Inventario (consulta) y Despacho.
3. En Despacho, pestana Pendientes, seleccionar una formula y confirmar la
   entrega de una cantidad.
4. Volver a Inventario y mostrar que la existencia bajo, y que el descuento se
   tomo del lote con vencimiento mas proximo (FEFO).
5. Mostrar el movimiento de salida registrado (entradas y salidas del
   medicamento) ligado a la formula.
6. Descargar el comprobante de despacho en PDF.
7. Mostrar un caso de entrega parcial (cuando no alcanza el inventario) y
   explicar que el estado queda como despacho parcial.
8. Explicar la validacion de permisos en el backend: intentar acceder a un
   modulo no permitido con este rol y mostrar que el servidor lo rechaza.

---

## 10. Glosario

- Dispensacion: entrega fisica de los medicamentos formulados.
- Despacho: registro de una dispensacion, con su estado y detalle.
- Lote: cantidad de un medicamento con un mismo precio y fecha de vencimiento.
- FEFO: politica que descuenta primero del lote que vence antes.
- Movimiento de salida: registro de la disminucion de inventario por una entrega.
- Transaccional: operacion que se aplica completa o no se aplica (no queda a
  medias).
- DESPACHO_MEDICO: perfil que opera el modulo de despacho con acceso restringido.

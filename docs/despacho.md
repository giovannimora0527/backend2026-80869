# Modulo de despacho y dispensacion (Fase 5)

Gestiona la entrega real de los medicamentos formulados, separando la
formulacion (que no afecta el inventario) de la dispensacion (que lo descuenta).
Lo opera el perfil DESPACHO_MEDICO (y el Administrador).

## Modelo de datos

- `despacho`: cabecera del despacho de una formula, con estado, fecha y usuario
  responsable.
- `despacho_detalle`: por cada medicamento, cantidad solicitada, cantidad
  entregada y lote del que se descuenta.
- `log_despacho`: historial de cambios de estado del despacho.
- Perfil `DESPACHO_MEDICO`: accede solo a inventario (consulta) y despacho.

Script: `docs/sql/fase-5-despacho.sql`.

## Estados del despacho

PENDIENTE, EN_PREPARACION, DESPACHO_PARCIAL, DESPACHADO,
PENDIENTE_DISPONIBILIDAD, CANCELADO.

## Logica de dispensacion (FEFO y transaccional)

Al confirmar un despacho, por cada medicamento y segun la cantidad a entregar:

1. Se buscan los lotes del medicamento ordenados por fecha de vencimiento
   ascendente (politica FEFO: primero el que vence antes).
2. Se omiten los lotes vencidos.
3. Se descuenta la cantidad de los lotes vigentes hasta cubrir lo solicitado.
4. Por cada lote afectado se registra un movimiento de salida de inventario,
   ligado al despacho y a la formula.
5. Si lo entregado es menor a lo prescrito, el despacho queda como parcial.

Toda la operacion es transaccional: si algo falla, no se aplica ningun
descuento (el inventario no queda a medias).

## Reglas de negocio

- Solo DESPACHO_MEDICO (y Administrador) operan el modulo (validado en backend).
- El inventario se descuenta unicamente al confirmar la dispensacion.
- No se dispensan medicamentos vencidos.
- No se puede entregar mas que las existencias disponibles.
- Se permiten entregas parciales.
- Cada salida de inventario queda relacionada con un despacho y una formula.
- No se descuenta dos veces: una formula ya despachada en su totalidad no se
  vuelve a despachar.
- Toda operacion registra usuario, fecha y hora. El cambio de estado queda en el
  log de despacho.

## Endpoints (/despacho)

- `GET /despacho/formulas-pendientes` : formulas pendientes con su disponibilidad.
- `GET /despacho/formula?formulaId=` : detalle de una formula para despacho.
- `POST /despacho/confirmar` : confirma la entrega y descuenta el inventario.
- `GET /despacho/listar` : historial de despachos.
- `GET /despacho/comprobante?id=` : comprobante de despacho en PDF.

## Integracion con el inventario (cierre del circuito)

Este modulo cierra la integracion inventario - formula - dispensacion:

- La Fase 1 creo el inventario por lotes y los movimientos.
- La Fase 3 permitio formular varios medicamentos sin tocar el inventario.
- La Fase 5 descuenta el inventario al dispensar, generando los movimientos de
  salida que luego alimentan los reportes de inventario.

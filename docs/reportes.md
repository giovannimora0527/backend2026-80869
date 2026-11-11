# Modulo de reportes administrativos y contables (Fase 4)

Implementa un modulo de reportes que consolida la informacion financiera, de
inventario y de gestion medica de la clinica. Todos los reportes permiten
filtrar por rango de fechas, muestran una vista previa y pueden exportarse a
PDF y Excel.

## Alcance y modelo de datos

El modulo es de solo lectura: no crea ni modifica tablas. Construye cada
reporte a partir de la informacion ya registrada por los modulos anteriores:

- Financieros: `factura`, `factura_detalle`, `pago`.
- Inventario: `medicamento`, `lote_medicamento`, `movimiento_inventario`.
- Gestion medica: `cita` (con sus relaciones a mascota, cliente y medico).

De esta forma los reportes son coherentes con la operacion real del sistema
(facturacion, dispensacion y agenda). El unico cambio en base de datos es la
insercion del menu del modulo y de dos perfiles nuevos; ver
`docs/sql/fase-4-reportes.sql`.

## Estructura de respuesta uniforme

Todos los reportes se construyen con una estructura generica (`ReporteRs`) que
contiene titulo, subtitulo, columnas, filas y totales. Esta estructura se usa
tanto para la vista previa (JSON) como para la exportacion a PDF y Excel, de
modo que la presentacion y la exportacion son completamente genericas y agregar
un nuevo reporte no requiere modificar la logica de salida.

## Catalogo de reportes

Cada reporte se identifica por su categoria y su tipo.

### Financieros (categoria `financiero`)

- `ingresos-periodo`: facturas del periodo con subtotal, IVA, descuento y total.
- `ingresos-consultas`: ingresos por servicios (consultas) agrupados.
- `ingresos-medicamentos`: ingresos por venta de medicamentos.
- `ingresos-especialidad`: ingresos agrupados por especialidad del veterinario.
- `ingresos-veterinario`: ingresos agrupados por veterinario.
- `facturas-estado`: conteo y monto por estado de pago (pendiente, pago parcial,
  pagada, anulada).
- `pagos-parciales`: facturas con pago parcial y su saldo pendiente.
- `impuestos-descuentos`: IVA recaudado y descuentos aplicados (excluye anuladas).
- `resumen-dia-mes-anio`: ingresos por dia, con subtotales por mes y anio.

### Inventario (categoria `inventario`)

- `existencias`: existencias actuales por medicamento con su estado.
- `valor-inventario`: valor a costo de adquisicion y valor potencial de venta.
- `agotados`: medicamentos con existencia cero.
- `stock-minimo`: medicamentos en o por debajo del stock minimo.
- `por-vencer`: lotes que vencen dentro de los proximos 30 dias.
- `vencidos`: lotes con fecha de vencimiento anterior a hoy.
- `movimientos`: entradas y salidas de inventario en el periodo.

### Gestion medica (categoria `gestion`)

- `citas-periodo`: citas del periodo con su detalle.
- `citas-estado`: conteo de citas por estado (atendidas, canceladas, pendientes).
- `consultas-veterinario`: numero de consultas por veterinario.
- `consultas-especialidad`: numero de consultas por especialidad.
- `mascotas-atendidas`: mascotas distintas atendidas en el periodo.

## Nota sobre el rango de fechas

El enunciado exige que todos los reportes permitan seleccionar un rango de
fechas. Los reportes de inventario que reflejan el estado actual (existencias,
valor, agotados, stock minimo, por vencer, vencidos) corresponden a una foto del
momento; aceptan el rango por uniformidad, pero internamente no filtran por el,
ya que la pregunta "cuantas unidades hay" no depende del periodo. El rango si es
determinante en los reportes por periodo (ingresos, movimientos y citas).

## Control de acceso

El acceso al modulo se restringe por rol. En el backend, el filtro
`JwtAutenticacionFilter` limita el prefijo `/reporte` a los perfiles
`Administrador`, `Contable` y `JEFE_BODEGA`; cualquier otro rol recibe HTTP 403
aunque manipule el frontend. En el frontend, el menu de reportes solo se muestra
a los perfiles que lo tengan asignado en `perfil_menu`.

## Endpoints (`/reporte`)

- `GET /reporte/generar?categoria=&tipo=&desde=&hasta=` : vista previa en JSON.
- `GET /reporte/pdf?categoria=&tipo=&desde=&hasta=` : exportacion a PDF.
- `GET /reporte/excel?categoria=&tipo=&desde=&hasta=` : exportacion a Excel (.xlsx).

Las fechas usan el formato aaaa-mm-dd. La vista previa y las exportaciones
comparten la misma construccion del reporte, de modo que lo que se ve en
pantalla coincide con el documento descargado.

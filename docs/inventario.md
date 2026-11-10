# Modulo de inventario (Fase 1)

Convierte la gestion de medicamentos en un modulo de inventario funcional, con
control de existencias por lotes, precios, registro Invima, codigo unico,
alertas de estado y carga masiva.

## Modelo de datos

- `medicamento` (ampliada): catalogo del producto. Campos agregados: `codigo`
  (8 caracteres, unico), `requiere_invima`, `registro_invima`, `precio_compra`,
  `precio_venta`, `stock_minimo`, `fecha_proximo_pedido`.
- `lote_medicamento` (nueva): lotes del medicamento. Un medicamento puede tener
  varios lotes, cada uno con su cantidad, precio de compra y fecha de
  vencimiento. La existencia del medicamento es la suma de las cantidades de
  sus lotes.
- `movimiento_inventario` (nueva): entradas y salidas, con usuario responsable
  y referencia al origen. Base de la trazabilidad y del descuento por
  dispensacion del modulo de despacho (Fase 5).
- `log_carga_inventario` (nueva): registro de cada carga masiva.

El script de creacion y modificacion esta en `docs/sql/fase-1-inventario.sql`.

## Reglas de negocio

- El registro Invima es obligatorio cuando `requiere_invima` es verdadero
  (medicamentos). Para utensilios u otros productos puede quedar vacio.
- El codigo, si se indica, debe tener exactamente 8 caracteres y es unico.
- Nombre unico por medicamento.
- Precios y cantidades no negativos.
- La existencia nunca es negativa (los movimientos de salida se validaran
  contra las existencias en el modulo de despacho).

## Indicadores de estado (calculados)

- Agotado: existencia total igual a cero.
- Stock bajo: existencia mayor que cero pero menor o igual al stock minimo.
- Proximo a vencer: al menos un lote vence dentro de los proximos 30 dias.
- Vencido: al menos un lote con fecha de vencimiento anterior a hoy.

## Endpoints (/inventario)

- `GET /inventario/listar` : lista con existencia e indicadores.
- `GET /inventario/obtener?id=` : detalle con lotes.
- `POST /inventario/guardar` : crea un medicamento (y lotes iniciales opcionales).
- `POST /inventario/actualizar` : actualiza el catalogo del medicamento.
- `GET /inventario/agotados` | `/stock-bajo` | `/proximos-vencer` | `/vencidos` :
  listados por estado.
- `GET /inventario/movimientos?medicamentoId=` : historial de movimientos.
- `POST /inventario/agregar-lote` : agrega un lote a un medicamento existente
  (reabastecimiento) y registra el movimiento de entrada.
- `POST /inventario/carga-masiva/previsualizar` : valida el archivo y devuelve la
  vista previa (sin persistir).
- `POST /inventario/carga-masiva/confirmar` : persiste las filas validas y
  registra el log.

## Carga masiva (Excel .xlsx / CSV .csv)

Columnas esperadas, en orden:

```
nombre, codigo, presentacion, requiere_invima, registro_invima,
precio_compra, precio_venta, stock_minimo, numero_lote, cantidad,
fecha_vencimiento
```

- La primera fila corresponde a los encabezados.
- La fecha de vencimiento usa el formato aaaa-mm-dd.
- Se valida estructura, encabezados, campos obligatorios, formatos, duplicados
  (dentro del archivo y contra la base) y vencimientos.
- La vista previa identifica filas validas y con error, indicando el numero de
  fila y el motivo del rechazo.
- La confirmacion persiste solo las filas validas; un error en una fila no
  afecta a las demas. Cada carga queda registrada en `log_carga_inventario`.

Archivo de ejemplo con 55 registros: `docs/ejemplos/carga_masiva_ejemplo.csv`.

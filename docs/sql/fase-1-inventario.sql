-- ============================================================
-- Fase 1 - Modulo de inventario y gestion de medicamentos
-- Fecha: 2026-11
-- ============================================================
--
-- Objetivo de la fase:
--   Convertir la tabla medicamento en un modulo de inventario funcional, con
--   control de existencias por lotes, precios, registro Invima, codigo unico y
--   trazabilidad de movimientos (entradas y salidas). Incluye la carga masiva
--   de medicamentos mediante archivos Excel o CSV.
--
-- Resumen de cambios en el modelo de datos:
--   1. Se amplia la tabla `medicamento` con los campos de configuracion e
--      identificacion del producto (codigo, Invima, precios de referencia,
--      stock minimo y fecha de proximo pedido).
--   2. Se crea la tabla `lote_medicamento` para soportar que un mismo
--      medicamento tenga varios lotes con distinto precio de compra y fecha de
--      vencimiento. La existencia actual de un medicamento es la suma de las
--      cantidades de sus lotes.
--   3. Se crea la tabla `movimiento_inventario` para registrar cada entrada y
--      salida del inventario, con el usuario responsable y una referencia al
--      origen del movimiento (por ejemplo, una carga masiva o, en fases
--      posteriores, un despacho de formula).
--   4. Se crea la tabla `log_carga_inventario` para registrar cada proceso de
--      carga masiva (fecha, usuario, archivo, totales de registros).
--
-- Justificacion:
--   El enunciado exige gestionar precios de compra y venta, cantidades, stock
--   minimo, fechas de vencimiento, registro Invima y un codigo de 8 caracteres,
--   contemplando que un mismo medicamento tenga varios lotes. La separacion
--   medicamento / lote evita mezclar la informacion del catalogo (que es unica
--   por producto) con la de cada compra (que varia por lote), y permite
--   calcular la existencia real y aplicar la logica de vencimientos por lote.
--   Los movimientos de inventario dan trazabilidad y son la base del descuento
--   por dispensacion del modulo de despacho (Fase 5).
--
-- Nota sobre el registro Invima:
--   Los medicamentos requieren registro Invima (obligatorio). Los utensilios o
--   productos que no son medicamentos pueden no tenerlo. Por eso se usa el
--   indicador `requiere_invima`: cuando es verdadero, el registro es
--   obligatorio; cuando es falso, el registro puede quedar vacio.
-- ============================================================

USE clinica;

SET @OLD_FOREIGN_KEY_CHECKS = @@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS = 0;

-- ------------------------------------------------------------
-- 1. Ampliacion de la tabla medicamento
--    Se agregan columnas solo si no existen (procedimiento idempotente).
-- ------------------------------------------------------------
ALTER TABLE `medicamento`
    ADD COLUMN IF NOT EXISTS `codigo` VARCHAR(8) NULL UNIQUE AFTER `nombre`,
    ADD COLUMN IF NOT EXISTS `requiere_invima` TINYINT(1) NOT NULL DEFAULT 1 AFTER `presentacion`,
    ADD COLUMN IF NOT EXISTS `registro_invima` VARCHAR(50) NULL AFTER `requiere_invima`,
    ADD COLUMN IF NOT EXISTS `precio_compra` DECIMAL(12,2) NOT NULL DEFAULT 0 AFTER `registro_invima`,
    ADD COLUMN IF NOT EXISTS `precio_venta` DECIMAL(12,2) NOT NULL DEFAULT 0 AFTER `precio_compra`,
    ADD COLUMN IF NOT EXISTS `stock_minimo` INT NOT NULL DEFAULT 0 AFTER `precio_venta`,
    ADD COLUMN IF NOT EXISTS `fecha_proximo_pedido` DATE NULL AFTER `stock_minimo`;

-- ------------------------------------------------------------
-- 2. Tabla de lotes de medicamento
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `lote_medicamento` (
    `id` BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
    `medicamento_id` BIGINT(20) UNSIGNED NOT NULL,
    `numero_lote` VARCHAR(50) NOT NULL,
    `cantidad` INT NOT NULL DEFAULT 0,
    `precio_compra` DECIMAL(12,2) NOT NULL DEFAULT 0,
    `fecha_ingreso` DATE NOT NULL,
    `fecha_vencimiento` DATE NOT NULL,
    `fecha_creacion_registro` DATETIME NOT NULL,
    `fecha_modificacion_registro` DATETIME DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_lote_medicamento` (`medicamento_id`),
    KEY `idx_lote_vencimiento` (`fecha_vencimiento`),
    CONSTRAINT `fk_lote_medicamento` FOREIGN KEY (`medicamento_id`)
        REFERENCES `medicamento` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
-- 3. Tabla de movimientos de inventario
--    tipo: ENTRADA | SALIDA
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `movimiento_inventario` (
    `id` BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
    `medicamento_id` BIGINT(20) UNSIGNED NOT NULL,
    `lote_id` BIGINT(20) UNSIGNED DEFAULT NULL,
    `tipo` VARCHAR(10) NOT NULL,
    `cantidad` INT NOT NULL,
    `fecha` DATETIME NOT NULL,
    `usuario_responsable` VARCHAR(100) DEFAULT NULL,
    `referencia` VARCHAR(150) DEFAULT NULL,
    `observacion` VARCHAR(255) DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_movimiento_medicamento` (`medicamento_id`),
    KEY `idx_movimiento_fecha` (`fecha`),
    CONSTRAINT `fk_movimiento_medicamento` FOREIGN KEY (`medicamento_id`)
        REFERENCES `medicamento` (`id`),
    CONSTRAINT `fk_movimiento_lote` FOREIGN KEY (`lote_id`)
        REFERENCES `lote_medicamento` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
-- 4. Tabla de log de carga masiva
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `log_carga_inventario` (
    `id` BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
    `fecha` DATETIME NOT NULL,
    `usuario_responsable` VARCHAR(100) DEFAULT NULL,
    `nombre_archivo` VARCHAR(255) DEFAULT NULL,
    `total_registros` INT NOT NULL DEFAULT 0,
    `registros_validos` INT NOT NULL DEFAULT 0,
    `registros_con_error` INT NOT NULL DEFAULT 0,
    `detalle` TEXT,
    PRIMARY KEY (`id`),
    KEY `idx_log_carga_fecha` (`fecha`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

SET FOREIGN_KEY_CHECKS = @OLD_FOREIGN_KEY_CHECKS;

-- ------------------------------------------------------------
-- 5. Menu del modulo de inventario y asignacion al Administrador
-- ------------------------------------------------------------
INSERT INTO `menu` (`code`, `title`, `url`, `icon`, `activo`, `fecha_creacion`)
SELECT 'inventario', 'Gestion de Inventario', '/inicio/inventario',
       'fa-solid fa-boxes-stacked', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM `menu` WHERE `code` = 'inventario');

INSERT INTO `perfil_menu` (`perfil_id`, `menu_id`)
SELECT 1, m.`menu_id` FROM `menu` m
WHERE m.`code` = 'inventario'
  AND NOT EXISTS (
      SELECT 1 FROM `perfil_menu` pm
      WHERE pm.`perfil_id` = 1 AND pm.`menu_id` = m.`menu_id`);

-- ============================================================
-- Fin del script de la Fase 1
-- ============================================================

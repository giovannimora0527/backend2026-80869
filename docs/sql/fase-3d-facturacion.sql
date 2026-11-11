-- ============================================================
-- Fase 3D - Facturacion de servicios veterinarios
-- Fecha: 2026-11
-- ============================================================
--
-- Objetivo de la fase:
--   Permitir generar facturas a partir de la atencion de una cita, con sus
--   servicios y medicamentos, calculo de subtotal, IVA, descuento y total,
--   estados de pago y registro de pagos. Incluye los datos de la clinica para
--   la cabecera de la factura y la generacion del documento en PDF.
--
-- Resumen de cambios en el modelo de datos:
--   1. `datos_clinica`: datos de la clinica (nombre, NIT, telefono, direccion,
--      correo) que se imprimen en la factura. Se gestiona como un unico
--      registro de configuracion.
--   2. `factura`: cabecera de la factura, relacionada con la cita, el cliente,
--      la mascota y el medico. Guarda subtotal, IVA, descuento, total, el
--      estado de pago y la fecha. Los valores se conservan historicamente.
--   3. `factura_detalle`: items de la factura. Cada item puede ser un servicio
--      o un medicamento, con cantidad, precio unitario, IVA e importe. El valor
--      de la tarifa se guarda en el detalle para conservarlo historicamente.
--   4. `pago`: pagos registrados sobre una factura. Permite calcular el estado
--      (pendiente, pago parcial, pagada).
--
-- Justificacion:
--   El enunciado exige una factura con datos de clinica, propietario, mascota,
--   veterinario, servicios y medicamentos, subtotal, IVA, descuentos, total,
--   estado de pago e historial de facturacion, conservando historicamente las
--   tarifas y permitiendo anular sin eliminar registros.
--
-- Estados de pago de la factura:
--   PENDIENTE, PAGO_PARCIAL, PAGADA, ANULADA.
--
-- Tipos de item en el detalle:
--   SERVICIO, MEDICAMENTO.
-- ============================================================

USE clinica;

SET @OLD_FOREIGN_KEY_CHECKS = @@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS = 0;

-- ------------------------------------------------------------
-- 1. Datos de la clinica
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `datos_clinica` (
    `id` BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
    `nombre` VARCHAR(150) NOT NULL,
    `nit` VARCHAR(30) DEFAULT NULL,
    `telefono` VARCHAR(30) DEFAULT NULL,
    `direccion` VARCHAR(200) DEFAULT NULL,
    `correo` VARCHAR(150) DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Registro inicial de datos de la clinica (si no existe).
INSERT INTO `datos_clinica` (`nombre`, `nit`, `telefono`, `direccion`, `correo`)
SELECT 'Clinica Veterinaria Uniminuto', '900.123.456-7', '601 000 0000',
       'Calle 80 # 100-20, Bogota', 'contacto@clinicaveterinaria.com'
WHERE NOT EXISTS (SELECT 1 FROM `datos_clinica`);

-- ------------------------------------------------------------
-- 2. Factura (cabecera)
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `factura` (
    `id` BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
    `cita_id` INT(11) DEFAULT NULL,
    `cliente_id` INT(11) DEFAULT NULL,
    `mascota_id` INT(11) DEFAULT NULL,
    `medico_id` INT(11) DEFAULT NULL,
    `fecha` DATETIME NOT NULL,
    `subtotal` DECIMAL(14,2) NOT NULL DEFAULT 0,
    `iva` DECIMAL(14,2) NOT NULL DEFAULT 0,
    `descuento` DECIMAL(14,2) NOT NULL DEFAULT 0,
    `total` DECIMAL(14,2) NOT NULL DEFAULT 0,
    `estado_pago` VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
    `observacion` VARCHAR(255) DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_factura_cita` (`cita_id`),
    KEY `idx_factura_cliente` (`cliente_id`),
    KEY `idx_factura_fecha` (`fecha`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
-- 3. Detalle de la factura
--    tipo: SERVICIO | MEDICAMENTO
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `factura_detalle` (
    `id` BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
    `factura_id` BIGINT(20) UNSIGNED NOT NULL,
    `tipo` VARCHAR(15) NOT NULL,
    `referencia_id` BIGINT(20) DEFAULT NULL,
    `descripcion` VARCHAR(200) NOT NULL,
    `cantidad` INT NOT NULL DEFAULT 1,
    `precio_unitario` DECIMAL(14,2) NOT NULL DEFAULT 0,
    `iva_porcentaje` DECIMAL(5,2) NOT NULL DEFAULT 0,
    `subtotal` DECIMAL(14,2) NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    KEY `idx_factura_detalle_factura` (`factura_id`),
    CONSTRAINT `fk_factura_detalle_factura` FOREIGN KEY (`factura_id`)
        REFERENCES `factura` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
-- 4. Pagos de la factura
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `pago` (
    `id` BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
    `factura_id` BIGINT(20) UNSIGNED NOT NULL,
    `monto` DECIMAL(14,2) NOT NULL,
    `fecha` DATETIME NOT NULL,
    `metodo` VARCHAR(30) DEFAULT NULL,
    `usuario_responsable` VARCHAR(100) DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_pago_factura` (`factura_id`),
    CONSTRAINT `fk_pago_factura` FOREIGN KEY (`factura_id`)
        REFERENCES `factura` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

SET FOREIGN_KEY_CHECKS = @OLD_FOREIGN_KEY_CHECKS;

-- ------------------------------------------------------------
-- 5. Menu del modulo de facturacion y asignacion de perfiles
-- ------------------------------------------------------------
INSERT INTO `menu` (`code`, `title`, `url`, `icon`, `activo`, `fecha_creacion`)
SELECT 'facturacion', 'Facturacion', '/inicio/facturacion',
       'fa-solid fa-file-invoice-dollar', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM `menu` WHERE `code` = 'facturacion');

-- Administrador (1) y Recepcion (2) pueden facturar.
INSERT INTO `perfil_menu` (`perfil_id`, `menu_id`)
SELECT 1, m.`menu_id` FROM `menu` m WHERE m.`code` = 'facturacion'
  AND NOT EXISTS (SELECT 1 FROM `perfil_menu` pm WHERE pm.`perfil_id` = 1 AND pm.`menu_id` = m.`menu_id`);
INSERT INTO `perfil_menu` (`perfil_id`, `menu_id`)
SELECT 2, m.`menu_id` FROM `menu` m WHERE m.`code` = 'facturacion'
  AND NOT EXISTS (SELECT 1 FROM `perfil_menu` pm WHERE pm.`perfil_id` = 2 AND pm.`menu_id` = m.`menu_id`);

-- ============================================================
-- Fin del script de la Fase 3D
-- ============================================================

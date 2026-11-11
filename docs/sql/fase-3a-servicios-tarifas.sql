-- ============================================================
-- Fase 3A - Servicios y tarifas
-- Fecha: 2026-11
-- ============================================================
--
-- Objetivo de la fase:
--   Crear un modulo administrativo para gestionar los servicios de la clinica
--   (consultas y procedimientos) y sus tarifas. Las tarifas son configurables
--   desde la aplicacion, sin modificar el codigo fuente, y se puede consultar
--   el historial de cambios de cada tarifa.
--
-- Resumen de cambios en el modelo de datos:
--   1. Tabla `servicio`: catalogo de servicios o procedimientos. Cada servicio
--      tiene nombre, descripcion, una especializacion opcional asociada, el
--      precio (valor del servicio), el porcentaje de IVA aplicable y un estado
--      activo/inactivo.
--   2. Tabla `log_tarifa`: historial de cambios de tarifa de un servicio. Cada
--      vez que cambia el precio o el IVA de un servicio, se registra el valor
--      anterior, el nuevo, la fecha y el usuario responsable.
--
-- Justificacion:
--   El enunciado exige gestionar tarifas por tipo de consulta o servicio, con
--   IVA configurable, estructura flexible para nuevos servicios y un historial
--   de cambios de tarifas. La relacion opcional con especializacion permite que
--   un servicio dependa de una especialidad (por ejemplo, un electrocardiograma
--   asociado a cardiologia) o sea general (por ejemplo, una consulta basica).
--
--   El historial (log_tarifa) garantiza la trazabilidad de los cambios de
--   precios, que luego permite conservar historicamente el valor cobrado en
--   cada factura.
-- ============================================================

USE clinica;

SET @OLD_FOREIGN_KEY_CHECKS = @@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS = 0;

-- ------------------------------------------------------------
-- 1. Tabla de servicios
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `servicio` (
    `id` BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
    `nombre` VARCHAR(150) NOT NULL,
    `descripcion` TEXT,
    `especializacion_id` BIGINT(20) UNSIGNED DEFAULT NULL,
    `precio` DECIMAL(12,2) NOT NULL DEFAULT 0,
    `iva_porcentaje` DECIMAL(5,2) NOT NULL DEFAULT 0,
    `activo` TINYINT(1) NOT NULL DEFAULT 1,
    `fecha_creacion` DATETIME NOT NULL,
    `fecha_modificacion` DATETIME DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_servicio_nombre` (`nombre`),
    KEY `idx_servicio_especializacion` (`especializacion_id`),
    CONSTRAINT `fk_servicio_especializacion` FOREIGN KEY (`especializacion_id`)
        REFERENCES `especializacion` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
-- 2. Tabla de log de tarifas (historial de cambios)
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `log_tarifa` (
    `id` BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
    `servicio_id` BIGINT(20) UNSIGNED NOT NULL,
    `precio_anterior` DECIMAL(12,2) DEFAULT NULL,
    `precio_nuevo` DECIMAL(12,2) DEFAULT NULL,
    `iva_anterior` DECIMAL(5,2) DEFAULT NULL,
    `iva_nuevo` DECIMAL(5,2) DEFAULT NULL,
    `fecha` DATETIME NOT NULL,
    `usuario_responsable` VARCHAR(100) DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_log_tarifa_servicio` (`servicio_id`),
    CONSTRAINT `fk_log_tarifa_servicio` FOREIGN KEY (`servicio_id`)
        REFERENCES `servicio` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

SET FOREIGN_KEY_CHECKS = @OLD_FOREIGN_KEY_CHECKS;

-- ------------------------------------------------------------
-- 3. Menu del modulo de servicios y asignacion al Administrador
-- ------------------------------------------------------------
INSERT INTO `menu` (`code`, `title`, `url`, `icon`, `activo`, `fecha_creacion`)
SELECT 'servicios', 'Gestion de Servicios', '/inicio/servicios',
       'fa-solid fa-hand-holding-medical', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM `menu` WHERE `code` = 'servicios');

INSERT INTO `perfil_menu` (`perfil_id`, `menu_id`)
SELECT 1, m.`menu_id` FROM `menu` m
WHERE m.`code` = 'servicios'
  AND NOT EXISTS (SELECT 1 FROM `perfil_menu` pm
                  WHERE pm.`perfil_id` = 1 AND pm.`menu_id` = m.`menu_id`);

-- ============================================================
-- Fin del script de la Fase 3A
-- ============================================================

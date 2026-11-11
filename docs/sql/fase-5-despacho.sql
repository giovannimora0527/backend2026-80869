-- ============================================================
-- Fase 5 - Modulo de despacho y dispensacion de medicamentos
-- Fecha: 2026-11
-- ============================================================
--
-- Objetivo de la fase:
--   Gestionar la dispensacion de los medicamentos formulados, separando la
--   formulacion (que no afecta el inventario) de la entrega real (que si lo
--   descuenta). El modulo lo opera el perfil DESPACHO_MEDICO. La entrega
--   descuenta de los lotes priorizando el vencimiento mas proximo (FEFO),
--   genera movimientos de salida, permite entregas parciales y produce un
--   comprobante en PDF. Todo se hace de forma transaccional.
--
-- Resumen de cambios en el modelo de datos:
--   1. `despacho`: cabecera del despacho de una formula, con su estado, fecha y
--      usuario responsable.
--   2. `despacho_detalle`: por cada medicamento, la cantidad solicitada, la
--      cantidad entregada y el lote del que se descuenta.
--   3. `log_despacho`: historial de cambios de estado del despacho.
--   4. Nuevo perfil DESPACHO_MEDICO con acceso solo a inventario (lectura) y
--      despacho.
--
-- Estados del despacho:
--   PENDIENTE, EN_PREPARACION, DESPACHO_PARCIAL, DESPACHADO,
--   PENDIENTE_DISPONIBILIDAD, CANCELADO.
--
-- Justificacion:
--   El enunciado exige un modulo independiente operado por DESPACHO_MEDICO que
--   verifique las formulas, valide existencias y lotes, descuente el inventario
--   solo al confirmar, soporte entregas parciales, conserve trazabilidad (log)
--   y no permita descontar dos veces ni entregar medicamentos vencidos.
-- ============================================================

USE clinica;

SET @OLD_FOREIGN_KEY_CHECKS = @@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS = 0;

-- ------------------------------------------------------------
-- 1. Despacho (cabecera)
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `despacho` (
    `id` BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
    `formula_id` BIGINT(20) UNSIGNED NOT NULL,
    `estado` VARCHAR(30) NOT NULL DEFAULT 'PENDIENTE',
    `fecha` DATETIME NOT NULL,
    `usuario_responsable` VARCHAR(100) DEFAULT NULL,
    `observacion` VARCHAR(255) DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_despacho_formula` (`formula_id`),
    CONSTRAINT `fk_despacho_formula` FOREIGN KEY (`formula_id`)
        REFERENCES `formula_medica` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
-- 2. Detalle del despacho
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `despacho_detalle` (
    `id` BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
    `despacho_id` BIGINT(20) UNSIGNED NOT NULL,
    `medicamento_id` BIGINT(20) UNSIGNED NOT NULL,
    `lote_id` BIGINT(20) UNSIGNED DEFAULT NULL,
    `cantidad_solicitada` INT NOT NULL DEFAULT 0,
    `cantidad_entregada` INT NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    KEY `idx_despacho_detalle_despacho` (`despacho_id`),
    CONSTRAINT `fk_despacho_detalle_despacho` FOREIGN KEY (`despacho_id`)
        REFERENCES `despacho` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
-- 3. Log de cambios de estado del despacho
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `log_despacho` (
    `id` BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
    `despacho_id` BIGINT(20) UNSIGNED NOT NULL,
    `estado_anterior` VARCHAR(30) DEFAULT NULL,
    `estado_nuevo` VARCHAR(30) NOT NULL,
    `fecha` DATETIME NOT NULL,
    `usuario_responsable` VARCHAR(100) DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_log_despacho_despacho` (`despacho_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

SET FOREIGN_KEY_CHECKS = @OLD_FOREIGN_KEY_CHECKS;

-- ------------------------------------------------------------
-- 4. Perfil DESPACHO_MEDICO y sus menus
-- ------------------------------------------------------------
INSERT INTO `perfil` (`nombre`, `activo`, `fecha_creacion`)
SELECT 'DESPACHO_MEDICO', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM `perfil` WHERE `nombre` = 'DESPACHO_MEDICO');

-- Menu del modulo de despacho.
INSERT INTO `menu` (`code`, `title`, `url`, `icon`, `activo`, `fecha_creacion`)
SELECT 'despacho', 'Despacho de Medicamentos', '/inicio/despacho',
       'fa-solid fa-truck-medical', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM `menu` WHERE `code` = 'despacho');

-- Asignar al perfil DESPACHO_MEDICO los menus de inventario (consulta) y despacho.
INSERT INTO `perfil_menu` (`perfil_id`, `menu_id`)
SELECT p.`perfil_id`, m.`menu_id`
FROM `perfil` p, `menu` m
WHERE p.`nombre` = 'DESPACHO_MEDICO' AND m.`code` = 'inventario'
  AND NOT EXISTS (SELECT 1 FROM `perfil_menu` pm
                  WHERE pm.`perfil_id` = p.`perfil_id` AND pm.`menu_id` = m.`menu_id`);

INSERT INTO `perfil_menu` (`perfil_id`, `menu_id`)
SELECT p.`perfil_id`, m.`menu_id`
FROM `perfil` p, `menu` m
WHERE p.`nombre` = 'DESPACHO_MEDICO' AND m.`code` = 'despacho'
  AND NOT EXISTS (SELECT 1 FROM `perfil_menu` pm
                  WHERE pm.`perfil_id` = p.`perfil_id` AND pm.`menu_id` = m.`menu_id`);

-- El Administrador tambien ve el modulo de despacho.
INSERT INTO `perfil_menu` (`perfil_id`, `menu_id`)
SELECT 1, m.`menu_id` FROM `menu` m WHERE m.`code` = 'despacho'
  AND NOT EXISTS (SELECT 1 FROM `perfil_menu` pm WHERE pm.`perfil_id` = 1 AND pm.`menu_id` = m.`menu_id`);

-- ============================================================
-- Fin del script de la Fase 5
-- ============================================================

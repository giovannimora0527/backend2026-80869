-- ============================================================
-- Fase 3B - Formula medica con varios medicamentos (detalle)
-- Fecha: 2026-11
-- ============================================================
--
-- Objetivo de la fase:
--   Permitir que una formula medica contenga varios medicamentos. Hasta ahora
--   la tabla formula_medica referenciaba un unico medicamento. Se agrega una
--   tabla de detalle (formula_medicamento) que relaciona una formula con uno o
--   varios medicamentos, cada uno con su dosis, frecuencia, duracion, cantidad
--   prescrita y via de administracion.
--
-- Resumen de cambios en el modelo de datos:
--   1. Se permite que formula_medica.medicamento_id y formula_medica.dosis sean
--      NULL, para que la cabecera de la formula ya no dependa de un unico
--      medicamento ni de una unica dosis. Las formulas antiguas conservan sus
--      valores; las nuevas registran dosis por medicamento en el detalle.
--   2. Se agrega a formula_medica la columna medico_id (quien formula) y
--      fecha_creacion, para la cabecera.
--   3. Se crea la tabla formula_medicamento (detalle de la formula).
--
-- Justificacion:
--   El enunciado exige que el veterinario seleccione uno o varios medicamentos
--   en una formula y registre por cada uno dosis, frecuencia, duracion,
--   cantidad y via. La tabla de detalle es la forma correcta de modelar una
--   relacion de uno a muchos entre formula y medicamento, y es la base para la
--   dispensacion por el modulo de despacho (Fase 5).
--
--   Se conserva la columna medicamento_id original (ahora opcional) para no
--   romper los datos ni el modulo de formula ya existente.
-- ============================================================

USE clinica;

SET @OLD_FOREIGN_KEY_CHECKS = @@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS = 0;

-- 1. Hacer opcional el medicamento en la cabecera de la formula.
ALTER TABLE `formula_medica`
    MODIFY COLUMN `medicamento_id` INT(11) NULL;

-- 1.1 Hacer opcional la dosis en la cabecera de la formula.
--     En el modelo nuevo la dosis se registra por cada medicamento en la tabla
--     de detalle (formula_medicamento). La cabecera ya no la necesita, por lo
--     que debe permitir NULL; de lo contrario, crear una formula nueva falla
--     con "Column 'dosis' cannot be null".
ALTER TABLE `formula_medica`
    MODIFY COLUMN `dosis` TEXT NULL;

-- 2. Columnas de cabecera (medico que formula y fecha).
ALTER TABLE `formula_medica`
    ADD COLUMN IF NOT EXISTS `medico_id` INT(11) NULL AFTER `cita_id`,
    ADD COLUMN IF NOT EXISTS `fecha_creacion` DATETIME NULL;

-- 3. Tabla de detalle: medicamentos de la formula.
CREATE TABLE IF NOT EXISTS `formula_medicamento` (
    `id` BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
    `formula_id` BIGINT(20) UNSIGNED NOT NULL,
    `medicamento_id` BIGINT(20) UNSIGNED NOT NULL,
    `dosis` VARCHAR(255) NOT NULL,
    `frecuencia` VARCHAR(255) DEFAULT NULL,
    `duracion` VARCHAR(255) DEFAULT NULL,
    `cantidad` INT NOT NULL DEFAULT 1,
    `via_administracion` VARCHAR(100) DEFAULT NULL,
    `indicaciones` TEXT,
    PRIMARY KEY (`id`),
    KEY `idx_formula_medicamento_formula` (`formula_id`),
    KEY `idx_formula_medicamento_medicamento` (`medicamento_id`),
    CONSTRAINT `fk_formula_medicamento_formula` FOREIGN KEY (`formula_id`)
        REFERENCES `formula_medica` (`id`),
    CONSTRAINT `fk_formula_medicamento_medicamento` FOREIGN KEY (`medicamento_id`)
        REFERENCES `medicamento` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

SET FOREIGN_KEY_CHECKS = @OLD_FOREIGN_KEY_CHECKS;

-- ============================================================
-- Fin del script de la Fase 3B
-- ============================================================

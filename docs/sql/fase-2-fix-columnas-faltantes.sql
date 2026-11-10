-- ============================================================
-- Fase 2 - Correccion de columnas faltantes entre entidades y base de datos
-- Fecha: 2026-11
-- ============================================================
--
-- Contexto:
--   Varias entidades del proyecto declaran columnas que no estaban presentes
--   en la base de datos de trabajo. La ausencia de estas columnas provoca
--   errores 500 ("Unknown column") al listar o guardar en los modulos
--   correspondientes.
--
--   Se realizo una revision de todas las entidades contra la base de datos y se
--   identificaron los siguientes faltantes:
--     - medico.estado                (activo/inactivo del medico)
--     - mascota.activo               (activo/inactivo de la mascota)
--     - formula_medica.frecuencia    (frecuencia de administracion)
--     - formula_medica.duracion      (duracion del tratamiento)
--     - anotacion_historia.diagnostico
--     - anotacion_historia.observaciones
--     - anotacion_historia.cita_id   (relacion con la cita)
--
-- Justificacion:
--   medico.estado soporta la regla de no asignar citas a medicos inactivos.
--   mascota.activo permite inactivar sin borrar (integridad).
--   formula_medica.frecuencia y duracion son datos clinicos exigidos por el
--   enunciado para la formulacion.
--   Las columnas de anotacion_historia soportan el registro clinico de la
--   atencion (diagnostico, observaciones y el vinculo con la cita atendida).
--
-- Efecto:
--   Con DEFAULT 1, los registros existentes de medico y mascota quedan activos.
--   Las columnas de texto se agregan como NULL (opcionales).
-- ============================================================

USE clinica;

SET @OLD_FOREIGN_KEY_CHECKS = @@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS = 0;

-- medico.estado
ALTER TABLE `medico`
    ADD COLUMN IF NOT EXISTS `estado` TINYINT(1) NOT NULL DEFAULT 1;

-- mascota.activo
ALTER TABLE `mascota`
    ADD COLUMN IF NOT EXISTS `activo` TINYINT(1) NOT NULL DEFAULT 1;

-- formula_medica: frecuencia y duracion
ALTER TABLE `formula_medica`
    ADD COLUMN IF NOT EXISTS `frecuencia` VARCHAR(255) NULL,
    ADD COLUMN IF NOT EXISTS `duracion` VARCHAR(255) NULL;

-- anotacion_historia: diagnostico, observaciones y cita_id
ALTER TABLE `anotacion_historia`
    ADD COLUMN IF NOT EXISTS `diagnostico` TEXT NULL,
    ADD COLUMN IF NOT EXISTS `observaciones` TEXT NULL,
    ADD COLUMN IF NOT EXISTS `cita_id` INT NULL;

SET FOREIGN_KEY_CHECKS = @OLD_FOREIGN_KEY_CHECKS;

-- ============================================================
-- Fin del script de correccion
-- ============================================================

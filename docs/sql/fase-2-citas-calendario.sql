-- ============================================================
-- Fase 2 - Modulo de gestion de citas medicas mediante calendario
-- Fecha: 2026-11
-- ============================================================
--
-- Objetivo de la fase:
--   Permitir que cada medico gestione sus citas mediante una vista de
--   calendario, con estados, reprogramacion, reglas de no solapamiento,
--   horarios de disponibilidad y reporte en Excel. La vista por rol muestra
--   al medico unicamente sus citas; recepcion y administrador ven todas.
--
-- Resumen de cambios en el modelo de datos:
--   1. Se agrega la columna `usuario_id` a la tabla `medico`, para vincular al
--      medico con su usuario del sistema. Asi, al iniciar sesion un medico, el
--      calendario puede mostrar las citas asignadas a ese profesional.
--   2. Se crea la tabla `horario_medico` para registrar la disponibilidad del
--      veterinario (dia de la semana y franja horaria). Se usa para impedir el
--      agendamiento de citas fuera del horario disponible.
--   3. Se documentan los estados de la cita en la columna `estado` existente.
--      No se modifica la estructura de `cita`, pues `estado` ya es un varchar.
--
-- Justificacion:
--   El enunciado exige que el calendario muestre las citas del veterinario
--   autenticado y que la informacion provenga de la base de datos. Para ello el
--   usuario que inicia sesion debe poder asociarse a un registro de medico; de
--   ahi la columna `usuario_id` en `medico`. Los horarios de disponibilidad son
--   una de las entidades minimas solicitadas en el ajuste del modelo (2.4).
--
-- Estados de la cita (valores usados por la aplicacion):
--   PROGRAMADA  : cita creada, pendiente de confirmacion.
--   CONFIRMADA  : el cliente confirmo o llego a la cita.
--   ATENDIDA    : la cita fue atendida por el medico.
--   CANCELADA   : la cita fue cancelada (se conserva su historial).
--   FINALIZADA  : la atencion asociada a la cita se cerro.
-- ============================================================

USE clinica;

SET @OLD_FOREIGN_KEY_CHECKS = @@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS = 0;

-- ------------------------------------------------------------
-- 1. Vinculo medico - usuario
-- ------------------------------------------------------------
ALTER TABLE `medico`
    ADD COLUMN IF NOT EXISTS `usuario_id` BIGINT(20) UNSIGNED NULL AFTER `id`;

-- Nota: se omite la restriccion de llave foranea formal para evitar conflictos
-- con datos existentes; la integridad se valida a nivel de aplicacion. Si se
-- desea agregar la FK y los tipos coinciden, puede ejecutarse:
--   ALTER TABLE `medico` ADD CONSTRAINT `fk_medico_usuario`
--       FOREIGN KEY (`usuario_id`) REFERENCES `usuario` (`id`);

-- ------------------------------------------------------------
-- 2. Horarios de disponibilidad del medico
--    dia_semana: 1 = lunes ... 7 = domingo (segun java.time.DayOfWeek)
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `horario_medico` (
    `id` BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
    `medico_id` BIGINT(20) UNSIGNED NOT NULL,
    `dia_semana` INT NOT NULL,
    `hora_inicio` TIME NOT NULL,
    `hora_fin` TIME NOT NULL,
    `activo` TINYINT(1) NOT NULL DEFAULT 1,
    PRIMARY KEY (`id`),
    KEY `idx_horario_medico` (`medico_id`),
    CONSTRAINT `fk_horario_medico` FOREIGN KEY (`medico_id`)
        REFERENCES `medico` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

SET FOREIGN_KEY_CHECKS = @OLD_FOREIGN_KEY_CHECKS;

-- ------------------------------------------------------------
-- 3. Menu del modulo de calendario medico y asignacion de perfiles
-- ------------------------------------------------------------
-- Menu de calendario (lo veran los medicos).
INSERT INTO `menu` (`code`, `title`, `url`, `icon`, `activo`, `fecha_creacion`)
SELECT 'calendario', 'Mi Calendario', '/inicio/calendario',
       'fa-solid fa-calendar-days', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM `menu` WHERE `code` = 'calendario');

-- Se asegura la existencia del perfil Medico (perfil_id = 3 segun datos base).
-- Asignacion de menus al perfil Medico: calendario, mascotas e historias.
INSERT INTO `perfil_menu` (`perfil_id`, `menu_id`)
SELECT 3, m.`menu_id` FROM `menu` m
WHERE m.`code` = 'calendario'
  AND NOT EXISTS (SELECT 1 FROM `perfil_menu` pm
                  WHERE pm.`perfil_id` = 3 AND pm.`menu_id` = m.`menu_id`);

-- El administrador tambien puede acceder al calendario.
INSERT INTO `perfil_menu` (`perfil_id`, `menu_id`)
SELECT 1, m.`menu_id` FROM `menu` m
WHERE m.`code` = 'calendario'
  AND NOT EXISTS (SELECT 1 FROM `perfil_menu` pm
                  WHERE pm.`perfil_id` = 1 AND pm.`menu_id` = m.`menu_id`);

-- ------------------------------------------------------------
-- 4. Ejemplo (opcional): vincular un medico con el usuario de prueba
-- ------------------------------------------------------------
-- Para probar el calendario como medico, vincule un registro de medico con el
-- usuario correspondiente. Ejemplo (ajuste los valores a su caso):
--   UPDATE medico SET usuario_id = (SELECT id FROM usuario WHERE username = 'medico_prueba')
--    WHERE id = 1;

-- ============================================================
-- Fin del script de la Fase 2
-- ============================================================

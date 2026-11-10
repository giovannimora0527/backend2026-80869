-- ============================================================
-- Gestion de roles desde la interfaz
-- Fecha: 2026-11
-- ============================================================
--
-- Objetivo:
--   Habilitar la administracion de roles (perfiles) y sus menus desde la
--   aplicacion, de modo que el administrador no requiera modificar la base de
--   datos manualmente para crear roles o cambiar sus permisos.
--
-- Cambios en el modelo de datos:
--   NINGUNO en estructura. Se utilizan las tablas existentes perfil,
--   perfil_menu y menu. La asignacion de menus a un rol se gestiona desde la
--   nueva pantalla de Gestion de Roles.
--
-- Este script solo agrega el menu "Gestion de Roles" y lo asigna al perfil
-- Administrador.
-- ============================================================

USE clinica;

-- Menu de gestion de roles.
INSERT INTO `menu` (`code`, `title`, `url`, `icon`, `activo`, `fecha_creacion`)
SELECT 'roles', 'Gestion de Roles', '/inicio/roles', 'feather icon-shield', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM `menu` WHERE `code` = 'roles');

-- Asignacion del menu de roles al Administrador (perfil_id = 1).
INSERT INTO `perfil_menu` (`perfil_id`, `menu_id`)
SELECT 1, m.`menu_id` FROM `menu` m
WHERE m.`code` = 'roles'
  AND NOT EXISTS (SELECT 1 FROM `perfil_menu` pm
                  WHERE pm.`perfil_id` = 1 AND pm.`menu_id` = m.`menu_id`);

-- ============================================================
-- Fin del script
-- ============================================================

-- ============================================================
-- Fase 0.1 - Gestion de usuarios
-- Fecha: 2026-11
-- ============================================================
--
-- Objetivo de la fase:
--   - Completar la gestion de usuarios desde la interfaz (listar, crear,
--     editar) para que el Administrador cree usuarios de prueba con distintos
--     roles sin necesidad de insertar registros por SQL.
--   - Agregar el endpoint de listado de perfiles para poblar la seleccion de
--     rol en el formulario.
--
-- Cambios en el modelo de datos:
--   NINGUNO. La estructura de usuario, perfil y perfil_menu ya existe.
--
-- ------------------------------------------------------------
-- Modelo de roles (referencia)
-- ------------------------------------------------------------
-- El rol de un usuario se define mediante tres tablas relacionadas:
--
--   usuario.rol  ->  perfil.perfil_id        (que rol tiene el usuario)
--   perfil_menu  ->  relaciona perfil y menu (que modulos ve el perfil)
--   menu                                       (catalogo de modulos)
--
-- Perfiles existentes:
--   1 = Administrador
--   2 = Recepcion
--   3 = Medico
--
-- Importante: en el estado actual, solo el perfil 1 (Administrador) tiene
-- filas en perfil_menu. Por lo tanto, un usuario creado con otro perfil no
-- vera menus hasta que se definan sus registros en perfil_menu. La asignacion
-- de menus por perfil se completara en las fases que incorporen cada modulo.
--
-- ------------------------------------------------------------
-- Datos de prueba (opcional)
-- ------------------------------------------------------------
-- Ejemplo para fijar una contrasena conocida a un usuario administrador de
-- pruebas (la contrasena se almacena en MD5). Reemplace el valor del hash por
-- el MD5 de la contrasena deseada antes de ejecutar.
--
--   UPDATE usuario
--      SET password_hash = MD5('su_contrasena'),
--          email = 'correo_real@dominio.com'
--    WHERE username = 'david';
--
-- Nota: el inicio de sesion usa doble factor; el codigo OTP se envia al correo
-- registrado, por lo que debe ser un correo real y accesible.
--
-- ------------------------------------------------------------
-- Asignacion de menus al perfil Administrador
-- ------------------------------------------------------------
-- Criterio de diseno: el perfil Administrador es el superusuario del sistema
-- y, por definicion, tiene acceso a todos los modulos. Los perfiles operativos
-- (Recepcion, Medico, Despacho) son los que se restringen a sus funciones.
--
-- En el estado inicial, el Administrador (perfil_id = 1) tenia asignados solo
-- 6 de los 9 menus existentes. Las siguientes sentencias le asignan los menus
-- faltantes (Clientes, Formulas, Historias). Se usa INSERT ... WHERE NOT EXISTS
-- para no duplicar filas si ya estuvieran presentes.
--
-- Clientes (menu_id = 2)
INSERT INTO perfil_menu (perfil_id, menu_id)
SELECT 1, 2 WHERE NOT EXISTS (
    SELECT 1 FROM perfil_menu WHERE perfil_id = 1 AND menu_id = 2);
-- Formulas (menu_id = 4)
INSERT INTO perfil_menu (perfil_id, menu_id)
SELECT 1, 4 WHERE NOT EXISTS (
    SELECT 1 FROM perfil_menu WHERE perfil_id = 1 AND menu_id = 4);
-- Historias (menu_id = 5)
INSERT INTO perfil_menu (perfil_id, menu_id)
SELECT 1, 5 WHERE NOT EXISTS (
    SELECT 1 FROM perfil_menu WHERE perfil_id = 1 AND menu_id = 5);
--
-- Nota: tras ejecutar estas sentencias, el usuario administrador debe cerrar
-- sesion y volver a iniciarla para que el token JWT se regenere con los menus
-- actualizados. En las fases siguientes, cada modulo nuevo agregara su menu y
-- la fila correspondiente en perfil_menu para el Administrador.
-- ============================================================

-- ============================================================
-- Fase 4 - Modulo de reportes administrativos y contables
-- Fecha: 2026-11
-- ============================================================
--
-- Objetivo de la fase:
--   Implementar un modulo de reportes que consolide la informacion financiera,
--   de inventario y de gestion medica de la clinica. Todos los reportes
--   permiten filtrar por rango de fechas, muestran una vista previa y pueden
--   exportarse a PDF y Excel. El acceso al modulo se restringe por rol tanto en
--   el frontend (ocultando el menu) como en el backend (filtro de seguridad).
--
-- Resumen de cambios en el modelo de datos:
--   NINGUNO en estructura de tablas. El modulo de reportes es de solo lectura y
--   se construye a partir de la informacion ya registrada por los modulos de
--   facturacion (factura, factura_detalle, pago), inventario (medicamento,
--   lote_medicamento, movimiento_inventario) y citas (cita). Por tanto, no crea
--   ni altera tablas: unicamente agrega el menu del modulo y dos perfiles
--   nuevos con sus asignaciones de menu.
--
-- Justificacion de los perfiles nuevos:
--   El enunciado (requerimiento 4) exige restringir el acceso a los reportes
--   segun los permisos del usuario y plantea, de forma opcional, perfiles
--   contables y de bodega. Para lograr una separacion de responsabilidades
--   realista se incorporan dos perfiles diferenciados:
--     - Contable: orientado a la informacion financiera. Accede al modulo de
--       reportes y a la facturacion en consulta.
--     - JEFE_BODEGA: orientado al control de existencias. Accede al modulo de
--       reportes y al inventario en consulta.
--   El perfil Administrador conserva el acceso completo e incorpora el nuevo
--   menu de reportes.
-- ============================================================

USE clinica;

-- ------------------------------------------------------------
-- 1. Menu del modulo de reportes
-- ------------------------------------------------------------
-- El atributo code ('reportes') debe coincidir con el id del item de
-- navegacion definido en el frontend (navigation.ts) para que el menu se
-- muestre a los perfiles autorizados.
INSERT INTO `menu` (`code`, `title`, `url`, `icon`, `activo`, `fecha_creacion`)
SELECT 'reportes', 'Reportes', '/inicio/reportes', 'fa-solid fa-chart-line', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM `menu` WHERE `code` = 'reportes');

-- ------------------------------------------------------------
-- 2. Perfiles nuevos: Contable y Jefe de bodega (JEFE_BODEGA)
-- ------------------------------------------------------------
INSERT INTO `perfil` (`nombre`, `activo`, `fecha_creacion`)
SELECT 'Contable', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM `perfil` WHERE `nombre` = 'Contable');

INSERT INTO `perfil` (`nombre`, `activo`, `fecha_creacion`)
SELECT 'JEFE_BODEGA', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM `perfil` WHERE `nombre` = 'JEFE_BODEGA');

-- ------------------------------------------------------------
-- 3. Asignacion del menu de reportes al Administrador (perfil_id = 1)
-- ------------------------------------------------------------
INSERT INTO `perfil_menu` (`perfil_id`, `menu_id`)
SELECT 1, m.`menu_id` FROM `menu` m
WHERE m.`code` = 'reportes'
  AND NOT EXISTS (SELECT 1 FROM `perfil_menu` pm
                  WHERE pm.`perfil_id` = 1 AND pm.`menu_id` = m.`menu_id`);

-- ------------------------------------------------------------
-- 4. Menus del perfil Contable: reportes + facturacion (consulta)
-- ------------------------------------------------------------
INSERT INTO `perfil_menu` (`perfil_id`, `menu_id`)
SELECT p.`perfil_id`, m.`menu_id`
FROM `perfil` p, `menu` m
WHERE p.`nombre` = 'Contable' AND m.`code` IN ('reportes', 'facturacion')
  AND NOT EXISTS (SELECT 1 FROM `perfil_menu` pm
                  WHERE pm.`perfil_id` = p.`perfil_id` AND pm.`menu_id` = m.`menu_id`);

-- ------------------------------------------------------------
-- 5. Menus del perfil JEFE_BODEGA: reportes + inventario (consulta)
-- ------------------------------------------------------------
INSERT INTO `perfil_menu` (`perfil_id`, `menu_id`)
SELECT p.`perfil_id`, m.`menu_id`
FROM `perfil` p, `menu` m
WHERE p.`nombre` = 'JEFE_BODEGA' AND m.`code` IN ('reportes', 'inventario')
  AND NOT EXISTS (SELECT 1 FROM `perfil_menu` pm
                  WHERE pm.`perfil_id` = p.`perfil_id` AND pm.`menu_id` = m.`menu_id`);

-- ============================================================
-- Notas sobre el control de acceso en el backend
-- ============================================================
-- El filtro JwtAutenticacionFilter restringe el prefijo de ruta /reporte a los
-- roles Administrador, Contable y JEFE_BODEGA. De este modo, aunque un usuario
-- manipule el frontend, el backend rechaza con HTTP 403 las peticiones de
-- reportes efectuadas por roles no autorizados.
--
-- La restriccion de la facturacion y el inventario a "consulta" para los
-- perfiles nuevos se refuerza ocultando en el frontend las acciones de
-- escritura; la operacion sensible de descuento de inventario sigue
-- controlada por el modulo de despacho (perfil DESPACHO_MEDICO).
-- ============================================================
-- Fin del script de la Fase 4
-- ============================================================

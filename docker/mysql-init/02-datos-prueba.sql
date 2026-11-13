-- ============================================================
--  Datos de prueba para la demostracion - Clinica DDSF
-- ============================================================
--
--  Este script se ejecuta DESPUES del dump limpio (01-clinica-dump.sql),
--  por el orden alfabetico de la carpeta de inicializacion de MySQL.
--
--  Puebla el sistema con datos coherentes y actuales para la sustentacion:
--    - Medicamentos con codigo, Invima, precios y stock minimo.
--    - Lotes variados que disparan las alertas del inventario (agotado,
--      stock bajo, proximo a vencer, vencido) y movimientos de entrada.
--    - Citas en la semana de la entrega (17 al 24 de noviembre de 2026) en
--      varios estados (programada, confirmada, atendida).
--    - Historias clinicas y anotaciones (atenciones) para las citas atendidas.
--    - Formulas medicas (con varios medicamentos) pendientes de despacho, para
--      demostrar la dispensacion de la Fase 5.
--    - Horarios de disponibilidad de algunos medicos.
--
--  A proposito NO se crean facturas ni despachos: se dejan para realizarlos en
--  vivo durante la sustentacion (son los pasos mas demostrativos). Las formulas
--  quedan listas para dispensar y las citas atendidas listas para facturar.
--
--  Nota sobre fechas: se usan fechas de noviembre de 2026 (fecha de entrega) y
--  los vencimientos de lotes se calculan con CURDATE() para que las alertas de
--  "proximo a vencer" y "vencido" funcionen en el momento de la demostracion.
-- ============================================================

USE clinica;

SET @OLD_FK = @@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS = 0;

-- ------------------------------------------------------------
-- 1. Medicamentos y productos
--    Codigo de 8 caracteres. requiere_invima = 1 para medicamentos
--    (registro Invima obligatorio) y 0 para utensilios.
-- ------------------------------------------------------------
INSERT INTO `medicamento`
 (`id`,`nombre`,`codigo`,`descripcion`,`presentacion`,`requiere_invima`,`registro_invima`,`precio_compra`,`precio_venta`,`stock_minimo`,`fecha_proximo_pedido`,`fecha_compra`,`fecha_vence`,`fecha_creacion_registro`)
VALUES
 (201,'Amoxicilina 500mg','AMOX0500','Antibiotico de amplio espectro','Tabletas 500mg',1,'INV-2023-001',800.00,1500.00,20,'2026-12-15','2026-07-01','2027-07-01',NOW()),
 (202,'Ibuprofeno 400mg','IBUP0400','Antiinflamatorio no esteroideo','Tabletas 400mg',1,'INV-2023-002',500.00,1200.00,15,'2026-12-20','2026-07-01','2027-05-01',NOW()),
 (203,'Dexametasona inyectable','DEXA0002','Corticoide antiinflamatorio','Ampolla 2mg/ml',1,'INV-2022-045',1200.00,2500.00,10,'2026-12-01','2026-06-01','2027-02-01',NOW()),
 (204,'Vitamina B12 inyectable','VITB0012','Suplemento vitaminico','Ampolla 1ml',1,'INV-2023-010',900.00,1800.00,10,'2027-01-10','2026-08-01','2027-08-01',NOW()),
 (205,'Suero fisiologico 500ml','SUER0500','Solucion salina para fluidoterapia','Bolsa 500ml',1,'INV-2021-033',2000.00,4000.00,8,'2026-12-05','2026-05-01','2027-03-01',NOW()),
 (206,'Jeringa desechable 5ml','JERI0005','Utensilio de aplicacion, no requiere Invima','Unidad',0,NULL,200.00,500.00,50,'2026-12-10','2026-09-01','2029-09-01',NOW()),
 (207,'Guantes de examen (caja)','GUAN0100','Utensilio de proteccion, no requiere Invima','Caja x 100',0,NULL,15000.00,28000.00,5,'2026-12-10','2026-09-01','2029-09-01',NOW()),
 (208,'Antipulgas pipeta','PULG0001','Tratamiento topico antipulgas','Pipeta 1ml',1,'INV-2023-050',3000.00,6000.00,12,'2026-12-18','2026-07-15','2027-06-01',NOW());

-- ------------------------------------------------------------
-- 2. Lotes de medicamento (existencias variadas para las alertas)
--    Vencimientos relativos a hoy con CURDATE() para que las alertas
--    (proximo a vencer: <= 30 dias; vencido: < hoy) funcionen en la demo.
-- ------------------------------------------------------------
INSERT INTO `lote_medicamento`
 (`id`,`medicamento_id`,`numero_lote`,`cantidad`,`precio_compra`,`fecha_ingreso`,`fecha_vencimiento`,`fecha_creacion_registro`)
VALUES
 -- Amoxicilina: dos lotes vigentes (uno vence pronto -> FEFO lo usara primero)
 (301,201,'LOTE-AMOX-A',40,800.00,'2026-08-01', DATE_ADD(CURDATE(), INTERVAL 20 DAY), NOW()),
 (302,201,'LOTE-AMOX-B',60,820.00,'2026-09-01', DATE_ADD(CURDATE(), INTERVAL 200 DAY), NOW()),
 -- Ibuprofeno: stock por debajo del minimo (stock bajo)
 (303,202,'LOTE-IBUP-A',10,500.00,'2026-08-10', DATE_ADD(CURDATE(), INTERVAL 150 DAY), NOW()),
 -- Dexametasona: un lote proximo a vencer
 (304,203,'LOTE-DEXA-A',25,1200.00,'2026-07-01', DATE_ADD(CURDATE(), INTERVAL 10 DAY), NOW()),
 -- Vitamina B12: un lote ya vencido (no debe dispensarse)
 (305,204,'LOTE-VITB-VENC',15,900.00,'2026-01-01', DATE_SUB(CURDATE(), INTERVAL 5 DAY), NOW()),
 -- Vitamina B12: ademas un lote vigente con buena cantidad
 (306,204,'LOTE-VITB-OK',30,920.00,'2026-09-01', DATE_ADD(CURDATE(), INTERVAL 250 DAY), NOW()),
 -- Suero: cantidad normal
 (307,205,'LOTE-SUER-A',20,2000.00,'2026-06-01', DATE_ADD(CURDATE(), INTERVAL 120 DAY), NOW()),
 -- Jeringas: cantidad alta (utensilio)
 (308,206,'LOTE-JERI-A',200,200.00,'2026-09-01', DATE_ADD(CURDATE(), INTERVAL 900 DAY), NOW()),
 -- Guantes: cantidad normal (utensilio)
 (309,207,'LOTE-GUAN-A',10,15000.00,'2026-09-01', DATE_ADD(CURDATE(), INTERVAL 900 DAY), NOW()),
 -- Antipulgas: dos lotes vigentes
 (310,208,'LOTE-PULG-A',18,3000.00,'2026-07-15', DATE_ADD(CURDATE(), INTERVAL 180 DAY), NOW());
-- Nota: el medicamento 205 (Suero) y el 208 tienen existencia; el 202 queda en
-- stock bajo (10 <= 15); el 204 tiene un lote vencido y uno vigente.

-- ------------------------------------------------------------
-- 3. Movimientos de entrada (trazabilidad del ingreso de cada lote)
-- ------------------------------------------------------------
INSERT INTO `movimiento_inventario`
 (`medicamento_id`,`lote_id`,`tipo`,`cantidad`,`fecha`,`usuario_responsable`,`referencia`,`observacion`)
VALUES
 (201,301,'ENTRADA',40,NOW(),'david','Ingreso lote LOTE-AMOX-A','Carga inicial de prueba'),
 (201,302,'ENTRADA',60,NOW(),'david','Ingreso lote LOTE-AMOX-B','Carga inicial de prueba'),
 (202,303,'ENTRADA',10,NOW(),'david','Ingreso lote LOTE-IBUP-A','Carga inicial de prueba'),
 (203,304,'ENTRADA',25,NOW(),'david','Ingreso lote LOTE-DEXA-A','Carga inicial de prueba'),
 (204,305,'ENTRADA',15,NOW(),'david','Ingreso lote LOTE-VITB-VENC','Carga inicial de prueba'),
 (204,306,'ENTRADA',30,NOW(),'david','Ingreso lote LOTE-VITB-OK','Carga inicial de prueba'),
 (205,307,'ENTRADA',20,NOW(),'david','Ingreso lote LOTE-SUER-A','Carga inicial de prueba'),
 (206,308,'ENTRADA',200,NOW(),'david','Ingreso lote LOTE-JERI-A','Carga inicial de prueba'),
 (207,309,'ENTRADA',10,NOW(),'david','Ingreso lote LOTE-GUAN-A','Carga inicial de prueba'),
 (208,310,'ENTRADA',18,NOW(),'david','Ingreso lote LOTE-PULG-A','Carga inicial de prueba');

-- ------------------------------------------------------------
-- 4. Horarios de disponibilidad de algunos medicos
--    dia_semana: 1 = lunes ... 7 = domingo
-- ------------------------------------------------------------
INSERT INTO `horario_medico` (`medico_id`,`dia_semana`,`hora_inicio`,`hora_fin`,`activo`) VALUES
 (3,1,'08:00:00','12:00:00',1),(3,3,'08:00:00','12:00:00',1),(3,5,'14:00:00','18:00:00',1),
 (2,2,'09:00:00','13:00:00',1),(2,4,'09:00:00','13:00:00',1);

-- ------------------------------------------------------------
-- 5. Citas de la semana de la entrega (17 al 24 de noviembre de 2026)
--    Estados: PROGRAMADA, CONFIRMADA, ATENDIDA.
--    Relacionan cliente, mascota y medico existentes del dump.
-- ------------------------------------------------------------
INSERT INTO `cita` (`id`,`cliente_id`,`mascota_id`,`medico_id`,`fecha_hora`,`estado`,`motivo`) VALUES
 (101,1,1,3,'2026-11-17 09:00:00','ATENDIDA','Consulta general y vacunacion'),
 (102,2,2,2,'2026-11-17 10:30:00','ATENDIDA','Control dermatologico'),
 (103,3,3,3,'2026-11-18 08:30:00','CONFIRMADA','Consulta por decaimiento'),
 (104,4,4,4,'2026-11-18 11:00:00','PROGRAMADA','Chequeo preventivo'),
 (105,5,5,3,'2026-11-19 14:00:00','PROGRAMADA','Desparasitacion'),
 (106,1,11,2,'2026-11-20 15:30:00','PROGRAMADA','Revision de rutina'),
 (107,6,6,5,'2026-11-21 09:30:00','CONFIRMADA','Consulta especializada'),
 (108,7,7,3,'2026-11-24 10:00:00','PROGRAMADA','Control post-tratamiento');

-- ------------------------------------------------------------
-- 6. Historias clinicas y anotaciones para las citas atendidas
-- ------------------------------------------------------------
INSERT INTO `historia_medica` (`id`,`paciente_id`,`fecha_creacion`) VALUES
 (101,1,'2026-11-17 09:30:00'),
 (102,2,'2026-11-17 11:00:00');

INSERT INTO `anotacion_historia`
 (`historia_id`,`medico_id`,`fecha`,`descripcion`,`diagnostico`,`observaciones`,`cita_id`)
VALUES
 (101,3,'2026-11-17 09:30:00','Paciente acude a consulta general. Se observa buen estado general, mucosas rosadas, hidratacion normal.','Animal sano. Se aplica refuerzo de vacunacion.','Continuar con alimentacion balanceada y proxima visita en seis meses.',101),
 (102,2,'2026-11-17 11:00:00','Paciente presenta enrojecimiento y prurito en zona dorsal. Se toma muestra para descartar alergia.','Dermatitis leve por contacto.','Aplicar tratamiento topico y evitar el alergeno. Control en quince dias.',102);

-- ------------------------------------------------------------
-- 7. Formulas medicas pendientes de despacho (cabecera + detalle)
--    Quedan listas para dispensar en el modulo de despacho (Fase 5).
-- ------------------------------------------------------------
INSERT INTO `formula_medica`
 (`id`,`cita_id`,`medico_id`,`medicamento_id`,`dosis`,`indicaciones`,`fecha_creacion_registro`,`frecuencia`,`duracion`,`fecha_creacion`)
VALUES
 (101,101,3,NULL,'Ver detalle','Administrar con alimento','2026-11-17 09:45:00',NULL,NULL,'2026-11-17 09:45:00'),
 (102,102,2,NULL,'Ver detalle','Aplicar segun indicaciones','2026-11-17 11:15:00',NULL,NULL,'2026-11-17 11:15:00');

INSERT INTO `formula_medicamento`
 (`formula_id`,`medicamento_id`,`dosis`,`frecuencia`,`duracion`,`cantidad`,`via_administracion`,`indicaciones`)
VALUES
 -- Formula 101: dos medicamentos (hay existencia -> despacho completo posible)
 (101,201,'500 mg','Cada 12 horas','7 dias',10,'Oral','Tomar con alimento'),
 (101,202,'400 mg','Cada 8 horas','5 dias',6,'Oral','No administrar en ayunas'),
 -- Formula 102: un medicamento con existencia y uno con lote vencido/escaso
 -- (util para demostrar entrega parcial o FEFO evitando el lote vencido)
 (102,208,'1 pipeta','Dosis unica','1 dia',1,'Topica','Aplicar en la nuca'),
 (102,204,'1 ml','Cada 24 horas','3 dias',3,'Intramuscular','No usar lote vencido');

SET FOREIGN_KEY_CHECKS = @OLD_FK;

-- ============================================================
--  Resumen de lo que queda listo para la demostracion:
--   - Inventario con alertas: Amoxicilina (una proxima a vencer), Ibuprofeno
--     (stock bajo), Dexametasona (proxima a vencer), Vitamina B12 (un lote
--     vencido), utensilios sin Invima (jeringas, guantes).
--   - Citas de la semana en varios estados; dos ya atendidas con su historia.
--   - Dos formulas pendientes de despacho (para dispensar en vivo).
--   - Las facturas y los despachos se realizan durante la sustentacion.
-- ============================================================

-- ============================================================
-- Fase 3A - Datos de prueba: 30 servicios
-- Fecha: 2026-11
-- ============================================================
-- Inserta 30 servicios de ejemplo (consultas y procedimientos) con precios e
-- IVA. Algunos se asocian a una especializacion; otros son generales.
-- Usa INSERT ... WHERE NOT EXISTS para no duplicar por nombre.
-- Ejecutar despues de fase-3a-servicios-tarifas.sql.
-- ============================================================
USE clinica;

INSERT INTO servicio (nombre, descripcion, especializacion_id, precio, iva_porcentaje, activo, fecha_creacion)
SELECT * FROM (
  SELECT 'Consulta general veterinaria' AS nombre, 'Valoracion clinica general' AS descripcion, NULL AS especializacion_id, 50000 AS precio, 19 AS iva_porcentaje, 1 AS activo, NOW() AS fecha_creacion UNION ALL
  SELECT 'Consulta de urgencias','Atencion de urgencia',NULL,80000,19,1,NOW() UNION ALL
  SELECT 'Vacunacion antirrabica','Aplicacion de vacuna antirrabica',NULL,35000,19,1,NOW() UNION ALL
  SELECT 'Vacunacion multiple','Esquema de vacunacion multiple',NULL,60000,19,1,NOW() UNION ALL
  SELECT 'Desparasitacion','Tratamiento antiparasitario',NULL,25000,19,1,NOW() UNION ALL
  SELECT 'Bano y peluqueria','Bano, secado y corte estetico',NULL,40000,19,1,NOW() UNION ALL
  SELECT 'Bano medicado antipulgas','Bano con producto antipulgas',NULL,55000,19,1,NOW() UNION ALL
  SELECT 'Corte de unas','Corte y limado de unas',NULL,15000,19,1,NOW() UNION ALL
  SELECT 'Limpieza dental','Profilaxis dental',NULL,120000,19,1,NOW() UNION ALL
  SELECT 'Extraccion dental','Extraccion de pieza dental',NULL,90000,19,1,NOW() UNION ALL
  SELECT 'Radiografia simple','Toma de radiografia',NULL,70000,19,1,NOW() UNION ALL
  SELECT 'Ecografia abdominal','Ecografia de abdomen',NULL,110000,19,1,NOW() UNION ALL
  SELECT 'Electrocardiograma',NULL,1,150000,19,1,NOW() UNION ALL
  SELECT 'Examen cardiologico','Valoracion cardiologica',1,130000,19,1,NOW() UNION ALL
  SELECT 'Consulta neurologica','Valoracion neurologica',2,140000,19,1,NOW() UNION ALL
  SELECT 'Consulta dermatologica','Valoracion de piel',5,90000,19,1,NOW() UNION ALL
  SELECT 'Prueba alergica cutanea',NULL,5,85000,19,1,NOW() UNION ALL
  SELECT 'Consulta oftalmologica','Valoracion ocular',9,95000,19,1,NOW() UNION ALL
  SELECT 'Medicion presion ocular',NULL,9,60000,19,1,NOW() UNION ALL
  SELECT 'Cirugia de esterilizacion','Esterilizacion quirurgica',NULL,250000,19,1,NOW() UNION ALL
  SELECT 'Cirugia de tejidos blandos',NULL,NULL,350000,19,1,NOW() UNION ALL
  SELECT 'Anestesia general','Procedimiento anestesico',12,100000,19,1,NOW() UNION ALL
  SELECT 'Hospitalizacion dia','Internacion por dia',NULL,120000,19,1,NOW() UNION ALL
  SELECT 'Aplicacion de suero','Fluidoterapia',NULL,45000,19,1,NOW() UNION ALL
  SELECT 'Hemograma','Examen de sangre',NULL,55000,19,1,NOW() UNION ALL
  SELECT 'Perfil bioquimico','Examen bioquimico sanguineo',NULL,95000,19,1,NOW() UNION ALL
  SELECT 'Examen coprologico','Analisis de materia fecal',NULL,30000,19,1,NOW() UNION ALL
  SELECT 'Microchip de identificacion','Implante de microchip',NULL,65000,19,1,NOW() UNION ALL
  SELECT 'Certificado de salud','Expedicion de certificado',NULL,20000,0,1,NOW() UNION ALL
  SELECT 'Eutanasia asistida','Procedimiento humanitario',NULL,150000,0,1,NOW()
) AS datos
WHERE NOT EXISTS (SELECT 1 FROM servicio s WHERE s.nombre = datos.nombre);

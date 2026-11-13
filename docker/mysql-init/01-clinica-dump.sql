-- ============================================================
--  Dump inicial LIMPIO de la base de datos "clinica" - Clinica DDSF
-- ============================================================
--
--  Contiene la estructura completa de todas las tablas y los datos de
--  CONFIGURACION y datos base utiles (clientes, mascotas, medicos,
--  especializaciones, servicios, razas, usuarios, perfiles y menus).
--
--  Se eliminaron los datos de ejemplo que no aportaban (citas y formulas
--  de prueba antiguas, factura anulada de prueba, medicamentos sin precio,
--  logs de carga, tokens OTP y sesiones). Asi las pantallas de citas,
--  formulas, facturacion e inventario inician limpias y se pueblan con datos
--  frescos durante la demostracion.
--
--  Correccion aplicada: la especializacion id=1 estaba como
--  "Cardiologia 2 / COD_100"; se dejo como "Cardiologia / COD_01".
--
--  Este archivo lo ejecuta MySQL automaticamente la primera vez que crea la
--  base (ver docker-compose.yml). Para recargarlo: docker compose down -v.
-- ============================================================

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `anotacion_historia`
--

DROP TABLE IF EXISTS `anotacion_historia`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `anotacion_historia` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `historia_id` int(11) NOT NULL,
  `medico_id` int(11) NOT NULL,
  `fecha` datetime DEFAULT current_timestamp(),
  `descripcion` text NOT NULL,
  `diagnostico` text DEFAULT NULL,
  `observaciones` text DEFAULT NULL,
  `cita_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cita`
--

DROP TABLE IF EXISTS `cita`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cita` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `cliente_id` int(11) NOT NULL,
  `mascota_id` int(11) NOT NULL,
  `medico_id` int(11) NOT NULL,
  `fecha_hora` datetime NOT NULL,
  `estado` varchar(20) NOT NULL,
  `motivo` text DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cliente`
--

DROP TABLE IF EXISTS `cliente`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cliente` (
  `cliente_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `usuario_id` int(11) DEFAULT NULL,
  `tipo_documento` varchar(10) NOT NULL,
  `numero_documento` varchar(20) NOT NULL,
  `nombres` varchar(100) NOT NULL,
  `apellidos` varchar(100) NOT NULL,
  `fecha_nacimiento` date NOT NULL,
  `genero` char(1) DEFAULT NULL,
  `telefono` varchar(20) DEFAULT NULL,
  `direccion` text DEFAULT NULL,
  `activo` tinyint(1) NOT NULL,
  PRIMARY KEY (`cliente_id`),
  UNIQUE KEY `id` (`cliente_id`),
  UNIQUE KEY `numero_documento` (`numero_documento`),
  UNIQUE KEY `usuario_id` (`usuario_id`),
  KEY `idx_paciente_documento` (`numero_documento`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `cliente` WRITE;
/*!40000 ALTER TABLE `cliente` DISABLE KEYS */;
INSERT INTO `cliente` VALUES (1,NULL,'CC','100000001','Juan','Pérez López','1990-05-12','M','3001112233','Calle 10 #12-34',1),(2,NULL,'CC','100000002','María','Gómez Rodríguez','1985-08-22','F','3012223344','Carrera 15 #20-10',1),(3,NULL,'TI','900000003','Andrés','Martínez Díaz','2005-03-18','M','3023334455','Av. Boyacá #45-67',1),(4,NULL,'CC','100000004','Laura','Hernández Ruiz','1992-12-01','F','3034445566','Calle 5 #7-89',1),(5,NULL,'CC','100000005','Carlos','Ramírez Torres','1980-07-30','M','3045556677','Carrera 8 #15-20',1),(6,NULL,'CE','200000006','Ana','Castro Moreno','1998-01-25','F','3056667788','Diagonal 12 #25-50',1),(7,NULL,'CC','100000007','Santiago','Moreno Vargas','1995-09-14','M','3067778899','Transversal 20 #40-22',1),(8,NULL,'CC','100000008','Paula','Ríos Fernández','1987-11-10','F','3078889900','Calle 22 #18-45',1),(9,NULL,'TI','900000009','Felipe','Gutiérrez Méndez','2006-06-06','M','3089990011','Carrera 50 #60-12',1),(10,NULL,'CC','100000010','Diana','Salazar Peña','1993-04-21','F','3090001122','Av. Caracas #100-50',1);
/*!40000 ALTER TABLE `cliente` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `datos_clinica`
--

DROP TABLE IF EXISTS `datos_clinica`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `datos_clinica` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `nombre` varchar(150) NOT NULL,
  `nit` varchar(30) DEFAULT NULL,
  `telefono` varchar(30) DEFAULT NULL,
  `direccion` varchar(200) DEFAULT NULL,
  `correo` varchar(150) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `datos_clinica` WRITE;
/*!40000 ALTER TABLE `datos_clinica` DISABLE KEYS */;
INSERT INTO `datos_clinica` VALUES (1,'Clinica DDSF - Veterinaria','900.123.456-7','601 000 0000','Calle 80 # 100-20, Bogota','contacto@clinicaddsf.com');
/*!40000 ALTER TABLE `datos_clinica` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `especializacion`
--

DROP TABLE IF EXISTS `especializacion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `especializacion` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) NOT NULL,
  `descripcion` text DEFAULT NULL,
  `codigo_especializacion` varchar(10) NOT NULL DEFAULT '""',
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`),
  UNIQUE KEY `nombre` (`nombre`),
  UNIQUE KEY `codigo_especializacion_UNIQUE` (`codigo_especializacion`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `especializacion` WRITE;
/*!40000 ALTER TABLE `especializacion` DISABLE KEYS */;
INSERT INTO `especializacion` VALUES (1,'Cardiología','Especialización médica centrada en el diagnóstico y tratamiento de enfermedades del corazón y otras enfermedades asociadas.','COD_01'),(2,'Neurología','Se ocupa de los trastornos del sistema nervioso central y periférico.','COD_02'),(3,'Pediatría','Atención médica especializada en niños y adolescentes.','COD_03'),(4,'Oncología','Estudio y tratamiento de diferentes tipos de cáncer.','COD_04'),(5,'Dermatología','Diagnóstico y tratamiento de enfermedades de la piel.','COD_05'),(6,'Ginecología','Enfocada en el sistema reproductor femenino.','COD_06'),(7,'Urología','Tratamiento del sistema urinario y el sistema reproductor masculino.','COD_07'),(8,'Psiquiatría','Estudio y tratamiento de enfermedades mentales y emocionales.','COD_08'),(9,'Oftalmología','Especialización en la salud ocular y la visión.','COD_09'),(10,'Otorrinolaringología','Se encarga de enfermedades de oído, nariz y garganta.','COD_10'),(11,'Traumatología','Tratamiento de lesiones y enfermedades del sistema musculoesquelético.','COD_11'),(12,'Anestesiología','Responsable de la anestesia y control del dolor durante procedimientos quirúrgicos.','COD_12'),(13,'Reumatología','Estudia enfermedades autoinmunes y musculoesqueléticas.','COD_13'),(14,'Endocrinología','Especialización en glándulas y trastornos hormonales.','COD_14'),(15,'Gastroenterología','Diagnóstico y tratamiento de enfermedades del aparato digestivo.','COD_15');
/*!40000 ALTER TABLE `especializacion` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `factura`
--

DROP TABLE IF EXISTS `factura`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `factura` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `cita_id` int(11) DEFAULT NULL,
  `cliente_id` int(11) DEFAULT NULL,
  `mascota_id` int(11) DEFAULT NULL,
  `medico_id` int(11) DEFAULT NULL,
  `fecha` datetime NOT NULL,
  `subtotal` decimal(14,2) NOT NULL DEFAULT 0.00,
  `iva` decimal(14,2) NOT NULL DEFAULT 0.00,
  `descuento` decimal(14,2) NOT NULL DEFAULT 0.00,
  `total` decimal(14,2) NOT NULL DEFAULT 0.00,
  `estado_pago` varchar(20) NOT NULL DEFAULT 'PENDIENTE',
  `observacion` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_factura_cita` (`cita_id`),
  KEY `idx_factura_cliente` (`cliente_id`),
  KEY `idx_factura_fecha` (`fecha`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `factura_detalle`
--

DROP TABLE IF EXISTS `factura_detalle`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `factura_detalle` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `factura_id` bigint(20) unsigned NOT NULL,
  `tipo` varchar(15) NOT NULL,
  `referencia_id` bigint(20) DEFAULT NULL,
  `descripcion` varchar(200) NOT NULL,
  `cantidad` int(11) NOT NULL DEFAULT 1,
  `precio_unitario` decimal(14,2) NOT NULL DEFAULT 0.00,
  `iva_porcentaje` decimal(5,2) NOT NULL DEFAULT 0.00,
  `subtotal` decimal(14,2) NOT NULL DEFAULT 0.00,
  PRIMARY KEY (`id`),
  KEY `idx_factura_detalle_factura` (`factura_id`),
  CONSTRAINT `fk_factura_detalle_factura` FOREIGN KEY (`factura_id`) REFERENCES `factura` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `formula_medica`
--

DROP TABLE IF EXISTS `formula_medica`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `formula_medica` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `cita_id` int(11) NOT NULL,
  `medico_id` int(11) DEFAULT NULL,
  `medicamento_id` int(11) DEFAULT NULL,
  `dosis` text NOT NULL,
  `indicaciones` text DEFAULT NULL,
  `fecha_creacion_registro` datetime NOT NULL,
  `fecha_actualizacion_registro` datetime DEFAULT NULL,
  `frecuencia` varchar(255) DEFAULT NULL,
  `duracion` varchar(255) DEFAULT NULL,
  `fecha_creacion` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `formula_medicamento`
--

DROP TABLE IF EXISTS `formula_medicamento`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `formula_medicamento` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `formula_id` bigint(20) unsigned NOT NULL,
  `medicamento_id` bigint(20) unsigned NOT NULL,
  `dosis` varchar(255) NOT NULL,
  `frecuencia` varchar(255) DEFAULT NULL,
  `duracion` varchar(255) DEFAULT NULL,
  `cantidad` int(11) NOT NULL DEFAULT 1,
  `via_administracion` varchar(100) DEFAULT NULL,
  `indicaciones` text DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_formula_medicamento_formula` (`formula_id`),
  KEY `idx_formula_medicamento_medicamento` (`medicamento_id`),
  CONSTRAINT `fk_formula_medicamento_formula` FOREIGN KEY (`formula_id`) REFERENCES `formula_medica` (`id`),
  CONSTRAINT `fk_formula_medicamento_medicamento` FOREIGN KEY (`medicamento_id`) REFERENCES `medicamento` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `historia_medica`
--

DROP TABLE IF EXISTS `historia_medica`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `historia_medica` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `paciente_id` int(11) NOT NULL,
  `fecha_creacion` datetime DEFAULT current_timestamp(),
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `horario_medico`
--

DROP TABLE IF EXISTS `horario_medico`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `horario_medico` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `medico_id` bigint(20) unsigned NOT NULL,
  `dia_semana` int(11) NOT NULL,
  `hora_inicio` time NOT NULL,
  `hora_fin` time NOT NULL,
  `activo` tinyint(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (`id`),
  KEY `idx_horario_medico` (`medico_id`),
  CONSTRAINT `fk_horario_medico` FOREIGN KEY (`medico_id`) REFERENCES `medico` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `log_carga_inventario`
--

DROP TABLE IF EXISTS `log_carga_inventario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `log_carga_inventario` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `fecha` datetime NOT NULL,
  `usuario_responsable` varchar(100) DEFAULT NULL,
  `nombre_archivo` varchar(255) DEFAULT NULL,
  `total_registros` int(11) NOT NULL DEFAULT 0,
  `registros_validos` int(11) NOT NULL DEFAULT 0,
  `registros_con_error` int(11) NOT NULL DEFAULT 0,
  `detalle` text DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_log_carga_fecha` (`fecha`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `log_tarifa`
--

DROP TABLE IF EXISTS `log_tarifa`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `log_tarifa` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `servicio_id` bigint(20) unsigned NOT NULL,
  `precio_anterior` decimal(12,2) DEFAULT NULL,
  `precio_nuevo` decimal(12,2) DEFAULT NULL,
  `iva_anterior` decimal(5,2) DEFAULT NULL,
  `iva_nuevo` decimal(5,2) DEFAULT NULL,
  `fecha` datetime NOT NULL,
  `usuario_responsable` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_log_tarifa_servicio` (`servicio_id`),
  CONSTRAINT `fk_log_tarifa_servicio` FOREIGN KEY (`servicio_id`) REFERENCES `servicio` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `lote_medicamento`
--

DROP TABLE IF EXISTS `lote_medicamento`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `lote_medicamento` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `medicamento_id` bigint(20) unsigned NOT NULL,
  `numero_lote` varchar(50) NOT NULL,
  `cantidad` int(11) NOT NULL DEFAULT 0,
  `precio_compra` decimal(12,2) NOT NULL DEFAULT 0.00,
  `fecha_ingreso` date NOT NULL,
  `fecha_vencimiento` date NOT NULL,
  `fecha_creacion_registro` datetime NOT NULL,
  `fecha_modificacion_registro` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_lote_medicamento` (`medicamento_id`),
  KEY `idx_lote_vencimiento` (`fecha_vencimiento`),
  CONSTRAINT `fk_lote_medicamento` FOREIGN KEY (`medicamento_id`) REFERENCES `medicamento` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `mascota`
--

DROP TABLE IF EXISTS `mascota`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `mascota` (
  `mascota_id` int(11) NOT NULL AUTO_INCREMENT,
  `nombre_mascota` varchar(100) NOT NULL,
  `edad` int(11) NOT NULL,
  `raza_id` int(11) NOT NULL,
  `fecha_registro` datetime NOT NULL,
  `fecha_modificacion` datetime DEFAULT NULL,
  `cliente_id` int(11) NOT NULL,
  `activo` tinyint(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (`mascota_id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `mascota` WRITE;
/*!40000 ALTER TABLE `mascota` DISABLE KEYS */;
INSERT INTO `mascota` VALUES (1,'Max',3,1,'2026-08-01 08:15:00','2026-08-15 10:20:00',1,1),(2,'Luna',2,2,'2026-08-01 09:30:00','2026-08-16 11:00:00',2,1),(3,'Rocky',5,1,'2026-08-02 10:00:00',NULL,3,1),(4,'Bella',4,2,'2026-08-02 11:20:00','2026-08-17 09:15:00',4,1),(5,'Toby',1,1,'2026-08-03 08:45:00',NULL,5,1),(6,'Nala',6,2,'2026-08-03 14:10:00','2026-08-18 14:30:00',6,1),(7,'Coco',3,1,'2026-08-04 09:00:00',NULL,7,1),(8,'Simba',2,2,'2026-08-04 15:30:00','2026-08-19 16:00:00',8,1),(9,'Milo',7,1,'2026-08-05 10:15:00',NULL,9,1),(10,'Maya',4,2,'2026-08-05 16:00:00','2026-08-20 10:00:00',10,1),(11,'Thor',5,1,'2026-08-06 08:30:00',NULL,1,1),(12,'Kira',2,2,'2026-08-06 13:45:00','2026-08-21 13:20:00',2,1),(13,'Bruno',8,1,'2026-08-07 09:20:00',NULL,3,1),(14,'Canela',3,2,'2026-08-07 14:30:00','2026-08-22 15:10:00',4,1),(15,'Zeus',6,1,'2026-08-08 10:45:00',NULL,5,1),(16,'Sasha',1,2,'2026-08-08 16:15:00','2026-08-23 11:45:00',6,1),(17,'Duke',4,1,'2026-08-09 08:00:00',NULL,7,1),(18,'Mia',5,2,'2026-08-09 11:30:00','2026-08-24 09:30:00',8,1),(19,'Oliver',2,1,'2026-08-10 13:00:00',NULL,9,1),(20,'Lola',7,2,'2026-08-10 15:45:00','2026-08-25 14:00:00',10,1);
/*!40000 ALTER TABLE `mascota` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `medicamento`
--

DROP TABLE IF EXISTS `medicamento`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `medicamento` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) NOT NULL,
  `codigo` varchar(8) DEFAULT NULL,
  `descripcion` text DEFAULT NULL,
  `presentacion` varchar(100) DEFAULT NULL,
  `requiere_invima` tinyint(1) NOT NULL DEFAULT 1,
  `registro_invima` varchar(50) DEFAULT NULL,
  `precio_compra` decimal(12,2) NOT NULL DEFAULT 0.00,
  `precio_venta` decimal(12,2) NOT NULL DEFAULT 0.00,
  `stock_minimo` int(11) NOT NULL DEFAULT 0,
  `fecha_proximo_pedido` date DEFAULT NULL,
  `fecha_compra` date NOT NULL,
  `fecha_vence` date NOT NULL,
  `fecha_creacion_registro` datetime NOT NULL,
  `fecha_modificacion_registro` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`),
  UNIQUE KEY `nombre` (`nombre`),
  UNIQUE KEY `codigo` (`codigo`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `medico`
--

DROP TABLE IF EXISTS `medico`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `medico` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `usuario_id` bigint(20) unsigned DEFAULT NULL,
  `tipo_documento` varchar(10) NOT NULL,
  `numero_documento` varchar(20) NOT NULL,
  `nombres` varchar(100) NOT NULL,
  `apellidos` varchar(100) NOT NULL,
  `telefono` varchar(20) DEFAULT NULL,
  `registro_profesional` varchar(50) NOT NULL,
  `especializacion_id` int(11) NOT NULL DEFAULT 0,
  `estado` tinyint(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`),
  UNIQUE KEY `numero_documento` (`numero_documento`),
  UNIQUE KEY `registro_profesional` (`registro_profesional`),
  KEY `idx_medico_documento` (`numero_documento`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `medico` WRITE;
/*!40000 ALTER TABLE `medico` DISABLE KEYS */;
INSERT INTO `medico` VALUES (1,NULL,'CC','1001001001','Juan','Pérez Gómez','3001112233','RP-1001',13,1),(2,NULL,'CC','1001001002','María','Rodríguez López','3001112234','RP-1002',2,1),(3,NULL,'CC','1001001003','Carlos','Martínez Ruiz','3001112235','RP-1003',3,1),(4,NULL,'CC','1001001004','Laura','Fernández Díaz','3001112236','RP-1004',4,1),(5,NULL,'CC','1001001005','Andrés','Ramírez Soto','3001112237','RP-1005',5,1),(6,NULL,'CC','1001001006','Diana','Gómez Torres','3001112238','RP-1006',6,1),(7,NULL,'CC','1001001007','Ricardo','Jiménez Vargas','3001112239','RP-1007',7,1),(8,NULL,'CC','1001001008','Patricia','Hernández Ríos','3001112240','RP-1008',8,1),(9,NULL,'CC','1001001009','Santiago','Morales Castro','3001112241','RP-1009',9,1),(10,NULL,'CC','1001001010','Natalia','García Mendoza','3001112242','RP-1010',9,1),(11,NULL,'CC','88274388','Giovanni','Mora','5711322','1234656789100',8,1),(12,NULL,'CC','12345678','Edson Arantes','Do nascimento','3207729661','RP-10011',12,1),(13,NULL,'CC','12345123','Giovanni','Mora Jaimes','3207729661','001RP1234',12,1),(14,2,'CC','1031649895','Danna Lizeth','Rodriguez Bonilla','3053084454','RP-10020',12,1);
/*!40000 ALTER TABLE `medico` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `menu`
--

DROP TABLE IF EXISTS `menu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `menu` (
  `menu_id` int(11) NOT NULL AUTO_INCREMENT,
  `code` varchar(100) NOT NULL,
  `title` varchar(255) NOT NULL,
  `url` longtext NOT NULL,
  `icon` varchar(255) NOT NULL,
  `activo` tinyint(1) NOT NULL,
  `fecha_creacion` datetime NOT NULL,
  `fecha_modificacion` datetime DEFAULT NULL,
  PRIMARY KEY (`menu_id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `menu` WRITE;
/*!40000 ALTER TABLE `menu` DISABLE KEYS */;
INSERT INTO `menu` VALUES (1,'cita','Gestión de citas','/inicio/citas','bi bi-calendar',1,'2026-01-01 00:00:00',NULL),(2,'clientes','Gestión de clientes','/inicio/clientes','bi bi-person-check',1,'2026-01-01 00:00:00',NULL),(3,'especializaciones','Gestión de Especializaciones','/inicio/especializaciones','fa-solid fa-user-graduate',1,'2026-01-01 00:00:00',NULL),(4,'formulas','Gestión de Formulas','/inicio/formulas','fa-solid fa-clipboard-list',1,'2026-01-01 00:00:00',NULL),(5,'historias','Gestión de Historias','/inicio/historias','fa-solid fa-book-medical',1,'2026-01-01 00:00:00',NULL),(6,'mascotas','Gestión de Mascotas','/inicio/mascotas','fa-solid fa-paw',1,'2026-01-01 00:00:00',NULL),(7,'medicamentos','Gestión de Medicamentos','/inicio/medicamentos','fa-solid fa-capsules',1,'2026-01-01 00:00:00',NULL),(8,'medicos','Gestión de Medicos','/inicio/medicos','fa-solid fa-user-doctor',1,'2026-01-01 00:00:00',NULL),(9,'usuarios','Gestión de Usuarios','/inicio/usuarios','feather icon-users',1,'2026-01-01 00:00:00',NULL),(10,'inventario','Gestion de Inventario','/inicio/inventario','fa-solid fa-boxes-stacked',1,'2026-11-09 00:46:12',NULL),(11,'calendario','Mi Calendario','/inicio/calendario','fa-solid fa-calendar-days',1,'2026-11-09 01:48:37',NULL),(12,'roles','Gestion de Roles','/inicio/roles','feather icon-shield',1,'2026-11-09 02:32:33',NULL),(13,'servicios','Gestion de Servicios','/inicio/servicios','fa-solid fa-hand-holding-medical',1,'2026-11-09 23:17:11',NULL),(14,'facturacion','Facturacion','/inicio/facturacion','fa-solid fa-file-invoice-dollar',1,'2026-11-09 23:18:46',NULL),(15,'reportes','Reportes','/inicio/reportes','fa-solid fa-chart-line',1,'2026-11-10 00:39:02',NULL),(16,'despacho','Despacho de Medicamentos','/inicio/despacho','fa-solid fa-truck-medical',1,'2026-11-10 00:39:02',NULL);
/*!40000 ALTER TABLE `menu` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `movimiento_inventario`
--

DROP TABLE IF EXISTS `movimiento_inventario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `movimiento_inventario` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `medicamento_id` bigint(20) unsigned NOT NULL,
  `lote_id` bigint(20) unsigned DEFAULT NULL,
  `tipo` varchar(10) NOT NULL,
  `cantidad` int(11) NOT NULL,
  `fecha` datetime NOT NULL,
  `usuario_responsable` varchar(100) DEFAULT NULL,
  `referencia` varchar(150) DEFAULT NULL,
  `observacion` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_movimiento_medicamento` (`medicamento_id`),
  KEY `idx_movimiento_fecha` (`fecha`),
  KEY `fk_movimiento_lote` (`lote_id`),
  CONSTRAINT `fk_movimiento_lote` FOREIGN KEY (`lote_id`) REFERENCES `lote_medicamento` (`id`),
  CONSTRAINT `fk_movimiento_medicamento` FOREIGN KEY (`medicamento_id`) REFERENCES `medicamento` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `otp_token`
--

DROP TABLE IF EXISTS `otp_token`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `otp_token` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `usuario_id` bigint(20) NOT NULL,
  `codigo` varchar(6) NOT NULL,
  `fecha_creacion` datetime NOT NULL,
  `fecha_expiracion` datetime NOT NULL,
  `intentos` int(11) NOT NULL,
  `usado` tinyint(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pago`
--

DROP TABLE IF EXISTS `pago`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pago` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `factura_id` bigint(20) unsigned NOT NULL,
  `monto` decimal(14,2) NOT NULL,
  `fecha` datetime NOT NULL,
  `metodo` varchar(30) DEFAULT NULL,
  `usuario_responsable` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_pago_factura` (`factura_id`),
  CONSTRAINT `fk_pago_factura` FOREIGN KEY (`factura_id`) REFERENCES `factura` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `perfil`
--

DROP TABLE IF EXISTS `perfil`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `perfil` (
  `perfil_id` int(11) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(50) NOT NULL,
  `activo` tinyint(1) NOT NULL,
  `fecha_creacion` datetime NOT NULL,
  `fecha_modificacion` datetime DEFAULT NULL,
  PRIMARY KEY (`perfil_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `perfil` WRITE;
/*!40000 ALTER TABLE `perfil` DISABLE KEYS */;
INSERT INTO `perfil` VALUES (1,'Administrador',1,'2026-01-01 00:00:00',NULL),(2,'Recepcion',1,'2026-01-01 00:00:00','2026-11-09 02:48:39'),(3,'Medico',1,'2026-01-01 00:00:00','2026-11-09 02:48:24'),(4,'Contable',1,'2026-11-10 00:39:02',NULL),(5,'Jefe de Bodega',1,'2026-11-10 00:39:02','2026-11-10 00:44:20'),(6,'DESPACHO_MEDICO',1,'2026-11-10 00:39:02',NULL);
/*!40000 ALTER TABLE `perfil` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `perfil_menu`
--

DROP TABLE IF EXISTS `perfil_menu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `perfil_menu` (
  `perfil_menu_id` int(11) NOT NULL AUTO_INCREMENT,
  `perfil_id` int(11) NOT NULL,
  `menu_id` int(11) NOT NULL,
  PRIMARY KEY (`perfil_menu_id`)
) ENGINE=InnoDB AUTO_INCREMENT=44 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `perfil_menu` WRITE;
/*!40000 ALTER TABLE `perfil_menu` DISABLE KEYS */;
INSERT INTO `perfil_menu` VALUES (3,1,3),(6,1,6),(7,1,7),(8,1,8),(9,1,9),(10,1,1),(11,1,2),(12,1,4),(13,1,5),(14,1,10),(16,1,11),(17,1,12),(20,3,11),(21,3,1),(22,3,5),(23,3,6),(24,3,4),(25,3,2),(26,2,1),(27,2,2),(28,2,6),(29,1,13),(30,1,14),(31,2,14),(32,1,15),(33,4,14),(34,4,15),(39,5,10),(40,5,15),(41,1,16),(42,6,10),(43,6,16);
/*!40000 ALTER TABLE `perfil_menu` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `despacho` (Fase 5)
--

DROP TABLE IF EXISTS `despacho`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `despacho` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `formula_id` bigint(20) unsigned NOT NULL,
  `estado` varchar(30) NOT NULL DEFAULT 'PENDIENTE',
  `fecha` datetime NOT NULL,
  `usuario_responsable` varchar(100) DEFAULT NULL,
  `observacion` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_despacho_formula` (`formula_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `despacho_detalle` (Fase 5)
--

DROP TABLE IF EXISTS `despacho_detalle`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `despacho_detalle` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `despacho_id` bigint(20) unsigned NOT NULL,
  `medicamento_id` bigint(20) unsigned NOT NULL,
  `lote_id` bigint(20) unsigned DEFAULT NULL,
  `cantidad_solicitada` int(11) NOT NULL DEFAULT 0,
  `cantidad_entregada` int(11) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_despacho_detalle_despacho` (`despacho_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `log_despacho` (Fase 5)
--

DROP TABLE IF EXISTS `log_despacho`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `log_despacho` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `despacho_id` bigint(20) unsigned NOT NULL,
  `estado_anterior` varchar(30) DEFAULT NULL,
  `estado_nuevo` varchar(30) NOT NULL,
  `fecha` datetime NOT NULL,
  `usuario_responsable` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_log_despacho_despacho` (`despacho_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `raza`
--

DROP TABLE IF EXISTS `raza`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `raza` (
  `raza_id` int(11) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) NOT NULL,
  `especie` varchar(100) NOT NULL,
  `fecha_creacion` datetime NOT NULL,
  `fecha_modificacion` datetime DEFAULT NULL,
  PRIMARY KEY (`raza_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `raza` WRITE;
/*!40000 ALTER TABLE `raza` DISABLE KEYS */;
INSERT INTO `raza` VALUES (1,'Gato','Felino','2026-01-01 00:00:00','2026-01-01 00:00:00'),(2,'Perro','Canino','2026-01-01 00:00:00','2026-01-01 00:00:00');
/*!40000 ALTER TABLE `raza` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `servicio`
--

DROP TABLE IF EXISTS `servicio`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `servicio` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `nombre` varchar(150) NOT NULL,
  `descripcion` text DEFAULT NULL,
  `especializacion_id` bigint(20) unsigned DEFAULT NULL,
  `precio` decimal(12,2) NOT NULL DEFAULT 0.00,
  `iva_porcentaje` decimal(5,2) NOT NULL DEFAULT 0.00,
  `activo` tinyint(1) NOT NULL DEFAULT 1,
  `fecha_creacion` datetime NOT NULL,
  `fecha_modificacion` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_servicio_nombre` (`nombre`),
  KEY `idx_servicio_especializacion` (`especializacion_id`),
  CONSTRAINT `fk_servicio_especializacion` FOREIGN KEY (`especializacion_id`) REFERENCES `especializacion` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `servicio` WRITE;
/*!40000 ALTER TABLE `servicio` DISABLE KEYS */;
INSERT INTO `servicio` VALUES (1,'Consulta general veterinaria','Valoracion clinica general',NULL,50000.00,19.00,1,'2026-11-09 23:17:35',NULL),(2,'Consulta de urgencias','Atencion de urgencia',NULL,80000.00,19.00,1,'2026-11-09 23:17:35',NULL),(3,'Vacunacion antirrabica','Aplicacion de vacuna antirrabica',NULL,35000.00,19.00,1,'2026-11-09 23:17:35',NULL),(4,'Vacunacion multiple','Esquema de vacunacion multiple',NULL,60000.00,19.00,1,'2026-11-09 23:17:35',NULL),(5,'Desparasitacion','Tratamiento antiparasitario',NULL,25000.00,19.00,1,'2026-11-09 23:17:35',NULL),(6,'Bano y peluqueria','Bano, secado y corte estetico',NULL,40000.00,19.00,1,'2026-11-09 23:17:35',NULL),(7,'Bano medicado antipulgas','Bano con producto antipulgas',NULL,55000.00,19.00,1,'2026-11-09 23:17:35',NULL),(8,'Corte de unas','Corte y limado de unas',NULL,15000.00,19.00,1,'2026-11-09 23:17:35',NULL),(9,'Limpieza dental','Profilaxis dental',NULL,120000.00,19.00,1,'2026-11-09 23:17:35',NULL),(10,'Extraccion dental','Extraccion de pieza dental',NULL,90000.00,19.00,1,'2026-11-09 23:17:35',NULL),(11,'Radiografia simple','Toma de radiografia',NULL,70000.00,19.00,1,'2026-11-09 23:17:35',NULL),(12,'Ecografia abdominal','Ecografia de abdomen',NULL,110000.00,19.00,1,'2026-11-09 23:17:35',NULL),(13,'Electrocardiograma',NULL,1,150000.00,19.00,1,'2026-11-09 23:17:35',NULL),(14,'Examen cardiologico','Valoracion cardiologica',1,130000.00,19.00,1,'2026-11-09 23:17:35',NULL),(15,'Consulta neurologica','Valoracion neurologica',2,140000.00,19.00,1,'2026-11-09 23:17:35',NULL),(16,'Consulta dermatologica','Valoracion de piel',5,90000.00,19.00,1,'2026-11-09 23:17:35',NULL),(17,'Prueba alergica cutanea',NULL,5,85000.00,19.00,1,'2026-11-09 23:17:35',NULL),(18,'Consulta oftalmologica','Valoracion ocular',9,95000.00,19.00,1,'2026-11-09 23:17:35',NULL),(19,'Medicion presion ocular',NULL,9,60000.00,19.00,1,'2026-11-09 23:17:35',NULL),(20,'Cirugia de esterilizacion','Esterilizacion quirurgica',NULL,250000.00,19.00,1,'2026-11-09 23:17:35',NULL),(21,'Cirugia de tejidos blandos',NULL,NULL,350000.00,19.00,1,'2026-11-09 23:17:35',NULL),(22,'Anestesia general','Procedimiento anestesico',12,100000.00,19.00,1,'2026-11-09 23:17:35',NULL),(23,'Hospitalizacion dia','Internacion por dia',NULL,120000.00,19.00,1,'2026-11-09 23:17:35',NULL),(24,'Aplicacion de suero','Fluidoterapia',NULL,45000.00,19.00,1,'2026-11-09 23:17:35',NULL),(25,'Hemograma','Examen de sangre',NULL,55000.00,19.00,1,'2026-11-09 23:17:35',NULL),(26,'Perfil bioquimico','Examen bioquimico sanguineo',NULL,95000.00,19.00,1,'2026-11-09 23:17:35',NULL),(27,'Examen coprologico','Analisis de materia fecal',NULL,30000.00,19.00,1,'2026-11-09 23:17:35',NULL),(28,'Microchip de identificacion','Implante de microchip',NULL,65000.00,19.00,1,'2026-11-09 23:17:35',NULL),(29,'Certificado de salud','Expedicion de certificado',NULL,20000.00,0.00,1,'2026-11-09 23:17:35',NULL),(30,'Eutanasia asistida','Procedimiento humanitario',NULL,150000.00,0.00,1,'2026-11-09 23:17:35',NULL);
/*!40000 ALTER TABLE `servicio` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `session`
--

DROP TABLE IF EXISTS `session`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `session` (
  `session_id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `token` text NOT NULL,
  `fecha_ini_sesion` datetime DEFAULT NULL,
  `fecha_expiracion` datetime DEFAULT NULL,
  PRIMARY KEY (`session_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `usuario`
--

DROP TABLE IF EXISTS `usuario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuario` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password_hash` text DEFAULT NULL,
  `rol` int(11) NOT NULL,
  `fecha_creacion` datetime DEFAULT current_timestamp(),
  `activo` tinyint(1) DEFAULT 1,
  `email` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `usuario` WRITE;
/*!40000 ALTER TABLE `usuario` DISABLE KEYS */;
INSERT INTO `usuario` VALUES (1,'david','55fc5b709962876903785fd64a6961e5',1,'2026-11-09 00:21:10',1,'davidlolproo@gmail.com'),(2,'danna','206e5ca7ade1aaa5e1c05c4a1064b61f',3,'2026-11-09 02:38:04',1,'davidlolproo+medico@gmail.com'),(3,'lizeth','9b6ddb1ad552a6fafcea641de67c0cfb',2,'2026-11-09 23:24:02',1,'davidlolproo+recepcion@gmail.com'),(4,'sebastian','dc8c2105224a4516e0597e7f7f135477',1,'2026-11-10 00:44:55',1,'sebasvizgar999@gmail.com'),(5,'farid','fc076d31f6c3adfab90047c5b109895d',1,'2026-11-10 00:45:25',1,'faridleocasas@gmail.com');
/*!40000 ALTER TABLE `usuario` ENABLE KEYS */;
UNLOCK TABLES;

/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump limpio generado para Clinica DDSF

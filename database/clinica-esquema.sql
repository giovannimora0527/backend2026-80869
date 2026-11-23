-- MySQL dump 10.13  Distrib 8.0.46, for Win64 (x86_64)
--
-- Host: localhost    Database: clinica
-- ------------------------------------------------------
-- Server version	8.0.46

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Current Database: `clinica`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `clinica` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `clinica`;

--
-- Table structure for table `anotacion_historia`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `anotacion_historia` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `historia_id` int NOT NULL,
  `medico_id` int NOT NULL,
  `fecha` datetime DEFAULT CURRENT_TIMESTAMP,
  `descripcion` text NOT NULL,
  `cita_id` bigint unsigned DEFAULT NULL,
  `diagnostico` text,
  `observaciones` text,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`),
  KEY `fk_anotacion_historia_cita` (`cita_id`),
  CONSTRAINT `fk_anotacion_historia_cita` FOREIGN KEY (`cita_id`) REFERENCES `cita` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cita`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cita` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `cliente_id` int NOT NULL,
  `mascota_id` int NOT NULL,
  `medico_id` int NOT NULL,
  `fecha_hora` datetime NOT NULL,
  `estado` varchar(20) NOT NULL,
  `motivo` text,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cliente`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cliente` (
  `cliente_id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `usuario_id` int DEFAULT NULL,
  `tipo_documento` varchar(10) NOT NULL,
  `numero_documento` varchar(20) NOT NULL,
  `nombres` varchar(100) NOT NULL,
  `apellidos` varchar(100) NOT NULL,
  `fecha_nacimiento` date NOT NULL,
  `genero` char(1) DEFAULT NULL,
  `telefono` varchar(20) DEFAULT NULL,
  `direccion` text,
  `activo` tinyint(1) NOT NULL,
  PRIMARY KEY (`cliente_id`),
  UNIQUE KEY `id` (`cliente_id`),
  UNIQUE KEY `numero_documento` (`numero_documento`),
  UNIQUE KEY `usuario_id` (`usuario_id`),
  KEY `idx_paciente_documento` (`numero_documento`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `despacho`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `despacho` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `formula_id` bigint unsigned NOT NULL,
  `estado` varchar(30) NOT NULL DEFAULT 'FORMULADO',
  `fecha_creacion` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_despacho_formula` (`formula_id`),
  CONSTRAINT `fk_despacho_formula` FOREIGN KEY (`formula_id`) REFERENCES `formula_medica` (`id`),
  CONSTRAINT `chk_despacho_estado` CHECK ((`estado` in (_utf8mb4'FORMULADO',_utf8mb4'EN_PREPARACION',_utf8mb4'DESPACHO_PARCIAL',_utf8mb4'DESPACHADO',_utf8mb4'PENDIENTE_DISPONIBILIDAD',_utf8mb4'CANCELADO')))
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `despacho_detalle`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `despacho_detalle` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `entrega_id` bigint unsigned NOT NULL,
  `formula_id` bigint unsigned NOT NULL,
  `numero_linea` int unsigned NOT NULL,
  `lote_id` bigint unsigned NOT NULL,
  `cantidad_entregada` int unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_entrega_linea_lote` (`entrega_id`,`formula_id`,`numero_linea`,`lote_id`),
  KEY `idx_despacho_detalle_lote` (`lote_id`),
  KEY `fk_despacho_linea_formula` (`formula_id`,`numero_linea`),
  CONSTRAINT `fk_despacho_detalle_lote` FOREIGN KEY (`lote_id`) REFERENCES `medicamento_lote` (`id`),
  CONSTRAINT `fk_despacho_linea_formula` FOREIGN KEY (`formula_id`, `numero_linea`) REFERENCES `formula_medica_detalle` (`formula_id`, `numero_linea`),
  CONSTRAINT `fk_detalle_entrega` FOREIGN KEY (`entrega_id`) REFERENCES `despacho_entrega` (`id`),
  CONSTRAINT `chk_despacho_cantidad` CHECK ((`cantidad_entregada` > 0))
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `despacho_entrega`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `despacho_entrega` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `despacho_id` bigint unsigned NOT NULL,
  `usuario_id` bigint unsigned NOT NULL,
  `clave_operacion` char(36) NOT NULL,
  `fecha_entrega` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_entrega_operacion` (`clave_operacion`),
  KEY `idx_entrega_despacho_fecha` (`despacho_id`,`fecha_entrega`),
  KEY `fk_entrega_usuario` (`usuario_id`),
  CONSTRAINT `fk_entrega_despacho` FOREIGN KEY (`despacho_id`) REFERENCES `despacho` (`id`),
  CONSTRAINT `fk_entrega_usuario` FOREIGN KEY (`usuario_id`) REFERENCES `usuario` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `despacho_historial`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `despacho_historial` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `despacho_id` bigint unsigned NOT NULL,
  `usuario_id` bigint unsigned NOT NULL,
  `estado_anterior` varchar(30) DEFAULT NULL,
  `estado_nuevo` varchar(30) NOT NULL,
  `motivo` varchar(500) DEFAULT NULL,
  `fecha` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_historial_despacho_fecha` (`despacho_id`,`fecha`),
  KEY `fk_historial_despacho_usuario` (`usuario_id`),
  CONSTRAINT `fk_historial_despacho` FOREIGN KEY (`despacho_id`) REFERENCES `despacho` (`id`),
  CONSTRAINT `fk_historial_despacho_usuario` FOREIGN KEY (`usuario_id`) REFERENCES `usuario` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `especializacion`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `especializacion` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) NOT NULL,
  `descripcion` text,
  `codigo_especializacion` varchar(10) NOT NULL DEFAULT '""',
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`),
  UNIQUE KEY `nombre` (`nombre`),
  UNIQUE KEY `codigo_especializacion_UNIQUE` (`codigo_especializacion`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `factura`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `factura` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `cita_id` bigint unsigned NOT NULL,
  `usuario_emisor_id` bigint unsigned NOT NULL,
  `fecha_emision` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `estado_pago` varchar(25) NOT NULL DEFAULT 'PENDIENTE_DE_PAGO',
  `subtotal` decimal(15,2) NOT NULL,
  `descuento` decimal(15,2) NOT NULL DEFAULT '0.00',
  `impuestos` decimal(15,2) NOT NULL DEFAULT '0.00',
  `total` decimal(15,2) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_factura_id_cita` (`id`,`cita_id`),
  KEY `idx_factura_cita_fecha` (`cita_id`,`fecha_emision`),
  KEY `fk_factura_emisor` (`usuario_emisor_id`),
  CONSTRAINT `fk_factura_cita` FOREIGN KEY (`cita_id`) REFERENCES `cita` (`id`),
  CONSTRAINT `fk_factura_emisor` FOREIGN KEY (`usuario_emisor_id`) REFERENCES `usuario` (`id`),
  CONSTRAINT `chk_factura_estado_pago` CHECK ((`estado_pago` in (_utf8mb4'PENDIENTE_DE_PAGO',_utf8mb4'PAGO_PARCIAL',_utf8mb4'PAGADA',_utf8mb4'ANULADA'))),
  CONSTRAINT `chk_factura_importes` CHECK (((`subtotal` >= 0) and (`descuento` >= 0) and (`descuento` <= `subtotal`) and (`impuestos` >= 0) and (`total` = ((`subtotal` - `descuento`) + `impuestos`))))
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `factura_anulacion`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `factura_anulacion` (
  `factura_id` bigint unsigned NOT NULL,
  `usuario_anulacion_id` bigint unsigned NOT NULL,
  `fecha_anulacion` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `estado_pago_anterior` varchar(25) NOT NULL,
  `motivo` varchar(500) NOT NULL,
  PRIMARY KEY (`factura_id`),
  KEY `fk_factura_anulacion_usuario` (`usuario_anulacion_id`),
  CONSTRAINT `fk_factura_anulacion_factura` FOREIGN KEY (`factura_id`) REFERENCES `factura` (`id`),
  CONSTRAINT `fk_factura_anulacion_usuario` FOREIGN KEY (`usuario_anulacion_id`) REFERENCES `usuario` (`id`),
  CONSTRAINT `chk_factura_anulacion_motivo` CHECK ((char_length(trim(`motivo`)) > 0))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `factura_detalle`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `factura_detalle` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `factura_id` bigint unsigned NOT NULL,
  `cita_id` bigint unsigned NOT NULL,
  `tipo` varchar(15) NOT NULL,
  `descripcion` varchar(255) NOT NULL,
  `tarifa_vigencia_id` bigint unsigned DEFAULT NULL,
  `despacho_detalle_id` bigint unsigned DEFAULT NULL,
  `cantidad` int unsigned NOT NULL,
  `precio_unitario` decimal(12,2) NOT NULL,
  `porcentaje_iva` decimal(5,2) NOT NULL DEFAULT '0.00',
  `descuento` decimal(15,2) NOT NULL DEFAULT '0.00',
  `subtotal` decimal(15,2) GENERATED ALWAYS AS ((`cantidad` * `precio_unitario`)) STORED,
  `valor_iva` decimal(15,2) GENERATED ALWAYS AS (round(((((`cantidad` * `precio_unitario`) - `descuento`) * `porcentaje_iva`) / 100),2)) STORED,
  `total_linea` decimal(15,2) GENERATED ALWAYS AS (((`subtotal` - `descuento`) + `valor_iva`)) STORED,
  `cita_consulta_id` bigint unsigned GENERATED ALWAYS AS ((case when (`tipo` = _utf8mb4'CONSULTA') then `cita_id` else NULL end)) STORED,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_factura_consulta_por_cita` (`cita_consulta_id`),
  UNIQUE KEY `uq_factura_entrega_cobrada` (`despacho_detalle_id`),
  KEY `idx_factura_detalle_cabecera` (`factura_id`,`cita_id`),
  KEY `fk_factura_detalle_tarifa` (`tarifa_vigencia_id`),
  CONSTRAINT `fk_factura_detalle_cabecera` FOREIGN KEY (`factura_id`, `cita_id`) REFERENCES `factura` (`id`, `cita_id`),
  CONSTRAINT `fk_factura_detalle_entrega` FOREIGN KEY (`despacho_detalle_id`) REFERENCES `despacho_detalle` (`id`),
  CONSTRAINT `fk_factura_detalle_tarifa` FOREIGN KEY (`tarifa_vigencia_id`) REFERENCES `tarifa_vigencia` (`id`),
  CONSTRAINT `chk_factura_detalle_importes` CHECK (((`cantidad` > 0) and (`precio_unitario` >= 0) and (`porcentaje_iva` between 0 and 100) and (`descuento` >= 0) and (`descuento` <= (`cantidad` * `precio_unitario`)))),
  CONSTRAINT `chk_factura_detalle_origen` CHECK ((((`tipo` = _utf8mb4'CONSULTA') and (`cantidad` = 1) and (`tarifa_vigencia_id` is not null) and (`despacho_detalle_id` is null)) or ((`tipo` = _utf8mb4'MEDICAMENTO') and (`despacho_detalle_id` is not null) and (`tarifa_vigencia_id` is null))))
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `factura_pago`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `factura_pago` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `factura_id` bigint unsigned NOT NULL,
  `usuario_registro_id` bigint unsigned NOT NULL,
  `fecha_pago` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `monto` decimal(15,2) NOT NULL,
  `medio_pago` varchar(40) NOT NULL,
  `referencia` varchar(100) DEFAULT NULL,
  `clave_operacion` char(36) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_factura_pago_operacion` (`clave_operacion`),
  KEY `idx_factura_pago_factura_fecha` (`factura_id`,`fecha_pago`),
  KEY `fk_factura_pago_usuario` (`usuario_registro_id`),
  CONSTRAINT `fk_factura_pago_factura` FOREIGN KEY (`factura_id`) REFERENCES `factura` (`id`),
  CONSTRAINT `fk_factura_pago_usuario` FOREIGN KEY (`usuario_registro_id`) REFERENCES `usuario` (`id`),
  CONSTRAINT `chk_factura_pago_monto` CHECK ((`monto` > 0))
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `formula_medica`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `formula_medica` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `cita_id` int NOT NULL,
  `medicamento_id` int NOT NULL,
  `dosis` text NOT NULL,
  `indicaciones` text,
  `fecha_creacion_registro` datetime NOT NULL,
  `fecha_actualizacion_registro` datetime DEFAULT NULL,
  `fecha_emision` date DEFAULT NULL,
  `frecuencia` varchar(100) DEFAULT NULL,
  `duracion_tratamiento` varchar(100) DEFAULT NULL,
  `cantidad_prescrita` int unsigned DEFAULT NULL,
  `via_administracion` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`),
  CONSTRAINT `chk_formula_cantidad_prescrita` CHECK (((`cantidad_prescrita` is null) or (`cantidad_prescrita` > 0)))
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `formula_medica_detalle`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `formula_medica_detalle` (
  `formula_id` bigint unsigned NOT NULL,
  `numero_linea` int unsigned NOT NULL,
  `medicamento_id` bigint unsigned NOT NULL,
  `dosis` text NOT NULL,
  `frecuencia` varchar(100) DEFAULT NULL,
  `duracion_tratamiento` varchar(100) DEFAULT NULL,
  `cantidad_prescrita` int unsigned DEFAULT NULL,
  `via_administracion` varchar(100) DEFAULT NULL,
  `indicaciones` text,
  PRIMARY KEY (`formula_id`,`numero_linea`),
  KEY `fk_formula_detalle_medicamento` (`medicamento_id`),
  CONSTRAINT `fk_formula_detalle_formula` FOREIGN KEY (`formula_id`) REFERENCES `formula_medica` (`id`),
  CONSTRAINT `fk_formula_detalle_medicamento` FOREIGN KEY (`medicamento_id`) REFERENCES `medicamento` (`id`),
  CONSTRAINT `chk_formula_detalle_cantidad` CHECK (((`cantidad_prescrita` is null) or (`cantidad_prescrita` > 0))),
  CONSTRAINT `chk_formula_detalle_linea` CHECK ((`numero_linea` > 0))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `historia_medica`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `historia_medica` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `paciente_id` int NOT NULL,
  `fecha_creacion` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `inventario_importacion`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `inventario_importacion` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `clave_operacion` char(36) NOT NULL,
  `usuario_id` bigint unsigned NOT NULL,
  `nombre_archivo` varchar(255) NOT NULL,
  `fecha_confirmacion` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `total_filas` int unsigned NOT NULL,
  `filas_importadas` int unsigned NOT NULL,
  `filas_rechazadas` int unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_importacion_operacion` (`clave_operacion`),
  KEY `idx_importacion_usuario_fecha` (`usuario_id`,`fecha_confirmacion`),
  CONSTRAINT `fk_importacion_usuario` FOREIGN KEY (`usuario_id`) REFERENCES `usuario` (`id`),
  CONSTRAINT `chk_importacion_totales` CHECK ((`total_filas` = (`filas_importadas` + `filas_rechazadas`)))
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `inventario_importacion_fila`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `inventario_importacion_fila` (
  `importacion_id` bigint unsigned NOT NULL,
  `numero_fila` int unsigned NOT NULL,
  `codigo_leido` text,
  `nombre_leido` text,
  `estado` enum('IMPORTADA','RECHAZADA') NOT NULL,
  `medicamento_id` bigint unsigned DEFAULT NULL,
  `motivo` text,
  PRIMARY KEY (`importacion_id`,`numero_fila`),
  KEY `fk_importacion_fila_producto` (`medicamento_id`),
  CONSTRAINT `fk_importacion_fila_carga` FOREIGN KEY (`importacion_id`) REFERENCES `inventario_importacion` (`id`),
  CONSTRAINT `fk_importacion_fila_producto` FOREIGN KEY (`medicamento_id`) REFERENCES `medicamento` (`id`),
  CONSTRAINT `chk_importacion_fila_resultado` CHECK ((((`estado` = _utf8mb4'IMPORTADA') and (`medicamento_id` is not null)) or ((`estado` = _utf8mb4'RECHAZADA') and (`medicamento_id` is null) and (`motivo` is not null))))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `mascota`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `mascota` (
  `mascota_id` int NOT NULL AUTO_INCREMENT,
  `nombre_mascota` varchar(100) NOT NULL,
  `edad` int NOT NULL,
  `raza_id` int NOT NULL,
  `fecha_registro` datetime NOT NULL,
  `fecha_modificacion` datetime DEFAULT NULL,
  `cliente_id` int NOT NULL,
  `activo` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`mascota_id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `medicamento`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `medicamento` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) NOT NULL,
  `descripcion` text,
  `presentacion` varchar(100) DEFAULT NULL,
  `fecha_compra` date DEFAULT NULL,
  `fecha_vence` date DEFAULT NULL,
  `fecha_creacion_registro` datetime NOT NULL,
  `fecha_modificacion_registro` datetime DEFAULT NULL,
  `codigo` varchar(8) DEFAULT NULL,
  `tipo_producto` enum('MEDICAMENTO','UTENSILIO') NOT NULL DEFAULT 'MEDICAMENTO',
  `registro_invima` varchar(100) DEFAULT NULL,
  `precio_venta` decimal(12,2) unsigned DEFAULT NULL,
  `stock_minimo` int unsigned NOT NULL DEFAULT '0',
  `fecha_proximo_pedido` date DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`),
  UNIQUE KEY `nombre` (`nombre`),
  UNIQUE KEY `uq_medicamento_codigo` (`codigo`),
  CONSTRAINT `chk_medicamento_codigo` CHECK (((`codigo` is null) or regexp_like(`codigo`,_utf8mb4'^[A-Za-z0-9]{8}$')))
) ENGINE=InnoDB AUTO_INCREMENT=250 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `medicamento_lote`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `medicamento_lote` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `medicamento_id` bigint unsigned NOT NULL,
  `numero_lote` varchar(60) NOT NULL,
  `fecha_ingreso` date NOT NULL,
  `fecha_compra` date NOT NULL,
  `fecha_vence` date DEFAULT NULL,
  `precio_compra` decimal(12,2) unsigned NOT NULL,
  `cantidad_ingresada` int unsigned NOT NULL,
  `cantidad_disponible` int unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_lote_por_medicamento` (`medicamento_id`,`numero_lote`),
  CONSTRAINT `fk_lote_medicamento` FOREIGN KEY (`medicamento_id`) REFERENCES `medicamento` (`id`),
  CONSTRAINT `chk_lote_cantidad_disponible` CHECK ((`cantidad_disponible` <= `cantidad_ingresada`)),
  CONSTRAINT `chk_lote_cantidad_ingresada` CHECK ((`cantidad_ingresada` > 0)),
  CONSTRAINT `chk_lote_fechas_ingreso` CHECK ((`fecha_ingreso` >= `fecha_compra`)),
  CONSTRAINT `chk_lote_fechas_vencimiento` CHECK (((`fecha_vence` is null) or (`fecha_vence` >= `fecha_compra`)))
) ENGINE=InnoDB AUTO_INCREMENT=251 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `medico`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `medico` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `tipo_documento` varchar(10) NOT NULL,
  `numero_documento` varchar(20) NOT NULL,
  `nombres` varchar(100) NOT NULL,
  `apellidos` varchar(100) NOT NULL,
  `telefono` varchar(20) DEFAULT NULL,
  `registro_profesional` varchar(50) NOT NULL,
  `especializacion_id` int NOT NULL DEFAULT '0',
  `estado` tinyint(1) NOT NULL DEFAULT '1',
  `usuario_id` bigint unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`),
  UNIQUE KEY `numero_documento` (`numero_documento`),
  UNIQUE KEY `registro_profesional` (`registro_profesional`),
  UNIQUE KEY `uq_medico_usuario` (`usuario_id`),
  KEY `idx_medico_documento` (`numero_documento`),
  CONSTRAINT `fk_medico_usuario` FOREIGN KEY (`usuario_id`) REFERENCES `usuario` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `menu`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `menu` (
  `menu_id` int NOT NULL AUTO_INCREMENT,
  `code` varchar(100) NOT NULL,
  `title` varchar(255) NOT NULL,
  `url` longtext NOT NULL,
  `icon` varchar(255) NOT NULL,
  `activo` tinyint(1) NOT NULL,
  `fecha_creacion` datetime NOT NULL,
  `fecha_modificacion` datetime DEFAULT NULL,
  PRIMARY KEY (`menu_id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `otp_token`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `otp_token` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `usuario_id` bigint NOT NULL,
  `codigo` varchar(6) NOT NULL,
  `fecha_creacion` datetime NOT NULL,
  `fecha_expiracion` datetime NOT NULL,
  `intentos` int NOT NULL,
  `usado` tinyint(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `perfil`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `perfil` (
  `perfil_id` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(50) NOT NULL,
  `activo` tinyint(1) NOT NULL,
  `fecha_creacion` datetime NOT NULL,
  `fecha_modificacion` datetime DEFAULT NULL,
  PRIMARY KEY (`perfil_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `perfil_menu`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `perfil_menu` (
  `perfil_menu_id` int NOT NULL AUTO_INCREMENT,
  `perfil_id` int NOT NULL,
  `menu_id` int NOT NULL,
  PRIMARY KEY (`perfil_menu_id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `raza`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `raza` (
  `raza_id` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) NOT NULL,
  `especie` varchar(100) NOT NULL,
  `fecha_creacion` datetime NOT NULL,
  `fecha_modificacion` datetime DEFAULT NULL,
  PRIMARY KEY (`raza_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `session`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `session` (
  `session_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `token` text NOT NULL,
  `fecha_ini_sesion` datetime DEFAULT NULL,
  `fecha_expiracion` datetime DEFAULT NULL,
  PRIMARY KEY (`session_id`)
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tarifa_servicio`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tarifa_servicio` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `especializacion_id` bigint unsigned NOT NULL,
  `nombre_servicio` varchar(120) NOT NULL,
  `activo` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_tarifa_especialidad_servicio` (`especializacion_id`,`nombre_servicio`),
  CONSTRAINT `fk_tarifa_especializacion` FOREIGN KEY (`especializacion_id`) REFERENCES `especializacion` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tarifa_vigencia`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tarifa_vigencia` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `tarifa_servicio_id` bigint unsigned NOT NULL,
  `vigente_desde` date NOT NULL,
  `valor_base` decimal(12,2) NOT NULL,
  `porcentaje_iva` decimal(5,2) NOT NULL,
  `usuario_id` bigint unsigned NOT NULL,
  `fecha_registro` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `motivo` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_tarifa_fecha` (`tarifa_servicio_id`,`vigente_desde`),
  KEY `fk_vigencia_usuario` (`usuario_id`),
  CONSTRAINT `fk_vigencia_tarifa` FOREIGN KEY (`tarifa_servicio_id`) REFERENCES `tarifa_servicio` (`id`),
  CONSTRAINT `fk_vigencia_usuario` FOREIGN KEY (`usuario_id`) REFERENCES `usuario` (`id`),
  CONSTRAINT `chk_tarifa_iva` CHECK ((`porcentaje_iva` between 0 and 100)),
  CONSTRAINT `chk_tarifa_valor` CHECK ((`valor_base` >= 0))
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `usuario`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuario` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password_hash` text,
  `rol` varchar(30) NOT NULL,
  `fecha_creacion` datetime DEFAULT CURRENT_TIMESTAMP,
  `activo` tinyint(1) DEFAULT '1',
  `email` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Temporary view structure for view `v_prefactura_medicamento`
--

SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `v_prefactura_medicamento` AS SELECT 
 1 AS `cita_id`,
 1 AS `formula_id`,
 1 AS `numero_linea`,
 1 AS `medicamento_id`,
 1 AS `medicamento`,
 1 AS `cantidad_prescrita`,
 1 AS `precio_unitario`,
 1 AS `subtotal_estimado`,
 1 AS `estado_despacho`*/;
SET character_set_client = @saved_cs_client;

--
-- Dumping events for database 'clinica'
--

--
-- Dumping routines for database 'clinica'
--

--
-- Current Database: `clinica`
--

USE `clinica`;

--
-- Final view structure for view `v_prefactura_medicamento`
--

/*!50001 DROP VIEW IF EXISTS `v_prefactura_medicamento`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `v_prefactura_medicamento` AS select `f`.`cita_id` AS `cita_id`,`f`.`id` AS `formula_id`,`fd`.`numero_linea` AS `numero_linea`,`m`.`id` AS `medicamento_id`,`m`.`nombre` AS `medicamento`,`fd`.`cantidad_prescrita` AS `cantidad_prescrita`,`m`.`precio_venta` AS `precio_unitario`,(`fd`.`cantidad_prescrita` * `m`.`precio_venta`) AS `subtotal_estimado`,coalesce(`d`.`estado`,'FORMULADO') AS `estado_despacho` from (((`formula_medica` `f` join `formula_medica_detalle` `fd` on((`fd`.`formula_id` = `f`.`id`))) join `medicamento` `m` on((`m`.`id` = `fd`.`medicamento_id`))) left join `despacho` `d` on((`d`.`formula_id` = `f`.`id`))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-11-23 16:33:56

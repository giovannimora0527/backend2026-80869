-- =====================================================================================
-- Migración de la base de datos `clinica` para el Segundo Parcial de Programación Web
-- Ejecutar UNA sola vez, DESPUÉS de cargar 01_clinica_respaldo_original.sql.
--
-- Qué hace:
--   1. Corrige datos inconsistentes (cita cuyo cliente no es el dueño de la mascota) y
--      normaliza el texto del estado de las citas.
--   2. Alinea los tipos de las columnas de relación (BIGINT UNSIGNED / INT) para poder
--      crear llaves foráneas y así garantizar que no exista información huérfana.
--   3. Recrea `historia_medica` y `anotacion_historia` con la estructura que usa el backend
--      (estaban vacías en el respaldo; si en su base tienen datos, haga un respaldo antes).
--   4. Amplía `formula_medica` (mascota, veterinario, historia, frecuencia, duración,
--      fecha de emisión, activo) y completa los registros existentes.
--   5. Crea las llaves foráneas e índices de consulta.
-- =====================================================================================
   SET SQL_SAFE_UPDATES = 0;
USE clinica;
SET FOREIGN_KEY_CHECKS = 0;

-- 1. Datos ---------------------------------------------------------------------------
UPDATE cita c
  JOIN mascota m ON m.mascota_id = c.mascota_id
   SET c.cliente_id = m.cliente_id;

UPDATE cita
   SET estado = CASE LOWER(estado)
                  WHEN 'pendiente'  THEN 'Pendiente'
                  WHEN 'confirmada' THEN 'Confirmada'
                  WHEN 'atendida'   THEN 'Atendida'
                  WHEN 'cancelada'  THEN 'Cancelada'
                  WHEN 'programada' THEN 'Programada'
                  ELSE estado
                END;

-- 2. Tipos de las columnas de relación -----------------------------------------------
ALTER TABLE mascota MODIFY cliente_id BIGINT UNSIGNED NOT NULL;
ALTER TABLE cita
  MODIFY cliente_id BIGINT UNSIGNED NOT NULL,
  MODIFY medico_id  BIGINT UNSIGNED NOT NULL;

-- 3. Historias clínicas y anotaciones ------------------------------------------------
DROP TABLE IF EXISTS anotacion_historia;
DROP TABLE IF EXISTS historia_medica;

CREATE TABLE historia_medica (
  id             BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  cliente_id     BIGINT UNSIGNED NOT NULL,
  mascota_id     INT             NOT NULL,
  cita_id        BIGINT UNSIGNED NOT NULL,
  medico_id      BIGINT UNSIGNED NOT NULL,
  fecha_creacion DATETIME        NOT NULL COMMENT 'Fecha y hora de la consulta',
  motivo         TEXT            NOT NULL,
  diagnostico    TEXT            NOT NULL,
  observaciones  TEXT            NULL,
  estado         VARCHAR(20)     NOT NULL DEFAULT 'Abierta',
  PRIMARY KEY (id),
  UNIQUE KEY uk_historia_cita (cita_id),
  KEY idx_historia_fecha (fecha_creacion),
  CONSTRAINT fk_historia_cliente FOREIGN KEY (cliente_id) REFERENCES cliente (cliente_id),
  CONSTRAINT fk_historia_mascota FOREIGN KEY (mascota_id) REFERENCES mascota (mascota_id),
  CONSTRAINT fk_historia_cita    FOREIGN KEY (cita_id)    REFERENCES cita (id),
  CONSTRAINT fk_historia_medico  FOREIGN KEY (medico_id)  REFERENCES medico (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE anotacion_historia (
  id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  historia_id BIGINT UNSIGNED NOT NULL,
  medico_id   BIGINT UNSIGNED NOT NULL,
  fecha       DATETIME        NOT NULL,
  descripcion TEXT            NOT NULL,
  observacion TEXT            NOT NULL,
  diagnostico TEXT            NOT NULL,
  PRIMARY KEY (id),
  KEY idx_anotacion_historia_fecha (historia_id, fecha),
  CONSTRAINT fk_anotacion_historia FOREIGN KEY (historia_id) REFERENCES historia_medica (id),
  CONSTRAINT fk_anotacion_medico   FOREIGN KEY (medico_id)   REFERENCES medico (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 4. Fórmulas médicas ----------------------------------------------------------------
ALTER TABLE formula_medica
  MODIFY cita_id        BIGINT UNSIGNED NOT NULL,
  MODIFY medicamento_id BIGINT UNSIGNED NOT NULL,
  ADD COLUMN mascota_id    INT             NULL AFTER cita_id,
  ADD COLUMN medico_id     BIGINT UNSIGNED NULL AFTER mascota_id,
  ADD COLUMN historia_id   BIGINT UNSIGNED NULL AFTER medico_id,
  ADD COLUMN frecuencia    VARCHAR(100)    NULL AFTER dosis,
  ADD COLUMN duracion_dias INT             NULL AFTER frecuencia,
  ADD COLUMN fecha_emision DATETIME        NULL AFTER indicaciones,
  ADD COLUMN activo        TINYINT(1)      NOT NULL DEFAULT 1;

-- Los registros existentes toman mascota y veterinario de su cita; frecuencia y duración
-- son valores por defecto para los registros históricos (7 días es lo que indicaban).
UPDATE formula_medica f
  JOIN cita c ON c.id = f.cita_id
   SET f.mascota_id    = c.mascota_id,
       f.medico_id     = c.medico_id,
       f.frecuencia    = COALESCE(f.frecuencia, 'No especificada'),
       f.duracion_dias = COALESCE(f.duracion_dias, 7),
       f.indicaciones  = COALESCE(f.indicaciones, 'Sin indicaciones'),
       f.fecha_emision = GREATEST(f.fecha_creacion_registro, c.fecha_hora);

ALTER TABLE formula_medica
  MODIFY mascota_id    INT             NOT NULL,
  MODIFY medico_id     BIGINT UNSIGNED NOT NULL,
  MODIFY frecuencia    VARCHAR(100)    NOT NULL,
  MODIFY duracion_dias INT             NOT NULL,
  MODIFY indicaciones  TEXT            NOT NULL,
  MODIFY fecha_emision DATETIME        NOT NULL;

-- 5. Llaves foráneas e índices -------------------------------------------------------
ALTER TABLE mascota
  ADD CONSTRAINT fk_mascota_cliente FOREIGN KEY (cliente_id) REFERENCES cliente (cliente_id),
  ADD CONSTRAINT fk_mascota_raza    FOREIGN KEY (raza_id)    REFERENCES raza (raza_id);

ALTER TABLE cita
  ADD KEY idx_cita_medico_fecha  (medico_id, fecha_hora),
  ADD KEY idx_cita_mascota_fecha (mascota_id, fecha_hora),
  ADD CONSTRAINT fk_cita_cliente FOREIGN KEY (cliente_id) REFERENCES cliente (cliente_id),
  ADD CONSTRAINT fk_cita_mascota FOREIGN KEY (mascota_id) REFERENCES mascota (mascota_id),
  ADD CONSTRAINT fk_cita_medico  FOREIGN KEY (medico_id)  REFERENCES medico (id);

ALTER TABLE formula_medica
  ADD KEY idx_formula_fecha (fecha_creacion_registro),
  ADD CONSTRAINT fk_formula_cita        FOREIGN KEY (cita_id)        REFERENCES cita (id),
  ADD CONSTRAINT fk_formula_mascota     FOREIGN KEY (mascota_id)     REFERENCES mascota (mascota_id),
  ADD CONSTRAINT fk_formula_medico      FOREIGN KEY (medico_id)      REFERENCES medico (id),
  ADD CONSTRAINT fk_formula_historia    FOREIGN KEY (historia_id)    REFERENCES historia_medica (id),
  ADD CONSTRAINT fk_formula_medicamento FOREIGN KEY (medicamento_id) REFERENCES medicamento (id);

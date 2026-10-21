-- ============================================================================
-- Datos de prueba para la base de datos H2 embebida
-- Se ejecuta automáticamente al arrancar la aplicación (src/main/resources)
-- ============================================================================

-- 1. Razas (mascotas)
INSERT INTO raza (raza_id, nombre, especie, fecha_creacion, fecha_modificacion)
VALUES (1, 'Labrador Retriever', 'Perro', '2026-01-01 00:00:00', NULL);

INSERT INTO raza (raza_id, nombre, especie, fecha_creacion, fecha_modificacion)
VALUES (2, 'Siamés', 'Gato', '2026-01-01 00:00:00', NULL);

INSERT INTO raza (raza_id, nombre, especie, fecha_creacion, fecha_modificacion)
VALUES (3, 'Caniche', 'Perro', '2026-01-01 00:00:00', NULL);

-- 2. Clientes
INSERT INTO cliente (cliente_id, usuario_id, tipo_documento, numero_documento, nombres, apellidos, fecha_nacimiento, genero, telefono, direccion, activo)
VALUES (1, 1, 'CC', '100000001', 'Juan', 'Pérez López', '1990-05-12', 'M', '3001112233', 'Calle 10 #12-34', 1);

INSERT INTO cliente (cliente_id, usuario_id, tipo_documento, numero_documento, nombres, apellidos, fecha_nacimiento, genero, telefono, direccion, activo)
VALUES (2, 2, 'CC', '100000002', 'María', 'Gómez Rodríguez', '1985-08-22', 'F', '3012223344', 'Carrera 15 #20-10', 1);

INSERT INTO cliente (cliente_id, usuario_id, tipo_documento, numero_documento, nombres, apellidos, fecha_nacimiento, genero, telefono, direccion, activo)
VALUES (3, 3, 'TI', '900000003', 'Andrés', 'Martínez Díaz', '2005-03-18', 'M', '3023334455', 'Av. Boyacá #45-67', 1);

-- 3. Médicos
INSERT INTO medico (id, tipo_documento, numero_documento, nombres, apellidos, telefono, registro_profesional, especializacion_id)
VALUES (1, 'CC', '1001001001', 'Carlos', 'Mendoza Gómez', '3001112233', 'RP-1001', 1);

INSERT INTO medico (id, tipo_documento, numero_documento, nombres, apellidos, telefono, registro_profesional, especializacion_id)
VALUES (2, 'CC', '1001001002', 'Ana María', 'Rodríguez López', '3001112234', 'RP-1002', 2);

INSERT INTO medico (id, tipo_documento, numero_documento, nombres, apellidos, telefono, registro_profesional, especializacion_id)
VALUES (3, 'CC', '1001001003', 'Luis', 'García Torres', '3001112235', 'RP-1003', 3);

-- 4. Mascotas
INSERT INTO mascota (mascota_id, nombre_mascota, edad, raza_id, fecha_registro, fecha_modificacion, cliente_id)
VALUES (1, 'Max', 3, 1, '2026-08-01 08:15:00', '2026-08-15 10:20:00', 1);

INSERT INTO mascota (mascota_id, nombre_mascota, edad, raza_id, fecha_registro, fecha_modificacion, cliente_id)
VALUES (2, 'Luna', 2, 2, '2026-08-01 09:30:00', '2026-08-16 11:00:00', 2);

INSERT INTO mascota (mascota_id, nombre_mascota, edad, raza_id, fecha_registro, fecha_modificacion, cliente_id)
VALUES (3, 'Rocky', 5, 3, '2026-08-02 10:00:00', NULL, 3);

-- 5. Citas
INSERT INTO cita (id, cliente_id, mascota_id, medico_id, fecha_hora, estado, motivo)
VALUES (1, 1, 1, 1, '2026-10-16 21:30:00', 'Programada', 'Consulta general');

INSERT INTO cita (id, cliente_id, mascota_id, medico_id, fecha_hora, estado, motivo)
VALUES (2, 2, 2, 2, '2026-10-17 14:30:00', 'Programada', 'Consulta odontológica');

INSERT INTO cita (id, cliente_id, mascota_id, medico_id, fecha_hora, estado, motivo)
VALUES (3, 3, 3, 3, '2026-10-18 18:30:00', 'Pendiente', 'Vacunación anual');

-- 6. Medicamentos
INSERT INTO medicamento (id, nombre, descripcion, presentacion, fecha_compra, fecha_vence, fecha_creacion_registro, fecha_modificacion_registro)
VALUES (1, 'Paracetamol', 'Analgésico y antipirético', 'Tabletas 500mg', '2024-06-01', '2026-06-01', '2025-09-11 20:56:13', NULL);

INSERT INTO medicamento (id, nombre, descripcion, presentacion, fecha_compra, fecha_vence, fecha_creacion_registro, fecha_modificacion_registro)
VALUES (2, 'Amoxicilina', 'Antibiótico de amplio espectro', 'Tabletas 500mg', '2024-06-01', '2027-06-01', '2025-09-12 10:00:00', NULL);

INSERT INTO medicamento (id, nombre, descripcion, presentacion, fecha_compra, fecha_vence, fecha_creacion_registro, fecha_modificacion_registro)
VALUES (3, 'Meloxicam', 'Antiinflamatorio no esteroideo', 'Tabletas 15mg', '2024-07-01', '2027-07-01', '2025-09-13 11:00:00', NULL);

-- 7. Fórmulas médicas
INSERT INTO formula_medica (id, cita_id, mascota_id, medico_id, historia_id, medicamento_id, dosis, frecuencia, duracion_dias, indicaciones, fecha_emision, activo, fecha_creacion_registro, fecha_actualizacion_registro)
VALUES (1, 1, 1, 1, NULL, 1, '500 mg cada 12 horas', 'cada 12 horas', 7, 'Tomar después de las comidas', '2026-10-16 22:00:00', 1, '2026-10-16 22:00:00', NULL);

INSERT INTO formula_medica (id, cita_id, mascota_id, medico_id, historia_id, medicamento_id, dosis, frecuencia, duracion_dias, indicaciones, fecha_emision, activo, fecha_creacion_registro, fecha_actualizacion_registro)
VALUES (2, 2, 2, 2, NULL, 2, '500 mg cada 8 horas', 'cada 8 horas', 5, 'Tomar con abundante agua', '2026-10-17 15:00:00', 1, '2026-10-17 15:00:00', NULL);

INSERT INTO formula_medica (id, cita_id, mascota_id, medico_id, historia_id, medicamento_id, dosis, frecuencia, duracion_dias, indicaciones, fecha_emision, activo, fecha_creacion_registro, fecha_actualizacion_registro)
VALUES (3, 3, 3, 3, NULL, 3, '15 mg cada 24 horas', 'cada 24 horas', 3, 'Tomar con alimentos', '2026-10-18 19:00:00', 1, '2026-10-18 19:00:00', NULL);

-- 8. Historias clínicas
INSERT INTO historia_medica (id, cliente_id, mascota_id, cita_id, medico_id, fecha_creacion, motivo, diagnostico, observaciones, estado)
VALUES (1, 1, 1, 1, 1, '2026-10-16 21:30:00', 'Consulta general de rutina', 'Paciente sano', 'Sin síntomas relevantes', 'Abierta');

INSERT INTO historia_medica (id, cliente_id, mascota_id, cita_id, medico_id, fecha_creacion, motivo, diagnostico, observaciones, estado)
VALUES (2, 2, 2, 2, 2, '2026-10-17 14:30:00', 'Revisión dental', 'Gingivitis leve', 'Requiere limpieza dental', 'Abierta');

INSERT INTO historia_medica (id, cliente_id, mascota_id, cita_id, medico_id, fecha_creacion, motivo, diagnostico, observaciones, estado)
VALUES (3, 3, 3, 3, 3, '2026-10-18 18:30:00', 'Vacunación anual', 'Al día con calendario', 'Sin observaciones', 'Abierta');

-- 9. Anotaciones de historia
INSERT INTO anotacion_historia (id, historia_id, medico_id, fecha, descripcion, observacion, diagnostico)
VALUES (1, 1, 1, '2026-10-16 22:30:00', 'Revisión física completa', 'Mascota ingresa estable', 'Control rutinario');

INSERT INTO anotacion_historia (id, historia_id, medico_id, fecha, descripcion, observacion, diagnostico)
VALUES (2, 2, 2, '2026-10-17 15:30:00', 'Limpieza dental realizada', 'Procedimiento exitoso', 'Gingivitis tratada');

INSERT INTO anotacion_historia (id, historia_id, medico_id, fecha, descripcion, observacion, diagnostico)
VALUES (3, 3, 3, '2026-10-18 19:30:00', 'Vacuna aplicada', 'Sin reacción adversa', 'Inmunización completa');

-- 10. Usuarios del sistema
INSERT INTO usuario (id, username, password_hash, rol, fecha_creacion, activo, email)
VALUES (1, 'admin', '8f8439bd01556aace5fd2b93ea927958', 'ADMIN', '2026-01-01 00:00:00', 1, 'admin@clinica.com');

INSERT INTO usuario (id, username, password_hash, rol, fecha_creacion, activo, email)
VALUES (2, 'vet1', '8f8439bd01556aace5fd2b93ea927958', 'VETERINARIO', '2026-01-01 00:00:00', 1, 'vet1@clinica.com');

-- 11. Sesiones
INSERT INTO session (session_id, user_id, token, fecha_ini_sesion, fecha_expiracion)
VALUES (1, 1, 'token-admin-001', '2026-10-20 10:00:00', '2026-10-21 10:00:00');

INSERT INTO session (session_id, user_id, token, fecha_ini_sesion, fecha_expiracion)
VALUES (2, 2, 'token-vet-002', '2026-10-20 11:00:00', '2026-10-21 11:00:00');

-- ============================================================================
-- Fin de datos de prueba
-- ============================================================================

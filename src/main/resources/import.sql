INSERT INTO formulas_medicas (id, descripcion, fecha_creacion) VALUES (1, 'Amoxicilina 500mg cada 12h por 7 dias', '2026-09-01 10:00:00');
INSERT INTO formulas_medicas (id, descripcion, fecha_creacion) VALUES (2, 'Meloxicam 2mg diario por 3 dias', '2026-09-10 15:30:00');

INSERT INTO medicos (id, nombre) VALUES (1, 'Dr. Carlos Mendoza');
INSERT INTO medicos (id, nombre) VALUES (2, 'Dra. Ana Maria Gomez');

INSERT INTO medico_especializaciones (medico_id, especializacion) VALUES (1, 'Cirugía General');
INSERT INTO medico_especializaciones (medico_id, especializacion) VALUES (1, 'Dermatología');
INSERT INTO medico_especializaciones (medico_id, especializacion) VALUES (2, 'Cardiología Veterinaria');

INSERT INTO citas (id, fecha_hora, motivo, paciente) VALUES (1, '2026-09-05 09:00:00', 'Vacunación anual', 'Lucas (Perro)');
INSERT INTO citas (id, fecha_hora, motivo, paciente) VALUES (2, '2026-09-12 11:30:00', 'Revisión dermatológica', 'Michi (Gato)');

INSERT INTO historias_medicas (id, nombre_mascota) VALUES (1, 'Lucas (Perro)');

INSERT INTO anotaciones_historia (id, observacion, fecha_creacion, historia_medica_id) VALUES (1, 'Mascota ingresa estable sin síntomas raros.', '2026-09-02 08:00:00', 1);
INSERT INTO anotaciones_historia (id, observacion, fecha_creacion, historia_medica_id) VALUES (2, 'Reacción alérgica leve en la piel tratada con crema.', '2026-09-08 14:20:00', 1);
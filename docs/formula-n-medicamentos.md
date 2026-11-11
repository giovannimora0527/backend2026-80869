# Formula medica con varios medicamentos (Fase 3B)

Convierte la formula medica para que contenga uno o varios medicamentos, y
permite generar la formula en PDF.

## Modelo de datos

- `formula_medica` (cabecera): se amplia con medico_id y fecha_creacion, y se
  hace opcional medicamento_id (modelo antiguo de un solo medicamento). Las
  formulas nuevas usan el detalle.
- `formula_medicamento` (detalle, nueva): cada fila es un medicamento prescrito,
  con dosis, frecuencia, duracion, cantidad y via de administracion.

Script: `docs/sql/fase-3b-formula-detalle.sql`.

## Reglas de negocio

- Una formula se asocia a una cita y debe tener al menos un medicamento.
- Cada medicamento prescrito requiere, como minimo, dosis.
- Crear una formula NO descuenta el inventario. El descuento ocurre al confirmar
  la dispensacion en el modulo de despacho (Fase 5). Esta separacion entre
  formulacion y dispensacion es una regla central del enunciado.

## Endpoints (/formula)

- `GET /formula/listar` : lista las formulas con su detalle.
- `GET /formula/obtener?id=` : una formula con su detalle.
- `POST /formula/guardar` : crea una formula con varios medicamentos.
- `GET /formula/pdf?id=` : genera el PDF de la formula (OpenPDF).

## PDF

El PDF incluye los datos de la clinica, el numero y fecha de la formula, la
mascota, el propietario, el medico, las indicaciones generales y una tabla con
los medicamentos prescritos (dosis, frecuencia, duracion, cantidad y via).

## Nota sobre el modulo anterior

El modulo anterior de formula (`/formula-medica`, un solo medicamento) se
conserva para no perder datos ni romper el codigo existente. El frontend usa el
nuevo modulo `/formula`. La libreria de PDF utilizada es OpenPDF.

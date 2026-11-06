# backend2026-80869
Backend para una clinica veterinaria

`GET /historia-clinica/listar` incluye `formulasMedicas` en cada elemento de
`anotaciones`, conservando los campos existentes. Contiene las formulas de la cita
asociada a esa anotacion, ordenadas por fecha de creacion descendente, con su
medicamento, dosis, frecuencia, duracion e indicaciones. Si no hay formulas,
`formulasMedicas` es una lista vacia (`[]`).

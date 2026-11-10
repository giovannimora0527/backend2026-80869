# Documentacion tecnica - Backend Clinica Veterinaria

Esta carpeta concentra la documentacion tecnica del backend del sistema de
gestion de la clinica veterinaria: cambios en el modelo de datos, scripts SQL,
decisiones de arquitectura y justificacion de las entidades agregadas en cada
fase del desarrollo.

## Estructura

- `sql/` : Scripts SQL de creacion y modificacion de tablas, organizados por
  fase. Cada script indica la fecha, el objetivo y el detalle de los cambios.
- `arquitectura.md` : Descripcion general de la arquitectura del backend y de
  las convenciones de capas utilizadas.
- `seguridad.md` : Descripcion del mecanismo de autenticacion y del control de
  acceso por rol implementado en el backend.
- `endpoints.md` : Resumen de los endpoints disponibles y referencia a la
  documentacion interactiva (Swagger UI).

## Convenciones

- La documentacion se mantiene en espanol, con lenguaje tecnico y sin
  caracteres decorativos.
- Cada cambio en el modelo de datos debe quedar registrado en `sql/` con su
  justificacion correspondiente, para cumplir el entregable de modelo de datos.

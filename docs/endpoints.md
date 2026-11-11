# Endpoints y documentacion de la API

La documentacion completa e interactiva de los endpoints se genera
automaticamente con springdoc-openapi (Swagger).

## Acceso a la documentacion

Con la aplicacion en ejecucion y considerando el context-path `/clinica/v1`:

- Swagger UI (interfaz): `http://localhost:8080/clinica/v1/swagger-ui.html`
- OpenAPI en formato JSON: `http://localhost:8080/clinica/v1/v3/api-docs`
- OpenAPI en formato YAML: `http://localhost:8080/clinica/v1/v3/api-docs.yaml`

Estas rutas son publicas y no requieren token, de modo que la documentacion
puede consultarse sin iniciar sesion. Para probar un endpoint protegido desde
Swagger UI, use el boton Authorize e ingrese el token JWT obtenido al validar
el OTP.

## Endpoints agregados o completados en la Fase 0

Autenticacion (`/auth`):

- `POST /auth/login` : inicio de sesion (ya existente).
- `POST /auth/validate-token` : validacion del OTP y emision del JWT (ya existente).
- `POST /auth/cerrar-sesion` : cierre de sesion. Elimina las sesiones del
  usuario. (Se completo la implementacion en esta fase.)
- `POST /auth/recover-password` : recuperacion de contrasena mediante una
  contrasena temporal enviada al correo. (Nuevo.)

Perfil del usuario autenticado (`/perfil`):

- `GET /perfil/obtener` : consulta del perfil propio. (Nuevo.)
- `POST /perfil/editar` : edicion de usuario y correo. (Nuevo.)
- `POST /perfil/cambiar-password` : cambio de contrasena. (Nuevo.)

Todos los endpoints distintos de `/auth` y de la documentacion requieren un
token JWT valido. Consulte `seguridad.md` para el detalle del control de acceso.

## Endpoints agregados en la Fase 4 (Reportes)

Modulo de reportes (`/reporte`), restringido a los roles `Administrador`,
`Contable` y `JEFE_BODEGA`:

- `GET /reporte/generar?categoria=&tipo=&desde=&hasta=` : vista previa del
  reporte en formato JSON.
- `GET /reporte/pdf?categoria=&tipo=&desde=&hasta=` : exportacion del reporte a PDF.
- `GET /reporte/excel?categoria=&tipo=&desde=&hasta=` : exportacion del reporte a
  Excel (.xlsx).

Las categorias disponibles son `financiero`, `inventario` y `gestion`; el
catalogo completo de tipos de reporte por categoria se describe en
`reportes.md`. Las fechas usan el formato aaaa-mm-dd.

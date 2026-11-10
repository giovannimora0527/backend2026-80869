# Arquitectura del backend

El backend esta construido con Spring Boot (Java 21) y expone servicios REST
bajo el context-path `/clinica/v1`. La organizacion del codigo sigue una
arquitectura por capas con responsabilidades bien separadas.

## Capas

- `api` : Interfaces que declaran los endpoints (rutas, verbos HTTP, tipos de
  entrada y salida). Definen el contrato de cada modulo.
- `apicontroller` : Implementaciones de las interfaces `api`. Son los
  `@RestController` que delegan la logica al servicio correspondiente.
- `service` : Interfaces de los servicios de negocio.
- `serviceimpl` : Implementaciones de los servicios. Contienen la logica de
  negocio y las validaciones. Lanzan `BadRequestException` ante datos
  invalidos.
- `repository` : Repositorios Spring Data JPA para el acceso a datos.
- `entity` : Entidades JPA que representan las tablas de la base de datos.
- `models` : Objetos de transferencia de datos (DTO) de entrada (`Rq`) y
  salida (`Rs`), ademas de respuestas genericas como `MiRespuestaRS`.
- `util` : Utilidades transversales (generacion y validacion de JWT,
  generacion de OTP y contrasenas temporales, cifrado MD5).
- `config` : Configuraciones de la aplicacion (documentacion OpenAPI, CORS,
  verificacion de conexion a base de datos).
- `security` : Componentes del control de acceso: filtro de autenticacion por
  JWT y contexto del usuario autenticado.
- `exception` : Manejo centralizado de errores (`GlobalExceptionHandler`) y
  estructura de respuesta de error (`ErrorResponse`).

## Flujo de una peticion protegida

1. La peticion ingresa y es interceptada por `JwtAutenticacionFilter`.
2. El filtro deja pasar las rutas publicas (autenticacion y documentacion).
3. Para el resto, valida el token JWT (firma y vigencia) y el rol.
4. Si es valido, publica el usuario autenticado en `ContextoSeguridad` y la
   peticion continua hacia el controlador correspondiente.
5. El controlador delega en el servicio, que aplica la logica de negocio y
   accede a datos mediante los repositorios.

## Documentacion de la API

La documentacion interactiva de los endpoints se genera automaticamente con
springdoc-openapi y queda disponible en Swagger UI. Consulte `endpoints.md`.

# Seguridad y control de acceso

## Autenticacion

El acceso al sistema se realiza mediante un flujo de doble factor:

1. `POST /auth/login` recibe usuario y contrasena. La contrasena se compara
   cifrada en MD5. Si las credenciales son correctas se genera un codigo OTP
   de 6 caracteres y se envia al correo del usuario.
2. `POST /auth/validate-token` valida el OTP. Si es correcto, se genera un
   token JWT firmado con el algoritmo HS512 y se registra la sesion.

El token JWT incluye los siguientes datos (claims): identificador del usuario
(`sub`), `username`, `email`, `rol` y la lista de menus del perfil (`menus`).

## Validacion del token en el backend

Antes de esta fase, el backend no verificaba el token en las peticiones. Se
agrego el filtro `JwtAutenticacionFilter`, que se ejecuta una vez por peticion
y cumple las siguientes funciones:

- Permite el acceso sin token a las rutas publicas: autenticacion (`/auth/`),
  documentacion de la API (`/swagger-ui`, `/v3/api-docs`) y las peticiones de
  verificacion CORS (metodo OPTIONS).
- Para el resto de rutas, exige la cabecera `Authorization: Bearer <token>`.
- Valida la firma y la vigencia del token. Si el token falta, es invalido o
  esta expirado, responde con el codigo HTTP 401 y un mensaje claro.
- Verifica el rol del usuario contra las reglas de acceso definidas. Si el rol
  no tiene permiso para la ruta, responde con el codigo HTTP 403.

## Control de acceso por rol

El filtro mantiene un conjunto de reglas que asocian prefijos de ruta con los
roles autorizados. Las rutas sin regla especifica solo requieren un token
valido (cualquier usuario autenticado).

Reglas vigentes:

- `/usuario` : solo el rol `Administrador`.
- `/rol` : solo el rol `Administrador`.

Los menus que ve cada rol se configuran desde la pantalla de Gestion de Roles
(ver `gestion-roles.md`), que actualiza la tabla `perfil_menu`.

Este mapa de reglas se ampliara en las siguientes fases a medida que se
incorporen modulos con restricciones de rol, por ejemplo el modulo de despacho
para el perfil `DESPACHO_MEDICO`.

El control de acceso se aplica en el backend y no depende de que el frontend
oculte las opciones del menu. La ocultacion de opciones en la interfaz es una
mejora de experiencia de usuario, no un mecanismo de seguridad.

## Contexto del usuario autenticado

El usuario autenticado se publica en `ContextoSeguridad` durante el ciclo de
vida de la peticion. Esto permite que los servicios conozcan al responsable de
cada operacion sin volver a procesar el token, lo cual sera util para registrar
el usuario responsable en los movimientos de inventario y despacho.

## Gestion del perfil propio

Los endpoints bajo `/perfil` permiten que cualquier usuario autenticado
consulte y edite su propia cuenta:

- `GET /perfil/obtener` : consulta los datos del perfil.
- `POST /perfil/editar` : edita el nombre de usuario y el correo.
- `POST /perfil/cambiar-password` : cambia la contrasena (requiere la actual).

Adicionalmente, `POST /auth/recover-password` permite recuperar el acceso
generando una contrasena temporal que se envia al correo registrado, dado que
la contrasena original no puede recuperarse por estar almacenada en MD5.

Estos endpoints validan que el usuario solo pueda operar sobre su propia cuenta.

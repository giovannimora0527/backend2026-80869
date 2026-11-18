# Despliegue con Docker y Docker Compose

Este documento describe como ejecutar todo el sistema (base de datos, backend y
frontend) en contenedores, cumpliendo el entregable de despliegue del examen.

## Que resuelve

Docker empaqueta cada parte de la aplicacion con todo lo que necesita para
funcionar, de modo que el sistema corre igual en cualquier equipo sin instalar
manualmente Java, Maven, Node, Angular ni MySQL. Docker Compose coordina los
tres servicios y los levanta con un solo comando.

## Servicios

- `mysql`: base de datos MySQL 8. Crea la base `clinica` y ejecuta el dump de
  inicializacion la primera vez.
- `backend`: API Spring Boot (Java 21). Se construye con un Dockerfile de dos
  etapas (Maven para compilar, JRE para ejecutar).
- `frontend`: aplicacion Angular compilada y servida con Nginx.

## Estructura de carpetas esperada

Los dos repositorios deben estar como carpetas hermanas:

```
proyecto/
  Backen-p/     (contiene docker-compose.yml, Dockerfile y docker/mysql-init/)
  Frontend-p/   (contiene su propio Dockerfile y nginx.conf)
```

Las carpetas deben llamarse EXACTAMENTE `Backen-p` y `Frontend-p` y estar
juntas. Si tienen otro nombre, el build fallara con
`unable to prepare context: path "...\Frontend-p" not found`; la solucion es
renombrarlas. Alternativamente, se puede ajustar
`services.frontend.build.context` en `docker-compose.yml` para apuntar al nombre
real de la carpeta del frontend.

Para instrucciones paso a paso orientadas a quien solo quiere ejecutar la
aplicacion (sin Git), ver el archivo `COMO-EJECUTAR.md` en la raiz del backend.

## Paso previo: dump de la base de datos

Coloque el dump completo de la base `clinica` (exportado desde MySQL Workbench)
en `Backen-p/docker/mysql-init/` con un nombre como `01-clinica-dump.sql`. Ese
archivo se ejecuta automaticamente al crear la base por primera vez. Los
detalles estan en `docker/mysql-init/README.md`.

## Ejecucion

Desde la carpeta `Backen-p/`:

```bash
docker compose up --build
```

La primera vez tarda varios minutos (descarga imagenes, compila el backend y el
frontend). Cuando termine, los servicios quedan disponibles en:

- Frontend: http://localhost:4200
- Backend (API): http://localhost:8080/clinica/v1
- Swagger UI: http://localhost:8080/clinica/v1/swagger-ui.html
- MySQL: localhost:3307 (usuario root, contrasena clinica123)

Para detener los servicios: `Ctrl+C`, y para eliminarlos:

```bash
docker compose down
```

Para borrar tambien los datos de la base (empezar de cero):

```bash
docker compose down -v
```

## Configuracion del correo (OTP)

El inicio de sesion usa doble factor: envia un codigo al correo. Para que el
envio funcione dentro de Docker, defina las variables de correo. La forma mas
comoda es copiar la plantilla `.env.example` como `.env` (junto al
`docker-compose.yml`) y completar los valores:

```
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=su_correo@gmail.com
MAIL_PASSWORD=su_contrasena_de_aplicacion
MAIL_TO=correo_destino@gmail.com
```

Docker Compose toma esos valores automaticamente. Si no se configuran, el resto
del sistema funciona, pero el envio del codigo OTP fallara.

## Variables y perfil

El backend corre con el perfil `docker` (`application-docker.properties`), que
lee la configuracion desde variables de entorno definidas en el
`docker-compose.yml`. Asi no quedan credenciales quemadas en el codigo y la
conexion apunta al servicio `mysql` de la red interna de Compose.

## Notas

- El backend espera a que MySQL este saludable (healthcheck) antes de arrancar.
- Los datos de MySQL persisten en el volumen `mysql_data` entre reinicios.
- El puerto de MySQL se publica en 3307 para no chocar con una instalacion
  local en 3306.

## Solucion de problemas comunes

Durante la puesta en marcha con Docker pueden presentarse estos dos errores en
el contenedor del backend. Ambos ya estan corregidos en la configuracion del
proyecto; se documentan como referencia.

### 1. El backend se reinicia en bucle con "Circular placeholder reference 'JWT_SECRET'"

Sintoma: en el log aparece repetidamente

```
Circular placeholder reference 'JWT_SECRET' in value "${JWT_SECRET:...}"
```

y el contenedor `clinica-backend` entra en un ciclo de reinicios.

Causa: en el perfil `docker` la propiedad `JWT_SECRET` se definia leyendo una
variable de entorno con el mismo nombre (`JWT_SECRET=${JWT_SECRET:...}`), lo que
hace que la propiedad se referencie a si misma.

Solucion: usar un nombre de variable distinto para el valor de entorno. En
`application-docker.properties` la propiedad quedo asi:

```
JWT_SECRET=${JWT_SECRET_ENV:valor_por_defecto}
```

De este modo, si se desea sobreescribir la clave, se define la variable de
entorno `JWT_SECRET_ENV` (por ejemplo en el `.env`); si no, se usa el valor por
defecto.

### 2. El backend no conecta a MySQL: "Public Key Retrieval is not allowed"

Sintoma: el backend arranca (muestra "Started ClinicaApplication") pero falla al
obtener la conexion con

```
java.sql.SQLNonTransientConnectionException: Public Key Retrieval is not allowed
```

Causa: MySQL 8 usa el metodo de autenticacion `caching_sha2_password`. Cuando la
conexion no usa SSL, el conector necesita permiso explicito para solicitar la
clave publica del servidor; por defecto ese permiso esta deshabilitado.

Solucion: agregar el parametro `allowPublicKeyRetrieval=true` a la URL JDBC. En
`application-docker.properties` la URL quedo asi:

```
jdbc:mysql://${DB_HOST:mysql}:${DB_PORT:3306}/${DB_NAME:clinica}?...&useSSL=false&allowPublicKeyRetrieval=true
```

### Recordatorio: recargar el dump de la base

MySQL solo ejecuta los scripts de `docker/mysql-init/` la primera vez que crea
la base (cuando el volumen de datos esta vacio). Si cambia el dump y necesita
que se vuelva a cargar, elimine el volumen y vuelva a levantar:

```bash
docker compose down -v
docker compose up --build
```

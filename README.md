# Backend de la clínica veterinaria - Clínica DDSF

API REST para la gestión de clientes, mascotas, citas, historias clínicas,
médicos, usuarios, fórmulas médicas, inventario, servicios, facturación,
reportes y despacho. Está implementada con Java 21, Spring Boot 4, Spring Web,
Spring Data JPA y MySQL.

## Ejecución rápida con Docker (recomendada)

Levanta la base de datos, el backend y el frontend con un solo comando. Requiere
tener Docker instalado y los repositorios `Backen-p` y `Frontend-p` como
carpetas hermanas.

1. Coloque el dump de la base `clinica` en `docker/mysql-init/01-clinica-dump.sql`
   (ver `docker/mysql-init/README.md`).
2. Desde esta carpeta ejecute:

   ```bash
   docker compose up --build
   ```

3. Acceda a:
   - Frontend: http://localhost:4200
   - API: http://localhost:8080/clinica/v1
   - Swagger: http://localhost:8080/clinica/v1/swagger-ui.html

El detalle completo del despliegue con contenedores está en
`docs/docker-despliegue.md`.

## Ejecución local sin Docker

Requiere Java 21, Maven y una instancia de MySQL con la base `clinica`.

```bash
mvn spring-boot:run
```

Por defecto usa el perfil `dev` (`application-dev.properties`). El servidor
queda en `http://localhost:8080/clinica/v1`.

## Documentación

La documentación técnica (modelo de datos, seguridad, endpoints y cada módulo)
está en la carpeta `docs/`. Los scripts SQL por fase están en `docs/sql/`.

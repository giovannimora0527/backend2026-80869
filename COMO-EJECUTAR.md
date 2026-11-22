# Como ejecutar la aplicacion - Clinica DDSF

Guia para correr todo el sistema (base de datos, backend y frontend) con un
solo comando usando Docker. No necesitas instalar Java, MySQL ni Angular: solo
Docker.

---

## 1. Instalar Docker Desktop

1. Descargalo de: https://www.docker.com/products/docker-desktop/
2. Instalalo (si pide activar WSL 2, acepta).
3. Abre Docker Desktop y espera a que diga "Engine running" (el icono de la
   ballena, abajo a la izquierda, debe quedar fijo/verde). Esto puede tardar
   uno o dos minutos la primera vez.

Para verificar que quedo listo, abre una terminal (PowerShell) y escribe:

```
docker ps
```

Si muestra una tabla (aunque este vacia), Docker ya esta corriendo.

---

## 2. IMPORTANTE: los nombres de las carpetas

El sistema son dos carpetas que deben estar JUNTAS (una al lado de la otra)
dentro de una carpeta contenedora, y deben llamarse EXACTAMENTE asi:

```
UnaCarpetaCualquiera/
  Backen-p/      <- carpeta del backend (aqui esta este archivo)
  Frontend-p/    <- carpeta del frontend
```

Los nombres `Backen-p` y `Frontend-p` son obligatorios. Si al descargar o
descomprimir quedaron con otro nombre (por ejemplo `backend2026-xxxx` o
`frontend26-xxxx`), RENOMBRALAS:

- La del backend debe llamarse `Backen-p`.
- La del frontend debe llamarse `Frontend-p`.

(En Windows: clic derecho sobre la carpeta -> Cambiar nombre.)

Si no se llaman asi, al ejecutar vas a ver un error como:
`unable to prepare context: path "...\Frontend-p" not found`
Eso significa justamente que las carpetas no tienen el nombre correcto o no
estan juntas.

---

## 3. Configurar el correo (archivo .env)

El inicio de sesion envia un codigo de verificacion (OTP) al correo del usuario.
Para que ese envio funcione hace falta un archivo llamado `.env` dentro de
`Backen-p`.

- Si el proyecto ya trae el archivo `.env`, no hagas nada.
- Si solo esta `.env.example`, copialo y renombralo a `.env`. Pide a David los
  datos del correo para completarlo (o usalos si ya te los paso).

Sin el `.env`, la aplicacion abre pero el login no podra enviar el codigo.

---

## 4. Ejecutar

1. Entra a la carpeta `Backen-p`.
2. Abre una terminal dentro de esa carpeta (clic derecho -> "Abrir en Terminal"
   o "Abrir en PowerShell").
3. Ejecuta:

   ```
   docker compose up --build
   ```

4. Espera. La primera vez tarda varios minutos (descarga y construye todo).
   Cuando el texto se detiene y queda mostrando registros del sistema, ya esta
   listo (no cierres la terminal).

5. Abre el navegador en:
   - Aplicacion: http://localhost:4200
   - Documentacion de la API (Swagger): http://localhost:8080/clinica/v1/swagger-ui.html

---

## 5. Usuarios de prueba

El codigo de verificacion llega al correo del usuario con el que inicias sesion.

| Usuario   | Contrasena    | A que correo llega el codigo     | Rol           |
|-----------|---------------|----------------------------------|---------------|
| david     | david123      | davidlolproo@gmail.com           | Administrador |
| sebastian | Sebastian123  | sebasvizgar999@gmail.com         | Administrador |
| farid     | Farid123      | faridleocasas@gmail.com          | Administrador |

(Hay mas usuarios con otros roles: lizeth/Recepcion, danna/Medico.)

---

## 6. Apagar y volver a arrancar

- Apagar: en la terminal presiona `Ctrl + C`, y luego ejecuta:
  ```
  docker compose down
  ```
- Volver a arrancar (ya construido, es rapido):
  ```
  docker compose up
  ```
- Empezar de cero con datos limpios (borra lo que hayas creado en la app):
  ```
  docker compose down -v
  docker compose up --build
  ```

---

## 7. Que es Docker (en pocas palabras)

Docker "empaqueta" cada parte de la aplicacion (base de datos, backend y
frontend) en contenedores que ya traen todo lo necesario para funcionar. Docker
Compose enciende y conecta esos tres contenedores con un solo comando
(`docker compose up`). Por eso no hace falta instalar Java, MySQL ni Angular por
separado: basta con tener Docker.

---

## 8. Si algo falla

- "path ...Frontend-p not found" -> las carpetas no se llaman `Backen-p` y
  `Frontend-p` o no estan juntas. Renombralas (ver punto 2).
- "error during connect" / "cannot find the file" -> Docker Desktop no esta
  abierto o no termino de arrancar. Abrelo y espera a "Engine running".
- El backend se reinicia o no abre -> espera un par de minutos (la base de datos
  tarda en quedar lista la primera vez); si persiste, haz `docker compose down -v`
  y luego `docker compose up --build`.
- El login no envia el codigo -> falta configurar el archivo `.env` (ver punto 3).
- En los registros aparece `UnknownHostException: smtp.gmail.com` -> el
  contenedor no logro resolver internet. El `docker-compose.yml` ya incluye DNS
  (8.8.8.8); si persiste, verifique que el equipo tenga internet y que Docker
  Desktop no este en un modo de red restringido, luego haga `docker compose down`
  y `docker compose up` de nuevo.

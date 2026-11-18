# Guia de entrega del parcial - Clinica DDSF

Este documento reune, en un solo lugar, el flujo recomendado para preparar y
presentar la entrega del parcial: que ejecutar, en que orden, como obtener el
diagrama entidad-relacion, que mostrar en la sustentacion y la lista de
entregables del enunciado con su ubicacion en el proyecto.

---

## 1. Preparacion del entorno (una sola vez)

Requisitos: tener Docker Desktop instalado y los dos repositorios como carpetas
hermanas:

```
ParcialPW-P/
  Backen-p/     (backend + docker-compose.yml + docker/mysql-init)
  Frontend-p/   (frontend)
```

Pasos:

1. Actualizar ambos repositorios a la rama de entrega:
   ```bash
   # En Backen-p
   git checkout parcial_final
   git pull origin parcial_final
   # En Frontend-p
   git checkout parcial_final
   git pull origin parcial_final
   ```

2. Crear el archivo de correo. En `Backen-p`, copiar `.env.example` como `.env`
   y completar los datos del correo (para el codigo OTP del login). Sin esto el
   sistema arranca, pero el login no enviara el codigo.

3. Levantar todo con Docker (desde `Backen-p`):
   ```bash
   docker compose up --build
   ```
   La primera vez tarda varios minutos. Cuando termine, los servicios quedan en:
   - Frontend: http://localhost:4200
   - API: http://localhost:8080/clinica/v1
   - Swagger: http://localhost:8080/clinica/v1/swagger-ui.html

### Comandos utiles de Docker

- Apagar sin perder datos: `docker compose down`.
- Apagar y empezar de cero (recarga el dump + datos de prueba): `docker compose down -v`.
- Importante: use `down -v` cuando cambie el dump o quiera una base limpia. Si
  no usa `-v`, se conservan los datos que haya creado en la aplicacion.

---

## 2. Diagrama entidad-relacion (Entregable 1)

El modelo de datos y el diagrama estan en `docs/modelo-datos.md` (incluye el
diagrama en formato Mermaid, la descripcion de tablas, las relaciones y la
justificacion de los cambios).

Para entregar el diagrama de forma visual, se recomienda generarlo desde MySQL
Workbench por ingenieria inversa sobre la base ya creada:

1. Abrir MySQL Workbench y conectar al servidor. Para conectarse a la base de
   Docker: host `127.0.0.1`, puerto `3307`, usuario `root`, contrasena
   `clinica123`. (Tambien sirve cualquier instancia donde se haya cargado el
   modelo.)
2. Menu `Database` -> `Reverse Engineer...` (Ctrl+R).
3. Seleccionar la conexion, dar `Next` y elegir el esquema `clinica`.
4. Seleccionar todas las tablas y finalizar. Workbench dibuja el diagrama EER.
5. Acomodar las tablas y exportar con `File` -> `Export` -> `Export as PNG...`
   (o PDF) para adjuntarlo al informe.

El diagrama ya contempla todas las tablas, incluidas las de despacho
(`despacho`, `despacho_detalle`, `log_despacho`). No requiere ajustes.

---

## 3. Credenciales de prueba por rol

Usuarios incluidos en la base (contrasenas definidas en el entorno; no se
publican en texto plano). El codigo OTP llega al correo del usuario.

| Rol             | Usuario   | Que puede hacer                                  |
|-----------------|-----------|--------------------------------------------------|
| Administrador   | david     | Todo el sistema                                  |
| Recepcion       | lizeth    | Citas, clientes, mascotas, facturacion           |
| Medico          | danna     | Su calendario, atencion e historias              |
| Jefe de bodega  | sebastian | Reportes e inventario (consulta)                 |
| Contable        | farid     | Reportes y facturacion (consulta)                |

El perfil DESPACHO_MEDICO existe en la base; puede crearse un usuario con ese
perfil desde Gestion de Usuarios para demostrar el modulo de despacho.

---

## 4. Flujo recomendado para la sustentacion

Este recorrido demuestra la solucion como un todo integrado y cubre las
evidencias que pide el enunciado.

1. Levantar el sistema con `docker compose up --build` y abrir el frontend.
   Explicar brevemente que todo corre en contenedores (MySQL + backend + frontend).
2. Iniciar sesion como administrador (david). Mostrar el menu agrupado por areas.
3. Inventario: mostrar el catalogo de medicamentos, un producto con sus lotes y
   las alertas (agotado, stock bajo, proximo a vencer, vencido). Hacer una
   carga masiva con el archivo de mas de 50 registros (vista previa e informe de
   errores).
4. Citas: abrir el calendario, mostrar las citas de la semana en distintos
   estados y crear una cita nueva (mostrar que respeta horario y no solapa).
5. Atencion y formulas: atender una cita y registrar la atencion en la historia;
   crear una formula con varios medicamentos e imprimir el PDF.
6. Facturacion: generar la factura de una cita atendida agregando un servicio y
   un medicamento; mostrar el calculo de subtotal, IVA y total; registrar un
   pago parcial y luego el total; descargar el PDF.
7. Despacho: iniciar sesion con el usuario de perfil DESPACHO_MEDICO; en el
   modulo de despacho, dispensar una formula pendiente; volver a inventario y
   mostrar que la existencia bajo (tomando del lote que vence primero, FEFO) y
   descargar el comprobante PDF.
8. Reportes: iniciar sesion como Contable o Jefe de bodega; generar un reporte
   financiero y uno de inventario; exportar a PDF y a Excel.
9. Seguridad: mostrar que un rol no autorizado no puede entrar a un modulo (el
   backend rechaza la peticion), evidenciando el control de acceso real.
10. Cerrar explicando la integridad de datos (no se borra, se anula o desactiva;
    operaciones transaccionales; valores historicos) y el despliegue con Docker.

Sugerencia: antes de grabar o presentar, hacer `docker compose down -v` seguido
de `docker compose up --build` para partir de una base limpia y predecible.

---

## 5. Evidencias que pide el enunciado

- Carga masiva con mas de 50 registros: archivo de ejemplo en
  `docs/ejemplos/` del backend; se demuestra en el paso 3.
- Descuento de inventario mediante dispensacion: paso 7 (despacho).
- Gestion de citas mediante calendario: paso 4.
- Generacion de formulas y facturas (PDF): pasos 5 y 6.
- Generacion de reportes PDF y Excel: paso 8.

---

## 6. Entregables del parcial y su ubicacion

| Entregable | Donde esta |
|------------|------------|
| 1. Modelo de datos (diagrama ER, scripts, justificacion) | `docs/modelo-datos.md` y `docs/sql/` |
| 2. Aplicacion Full Stack con Docker y Docker Compose | Codigo de ambos repos + `docker-compose.yml` y Dockerfiles |
| 3. Evidencias funcionales | Se graban/demuestran segun el paso 4 y 5 |
| 4. Documentacion tecnica (instalacion, arquitectura, API, reglas, credenciales) | `docs/` (arquitectura, seguridad, endpoints, docker-despliegue, un md por modulo) |
| 4. Swagger de la API | http://localhost:8080/clinica/v1/swagger-ui.html |
| 5. Sustentacion | Guion en este documento (paso 4) y en `docs/guia-estudio/guia-general.md` |

---

## 7. Material de estudio para los videos

En `docs/guia-estudio/` estan las guias por bloques, cada una en `.md` y en
`.html` imprimible:

- Guia 1: fundamentos (seguridad, inventario, citas).
- Guia 2: atencion, formulas y facturacion.
- Guia 3: despacho y dispensacion.
- Guia 4: reportes y despliegue con Docker.
- Guia general: vision integral del proyecto.

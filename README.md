# Backend – Centro Veterinario (Segundo Parcial de Programación Web)

Backend REST en **Spring Boot 3 / Java 17 / MySQL** para gestionar clientes, mascotas, citas, historias clínicas,
anotaciones y fórmulas médicas.

## 1. Cómo ejecutarlo

1. Crear la base de datos con el script original y luego aplicar la migración (en este orden):
   `database/01_clinica_respaldo_original.sql` → `database/02_migracion_parcial.sql`.
2. Ajustar usuario y contraseña de MySQL en `src/main/resources/application.properties`.
3. `mvn spring-boot:run` (el servicio queda en `http://localhost:8080/clinica/v1`).
4. `mvn test` verifica que el contexto carga y que las consultas son válidas (usa H2 en memoria, no necesita MySQL).

CORS: por defecto acepta `http://localhost:*`; se cambia con la propiedad `clinica.cors.allowed-origins`.

## 2. Arquitectura (capas)

| Paquete | Responsabilidad |
|---|---|
| `api` | Contratos REST (interfaces con los mapeos HTTP y su documentación). |
| `apicontroller` | Controladores: solo traducen HTTP ↔ servicio. Sin lógica ni `try/catch`. |
| `service` / `serviceimpl` | Reglas de negocio y validaciones (**toda regla crítica vive aquí**, no en Angular). |
| `repository` | Acceso a datos (Spring Data JPA, `JpaSpecificationExecutor` para filtros dinámicos). |
| `specification` | Construcción de filtros dinámicos de los listados. |
| `entity` | Entidades JPA. |
| `models` / `models.filtro` | DTO de entrada (`*Rq`), de salida resumida (`*ResumenRs`), `PaginaRs` y filtros. |
| `enums` | `EstadoCita`, `EstadoHistoria`. |
| `exception` | Excepciones de negocio y `GlobalExceptionHandler` (respuestas de error uniformes). |
| `util`, `config` | Utilidades (paginación, fechas, texto) y CORS. |

## 3. Modelo de datos

```
CLIENTE 1─N MASCOTA 1─N CITA N─1 MEDICO
                │
                └─ 1─N HISTORIA_MEDICA (una por cita atendida) 1─N ANOTACION_HISTORIA
CITA 1─N FORMULA_MEDICA (también ligada a mascota, médico, historia y medicamento)
```

* Todas las relaciones tienen **llave foránea** en MySQL: no puede existir información huérfana.
* `historia_medica.cita_id` es único: una cita genera como máximo una historia.
* Las fórmulas se **inactivan** (`activo = false`), nunca se borran; las anotaciones no se editan ni se eliminan.

## 4. Endpoints

Ruta base: `/clinica/v1`. Los listados paginados reciben `page, size, sortBy, sortDir` más sus filtros.

| Módulo | Método y ruta | Descripción |
|---|---|---|
| Cliente | `GET /cliente/listar` · `GET /cliente/consultar/{id}` | Listar y consultar clientes |
| Mascota | `GET /mascota/listar` · `GET /mascota/consultar/{id}` | Listar y consultar mascotas |
| | `GET /mascota/buscarPorCliente?clienteId=` | Mascotas de un cliente (combo dependiente) |
| | `GET /mascota/buscar?nombre=` · `GET /mascota/buscarPorRaza?razaId=` | Búsquedas |
| | `POST /mascota/guardar` · `PUT /mascota/actualizar/{id}` | Crear / editar |
| Raza | `GET /raza/listar` · `POST /raza/guardar` | Listar (combo) / crear raza |
| Veterinario | `GET /medico/listar` · `GET /medico/consultar/{id}` | Listar y consultar |
| Medicamento | `GET /medicamento/listar` | Combo de fórmulas |
| Cita | `GET /cita/paginado` | Listado resumido (orden por defecto: fecha y hora desc). Filtros: `texto, estado, clienteId, mascotaId, medicoId, fechaDesde, fechaHasta` |
| | `GET /cita/listar` · `GET /cita/consultar/{id}` | Lista completa / detalle |
| | `POST /cita/guardar` · `PUT /cita/actualizar/{id}` | Crear / editar |
| | `PATCH /cita/cancelar/{id}` · `PATCH /cita/reactivar/{id}` | Cancelar / reactivar (operación explícita) |
| Historia | `GET /historia-medica/paginado` | Listado resumido (fecha de consulta desc). Filtros: `texto, estado, clienteId, mascotaId, medicoId, citaId, fechaDesde, fechaHasta` |
| | `GET /historia-medica/listar` · `GET /historia-medica/consultar/{id}` | Lista completa / detalle |
| | `POST /historia-medica/guardar` · `PATCH /historia-medica/cerrar/{id}` | Crear (desde cita atendida) / cerrar |
| Anotación | `GET /anotacion-historia/paginado` | Histórico (fecha desc). Filtros: `texto, historiaId, mascotaId, medicoId, fechaDesde, fechaHasta` |
| | `GET /anotacion-historia/listarPorHistoria/{historiaId}` · `GET /anotacion-historia/consultar/{id}` | Anotaciones de una historia / detalle |
| | `POST /anotacion-historia/guardar` | Agregar anotación |
| Fórmula | `GET /formula-medica/paginado` | Listado resumido (fecha de creación desc). Filtros: `texto, activo, clienteId, mascotaId, medicoId, medicamentoId, citaId, fechaDesde, fechaHasta` |
| | `GET /formula-medica/listar` · `GET /formula-medica/consultar/{id}` | Lista completa / detalle |
| | `POST /formula-medica/guardar` · `PUT /formula-medica/actualizar/{id}` | Crear / editar |
| | `PATCH /formula-medica/inactivar/{id}` | Inactivar (no se borra) |

Los listados `paginado` devuelven `{ content, page, size, totalElements, totalPages }` con datos **resumidos**;
el detalle completo se pide con `consultar/{id}`. Las escrituras devuelven `{ status, message, id }`.

### Códigos HTTP y errores

| Código | Cuándo |
|---|---|
| 200 / 201 | Operación exitosa / recurso creado |
| 400 | Campos obligatorios vacíos, valores inválidos, referencia inexistente en el cuerpo, mascota que no es del cliente |
| 404 | Registro no encontrado (`consultar/{id}`, cliente inexistente en `buscarPorCliente`) |
| 409 | Cita duplicada o veterinario no disponible, intento de modificar información que ya no es modificable, historia cerrada |
| 500 | Error inesperado (mensaje genérico; el detalle solo queda en el log) |

Todos los errores tienen el formato `{ "status": 409, "error": "Conflict", "message": "texto para el usuario" }`.

## 5. Reglas de negocio y su justificación

**Citas** (`CitaServiceImpl`)
1. Cliente, mascota y veterinario deben existir; la mascota debe pertenecer al cliente (evita datos cruzados).
2. No se registran citas en fechas pasadas.
3. Un veterinario no puede tener dos citas vigentes a menos de 15 minutos (antes o después); una mascota tampoco puede
   tener citas simultáneas. Las citas **canceladas liberan la agenda**.
4. Estados: Pendiente, Confirmada, Atendida, Cancelada, Programada. Una cita nueva solo nace en un estado activo.
5. **Atendida** es un estado final: es historia clínica y no admite cambios. **Cancelada** no se edita; solo vuelve
   a estar activa con `PATCH /cita/reactivar/{id}` (que revalida fecha y disponibilidad).
6. Una cita activa puede cambiar fecha, hora, veterinario, motivo y estado; el cliente y la mascota no se cambian
   (se cancela y se registra otra) para no romper historias y fórmulas ya asociadas.
7. No existe borrado físico de citas: `cancelar` la deja con estado Cancelada.

**Historias y anotaciones**
1. La historia se crea solo desde una cita **Atendida** de la misma mascota, y cada cita tiene una sola historia.
2. La relación cliente → mascota → cita se valida; la fecha de consulta no puede ser futura ni anterior a la cita.
3. Una historia **Cerrada** es de solo lectura (no admite anotaciones).
4. Anotación: descripción, observación y diagnóstico obligatorios; fecha no futura ni anterior a la consulta.
   Las anotaciones no se editan ni eliminan (historial clínico).

**Fórmulas médicas**
1. La mascota (la de la cita) debe existir y pertenecer a un cliente registrado; el veterinario debe existir.
2. La fecha de emisión no puede ser anterior a la consulta (cita) ni futura.
3. La dosis debe contener una cantidad mayor que cero (por ejemplo "500 mg"); la duración, entre 1 y 365 días.
4. La cita y la historia deben ser de la misma mascota; no se emite sobre citas canceladas.
5. No se duplica la misma receta activa (mismo medicamento) en una cita.
6. Solo se editan dosis, frecuencia, duración e indicaciones; para cambiar cita/mascota/medicamento se inactiva y se crea otra.
7. Se **inactiva**, no se elimina físicamente.

## 6. Decisiones técnicas relevantes

* **Reglas en el backend**: Angular solo mejora la experiencia; el servicio valida siempre (parcial, punto 5).
* **Un único manejador de errores** (`GlobalExceptionHandler`) y tres excepciones de negocio (400, 404, 409), por lo que los
  controladores quedan sin `try/catch` y ningún mensaje expone detalles técnicos.
* **Filtros con `Specification`** y **paginación con `Pageable`**; el ordenamiento usa una **lista blanca de columnas**
  (`PaginacionUtil`) para que un nombre de campo arbitrario no provoque errores.
* **DTO resumidos** (`*ResumenRs`) para los listados y entidades completas para el detalle.
* **Llaves foráneas** en la base de datos como segunda barrera contra información huérfana.
* **CORS centralizado** en `CorsConfig`.

## 7. Cambios de contrato que debe reflejar el frontend

* `PUT /cita/actualizar/{id}` reemplaza a `POST /cita/actualizar`; `PATCH /cita/cancelar/{id}` reemplaza a `DELETE /cita/eliminar/{id}`.
* `PATCH /formula-medica/inactivar/{id}` reemplaza a `DELETE /formula-medica/eliminar/{id}`.
* `POST /formula-medica/guardar` recibe `{ citaId, medicamentoId, dosis, frecuencia, duracionDias, indicaciones }` (+ opcionales
  `mascotaId, medicoId, historiaId, fechaEmision`) y responde `{ status, message, id }`.
* `POST /cita/guardar` acepta `{ clienteId, mascotaId, medicoId, fecha, hora, motivo, estado }` (o `fechaHora`).
* Las anotaciones requieren `{ historiaId, medicoId, descripcion, observacion, diagnostico }`.

# Guia general - Vision integral del proyecto

Esta guia resume todo el sistema de gestion de la clinica veterinaria Clinica
DDSF como una solucion integral. Sirve para el repaso final y para la
sustentacion. Las guias 1 a 4 cubren cada bloque en detalle; esta las conecta.

---

## 1. Que es el sistema

Una aplicacion web Full Stack para gestionar integralmente los procesos de una
clinica veterinaria: administrativos, medicos, comerciales y financieros.

- Backend: Spring Boot (Java 21), con API REST documentada en Swagger.
- Frontend: Angular 19 (plantilla Datta Able + Bootstrap 5), con identidad de
  marca propia.
- Base de datos: MySQL.
- Despliegue: Docker y Docker Compose (MySQL + backend + frontend).

---

## 2. Los modulos y como se conectan

El sistema no es un conjunto de formularios aislados, sino un flujo integrado:

1. Inventario (Fase 1): catalogo de medicamentos y productos, con lotes,
   precios, registro Invima, codigo, alertas de estado y carga masiva por Excel
   o CSV.
2. Citas con calendario (Fase 2): agenda de citas por veterinario, con vista de
   calendario, estados y reglas de no solapamiento.
3. Atencion, formulas y facturacion (Fase 3): el veterinario atiende la cita,
   formula medicamentos y se genera la factura con servicios y medicamentos.
4. Reportes (Fase 4): informacion consolidada financiera, de inventario y de
   gestion medica, exportable a PDF y Excel.
5. Despacho (Fase 5): entrega real de los medicamentos formulados; es el unico
   punto donde se descuenta el inventario.

La idea que conecta todo: formular no descuenta inventario; el inventario solo
se afecta cuando el despacho confirma la entrega. La facturacion toma los
servicios y medicamentos; los reportes leen las facturas, los movimientos y las
citas.

---

## 3. Seguridad y roles

El acceso se controla por perfiles, y la validacion es real en el backend (un
filtro revisa el token y el rol en cada peticion), no solo ocultando menus.

- Administrador: acceso completo (superusuario).
- Recepcion: gestion operativa y facturacion.
- Medico: su calendario de citas, atencion e historias.
- DESPACHO_MEDICO: inventario en consulta y modulo de despacho.
- Contable: reportes y facturacion en consulta.
- JEFE_BODEGA: reportes e inventario en consulta.

Cada perfil ve en el menu solo los modulos que tiene asignados (tabla
`perfil_menu`), y el backend rechaza con error de permisos cualquier acceso no
autorizado.

---

## 4. Integridad de la informacion

- No se eliminan registros fisicamente: se desactivan o anulan (clientes,
  mascotas, medicos, servicios, facturas), conservando el historial.
- Las operaciones criticas (descuento de inventario en el despacho) son
  transaccionales: se aplican completas o no se aplican.
- Las tarifas se conservan historicamente en la factura.
- Los cambios de estado y de tarifa quedan registrados en tablas de log.

---

## 5. Credenciales de prueba por rol

Usuarios de prueba para la demostracion. Las contrasenas reales no se publican;
use las definidas en su entorno. Para las pruebas por rol se emplean alias de
correo con el signo mas.

| Rol              | Usuario sugerido | Correo de prueba (alias)             | Que puede hacer                                  |
|------------------|------------------|--------------------------------------|--------------------------------------------------|
| Administrador    | david            | (correo real del administrador)      | Todo el sistema                                  |
| Recepcion        | recepcion        | davidolproo+recepcion@gmail.com      | Citas, clientes, mascotas, facturacion           |
| Medico           | medico           | davidolproo+medico@gmail.com         | Su calendario, atencion e historias              |
| Despacho medico  | despacho         | davidolproo+despacho@gmail.com       | Inventario (consulta) y despacho                 |
| Contable         | contable         | davidolproo+contable@gmail.com       | Reportes y facturacion (consulta)                |
| Jefe de bodega   | bodega           | davidolproo+bodega@gmail.com         | Reportes e inventario (consulta)                 |

Nota: el inicio de sesion usa doble factor; el codigo de verificacion llega al
correo del usuario. Los usuarios deben existir en la base que esta corriendo
(por ejemplo, creados desde Gestion de Usuarios con el administrador).

---

## 6. Documentacion del proyecto

- Modelo de datos y diagrama entidad-relacion: `docs/modelo-datos.md`.
- Scripts SQL por fase: `docs/sql/`.
- Seguridad y control de acceso: `docs/seguridad.md`.
- Endpoints y Swagger: `docs/endpoints.md`.
- Un documento por modulo: inventario, citas, atencion-facturacion, servicios,
  formulas, reportes, despacho, gestion de roles.
- Despliegue con Docker: `docs/docker-despliegue.md`.
- Identidad visual (frontend): `docs/estetica-identidad.md`.
- Guias de estudio: `docs/guia-estudio/` (guias 1 a 4 y esta guia general).

---

## 7. Guion sugerido para la sustentacion (recorrido integral)

1. Levantar el sistema con `docker compose up --build` y abrir el frontend.
2. Iniciar sesion como administrador y mostrar el menu agrupado por areas.
3. Inventario: mostrar el catalogo, un medicamento con sus lotes y la carga
   masiva con mas de 50 registros (vista previa e informe de errores).
4. Citas: mostrar el calendario, crear una cita y mostrar la vista del medico.
5. Atencion y formulas: atender una cita, formular medicamentos e imprimir la
   formula en PDF.
6. Facturacion: generar la factura, registrar pagos y descargar el PDF.
7. Despacho: entrar como DESPACHO_MEDICO, dispensar la formula y mostrar como
   baja el inventario (FEFO) y se genera el comprobante.
8. Reportes: entrar como Contable o Jefe de bodega, generar un reporte y
   exportarlo a PDF y Excel.
9. Seguridad: mostrar que un rol no autorizado no puede acceder a un modulo (el
   backend lo rechaza).
10. Cerrar explicando la integridad (no se borra, se anula; transacciones;
    historicos) y el despliegue en contenedores.

---

## 8. Mapa de las guias

- Guia 1 - Fundamentos: base tecnica, seguridad, inventario y citas (Fases 0, 1 y 2).
- Guia 2 - Atencion, formulas y facturacion (Fase 3).
- Guia 3 - Despacho y dispensacion (Fase 5).
- Guia 4 - Reportes y despliegue con Docker (Fase 4 y entregables).
- Guia general - esta vision integral.

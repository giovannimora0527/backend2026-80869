# Guia de estudio 1 - Fundamentos del sistema

Esta guia cubre los cimientos del sistema de la clinica veterinaria: seguridad y
autenticacion, gestion de usuarios y roles, inventario y citas con calendario.
Esta escrita en lenguaje claro para repasar y para generar un video de estudio.

Fases incluidas: Fase 0 (base tecnica y seguridad), Fase 0.1 (gestion de
usuarios), Fase 1 (inventario), Fase 2 (citas con calendario), la fase de
estabilizacion y la gestion de roles.

---

## 1. Vision general y arquitectura

La clinica veterinaria amplia su sistema con nuevos modulos. El backend esta
hecho con Spring Boot (Java 21) y expone servicios REST bajo la ruta base
`/clinica/v1`. El frontend es Angular 19 con la plantilla Datta Able y Bootstrap
5. La base de datos es MySQL (esquema `clinica`).

El backend se organiza en capas: `api` (contratos de endpoints),
`apicontroller` (controladores REST), `service` y `serviceimpl` (logica de
negocio), `repository` (acceso a datos), `entity` (tablas), `models` (objetos de
entrada Rq y salida Rs), `security` (seguridad) y `config` (configuracion).

---

## 2. Fase 0 - Base tecnica y seguridad

Antes de construir modulos grandes se prepararon los cimientos.

### Autenticacion con doble factor

El ingreso tiene dos pasos: primero usuario y contrasena (la contrasena se
compara cifrada en MD5); si son correctos, se genera un codigo OTP de 6
caracteres y se envia al correo del usuario. El segundo paso valida ese codigo
y, si es correcto, entrega un token JWT firmado. El token contiene el
identificador del usuario, su nombre, correo, rol y la lista de menus de su
perfil.

### Seguridad real en el backend

Antes, el backend no verificaba el token: cualquiera podia llamar un endpoint.
Se agrego un filtro que, en cada peticion (salvo las publicas de autenticacion y
documentacion), exige un token valido y, segun la ruta, valida el rol. Si el
token falta o expiro responde 401; si el rol no tiene permiso, responde 403. El
control de acceso es real en el servidor, no solo ocultando menus en la
interfaz.

### Documentacion de la API

Se integro Swagger (springdoc-openapi): una pagina interactiva donde se ven y
prueban todos los endpoints. Queda disponible en la ruta `/swagger-ui.html`.

### Gestion del perfil propio

Cualquier usuario puede editar su nombre de usuario y correo, y cambiar su
contrasena. Tambien existe la recuperacion de contrasena, que genera una
contrasena temporal y la envia al correo. Se decidio editar solo usuario y
correo (no datos como telefono o direccion) porque esos datos viven en las
tablas de cliente o medico y no se deben duplicar.

### Soporte en el frontend

Un interceptor adjunta el token a cada peticion y maneja de forma central los
errores 401 (sesion expirada, lleva al login) y 403 (sin permisos). Los
guardias de ruta validan que el token exista y este vigente.

---

## 3. Fase 0.1 - Gestion de usuarios y modelo de roles

Se completo la pantalla para listar, crear y editar usuarios asignandoles un
rol. La contrasena es obligatoria al crear y opcional al editar.

El rol de un usuario se define con tres tablas relacionadas: `usuario.rol`
apunta a `perfil` (el nombre del rol); `perfil_menu` relaciona cada perfil con
los menus que puede ver; y `menu` es el catalogo de modulos. El menu lateral se
arma a partir de los menus incluidos en el token JWT. Por eso, al cambiar los
menus de un rol, el usuario debe cerrar sesion y volver a entrar.

El Administrador es el superusuario (ve todos los modulos). Los perfiles
operativos (Recepcion, Medico y los que se agreguen) se restringen a lo suyo.

---

## 4. Fase 1 - Inventario y carga masiva

Convierte la gestion de medicamentos en un modulo de inventario completo.

### Modelo de datos

- Se amplio `medicamento` con codigo de 8 caracteres, registro Invima, precios
  de compra y venta, stock minimo y fecha de proximo pedido.
- Se creo `lote_medicamento`: un medicamento puede tener varios lotes, cada uno
  con su cantidad, precio y fecha de vencimiento. La existencia del medicamento
  es la suma de las cantidades de sus lotes.
- Se creo `movimiento_inventario`: registra cada entrada y salida con el usuario
  responsable y una referencia. Es la base de la trazabilidad y del descuento
  por dispensacion del modulo de despacho.
- Se creo `log_carga_inventario`: registro de cada carga masiva.

### Reglas y alertas

El registro Invima es obligatorio solo para medicamentos (los utensilios pueden
no tenerlo). El sistema marca cada medicamento como agotado, con stock bajo,
proximo a vencer o vencido, calculandolo a partir de sus lotes.

### Carga masiva (Excel y CSV)

Permite cargar mas de 50 registros. Tiene dos pasos: una vista previa que valida
cada fila (estructura, obligatorios, formatos, duplicados y vencimientos) y
muestra las filas validas y las que tienen error con su motivo; y una
confirmacion que persiste solo las validas y registra el log. Un error en una
fila no afecta a las demas.

### Diferencia clave: formular no es dispensar

Crear una formula no descuenta el inventario. El descuento real ocurre solo al
confirmar la entrega en el modulo de despacho. Esta separacion es una regla
central del enunciado.

---

## 5. Fase 2 - Citas con calendario

Permite gestionar las citas mediante un calendario, con vista segun el rol.

### Modelo de datos

- Se agrego `medico.usuario_id` para vincular a un medico con su usuario del
  sistema. Asi, al iniciar sesion un medico, el calendario muestra solo sus
  citas.
- Se creo `horario_medico` para la disponibilidad del veterinario, que se usa
  para no agendar fuera de horario.

### Vista por rol

Un medico ve unicamente sus citas; recepcion y administrador ven todas. Esta
decision se aplica en el backend segun el rol del usuario autenticado.

### Funcionalidad

Estados de la cita: programada, confirmada, atendida, cancelada y finalizada
(las canceladas conservan su historial). Reprogramar valida que el medico este
activo, que no se solape con otra cita y que este dentro de su horario. Hay
historial de citas por mascota y un reporte en Excel por rango de fechas. No se
asignan citas a medicos inactivos.

### Frontend

Se uso la libreria FullCalendar con vistas de mes, semana y dia, con las citas
coloreadas segun su estado. Al hacer clic en una cita se ve su detalle y las
acciones disponibles.

---

## 6. Estabilizacion de modulos

Antes de seguir con los modulos grandes, se dejo todo navegable y estable:

- Se corrigieron columnas que las entidades esperaban y no existian en la base
  de datos (en medico, mascota, formula_medica y anotacion_historia), que
  provocaban errores al listar.
- Se completo el CRUD de clientes (propietarios de mascotas).
- Se unieron la lista de citas y el calendario en una sola pantalla con dos
  pestanas.
- Se retiro del menu el modulo antiguo de medicamentos (lo cubre inventario) y
  los listados muestran mas filas por pagina.

---

## 7. Gestion de roles desde la interfaz

Se creo una pantalla para que el administrador cree y edite roles y asigne, con
casillas de verificacion, los modulos que cada rol puede ver. Esto reemplaza la
insercion manual en la base de datos.

Ademas, el formulario de medicos incluye un campo para asociar el medico con un
usuario de rol Medico. Concepto importante: usuario (cuenta para iniciar sesion)
y medico (ficha del profesional) son cosas distintas que se vinculan; es normal
tener medicos sin cuenta y cuentas que no son medicos.

Flujo para dar de alta un medico operativo, sin tocar la base de datos: definir
los menus del rol Medico en Gestion de Roles, crear la cuenta en Gestion de
Usuarios, y en Gestion de Medicos asociar esa cuenta al medico. Luego el medico
inicia sesion y ve su calendario con sus citas.

---

## 8. Como usar cada modulo

### Iniciar sesion
1. Ingresar usuario y contrasena.
2. Revisar el correo y escribir el codigo OTP de 6 caracteres.
3. Al validar, se entra al sistema y el menu lateral muestra los modulos del rol.

### Gestion de usuarios (Administrador)
- Boton "Nuevo usuario": se ingresa usuario, correo, contrasena y rol.
- Boton "Editar": cambia los datos; la contrasena se deja vacia si no se cambia.

### Gestion de roles (Administrador)
- Boton "Nuevo rol" o "Editar": se da un nombre y se marcan, con casillas, los
  modulos que ese rol podra ver.
- Tras cambiar los menus de un rol, el usuario de ese rol debe cerrar sesion y
  volver a entrar para ver el menu actualizado.

### Gestion de medicos (Administrador)
- Boton "Nuevo" o "Editar": datos del medico y, opcionalmente, el campo "Usuario
  del sistema" para asociarlo a una cuenta de rol Medico.

### Inventario
- Pestana Inventario: tabla de medicamentos con existencia y estado (agotado,
  stock bajo, por vencer, vencido) y filtros rapidos. Botones por fila: ver
  detalle (lotes y movimientos), agregar lote (reabastecer) y editar.
- Pestana Carga masiva: seleccionar un archivo Excel o CSV, generar la vista
  previa, revisar las filas validas y con error, y confirmar la importacion.

### Citas y calendario
- Pestana Lista: crear y editar citas.
- Pestana Calendario: ver el calendario (mes, semana o dia). Un medico ve solo
  sus citas; recepcion y administrador ven todas. Al hacer clic en una cita se
  ve su detalle y las acciones (confirmar, atender, reprogramar, cancelar).
- Boton "Reporte Excel": descarga las citas del rango visible.

### Clientes
- Boton "Nuevo cliente" o "Editar": datos del propietario (documento unico).

### Mi perfil
- Editar usuario y correo, y cambiar la contrasena.

---

## 9. Flujo de prueba sugerido para la sustentacion

Este recorrido demuestra los fundamentos de forma ordenada:

1. Iniciar sesion como Administrador (mostrar el doble factor con el OTP al
   correo).
2. En Gestion de Roles, mostrar el rol Medico y los modulos que tiene marcados.
3. En Gestion de Usuarios, crear un usuario con rol Medico.
4. En Gestion de Medicos, asociar ese usuario a un medico.
5. En Inventario, hacer la carga masiva con un archivo de mas de 50 registros:
   mostrar la vista previa, el informe de errores y confirmar la importacion.
   Luego abrir el detalle de un medicamento para mostrar sus lotes y movimientos.
6. En Citas, crear una cita para ese medico y mostrarla en el calendario.
7. Cerrar sesion e iniciar como el medico: mostrar que solo ve sus modulos y, en
   el calendario, solo sus citas (demostracion de la vista por rol).
8. Como prueba de seguridad, intentar entrar por la URL a un modulo que el rol no
   tiene: el backend lo bloquea.

---

## 10. Glosario

- JWT: token firmado que identifica al usuario y su rol en cada peticion.
- OTP: codigo temporal de un solo uso enviado al correo para el segundo factor.
- Perfil o rol: conjunto de permisos (menus) de un usuario.
- Lote: unidades de un medicamento con una misma fecha de vencimiento y precio.
- Movimiento de inventario: registro de una entrada o salida de existencias.

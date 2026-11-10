# Gestion de roles y asignacion de menus

Permite que el administrador cree y configure roles (perfiles) desde la
interfaz, indicando que modulos (menus) puede ver cada rol, sin modificar la
base de datos manualmente.

## Por que

Antes, para que un rol tuviera menus habia que insertar filas en la tabla
perfil_menu directamente en la base de datos. Esto no es apropiado para la
operacion diaria. Con este modulo, el administrador gestiona los roles y sus
permisos desde la aplicacion.

## Modelo de datos

No cambia la estructura. Se usan las tablas existentes:

- perfil: el rol.
- menu: catalogo de modulos del sistema.
- perfil_menu: relacion rol - menu (que modulos ve cada rol).

El menu lateral del frontend se arma a partir de los menus incluidos en el
token JWT del usuario, que provienen de perfil_menu. Por eso, tras cambiar los
menus de un rol, el usuario debe cerrar sesion y volver a iniciarla para que su
token se regenere.

## Endpoints (/rol) - solo rol Administrador

- GET /rol/listar : lista los roles con los identificadores de sus menus.
- GET /rol/menus : lista los menus disponibles para asignar.
- POST /rol/guardar : crea un rol y asigna sus menus.
- POST /rol/actualizar : actualiza un rol y reasigna sus menus (elimina los
  anteriores y crea los nuevos dentro de una transaccion).

## Vinculo medico - usuario

El formulario de Gestion de Medicos incluye un campo opcional "Usuario del
sistema" para asociar un medico con un usuario de rol Medico. Esta asociacion
(columna medico.usuario_id) permite que, al iniciar sesion el medico, el
calendario muestre unicamente sus citas. Antes esta asociacion se hacia por SQL;
ahora se realiza desde la interfaz.

## Flujo recomendado para dar de alta un medico operativo

1. Gestion de Roles: verificar que el rol Medico tenga asignados los menus que
   debe ver (por ejemplo, Citas).
2. Gestion de Usuarios: crear el usuario con rol Medico.
3. Gestion de Medicos: crear o editar el medico y asociarle el usuario creado.

Con esto, el medico inicia sesion y ve su calendario con sus citas, sin
intervencion manual en la base de datos.

# Estrategia de pruebas por rol

El inicio de sesion del sistema utiliza doble factor (2FA): tras validar el
usuario y la contrasena, se envia un codigo OTP al correo registrado del
usuario. Ademas, el correo de cada usuario debe ser unico (no se permite
duplicarlo), lo cual es correcto para un escenario real.

Para efectos de prueba surge una limitacion practica: se requieren varios
usuarios con distintos roles, pero no siempre se dispone de un correo
diferente para cada uno.

## Solucion: alias de Gmail con el signo mas

Gmail permite agregar un sufijo al nombre de la cuenta usando el signo mas
(`+`). El proveedor ignora el sufijo al momento de entregar el mensaje, por lo
que todos los correos llegan a la misma bandeja de entrada, pero el sistema los
reconoce como direcciones distintas.

Ejemplo, partiendo del correo real `davidolproo@gmail.com`:

- `davidolproo+admin@gmail.com`
- `davidolproo+recepcion@gmail.com`
- `davidolproo+medico@gmail.com`
- `davidolproo+despacho@gmail.com`

Para el sistema son correos diferentes (no se activa la validacion de correo
duplicado), pero el codigo OTP de todos ellos llega a la misma bandeja de
entrada real.

## Uso

Al crear un usuario de prueba desde la pantalla de gestion de usuarios, se
ingresa en el campo de correo una direccion con el alias correspondiente al rol
que se desea probar. Al iniciar sesion con ese usuario, el codigo OTP llegara a
la bandeja de entrada principal.

Esta tecnica permite documentar credenciales de acceso de prueba por rol sin
necesidad de multiples cuentas de correo reales.

## Nota

El uso de alias con `+` es una caracteristica estandar de Gmail. Otros
proveedores pueden soportar una funcionalidad equivalente, aunque no esta
garantizado en todos los casos.

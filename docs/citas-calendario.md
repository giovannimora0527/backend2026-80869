# Modulo de citas con calendario (Fase 2)

Permite gestionar las citas medicas mediante una vista de agenda, diferenciada
por rol: un medico ve unicamente sus citas; recepcion y administrador ven todas.

## Modelo de datos

- `medico` (ampliada): columna `usuario_id` para vincular al medico con su
  usuario del sistema. Al iniciar sesion un medico, el calendario filtra las
  citas por ese vinculo.
- `horario_medico` (nueva): disponibilidad del medico por dia de la semana y
  franja horaria. Se usa para impedir citas fuera del horario.
- `cita` (sin cambios de estructura): la columna `estado` almacena el estado
  del ciclo de vida de la cita.

Script: `docs/sql/fase-2-citas-calendario.sql`.

## Estados de la cita

- PROGRAMADA: creada, pendiente de confirmacion.
- CONFIRMADA: el cliente confirmo o llego.
- ATENDIDA: atendida por el medico.
- CANCELADA: cancelada (conserva su historial, no se modifica).
- FINALIZADA: atencion cerrada.

## Reglas de negocio

- Un medico no puede tener dos citas que se solapen (ventana de 30 minutos).
- No se asignan citas a medicos inactivos.
- Si el medico tiene horarios definidos, la cita debe caer dentro de ellos.
- Las citas canceladas conservan su historial y no se pueden reprogramar ni
  cambiar de estado.
- No se reprograma una cita atendida o finalizada.
- Cada cita se relaciona con una mascota, un propietario y un medico.

## Vista por rol

El alcance de la informacion se decide en el backend a partir del rol del
usuario autenticado (publicado en el contexto de seguridad):

- Rol Medico: solo sus citas (vinculo `medico.usuario_id`).
- Recepcion / Administrador: todas las citas.

## Endpoints (/calendario)

- `GET /calendario/citas?inicio=&fin=` : citas visibles en el rango (segun rol).
- `GET /calendario/historial-mascota?mascotaId=` : historial de una mascota.
- `POST /calendario/cambiar-estado` : confirmar, atender, cancelar o finalizar.
- `POST /calendario/reprogramar` : cambia la fecha y hora (valida reglas).
- `GET /calendario/reporte-excel?medicoId=&inicio=&fin=` : reporte .xlsx de las
  citas del medico en el rango (un medico solo genera su propio reporte).

Las fechas usan formato ISO (aaaa-mm-ddThh:mm:ss).

## Nota

La creacion y edicion base de citas se mantiene en el modulo `/cita` existente;
este modulo agrega la vista de calendario, los estados, la reprogramacion, el
historial y el reporte. La creacion de cita ahora valida que el medico este
activo.

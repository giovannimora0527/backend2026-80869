# Flujo de atencion de cita en un solo paso (backend)

Este documento describe el endpoint que permite atender una cita desde el
calendario registrando, en una sola operacion, la anotacion de la atencion y la
formula medica opcional, y marcando la cita como atendida.

## Objetivo

Antes, atender una cita implicaba tres pantallas separadas: marcar la cita como
atendida en el calendario, registrar la anotacion clinica en "Gestion de
Historias" y crear la formula en "Gestion de Formulas". El nuevo flujo reune
estos pasos en una sola accion iniciada por el medico (o el administrador) al
atender la cita.

## Endpoint

`POST /clinica/v1/calendario/atender-cita`

### Cuerpo de la solicitud (AtenderCitaRq)

```json
{
  "citaId": 102,
  "descripcion": "Control general. Paciente estable.",
  "diagnostico": "Dermatitis leve.",
  "observaciones": "Repetir control en 15 dias.",
  "indicaciones": "Administrar con alimento.",
  "medicamentos": [
    {
      "medicamentoId": 3,
      "dosis": "1 tableta",
      "frecuencia": "Cada 12 horas",
      "duracion": "7 dias",
      "cantidad": 14,
      "viaAdministracion": "Oral",
      "indicaciones": "No suspender antes de tiempo."
    }
  ]
}
```

- `citaId`, `descripcion`, `diagnostico` y `observaciones` son obligatorios.
- `indicaciones` y `medicamentos` son opcionales. Si `medicamentos` viene vacio o
  nulo, la atencion solo registra la anotacion y no crea formula.
- El medico que atiende NO se recibe en la solicitud: el servidor lo toma del
  medico asignado a la cita. Asi se garantiza que "quien atendio" sea siempre el
  veterinario de la cita.

### Respuesta

`MiRespuestaRS` con estado 200 y un mensaje que indica si se creo formula o no.

## Comportamiento y reglas

El metodo `CalendarioServiceImpl.atenderCita` es transaccional
(`@Transactional`). Ejecuta, en orden:

1. Validacion de los campos obligatorios de la anotacion.
2. Recuperacion de la cita y validacion de su estado: no se puede atender una
   cita cancelada, ni una que ya este atendida o finalizada.
3. Toma del medico asignado a la cita como autor de la atencion.
4. Registro de la anotacion reutilizando `AnotacionHistoriaService`
   (crea la historia clinica de la mascota si aun no existe).
5. Creacion de la formula medica reutilizando `FormulaNuevaService`, solo si se
   indicaron medicamentos. Crear la formula NO descuenta inventario; el
   descuento ocurre despues en el modulo de despacho.
6. Cambio de estado de la cita a ATENDIDA, unicamente despues de que los pasos
   anteriores se completaron con exito.

Como todo ocurre dentro de la misma transaccion, si cualquier paso falla no se
guarda nada: la cita no queda marcada como atendida sin su anotacion, ni se crea
una formula huerfana.

## Relacion con el despacho

La formula creada por este flujo queda disponible automaticamente como
"pendiente" en el modulo de despacho (no requiere un paso adicional de envio). El
descuento de inventario y la dispensacion los realiza el rol de despacho
(DESPACHO_MEDICO o Administrador) desde la pantalla de Despacho. El medico no
despacha: solo deja la formula lista.

## Seguridad

La ruta `/calendario/**` no esta restringida a un rol especifico en
`JwtAutenticacionFilter`, por lo que cualquier usuario autenticado puede
invocarla. En la practica, el flujo lo usan el Medico y el Administrador, que son
quienes ven y atienden las citas. El control de que solo ellos vean el boton de
atencion se aplica en el frontend.

## Reutilizacion

No se duplico logica: el flujo orquesta servicios ya existentes
(`AnotacionHistoriaService` y `FormulaNuevaService`), de modo que las
validaciones clinicas y de formula se mantienen en un solo lugar.

## Verificacion

- Compilacion del backend correcta:
  `mvn -q -Dmaven.repo.local=/projects/sandbox/.m2repo compile`.
- Prueba funcional recomendada (requiere la base de datos en ejecucion): desde el
  calendario, atender una cita con su anotacion y uno o varios medicamentos; la
  cita debe quedar ATENDIDA, la anotacion aparecer en la historia de la mascota y
  la formula figurar como pendiente en el modulo de despacho.

Nota: el entorno de desarrollo usado para este cambio no cuenta con MySQL en
ejecucion, por lo que la verificacion automatizada se limita a la compilacion.

## Archivos modificados o creados

- `src/main/java/com/uniminuto/clinica/models/AtenderCitaRq.java` (nuevo)
- `src/main/java/com/uniminuto/clinica/service/CalendarioService.java`
- `src/main/java/com/uniminuto/clinica/serviceimpl/CalendarioServiceImpl.java`
- `src/main/java/com/uniminuto/clinica/api/CalendarioApi.java`
- `src/main/java/com/uniminuto/clinica/apicontroller/CalendarioApiController.java`
- `docs/atencion-cita-flujo.md` (este documento)

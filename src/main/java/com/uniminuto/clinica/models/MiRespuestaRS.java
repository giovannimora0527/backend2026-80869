package com.uniminuto.clinica.models;

<<<<<<< HEAD
/**
 * Respuesta estándar de las operaciones de escritura (crear, editar, cancelar...).
 */
public class MiRespuestaRS {

    /** Código HTTP del resultado (200, 201...). */
    private int status;

    /** Mensaje de éxito para mostrar al usuario. */
    private String message;

    /** Identificador del registro creado o modificado; puede ser nulo. */
    private Long id;

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    /**
     * Crea una respuesta con identificador del registro afectado.
     *
     * @param status  código HTTP
     * @param message mensaje para el usuario
     * @param id      identificador del registro creado o modificado
     * @return respuesta construida
     */
    public static MiRespuestaRS of(int status, String message, Long id) {
        MiRespuestaRS rs = new MiRespuestaRS();
        rs.setStatus(status);
        rs.setMessage(message);
        rs.setId(id);
        return rs;
    }

    /**
     * Crea una respuesta sin identificador.
     *
     * @param status  código HTTP
     * @param message mensaje para el usuario
     * @return respuesta construida
     */
    public static MiRespuestaRS of(int status, String message) {
        return of(status, message, null);
    }
=======
import lombok.Data;

@Data
public class MiRespuestaRS {

    /**
     * Status de la peticion.
     */
    private int status;

    /**
     * Mensaje de la peticion.
     */
    private String message;
>>>>>>> 3f64126562f972b8de9fb36bb740dd51a6fcca19
}

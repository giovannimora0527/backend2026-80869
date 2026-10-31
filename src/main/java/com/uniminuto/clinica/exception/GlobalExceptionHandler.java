package com.uniminuto.clinica.exception;

import com.uniminuto.clinica.exception.dto.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Manejador global de errores de la API.
 * <p>
 * Todas las respuestas de error tienen la misma forma ({@link ErrorResponse}):
 * código HTTP correcto (400, 404, 409, 500...), un mensaje entendible y
 * <b>nunca</b> información técnica (trazas, nombres de clases o SQL).
 * Extiende {@link ResponseEntityExceptionHandler} para que también los errores
 * propios de Spring MVC (JSON mal formado, parámetro faltante, método no
 * permitido, ruta inexistente) salgan con este mismo formato.
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /** Registro de eventos; el detalle técnico queda solo en el log del servidor. */
    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Maneja datos de entrada inválidos (HTTP 400).
     *
     * @param ex excepción lanzada por las validaciones de negocio.
     * @return respuesta de error con código 400.
     */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(BadRequestException ex) {
        return construir(ex.getStatus(), ex.getMessage());
    }

    /**
     * Maneja recursos inexistentes (HTTP 404).
     *
     * @param ex excepción lanzada cuando un registro no existe.
     * @return respuesta de error con código 404.
     */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NotFoundException ex) {
        return construir(ex.getStatus(), ex.getMessage());
    }

    /**
     * Maneja conflictos con el estado de los datos (HTTP 409).
     *
     * @param ex excepción lanzada por reglas como duplicados o traslapes.
     * @return respuesta de error con código 409.
     */
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflict(ConflictException ex) {
        return construir(ex.getStatus(), ex.getMessage());
    }

    /**
     * Red de seguridad: si la base de datos rechaza un dato (por ejemplo un
     * duplicado que se coló entre dos peticiones simultáneas) se responde 409
     * con un mensaje claro, sin exponer el SQL.
     *
     * @param ex excepción de integridad lanzada por JPA/Hibernate.
     * @return respuesta de error con código 409.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleIntegridad(DataIntegrityViolationException ex) {
        LOG.warn("Violación de integridad de datos: {}", ex.getMostSpecificCause().getMessage());
        return construir(HttpStatus.CONFLICT,
                "La operación no se pudo completar porque los datos están duplicados o relacionados con otros registros.");
    }

    /**
     * Último recurso para errores no previstos (HTTP 500). El detalle se guarda
     * en el log y al usuario solo se le muestra un mensaje general.
     *
     * @param ex cualquier excepción no manejada antes.
     * @return respuesta de error con código 500.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        LOG.error("Error inesperado en la API", ex);
        return construir(HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocurrió un error inesperado. Intenta de nuevo más tarde.");
    }

    /**
     * Mensaje específico cuando falta un parámetro obligatorio en la URL.
     */
    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {
        return construirObjeto(HttpStatus.BAD_REQUEST.value(),
                "Falta el dato obligatorio '" + ex.getParameterName() + "'.");
    }

    /**
     * Unifica el formato de los errores propios de Spring MVC (JSON mal
     * formado, tipo de dato incorrecto, método no permitido, ruta inexistente).
     */
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body,
            HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
        return construirObjeto(statusCode.value(), mensajePorEstado(statusCode.value()));
    }

    /**
     * Traduce un código HTTP a un mensaje entendible para el usuario.
     *
     * @param codigo código HTTP.
     * @return mensaje en español sin detalles técnicos.
     */
    private String mensajePorEstado(int codigo) {
        return switch (codigo) {
            case 400 -> "La solicitud no es válida. Revisa los datos enviados "
                    + "(las fechas deben ir como AAAA-MM-DD y los números sin letras).";
            case 404 -> "El recurso solicitado no existe.";
            case 405 -> "Esa operación no está permitida para esta ruta.";
            case 406, 415 -> "El formato de la solicitud no es compatible.";
            default -> "No fue posible procesar la solicitud.";
        };
    }

    private ResponseEntity<ErrorResponse> construir(HttpStatus estado, String mensaje) {
        return ResponseEntity.status(estado)
                .body(new ErrorResponse(estado.value(), etiqueta(estado.value()), mensaje));
    }

    private ResponseEntity<Object> construirObjeto(int codigo, String mensaje) {
        return ResponseEntity.status(codigo)
                .body(new ErrorResponse(codigo, etiqueta(codigo), mensaje));
    }

    private String etiqueta(int codigo) {
        HttpStatus estado = HttpStatus.resolve(codigo);
        return estado == null ? "ERROR" : estado.getReasonPhrase().toUpperCase();
    }
}

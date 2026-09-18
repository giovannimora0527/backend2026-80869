package com.uniminuto.clinica.apicontroller;

// Importamos la API que vamos a implementar
import com.uniminuto.clinica.api.FormulaMedicaApi;
// Importamos la entidad que vamos a devolver
import com.uniminuto.clinica.entity.FormulaMedica;
// Importamos el servicio que contiene la lógica de negocio
import com.uniminuto.clinica.service.FormulaMedicaService;
// @Autowired para inyección de dependencias (aunque usaremos constructor)
import org.springframework.beans.factory.annotation.Autowired;
// ResponseEntity para devolver respuestas HTTP estandarizadas
import org.springframework.http.ResponseEntity;
// @RestController marca esta clase como controlador REST
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
 * Controlador que implementa los endpoints de {@link FormulaMedicaApi}.
 * Delega la logica de negocio a la capa de servicio.
 *
 * ¿Qué significa @RestController?
 * - Combina @Controller y @ResponseBody
 * - Le dice a Spring: "Esta clase es un controlador web"
 * - Spring automáticamente convierte los objetos a JSON
 * - No necesitas poner @ResponseBody en cada método
 */
@RestController
public class FormulaMedicaApiController implements FormulaMedicaApi {

    /**
     * Servicio de formulas medicas inyectado.
     *
     * ¿Por qué usamos 'private final' en lugar de solo @Autowired?
     * - 'final' garantiza que el servicio nunca sea null
     * - Facilita el testing (puedes pasar un mock en el constructor)
     * - Es una buena práctica de inmutabilidad
     */
    @Autowired
    private FormulaMedicaService formulaMedicaService;

    /**
     * {@inheritDoc}
     *
     * ¿Qué hace este método?
     * - Recibe una petición GET a /api/formulas-medicas
     * - Delega la consulta al servicio (capa de negocio)
     * - Devuelve ResponseEntity.ok() con la lista (HTTP 200 OK)
     *
     * ¿Por qué no devolvemos List<FormulaMedica> directamente?
     * - Porque ResponseEntity nos da control total sobre la respuesta
     * - Podemos cambiar el status code si es necesario (404, 500, etc.)
     * - Podemos agregar headers personalizados si lo necesitamos
     */
    @Override
    public ResponseEntity<List<FormulaMedica>> obtenerTodasOrdenadas() {
        // Delegamos la consulta al servicio
        List<FormulaMedica> respuesta = formulaMedicaService.obtenerTodasOrdenadas();
        // Devolvemos HTTP 200 OK con la lista en el body
        return ResponseEntity.ok(respuesta);
    }
}
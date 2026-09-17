package com.uniminuto.clinica.api;

// Importamos ResponseEntity para devolver respuestas HTTP estandarizadas
import com.uniminuto.clinica.entity.FormulaMedica;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;

/**
 * Contrato de la API REST para el recurso de Formulas Medicas.
 *
 * ¿Qué significan estas anotaciones?
 * - @RequestMapping("/api/formulas-medicas"): Define la ruta base del recurso
 * - @CrossOrigin(origins="*"): Permite peticiones desde cualquier origen (CORS)
 *   Esto es necesario para que el frontend pueda consumir la API desde otro dominio
 */
@RequestMapping("/api/formulas-medicas")
@CrossOrigin(origins = "*")  // ← Permite CORS (Cross-Origin Resource Sharing)
public interface FormulaMedicaApi {

    /**
     * Endpoint GET para listar todas las formulas medicas en orden descendente por fecha.
     *
     * ¿Por qué no especificamos parámetros?
     * - Porque es un GET simple que no requiere filtros
     * - Spring automáticamente mapea esto a GET /api/formulas-medicas
     *
     * ¿Qué devuelve ResponseEntity?
     * - Es una clase de Spring que encapsula la respuesta HTTP completa
     * - Incluye: status code (200, 404, 500...), headers y body
     * - Es más flexible que devolver solo el objeto
     *
     * @return {@link ResponseEntity} con la lista de formulas medicas y estado HTTP 200 OK.
     */
    @GetMapping  // ← Mapea peticiones GET a este método
    ResponseEntity<List<FormulaMedica>> obtenerTodasOrdenadas();
}
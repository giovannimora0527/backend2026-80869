package com.uniminuto.clinica.api;

// Importamos ResponseEntity para devolver respuestas HTTP estandarizadas
import com.uniminuto.clinica.entity.FormulaMedica;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;


 */
@RequestMapping("/api/formulas-medicas")
@CrossOrigin(origins = "*")  // ← Permite CORS (Cross-Origin Resource Sharing)
public interface FormulaMedicaApi {



     * @return {@link ResponseEntity} con la lista de formulas medicas y estado HTTP 200 OK.
     */
    @GetMapping  // ← Mapea peticiones GET a este método
    ResponseEntity<List<FormulaMedica>> obtenerTodasOrdenadas();
}
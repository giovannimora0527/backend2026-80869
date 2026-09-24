package main.java.com.uniminuto.clinica.api;

import com.uniminuto.clinica.models.Medico;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * Interfaz para la exposición del catálogo de médicos.
 */
@RequestMapping("/api/medicos")
public interface MedicoApi {

    @GetMapping
    ResponseEntity<List<Medico>> listarMedicos();
}
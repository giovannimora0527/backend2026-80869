package com.uniminuto.clinica.api;

import com.uniminuto.clinica.entity.Medico;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/medico")
public interface MedicoApi {
    /**
     * Lista todos los médicos registrados en el sistema, incluyendo sus especializaciones.
     * Este listado se resuelve de forma directa retornando la relación existente en la base de datos.
     *
     * @return lista de médicos con su respectiva especialización anidada.
     * @throws BadRequestException si ocurre algún error de validación durante la consulta.
     */
    @GetMapping(value = "/especializaciones",
            produces = {"application/json"})
    ResponseEntity<List<Medico>> listarMedicosConEspecializaciones()
            throws BadRequestException;
}

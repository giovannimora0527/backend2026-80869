package com.uniminuto.clinica.api;

import com.uniminuto.clinica.entity.Medicamento;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/medicamento")
public interface MedicamentoApi {
    /**
     * Lista todos los medicamentos existentes en el inventario.
     * Los medicamentos son retornados ordenados por su fecha de creación de manera descendente (los más recientes primero).
     *
     * @return lista de medicamentos en orden cronológico descendente.
     * @throws BadRequestException si ocurre algún error de validación.
     */
    @GetMapping(value = "/inventario",
            produces = {"application/json"})
    ResponseEntity<List<Medicamento>> listarInventario()
            throws BadRequestException;
}

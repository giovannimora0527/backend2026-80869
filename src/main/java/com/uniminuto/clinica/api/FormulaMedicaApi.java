package com.uniminuto.clinica.api;

import com.uniminuto.clinica.entity.FormulaMedica;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/formula-medica")
public interface FormulaMedicaApi {
    /**
     * Lista todas las fórmulas médicas ordenadas por su fecha de creación (de la más reciente a la más antigua).
     * Este endpoint está diseñado para visualizar el inventario histórico.
     *
     * @return lista de fórmulas médicas ordenadas descendentemente.
     * @throws BadRequestException si ocurre algún error de validación.
     */
    @GetMapping(value = "/inventario",
            produces = {"application/json"})
    ResponseEntity<List<FormulaMedica>> listarInventario()
            throws BadRequestException;
}

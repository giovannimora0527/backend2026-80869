
package com.uniminuto.clinica.api;

import com.uniminuto.clinica.entity.FormulaMedica;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.FormulaMedicaRq;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@CrossOrigin(
        origins = "http://localhost:4200",
        allowedHeaders = "*",
        methods = {
                RequestMethod.GET,
                RequestMethod.POST,
                RequestMethod.PUT,
                RequestMethod.OPTIONS
        }
)
@RequestMapping("/formula-medica")
public interface FormulaMedicaApi {

    @GetMapping(
            value = "/listar",
            produces = {"application/json"}
    )
    ResponseEntity<List<FormulaMedica>> listarFormulas()
            throws BadRequestException;

    @GetMapping(
            value = "/consultar/{id}",
            produces = {"application/json"}
    )
    ResponseEntity<FormulaMedica> consultarFormula(
            @PathVariable("id") Long id
    ) throws BadRequestException;

    @PostMapping(
            value = "/crear",
            consumes = {"application/json"},
            produces = {"application/json"}
    )
    ResponseEntity<FormulaMedica> crearFormula(
            @RequestBody FormulaMedicaRq formulaRq
    ) throws BadRequestException;

    @PutMapping(
            value = "/actualizar/{id}",
            consumes = {"application/json"},
            produces = {"application/json"}
    )
    ResponseEntity<FormulaMedica> actualizarFormula(
            @PathVariable("id") Long id,
            @RequestBody FormulaMedicaRq formulaRq
    ) throws BadRequestException;
}

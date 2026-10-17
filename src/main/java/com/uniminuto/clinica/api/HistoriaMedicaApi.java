
package com.uniminuto.clinica.api;

import com.uniminuto.clinica.entity.HistoriaMedica;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.HistoriaMedicaRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/historia-medica")
public interface HistoriaMedicaApi {

    @PostMapping(
            value = "/crear",
            produces = {"application/json"},
            consumes = {"application/json"}
    )
    ResponseEntity<MiRespuestaRS> crearHistoriaMedica(
            @RequestBody HistoriaMedicaRq rq
    ) throws BadRequestException;

    @GetMapping(value = "/listar", produces = {"application/json"})
    ResponseEntity<List<HistoriaMedica>> listarHistoriasMedicas();
}

package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.entity.Medico;
import com.uniminuto.clinica.repository.MedicoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/medico")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class MedicoApiController {

    private final MedicoRepository medicoRepository;

    public MedicoApiController(MedicoRepository medicoRepository) {
        this.medicoRepository = medicoRepository;
    }

    @GetMapping("/listar")
    public ResponseEntity<List<Medico>> listarMedicos() {
        List<Medico> medicos = medicoRepository.findAll();
        return ResponseEntity.ok(medicos);
    }
}
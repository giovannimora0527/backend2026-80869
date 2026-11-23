package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.serviceimpl.InventarioImportacionService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/medicamento/importacion")
public class InventarioImportacionApiController {

    private final InventarioImportacionService importacionService;

    public InventarioImportacionApiController(InventarioImportacionService importacionService) {
        this.importacionService = importacionService;
    }

    @PostMapping(value = "/vista-previa", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> vistaPrevia(@RequestParam("archivo") MultipartFile archivo) {
        try {
            return ResponseEntity.ok(importacionService.revisar(archivo));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
        }
    }
}
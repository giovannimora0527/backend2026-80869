package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.serviceimpl.InventarioConfirmacionService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/medicamento/importacion")
public class InventarioConfirmacionApiController {

    private final InventarioConfirmacionService confirmacionService;

    public InventarioConfirmacionApiController(
            InventarioConfirmacionService confirmacionService
    ) {
        this.confirmacionService = confirmacionService;
    }

    @PostMapping(value = "/confirmar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> confirmar(
            @RequestHeader(value = "Authorization", required = false) String autorizacion,
            @RequestParam("archivo") MultipartFile archivo
    ) {
        try {
            return ResponseEntity.ok(confirmacionService.confirmar(autorizacion, archivo));
        } catch (ResponseStatusException ex) {
            String mensaje = ex.getReason() == null ? "No se pudo confirmar." : ex.getReason();
            return ResponseEntity.status(ex.getStatusCode()).body(Map.of("message", mensaje));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
        }
    }
}
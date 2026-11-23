package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.serviceimpl.ReporteGestionMedicaServiceImpl;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/reportes")
public class ReporteGestionMedicaApiController {

    private final ReporteGestionMedicaServiceImpl reportes;

    public ReporteGestionMedicaApiController(ReporteGestionMedicaServiceImpl reportes) {
        this.reportes = reportes;
    }

    @GetMapping("/gestion-medica")
    public ReporteGestionMedicaServiceImpl.Reporte consultar(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false)
            String autorizacion,
            @RequestParam("desde")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam("hasta")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta
    ) {
        return reportes.consultar(autorizacion, desde, hasta);
    }
}
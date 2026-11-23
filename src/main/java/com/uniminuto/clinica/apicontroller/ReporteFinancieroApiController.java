package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.serviceimpl.ReporteFinancieroServiceImpl;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/reportes")
public class ReporteFinancieroApiController {

    private final ReporteFinancieroServiceImpl reportes;

    public ReporteFinancieroApiController(ReporteFinancieroServiceImpl reportes) {
        this.reportes = reportes;
    }

    @GetMapping("/financieros/ingresos")
    public List<ReporteFinancieroServiceImpl.IngresoDiario> consultarIngresos(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false)
            String autorizacion,
            @RequestParam("desde")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam("hasta")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta
    ) {
        return reportes.consultarIngresos(autorizacion, desde, hasta);
    }
}
package com.uniminuto.clinica.api;

import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.ReporteRs;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

/**
 * Endpoints del modulo de reportes administrativos y contables.
 *
 * El acceso esta restringido en el filtro de seguridad a los perfiles
 * Administrador, Contable y JEFE_BODEGA. Cada reporte se identifica por su
 * categoria ("financiero", "inventario" o "gestion") y su tipo, y recibe un
 * rango de fechas (desde, hasta).
 *
 * Se ofrecen tres operaciones por reporte: vista previa en JSON, exportacion a
 * PDF y exportacion a Excel.
 */
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/reporte")
public interface ReporteApi {

    @GetMapping(value = "/generar", produces = {"application/json"})
    ResponseEntity<ReporteRs> generar(
            @RequestParam("categoria") String categoria,
            @RequestParam("tipo") String tipo,
            @RequestParam("desde") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam("hasta") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta)
            throws BadRequestException;

    @GetMapping(value = "/pdf", produces = {"application/pdf"})
    ResponseEntity<byte[]> pdf(
            @RequestParam("categoria") String categoria,
            @RequestParam("tipo") String tipo,
            @RequestParam("desde") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam("hasta") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta)
            throws BadRequestException;

    @GetMapping(value = "/excel")
    ResponseEntity<byte[]> excel(
            @RequestParam("categoria") String categoria,
            @RequestParam("tipo") String tipo,
            @RequestParam("desde") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam("hasta") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta)
            throws BadRequestException;
}

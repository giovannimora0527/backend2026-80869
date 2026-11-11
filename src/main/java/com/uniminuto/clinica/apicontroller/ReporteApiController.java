package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.api.ReporteApi;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.ReporteRs;
import com.uniminuto.clinica.service.ReporteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

/**
 * Controlador del modulo de reportes. Delega la construccion de cada reporte y
 * su exportacion en {@link ReporteService}. Los documentos (PDF y Excel) se
 * devuelven como contenido binario con las cabeceras adecuadas para su
 * descarga.
 */
@RestController
public class ReporteApiController implements ReporteApi {

    @Autowired
    private ReporteService reporteService;

    @Override
    public ResponseEntity<ReporteRs> generar(String categoria, String tipo,
                                             LocalDate desde, LocalDate hasta)
            throws BadRequestException {
        return ResponseEntity.ok(this.reporteService.generar(categoria, tipo, desde, hasta));
    }

    @Override
    public ResponseEntity<byte[]> pdf(String categoria, String tipo,
                                      LocalDate desde, LocalDate hasta)
            throws BadRequestException {
        byte[] contenido = this.reporteService.exportarPdf(categoria, tipo, desde, hasta);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("inline",
                "reporte-" + categoria + "-" + tipo + ".pdf");
        return ResponseEntity.ok().headers(headers).body(contenido);
    }

    @Override
    public ResponseEntity<byte[]> excel(String categoria, String tipo,
                                        LocalDate desde, LocalDate hasta)
            throws BadRequestException {
        byte[] contenido = this.reporteService.exportarExcel(categoria, tipo, desde, hasta);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment",
                "reporte-" + categoria + "-" + tipo + ".xlsx");
        return ResponseEntity.ok().headers(headers).body(contenido);
    }
}

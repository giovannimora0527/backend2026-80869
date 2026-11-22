package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.api.CalendarioApi;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.AtenderCitaRq;
import com.uniminuto.clinica.models.CambiarEstadoCitaRq;
import com.uniminuto.clinica.models.CitaRs;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.models.ReprogramarCitaRq;
import com.uniminuto.clinica.service.CalendarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
public class CalendarioApiController implements CalendarioApi {

    @Autowired
    private CalendarioService calendarioService;

    @Override
    public ResponseEntity<List<CitaRs>> listarCitas(LocalDateTime inicio, LocalDateTime fin)
            throws BadRequestException {
        return ResponseEntity.ok(this.calendarioService.listarCitasCalendario(inicio, fin));
    }

    @Override
    public ResponseEntity<List<CitaRs>> historialMascota(Integer mascotaId)
            throws BadRequestException {
        return ResponseEntity.ok(this.calendarioService.historialPorMascota(mascotaId));
    }

    @Override
    public ResponseEntity<MiRespuestaRS> cambiarEstado(CambiarEstadoCitaRq rq)
            throws BadRequestException {
        return ResponseEntity.ok(this.calendarioService.cambiarEstado(rq));
    }

    @Override
    public ResponseEntity<MiRespuestaRS> atenderCita(AtenderCitaRq rq)
            throws BadRequestException {
        return ResponseEntity.ok(this.calendarioService.atenderCita(rq));
    }

    @Override
    public ResponseEntity<MiRespuestaRS> reprogramar(ReprogramarCitaRq rq)
            throws BadRequestException {
        return ResponseEntity.ok(this.calendarioService.reprogramar(rq));
    }

    @Override
    public ResponseEntity<byte[]> reporteExcel(Long medicoId, LocalDateTime inicio, LocalDateTime fin)
            throws BadRequestException {
        byte[] contenido = this.calendarioService.generarReporteExcel(medicoId, inicio, fin);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "reporte-citas.xlsx");
        return ResponseEntity.ok().headers(headers).body(contenido);
    }
}

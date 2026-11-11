package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.api.FormulaApi;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.FormulaNuevaRq;
import com.uniminuto.clinica.models.FormulaNuevaRs;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.service.FormulaNuevaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class FormulaApiController implements FormulaApi {

    @Autowired
    private FormulaNuevaService formulaNuevaService;

    @Override
    public ResponseEntity<List<FormulaNuevaRs>> listar() throws BadRequestException {
        return ResponseEntity.ok(this.formulaNuevaService.listar());
    }

    @Override
    public ResponseEntity<FormulaNuevaRs> obtener(Long id) throws BadRequestException {
        return ResponseEntity.ok(this.formulaNuevaService.obtener(id));
    }

    @Override
    public ResponseEntity<MiRespuestaRS> guardar(FormulaNuevaRq formulaRq) throws BadRequestException {
        return ResponseEntity.ok(this.formulaNuevaService.crear(formulaRq));
    }

    @Override
    public ResponseEntity<byte[]> pdf(Long id) throws BadRequestException {
        byte[] contenido = this.formulaNuevaService.generarPdf(id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("inline", "formula-" + id + ".pdf");
        return ResponseEntity.ok().headers(headers).body(contenido);
    }
}

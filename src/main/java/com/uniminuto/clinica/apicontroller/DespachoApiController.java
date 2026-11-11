package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.api.DespachoApi;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.DespachoRq;
import com.uniminuto.clinica.models.DespachoRs;
import com.uniminuto.clinica.models.FormulaPendienteRs;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.service.DespachoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class DespachoApiController implements DespachoApi {

    @Autowired
    private DespachoService despachoService;

    @Override
    public ResponseEntity<List<FormulaPendienteRs>> formulasPendientes() throws BadRequestException {
        return ResponseEntity.ok(this.despachoService.listarFormulasPendientes());
    }

    @Override
    public ResponseEntity<FormulaPendienteRs> obtenerFormula(Long formulaId) throws BadRequestException {
        return ResponseEntity.ok(this.despachoService.obtenerFormulaParaDespacho(formulaId));
    }

    @Override
    public ResponseEntity<MiRespuestaRS> confirmar(DespachoRq despachoRq) throws BadRequestException {
        return ResponseEntity.ok(this.despachoService.confirmarDespacho(despachoRq));
    }

    @Override
    public ResponseEntity<List<DespachoRs>> listar() throws BadRequestException {
        return ResponseEntity.ok(this.despachoService.listarDespachos());
    }

    @Override
    public ResponseEntity<byte[]> comprobante(Long id) throws BadRequestException {
        byte[] contenido = this.despachoService.generarComprobantePdf(id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("inline", "comprobante-despacho-" + id + ".pdf");
        return ResponseEntity.ok().headers(headers).body(contenido);
    }
}

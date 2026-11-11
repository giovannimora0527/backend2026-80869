package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.api.FacturaApi;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.FacturaRq;
import com.uniminuto.clinica.models.FacturaRs;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.models.PagoRq;
import com.uniminuto.clinica.service.FacturaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class FacturaApiController implements FacturaApi {

    @Autowired
    private FacturaService facturaService;

    @Override
    public ResponseEntity<List<FacturaRs>> listar() throws BadRequestException {
        return ResponseEntity.ok(this.facturaService.listar());
    }

    @Override
    public ResponseEntity<List<FacturaRs>> listarPorCliente(Long clienteId) throws BadRequestException {
        return ResponseEntity.ok(this.facturaService.listarPorCliente(clienteId));
    }

    @Override
    public ResponseEntity<FacturaRs> obtener(Long id) throws BadRequestException {
        return ResponseEntity.ok(this.facturaService.obtener(id));
    }

    @Override
    public ResponseEntity<MiRespuestaRS> generar(FacturaRq facturaRq) throws BadRequestException {
        return ResponseEntity.ok(this.facturaService.generar(facturaRq));
    }

    @Override
    public ResponseEntity<MiRespuestaRS> registrarPago(PagoRq pagoRq) throws BadRequestException {
        return ResponseEntity.ok(this.facturaService.registrarPago(pagoRq));
    }

    @Override
    public ResponseEntity<MiRespuestaRS> anular(Long id) throws BadRequestException {
        return ResponseEntity.ok(this.facturaService.anular(id));
    }

    @Override
    public ResponseEntity<byte[]> pdf(Long id) throws BadRequestException {
        byte[] contenido = this.facturaService.generarPdf(id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("inline", "factura-" + id + ".pdf");
        return ResponseEntity.ok().headers(headers).body(contenido);
    }
}

package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.api.InventarioApi;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.AgregarLoteRq;
import com.uniminuto.clinica.models.CargaMasivaRs;
import com.uniminuto.clinica.models.LoteRq;
import com.uniminuto.clinica.models.MedicamentoRq;
import com.uniminuto.clinica.models.MedicamentoRs;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.models.MovimientoInventarioRs;
import com.uniminuto.clinica.service.CargaMasivaService;
import com.uniminuto.clinica.service.InventarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
public class InventarioApiController implements InventarioApi {

    @Autowired
    private InventarioService inventarioService;

    @Autowired
    private CargaMasivaService cargaMasivaService;

    @Override
    public ResponseEntity<List<MedicamentoRs>> listar() throws BadRequestException {
        return ResponseEntity.ok(this.inventarioService.listarMedicamentos());
    }

    @Override
    public ResponseEntity<MedicamentoRs> obtener(Long id) throws BadRequestException {
        return ResponseEntity.ok(this.inventarioService.obtenerMedicamento(id));
    }

    @Override
    public ResponseEntity<MiRespuestaRS> guardar(MedicamentoRq medicamentoRq) throws BadRequestException {
        return ResponseEntity.ok(this.inventarioService.crearMedicamento(medicamentoRq));
    }

    @Override
    public ResponseEntity<MiRespuestaRS> actualizar(MedicamentoRq medicamentoRq) throws BadRequestException {
        return ResponseEntity.ok(this.inventarioService.actualizarMedicamento(medicamentoRq));
    }

    @Override
    public ResponseEntity<List<MedicamentoRs>> agotados() throws BadRequestException {
        return ResponseEntity.ok(this.inventarioService.listarAgotados());
    }

    @Override
    public ResponseEntity<List<MedicamentoRs>> stockBajo() throws BadRequestException {
        return ResponseEntity.ok(this.inventarioService.listarStockBajo());
    }

    @Override
    public ResponseEntity<List<MedicamentoRs>> proximosAVencer() throws BadRequestException {
        return ResponseEntity.ok(this.inventarioService.listarProximosAVencer());
    }

    @Override
    public ResponseEntity<List<MedicamentoRs>> vencidos() throws BadRequestException {
        return ResponseEntity.ok(this.inventarioService.listarVencidos());
    }

    @Override
    public ResponseEntity<List<MovimientoInventarioRs>> movimientos(Long medicamentoId)
            throws BadRequestException {
        return ResponseEntity.ok(this.inventarioService.listarMovimientos(medicamentoId));
    }

    @Override
    public ResponseEntity<MiRespuestaRS> agregarLote(AgregarLoteRq agregarLoteRq)
            throws BadRequestException {
        if (agregarLoteRq == null || agregarLoteRq.getMedicamentoId() == null) {
            throw new BadRequestException("El identificador del medicamento es obligatorio.");
        }
        LoteRq loteRq = new LoteRq();
        loteRq.setNumeroLote(agregarLoteRq.getNumeroLote());
        loteRq.setCantidad(agregarLoteRq.getCantidad());
        loteRq.setPrecioCompra(agregarLoteRq.getPrecioCompra());
        loteRq.setFechaIngreso(agregarLoteRq.getFechaIngreso());
        loteRq.setFechaVencimiento(agregarLoteRq.getFechaVencimiento());
        return ResponseEntity.ok(
                this.inventarioService.agregarLote(agregarLoteRq.getMedicamentoId(), loteRq));
    }

    @Override
    public ResponseEntity<CargaMasivaRs> previsualizarCarga(MultipartFile archivo)
            throws BadRequestException {
        return ResponseEntity.ok(this.cargaMasivaService.previsualizar(archivo));
    }

    @Override
    public ResponseEntity<CargaMasivaRs> confirmarCarga(MultipartFile archivo)
            throws BadRequestException {
        return ResponseEntity.ok(this.cargaMasivaService.confirmar(archivo));
    }
}

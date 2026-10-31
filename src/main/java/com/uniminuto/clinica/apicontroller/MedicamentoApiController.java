package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.api.MedicamentoApi;
import com.uniminuto.clinica.entity.Medicamento;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.EstadoRq;
import com.uniminuto.clinica.models.MedicamentoRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.service.MedicamentoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controlador REST de medicamentos. Solo recibe la petición y delega en
 * {@link MedicamentoService}; no contiene reglas de negocio.
 */
@RestController
public class MedicamentoApiController implements MedicamentoApi {

    private final MedicamentoService medicamentoService;

    /**
     * Crea el controlador con su servicio.
     *
     * @param medicamentoService servicio de medicamentos.
     */
    public MedicamentoApiController(MedicamentoService medicamentoService) {
        this.medicamentoService = medicamentoService;
    }

    @Override
    public ResponseEntity<List<Medicamento>> listarMedicamentos() throws BadRequestException {
        return ResponseEntity.ok(medicamentoService.listarMedicamentos());
    }

    @Override
    public ResponseEntity<List<Medicamento>> listarMedicamentosActivos() {
        return ResponseEntity.ok(medicamentoService.listarMedicamentosActivos());
    }

    @Override
    public ResponseEntity<Medicamento> buscarMedicamentoPorId(Long id) {
        return ResponseEntity.ok(medicamentoService.buscarMedicamentoPorId(id));
    }

    @Override
    public ResponseEntity<MiRespuestaRS> crearMedicamento(MedicamentoRq medicamentoRq) {
        return ResponseEntity.status(HttpStatus.CREATED).body(medicamentoService.crearMedicamento(medicamentoRq));
    }

    @Override
    public ResponseEntity<MiRespuestaRS> actualizarMedicamento(Long id, MedicamentoRq medicamentoRq) {
        return ResponseEntity.ok(medicamentoService.actualizarMedicamento(id, medicamentoRq));
    }

    @Override
    public ResponseEntity<MiRespuestaRS> cambiarEstadoMedicamento(Long id, EstadoRq estadoRq) {
        Boolean activo = estadoRq == null ? null : estadoRq.getActivo();
        return ResponseEntity.ok(medicamentoService.cambiarEstadoMedicamento(id, activo));
    }
}

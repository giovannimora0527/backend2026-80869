package com.uniminuto.clinica.api;

import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.AgregarLoteRq;
import com.uniminuto.clinica.models.CargaMasivaRs;
import com.uniminuto.clinica.models.MedicamentoRq;
import com.uniminuto.clinica.models.MedicamentoRs;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.models.MovimientoInventarioRs;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Endpoints del modulo de inventario.
 */
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/inventario")
public interface InventarioApi {

    @GetMapping(value = "/listar", produces = {"application/json"})
    ResponseEntity<List<MedicamentoRs>> listar() throws BadRequestException;

    @GetMapping(value = "/obtener", produces = {"application/json"})
    ResponseEntity<MedicamentoRs> obtener(@RequestParam("id") Long id) throws BadRequestException;

    @PostMapping(value = "/guardar", produces = {"application/json"}, consumes = {"application/json"})
    ResponseEntity<MiRespuestaRS> guardar(@RequestBody MedicamentoRq medicamentoRq)
            throws BadRequestException;

    @PostMapping(value = "/actualizar", produces = {"application/json"}, consumes = {"application/json"})
    ResponseEntity<MiRespuestaRS> actualizar(@RequestBody MedicamentoRq medicamentoRq)
            throws BadRequestException;

    @GetMapping(value = "/agotados", produces = {"application/json"})
    ResponseEntity<List<MedicamentoRs>> agotados() throws BadRequestException;

    @GetMapping(value = "/stock-bajo", produces = {"application/json"})
    ResponseEntity<List<MedicamentoRs>> stockBajo() throws BadRequestException;

    @GetMapping(value = "/proximos-vencer", produces = {"application/json"})
    ResponseEntity<List<MedicamentoRs>> proximosAVencer() throws BadRequestException;

    @GetMapping(value = "/vencidos", produces = {"application/json"})
    ResponseEntity<List<MedicamentoRs>> vencidos() throws BadRequestException;

    @GetMapping(value = "/movimientos", produces = {"application/json"})
    ResponseEntity<List<MovimientoInventarioRs>> movimientos(
            @RequestParam("medicamentoId") Long medicamentoId) throws BadRequestException;

    @PostMapping(value = "/agregar-lote", produces = {"application/json"}, consumes = {"application/json"})
    ResponseEntity<MiRespuestaRS> agregarLote(@RequestBody AgregarLoteRq agregarLoteRq)
            throws BadRequestException;

    @PostMapping(value = "/carga-masiva/previsualizar", produces = {"application/json"})
    ResponseEntity<CargaMasivaRs> previsualizarCarga(
            @RequestParam("archivo") MultipartFile archivo) throws BadRequestException;

    @PostMapping(value = "/carga-masiva/confirmar", produces = {"application/json"})
    ResponseEntity<CargaMasivaRs> confirmarCarga(
            @RequestParam("archivo") MultipartFile archivo) throws BadRequestException;
}

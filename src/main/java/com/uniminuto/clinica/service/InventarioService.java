package com.uniminuto.clinica.service;

import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.LoteRq;
import com.uniminuto.clinica.models.MedicamentoRq;
import com.uniminuto.clinica.models.MedicamentoRs;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.models.MovimientoInventarioRs;

import java.util.List;

/**
 * Servicio de gestion del inventario de medicamentos y productos.
 */
public interface InventarioService {

    /** Lista todos los medicamentos con su existencia e indicadores de estado. */
    List<MedicamentoRs> listarMedicamentos() throws BadRequestException;

    /** Obtiene el detalle de un medicamento, incluyendo sus lotes. */
    MedicamentoRs obtenerMedicamento(Long id) throws BadRequestException;

    /** Crea un nuevo medicamento (y sus lotes iniciales si se envian). */
    MiRespuestaRS crearMedicamento(MedicamentoRq medicamentoRq) throws BadRequestException;

    /** Actualiza los datos de catalogo de un medicamento. */
    MiRespuestaRS actualizarMedicamento(MedicamentoRq medicamentoRq) throws BadRequestException;

    /**
     * Agrega un lote a un medicamento existente (reabastecimiento) y registra
     * el movimiento de entrada correspondiente.
     */
    MiRespuestaRS agregarLote(Long medicamentoId, LoteRq loteRq) throws BadRequestException;

    /** Lista los medicamentos cuya existencia es cero (agotados). */
    List<MedicamentoRs> listarAgotados() throws BadRequestException;

    /** Lista los medicamentos con existencia por debajo o igual al stock minimo. */
    List<MedicamentoRs> listarStockBajo() throws BadRequestException;

    /** Lista los medicamentos con al menos un lote proximo a vencer. */
    List<MedicamentoRs> listarProximosAVencer() throws BadRequestException;

    /** Lista los medicamentos con al menos un lote vencido. */
    List<MedicamentoRs> listarVencidos() throws BadRequestException;

    /** Historial de movimientos de inventario de un medicamento. */
    List<MovimientoInventarioRs> listarMovimientos(Long medicamentoId) throws BadRequestException;
}

package com.uniminuto.clinica.service;

import com.uniminuto.clinica.entity.Medicamento;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.MedicamentoRq;
import com.uniminuto.clinica.models.MiRespuestaRS;

import java.util.List;

/**
 * Reglas de negocio de la gestión de medicamentos.
 */
public interface MedicamentoService {

    /**
     * Lista todos los medicamentos (activos e inactivos) ordenados por nombre.
     *
     * @return lista de medicamentos.
     */
    List<Medicamento> listarMedicamentos() throws BadRequestException;

    /**
     * Lista solo los medicamentos activos; se usa al emitir fórmulas.
     *
     * @return lista de medicamentos activos.
     */
    List<Medicamento> listarMedicamentosActivos();

    /**
     * Busca un medicamento por su identificador.
     *
     * @param id identificador del medicamento.
     * @return el medicamento encontrado.
     */
    Medicamento buscarMedicamentoPorId(Long id);

    /**
     * Crea un medicamento nuevo (queda activo).
     *
     * @param rq datos del medicamento.
     * @return mensaje de confirmación.
     */
    MiRespuestaRS crearMedicamento(MedicamentoRq rq);

    /**
     * Actualiza los datos de un medicamento existente.
     *
     * @param id identificador del medicamento.
     * @param rq nuevos datos.
     * @return mensaje de confirmación.
     */
    MiRespuestaRS actualizarMedicamento(Long id, MedicamentoRq rq);

    /**
     * Activa o desactiva un medicamento (borrado lógico).
     *
     * @param id     identificador del medicamento.
     * @param activo estado deseado.
     * @return mensaje de confirmación.
     */
    MiRespuestaRS cambiarEstadoMedicamento(Long id, Boolean activo);
}

package com.uniminuto.clinica.service;

import com.uniminuto.clinica.entity.Medicamento;
import java.util.List;

/**
 * Interfaz que define los servicios de negocio para los Medicamentos.
 *
 * @author Yulian Estephania Montealegre
 */
public interface MedicamentoService {

    /**
     * Lista todos los medicamentos disponibles en inventario.
     *
     * @return Lista de entidades Medicamento.
     */
    List<Medicamento> listarMedicamentos();

    /**
     * Busca un medicamento por su ID único.
     *
     * @param id Identificador del medicamento.
     * @return Entidad Medicamento encontrada.
     */
    Medicamento buscarMedicamentoPorId(Long id);
}
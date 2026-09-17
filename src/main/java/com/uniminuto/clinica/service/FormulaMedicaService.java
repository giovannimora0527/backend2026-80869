package com.uniminuto.clinica.service;

// Importamos la entidad FormulaMedica que vamos a manipular
import com.uniminuto.clinica.entity.FormulaMedica;
import java.util.List;

/**
 * Servicio para la gestion de operaciones de negocio relativas a las formulas medicas.
 *
 * ¿Qué significa "interfaz"?
 * - Es un contrato que define QUÉ métodos debe tener la implementación
 * - Permite cambiar la implementación sin afectar el código que usa este servicio
 * - Facilita el testing (puedes crear implementaciones falsas para pruebas)
 */
public interface FormulaMedicaService {

    /**
     * Recupera todas las formulas medicas ordenadas por fecha de creacion descendente.
     *
     * ¿Por qué este método está en el Service y no directamente en el Controller?
     * - Porque el Service puede agregar lógica adicional (validaciones, logs, cache)
     * - Si mañana necesitas validar permisos antes de listar, solo modificas el Service
     * - El Controller no debe saber cómo se obtienen los datos, solo pedirlos
     *
     * @return Lista de formulas medicas ordenadas de la mas reciente a la mas antigua.
     */
    List<FormulaMedica> obtenerTodasOrdenadas();
}
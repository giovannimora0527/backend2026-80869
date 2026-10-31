package com.uniminuto.clinica.models;

import lombok.Data;

/**
 * Solicitud para activar o desactivar un registro (borrado lógico).
 * Se reutiliza en clientes, medicamentos y fórmulas.
 */
@Data
public class EstadoRq {

    /** {@code true} para activar el registro, {@code false} para desactivarlo. */
    private Boolean activo;
}

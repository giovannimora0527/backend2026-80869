package com.uniminuto.clinica.models;

import lombok.Data;

@Data
public class MedicoRq {

    private Long id;

    private String tipoDocumento;

    private String numeroDocumento;

    private String nombres;

    private String apellidos;

    private String telefono;

    private String registroProfesional;

    private Long especializacionId;

    /** Usuario del sistema asociado al medico (para el calendario por rol). Opcional. */
    private Long usuarioId;

    /** Estado activo/inactivo del medico. */
    private Boolean estado;
}

package com.uniminuto.clinica.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class FormulaMedicaDetalle {

    @ManyToOne(optional = false)
    @JoinColumn(name = "medicamento_id", nullable = false)
    private Medicamento medicamento;

    @Column(name = "dosis", nullable = false, columnDefinition = "TEXT")
    private String dosis;

    @Column(name = "frecuencia", length = 100)
    private String frecuencia;

    @Column(name = "duracion_tratamiento", length = 100)
    private String duracionTratamiento;

    @Column(name = "cantidad_prescrita", columnDefinition = "INT UNSIGNED")
    private Long cantidadPrescrita;

    @Column(name = "via_administracion", length = 100)
    private String viaAdministracion;

    @Column(name = "indicaciones", columnDefinition = "TEXT")
    private String indicaciones;
}

package com.uniminuto.clinica.entity;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Detalle de una formula medica: cada fila es un medicamento prescrito dentro
 * de la formula, con su dosis, frecuencia, duracion, cantidad y via de
 * administracion.
 *
 * Permite que una formula contenga varios medicamentos (relacion uno a muchos
 * entre FormulaMedica y FormulaMedicamento).
 */
@Entity
@Table(name = "formula_medicamento")
@Data
public class FormulaMedicamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "formula_id", nullable = false)
    private Long formulaId;

    @Column(name = "medicamento_id", nullable = false)
    private Long medicamentoId;

    @Column(name = "dosis", nullable = false, length = 255)
    private String dosis;

    @Column(name = "frecuencia", length = 255)
    private String frecuencia;

    @Column(name = "duracion", length = 255)
    private String duracion;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad = 1;

    @Column(name = "via_administracion", length = 100)
    private String viaAdministracion;

    @Column(name = "indicaciones", columnDefinition = "TEXT")
    private String indicaciones;
}

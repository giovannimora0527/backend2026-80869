package com.uniminuto.clinica.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@JsonPropertyOrder({
        "id",
        "dosis",
        "indicaciones",
        "fechaCreacionRegistro",
        "fechaActualizacionRegistro",
})
@Table(name = "formula_medica")
@Data
public class FormulaMedica {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @ManyToOne()
    @JoinColumn(name = "cita_id")
    @JsonIgnore
    private Cita cita;

    @Column(name = "dosis")
    private String dosis;

    @Column(name = "indicaciones")
    private String indicaciones;

    @Column(name = "fecha_creacion_registro")
    private LocalDateTime fechaCreacionRegistro;

    @Column(name = "fecha_actualizacion_registro")
    private LocalDateTime fechaActualizacionRegistro;
}

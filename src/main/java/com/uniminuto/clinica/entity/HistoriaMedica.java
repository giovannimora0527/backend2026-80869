package com.uniminuto.clinica.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Entidad que representa la cabecera de la historia medica de una mascota.
 */
@Entity
@Table(name = "historia_medica")
@Data
public class HistoriaMedica {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "paciente_id")
    private Mascota paciente;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @OneToMany(mappedBy = "historiaMedica", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<AnotacionHistoria> anotaciones;
}

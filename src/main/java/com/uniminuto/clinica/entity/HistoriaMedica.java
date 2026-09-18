package com.uniminuto.clinica.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import java.util.List;

/**
 * Entidad que representa la historia medica de una mascota.
 * Contiene una lista de anotaciones relacionadas.
 */
@Entity
@Table(name = "historia_medica")
@Data
public class HistoriaMedica {

    /**
     * Identificador unico de la historia medica.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * Descripcion de la historia medica.
     */
    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    /**
     * Lista de anotaciones asociadas a esta historia medica.
     * CascadeType.ALL permite que al guardar/actualizar/eliminar la historia,
     * se aplique la misma operacion a sus anotaciones.
     */
    @OneToMany(mappedBy = "historiaMedica", cascade = jakarta.persistence.CascadeType.ALL)
    private List<AnotacionHistoria> anotaciones;
}
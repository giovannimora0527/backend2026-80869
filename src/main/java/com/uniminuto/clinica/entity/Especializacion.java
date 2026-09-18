package com.uniminuto.clinica.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * Entidad que representa una especializacion medica.
 * Cada especializacion tiene un nombre unico que describe el area de expertise del medico.
 */
@Entity
@Table(name = "especializacion")
@Data
public class Especializacion {

    /**
     * Identificador unico de la especializacion.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * Nombre de la especializacion (ej: Cardiologia, Dermatologia, etc.).
     */
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;
}
package com.uniminuto.clinica.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * Entidad JPA que representa la tabla "raza" en la base de datos de la clínica.
 * Almacena la información de las diferentes razas asociadas a los animales o mascotas.
 *
 * @author Yulian Estephania Montealegre Rivas
 * @version 1.0
 */
@Entity
@Table(name = "raza")
@Data
public class Raza {

    /**
     * Identificador único de la raza (Clave primaria autoincremental).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    /**
     * Nombre comercial o descriptivo de la raza (ej. Golden Retriever, Siamés).
     */
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    /**
     * Especie animal a la que pertenece la raza (ej. Canino, Felino).
     */
    @Column(name = "especie", nullable = false, length = 50)
    private String especie;
}
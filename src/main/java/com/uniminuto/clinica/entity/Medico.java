package com.uniminuto.clinica.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * Entidad que representa un medico de la clinica veterinaria.
 * Un medico tiene una especializacion asociada.
 */
@Entity
@Table(name = "medico")
@Data
public class Medico {

    /**
     * Identificador unico del medico.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * Tipo de documento de identidad del medico.
     */
    @Column(name = "tipo_documento", nullable = false, length = 10)
    private String tipoDocumento;

    /**
     * Numero de documento de identidad del medico.
     */
    @Column(name = "numero_documento", nullable = false, unique = true, length = 20)
    private String numeroDocumento;

    /**
     * Nombres del medico.
     */
    @Column(name = "nombres", nullable = false, length = 100)
    private String nombres;

    /**
     * Apellidos del medico.
     */
    @Column(name = "apellidos", nullable = false, length = 100)
    private String apellidos;

    /**
     * Telefono de contacto del medico.
     */
    @Column(name = "telefono", length = 20)
    private String telefono;

    /**
     * Registro profesional del medico.
     */
    @Column(name = "registro_profesional", nullable = false, unique = true, length = 50)
    private String registroProfesional;

    /**
     * Especializacion del medico.
     * FetchType.EAGER asegura que la especializacion se cargue automaticamente al consultar el medico.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "especializacion_id", nullable = false)
    private Especializacion especializacion;
}
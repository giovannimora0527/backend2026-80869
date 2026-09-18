package com.uniminuto.clinica.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Data;
import java.util.Set;

/**
 * Entidad que representa un medico de la clinica veterinaria.
 * Un medico puede tener multiples especializaciones.
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
     * Conjunto de especializaciones del medico.
     * FetchType.EAGER asegura que las especializaciones se carguen automaticamente al consultar el medico.
     * @JoinTable define la tabla intermedia que relaciona medicos con especializaciones.
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "medico_especializacion",
            joinColumns = @JoinColumn(name = "medico_id"),
            inverseJoinColumns = @JoinColumn(name = "especializacion_id")
    )
    private Set<Especializacion> especializaciones;
}
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

/**
 * Médico veterinario del centro.
 */
@Entity
@Table(name = "medico")
public class Medico {

    /** Identificador del veterinario. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /** Tipo de documento. */
    @Column(name = "tipo_documento", nullable = false, length = 10)
    private String tipoDocumento;

    /** Número de documento; es único. */
    @Column(name = "numero_documento", nullable = false, unique = true, length = 20)
    private String numeroDocumento;

    /** Nombres del veterinario. */
    @Column(name = "nombres", nullable = false, length = 100)
    private String nombres;

    /** Apellidos del veterinario. */
    @Column(name = "apellidos", nullable = false, length = 100)
    private String apellidos;

    /** Teléfono de contacto. */
    @Column(name = "telefono", length = 20)
    private String telefono;

    /** Registro profesional; es único. */
    @Column(name = "registro_profesional", nullable = false, unique = true, length = 50)
    private String registroProfesional;

    /** Identificador de su especialización. */
    @Column(name = "especializacion_id", nullable = false)
    private Integer especializacionId;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(String tipoDocumento) { this.tipoDocumento = tipoDocumento; }
    public String getNumeroDocumento() { return numeroDocumento; }
    public void setNumeroDocumento(String numeroDocumento) { this.numeroDocumento = numeroDocumento; }
    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }
    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getRegistroProfesional() { return registroProfesional; }
    public void setRegistroProfesional(String registroProfesional) { this.registroProfesional = registroProfesional; }
    public Integer getEspecializacionId() { return especializacionId; }
    public void setEspecializacionId(Integer especializacionId) { this.especializacionId = especializacionId; }
}

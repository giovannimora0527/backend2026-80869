package com.uniminuto.clinica.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;

/**
 * Cliente del centro veterinario: persona propietaria de una o varias mascotas.
 */
@Entity
@Table(name = "cliente")
public class Cliente {

    /** Identificador del cliente. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cliente_id")
    private Long id;

    /** Usuario del sistema asociado al cliente, si lo tiene. */
    @Column(name = "usuario_id", unique = true)
    private Integer usuarioId;

    /** Tipo de documento (CC, TI, CE...). */
    @Column(name = "tipo_documento", nullable = false, length = 10)
    private String tipoDocumento;

    /** Número de documento; es único. */
    @Column(name = "numero_documento", nullable = false, unique = true, length = 20)
    private String numeroDocumento;

    /** Nombres del cliente. */
    @Column(name = "nombres", nullable = false, length = 100)
    private String nombres;

    /** Apellidos del cliente. */
    @Column(name = "apellidos", nullable = false, length = 100)
    private String apellidos;

    /** Fecha de nacimiento. */
    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    /** Género (M o F). */
    @Column(name = "genero", length = 1)
    private String genero;

    /** Teléfono de contacto. */
    @Column(name = "telefono", length = 20)
    private String telefono;

    /** Dirección de residencia. */
    @Column(name = "direccion", columnDefinition = "TEXT")
    private String direccion;

    /** Indica si el cliente está activo en el sistema. */
    @Column(name = "activo", nullable = false)
    private Boolean activo;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Integer getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Integer usuarioId) { this.usuarioId = usuarioId; }
    public String getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(String tipoDocumento) { this.tipoDocumento = tipoDocumento; }
    public String getNumeroDocumento() { return numeroDocumento; }
    public void setNumeroDocumento(String numeroDocumento) { this.numeroDocumento = numeroDocumento; }
    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }
    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }
    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
}

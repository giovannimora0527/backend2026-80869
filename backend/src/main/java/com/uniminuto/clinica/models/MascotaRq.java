package com.uniminuto.clinica.models;

/**
 * Clase que representa la solicitud para crear o actualizar una mascota.
 */
public class MascotaRq {

    /**
     * Id único de la mascota.
     */
    private Integer mascotaId;

    /**
     * Nombre de la mascota.
     */
    private String nombre;

    /**
     * Edad de la mascota.
     */
    private Integer edad;

    /**
     * Identificador de la raza de la mascota.
     */
    private Integer razaId;

    /**
     * Identificador del cliente propietario de la mascota.
     */
    private Long clienteId;

    public Integer getMascotaId() { return mascotaId; }
    public void setMascotaId(Integer mascotaId) { this.mascotaId = mascotaId; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public Integer getEdad() { return edad; }
    public void setEdad(Integer edad) { this.edad = edad; }
    public Integer getRazaId() { return razaId; }
    public void setRazaId(Integer razaId) { this.razaId = razaId; }
    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }
}

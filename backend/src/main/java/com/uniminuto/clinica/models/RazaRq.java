package com.uniminuto.clinica.models;

/**
 * Datos recibidos para registrar una raza nueva.
 */
public class RazaRq {

    /** Identificador de la raza (solo informativo; se genera automáticamente al crear). */
    private Integer razaId;

    /** Nombre de la raza. */
    private String nombre;

    /** Especie a la que pertenece la raza. */
    private String especie;

    public Integer getRazaId() { return razaId; }
    public void setRazaId(Integer razaId) { this.razaId = razaId; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getEspecie() { return especie; }
    public void setEspecie(String especie) { this.especie = especie; }
}

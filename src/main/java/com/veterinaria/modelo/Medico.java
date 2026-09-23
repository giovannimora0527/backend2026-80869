package com.veterinaria.modelo;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "medicos")
public class Medico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @ElementCollection
    @CollectionTable(name = "medico_especializaciones", joinColumns = @JoinColumn(name = "medico_id"))
    @Column(name = "especializacion")
    private List<String> especializaciones;

    public Medico() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public List<String> getEspecializaciones() { return especializaciones; }
    public void setEspecializaciones(List<String> especializaciones) { this.especializaciones = especializaciones; }
}
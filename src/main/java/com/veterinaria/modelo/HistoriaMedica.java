package com.veterinaria.modelo;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "historias_medicas")
public class HistoriaMedica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombreMascota;

    @OneToMany(mappedBy = "historiaMedica", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AnotacionHistoria> anotaciones;

    public HistoriaMedica() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombreMascota() { return nombreMascota; }
    public void setNombreMascota(String nombreMascota) { this.nombreMascota = nombreMascota; }
    public List<AnotacionHistoria> getAnotaciones() { return anotaciones; }
    public void setAnotaciones(List<AnotacionHistoria> anotaciones) { this.anotaciones = anotaciones; }
    
}
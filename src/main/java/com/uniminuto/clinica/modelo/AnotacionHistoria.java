package com.veterinaria.modelo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "anotaciones_historia")
public class AnotacionHistoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 1000)
    private String observacion;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "historia_medica_id", nullable = false)
    @JsonIgnore
    private HistoriaMedica historiaMedica;

    public AnotacionHistoria() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getObservacion() { return observacion; }
    public void setObservacion(String observacion) { this.observacion = observacion; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    public HistoriaMedica getHistoriaMedica() { return historiaMedica; }
    public void setHistoriaMedica(HistoriaMedica historiaMedica) { this.historiaMedica = historiaMedica; }
    
}
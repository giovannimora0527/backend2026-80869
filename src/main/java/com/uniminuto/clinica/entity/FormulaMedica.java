package com.uniminuto.clinica.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.annotations.ListIndexBase;

@Entity
@Table(name = "formula_medica")
@Data
public class FormulaMedica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cita_id", nullable = false)
    private Cita cita;

    @ManyToOne
    @JoinColumn(name = "medicamento_id", nullable = false)
    private Medicamento medicamento;

    @Column(name = "dosis", nullable = false, columnDefinition = "TEXT")
    private String dosis;

    @Column(name = "indicaciones", columnDefinition = "TEXT")
    private String indicaciones;

    @Column(name = "fecha_emision")
    private LocalDate fechaEmision;

    @Column(name = "frecuencia", length = 100)
    private String frecuencia;

    @Column(name = "duracion_tratamiento", length = 100)
    private String duracionTratamiento;

    @Column(name = "cantidad_prescrita", columnDefinition = "INT UNSIGNED")
    private Long cantidadPrescrita;

    @Column(name = "via_administracion", length = 100)
    private String viaAdministracion;

    @Column(name = "fecha_creacion_registro", nullable = false)
    private LocalDateTime fechaCreacionRegistro;

    @Column(name = "fecha_actualizacion_registro")
    private LocalDateTime fechaActualizacionRegistro;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "formula_medica_detalle",
            joinColumns = @JoinColumn(name = "formula_id")
    )
    @OrderColumn(name = "numero_linea", nullable = false)
    @ListIndexBase(1)
    private List<FormulaMedicaDetalle> detalles = new ArrayList<>();

    public void sincronizarPrimeraLinea() {
        FormulaMedicaDetalle primera;
        if (detalles.isEmpty()) {
            primera = new FormulaMedicaDetalle();
            detalles.add(primera);
        } else {
            primera = detalles.get(0);
        }

        primera.setMedicamento(medicamento);
        primera.setDosis(dosis);
        primera.setFrecuencia(frecuencia);
        primera.setDuracionTratamiento(duracionTratamiento);
        primera.setCantidadPrescrita(cantidadPrescrita);
        primera.setViaAdministracion(viaAdministracion);
        primera.setIndicaciones(indicaciones);
    }

    public String getDuracion() {
        return duracionTratamiento;
    }

    public void setDuracion(String duracion) {
        this.duracionTratamiento = duracion;
    }
}
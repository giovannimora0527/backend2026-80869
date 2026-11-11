package com.uniminuto.clinica.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Servicio o procedimiento que ofrece la clinica (por ejemplo, consulta
 * general, bano, radiografia).
 *
 * Cada servicio tiene un precio y un porcentaje de IVA configurables, y puede
 * estar asociado opcionalmente a una especializacion (cuando el servicio
 * requiere una especialidad concreta). Los cambios de precio o IVA quedan
 * registrados en la entidad LogTarifa.
 */
@Entity
@Table(name = "servicio")
@Data
public class Servicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "nombre", nullable = false, length = 150, unique = true)
    private String nombre;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    /** Especializacion asociada (opcional). Null para servicios generales. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "especializacion_id")
    private Especializacion especializacion;

    @Column(name = "precio", nullable = false, precision = 12, scale = 2)
    private BigDecimal precio = BigDecimal.ZERO;

    /** Porcentaje de IVA aplicable al servicio (por ejemplo 19.00). */
    @Column(name = "iva_porcentaje", nullable = false, precision = 5, scale = 2)
    private BigDecimal ivaPorcentaje = BigDecimal.ZERO;

    @Column(name = "activo", nullable = false)
    private Boolean activo = Boolean.TRUE;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;
}

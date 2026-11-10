package com.uniminuto.clinica.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalTime;

/**
 * Horario de disponibilidad de un medico.
 *
 * Define, por dia de la semana, la franja horaria en la que el medico atiende.
 * Se utiliza para impedir el agendamiento de citas fuera de la disponibilidad
 * del veterinario.
 *
 * diaSemana sigue la convencion de java.time.DayOfWeek: 1 = lunes, 7 = domingo.
 */
@Entity
@Table(name = "horario_medico")
@Data
public class HorarioMedico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "medico_id", nullable = false)
    private Long medicoId;

    @Column(name = "dia_semana", nullable = false)
    private Integer diaSemana;

    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "hora_fin", nullable = false)
    private LocalTime horaFin;

    @Column(name = "activo", nullable = false)
    private Boolean activo = Boolean.TRUE;
}

package com.uniminuto.clinica.models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Datos recibidos para crear o editar una cita. La fecha y hora pueden enviarse juntas en
 * {@code fechaHora} o separadas en {@code fecha} y {@code hora} (como las captura el formulario).
 */
public class CitaRq {

    /** Cliente dueño de la mascota; obligatorio al crear. */
    private Long clienteId;

    /** Mascota que asiste a la cita; debe pertenecer al cliente. Obligatoria al crear. */
    private Integer mascotaId;

    /** Veterinario que atenderá la cita. */
    private Long medicoId;

    /** Fecha y hora completas de la cita (alternativa a {@code fecha} + {@code hora}). */
    private LocalDateTime fechaHora;

    /** Fecha de la cita (se usa junto con {@code hora}). */
    private LocalDate fecha;

    /** Hora de la cita (se usa junto con {@code fecha}). */
    private LocalTime hora;

    /** Motivo de la consulta. */
    private String motivo;

    /** Estado deseado de la cita; si se omite al crear, queda Pendiente. */
    private String estado;

    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }
    public Integer getMascotaId() { return mascotaId; }
    public void setMascotaId(Integer mascotaId) { this.mascotaId = mascotaId; }
    public Long getMedicoId() { return medicoId; }
    public void setMedicoId(Long medicoId) { this.medicoId = medicoId; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public LocalTime getHora() { return hora; }
    public void setHora(LocalTime hora) { this.hora = hora; }
    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    /**
     * Calcula la fecha y hora de la cita a partir de lo enviado.
     *
     * @return {@code fechaHora} si viene informada; si no, la combinación de {@code fecha} y
     *         {@code hora}; {@code null} si no hay datos suficientes
     */
    public LocalDateTime resolverFechaHora() {
        if (fechaHora != null) {
            return fechaHora;
        }
        if (fecha != null && hora != null) {
            return LocalDateTime.of(fecha, hora);
        }
        return null;
    }
}

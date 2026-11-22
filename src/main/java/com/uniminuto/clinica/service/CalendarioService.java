package com.uniminuto.clinica.service;

import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.AtenderCitaRq;
import com.uniminuto.clinica.models.CambiarEstadoCitaRq;
import com.uniminuto.clinica.models.CitaRs;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.models.ReprogramarCitaRq;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio del calendario de citas.
 *
 * Proporciona la informacion de citas segun el rol del usuario autenticado: un
 * medico ve unicamente sus citas; recepcion y administrador ven todas. Tambien
 * gestiona el cambio de estado, la reprogramacion y el historial por mascota.
 */
public interface CalendarioService {

    /**
     * Lista las citas visibles para el usuario autenticado en un rango de
     * fechas. Si el usuario es medico, se limita a sus citas.
     */
    List<CitaRs> listarCitasCalendario(LocalDateTime inicio, LocalDateTime fin)
            throws BadRequestException;

    /** Historial de citas de una mascota (todas, de la mas reciente a la mas antigua). */
    List<CitaRs> historialPorMascota(Integer mascotaId) throws BadRequestException;

    /** Cambia el estado de una cita (confirmar, atender, cancelar, finalizar). */
    MiRespuestaRS cambiarEstado(CambiarEstadoCitaRq rq) throws BadRequestException;

    /**
     * Atiende una cita en un solo paso: registra la anotacion de la atencion
     * (descripcion, diagnostico y observaciones), crea la formula medica si se
     * indicaron medicamentos y marca la cita como ATENDIDA. Todo se ejecuta de
     * forma transaccional: si algo falla, no se guarda nada.
     */
    MiRespuestaRS atenderCita(AtenderCitaRq rq) throws BadRequestException;

    /** Reprograma una cita a una nueva fecha y hora, validando las reglas. */
    MiRespuestaRS reprogramar(ReprogramarCitaRq rq) throws BadRequestException;

    /** Genera el reporte en Excel de las citas del medico en un rango de fechas. */
    byte[] generarReporteExcel(Long medicoId, LocalDateTime inicio, LocalDateTime fin)
            throws BadRequestException;
}

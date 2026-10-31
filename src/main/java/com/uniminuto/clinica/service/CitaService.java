package com.uniminuto.clinica.service;

import com.uniminuto.clinica.entity.Cita;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.CitaRq;
import com.uniminuto.clinica.models.CitaRs;
import com.uniminuto.clinica.models.FiltroPaginadoRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.models.PageRs;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Reglas de negocio de las citas médicas.
 */
public interface CitaService {

    /**
     * Lista todas las citas, de la más reciente a la más antigua.
     *
     * @return citas ordenadas por fecha y hora descendente.
     * @throws BadRequestException nunca en la práctica; se conserva por compatibilidad.
     */
    List<Cita> listarCitas() throws BadRequestException;

    /**
     * Lista las citas cuya fecha está dentro de un rango.
     *
     * @param fechaInicio inicio del rango.
     * @param fechaFin    fin del rango.
     * @return citas del rango, de la más antigua a la más reciente.
     * @throws BadRequestException si las fechas son nulas o están invertidas.
     */
    List<Cita> listarCitasRango(LocalDateTime fechaInicio, LocalDateTime fechaFin) throws BadRequestException;

    /**
     * Lista las citas de una mascota.
     *
     * @param mascotaId id de la mascota.
     * @return citas de la mascota, de la más reciente a la más antigua.
     * @throws BadRequestException si el id no es válido.
     */
    List<Cita> listarCitasPorMascota(Integer mascotaId) throws BadRequestException;

    /**
     * Listado paginado con filtros y ordenamiento para el DataTable.
     *
     * @param filtro parámetros de página, orden y filtros.
     * @return una página de filas resumidas; por defecto ordenada por fecha y hora descendente.
     */
    PageRs<CitaRs> listarCitasPaginado(FiltroPaginadoRq filtro);

    /**
     * Consulta una cita por su id.
     *
     * @param id identificador de la cita.
     * @return la cita completa.
     */
    Cita buscarCitaPorId(Long id);

    /**
     * Crea una cita validando cliente, mascota, veterinario, fecha y disponibilidad.
     *
     * @param citaRq datos de la cita.
     * @return mensaje de confirmación.
     */
    MiRespuestaRS crearCita(CitaRq citaRq) throws BadRequestException;

    /**
     * Edita una cita según su estado actual (la cancelada y la atendida no se editan).
     *
     * @param citaRq datos de la cita; debe incluir el id.
     * @return mensaje de confirmación.
     */
    MiRespuestaRS actualizarCita(CitaRq citaRq) throws BadRequestException;

    /**
     * Cambia solo el estado de la cita respetando las transiciones permitidas.
     *
     * @param id     identificador de la cita.
     * @param estado nuevo estado.
     * @return mensaje de confirmación.
     */
    MiRespuestaRS cambiarEstadoCita(Long id, String estado);

    /**
     * Operación explícita para volver a agendar una cita cancelada.
     *
     * @param id identificador de la cita cancelada.
     * @return mensaje de confirmación.
     */
    MiRespuestaRS reactivarCita(Long id);
}

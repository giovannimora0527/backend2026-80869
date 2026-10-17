package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.*;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.exception.ConflictException;
import com.uniminuto.clinica.exception.ResourceNotFoundException;
import com.uniminuto.clinica.models.HistoriaMedicaRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.repository.*;
import com.uniminuto.clinica.service.HistoriaMedicaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementación de la lógica de negocio para la gestión de Historias Médicas.
 * Se encarga de validar la existencia de clientes, mascotas, médicos y citas,
 * asegurando el cumplimiento de las reglas de negocio.
 *
 * @author Yulian Estefania
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class HistoriaMedicaServiceImpl implements HistoriaMedicaService {

    private final HistoriaMedicaRepository historiaMedicaRepository;
    private final ClienteRepository clienteRepository;
    private final MascotaRepository mascotaRepository;
    private final CitaRepository citaRepository;
    private final MedicoRepository medicoRepository;

    /**
     * Lista todas las historias médicas registradas en el sistema,
     * ordenadas por fecha y hora de forma descendente.
     *
     * @return {@link MiRespuestaRS} que contiene el estado HTTP, mensaje y la lista de historias.
     */
    @Override
    @Transactional(readOnly = true)
    public MiRespuestaRS listar() {
        List<HistoriaMedica> historias = historiaMedicaRepository.findAllByOrderByFechaHoraDesc();

        MiRespuestaRS response = new MiRespuestaRS();
        response.setStatus(200);
        response.setMessage("Historias médicas listadas exitosamente");
        response.setData(historias);
        return response;
    }

    /**
     * Busca una historia médica específica por su identificador único.
     *
     * @param id Identificador único de la historia médica.
     * @return {@link MiRespuestaRS} con los datos de la historia encontrada.
     * @throws ResourceNotFoundException si la historia médica no existe.
     */
    @Override
    @Transactional(readOnly = true)
    public MiRespuestaRS buscarPorId(Long id) {
        HistoriaMedica historia = historiaMedicaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Historia médica no encontrada con ID: " + id));

        MiRespuestaRS response = new MiRespuestaRS();
        response.setStatus(200);
        response.setMessage("Historia médica encontrada exitosamente");
        response.setData(historia);
        return response;
    }

    /**
     * Registra y guarda una nueva historia médica aplicando rigurosas validaciones.
     *
     * @param request DTO con los datos de entrada para la creación de la historia médica.
     * @return {@link MiRespuestaRS} con la historia médica recién guardada.
     */
    @Override
    @Transactional
    public MiRespuestaRS guardar(HistoriaMedicaRq request) {
        // 1. Validar que el cliente exista en la base de datos (404)
        Cliente cliente = clienteRepository.findById(request.getClienteId())
                .orElseThrow(() -> new ResourceNotFoundException("El cliente con ID " + request.getClienteId() + " no existe"));

        // 2. Validar que la mascota exista en la base de datos (404)
        Mascota mascota = mascotaRepository.findById(request.getMascotaId().intValue())
                .orElseThrow(() -> new ResourceNotFoundException("La mascota con ID " + request.getMascotaId() + " no existe"));

        // Validar regla de negocio: La mascota debe pertenecer al cliente especificado
        if (mascota.getCliente() == null || !mascota.getCliente().getId().equals(cliente.getId())) {
            throw new BadRequestException("La mascota no pertenece al cliente especificado");
        }

        // 3. Validar que el médico exista en la base de datos (404)
        Medico medico = medicoRepository.findById(request.getMedicoId())
                .orElseThrow(() -> new ResourceNotFoundException("El médico con ID " + request.getMedicoId() + " no existe"));

        // 4. Validar que la cita exista en la base de datos (404)
        Cita cita = citaRepository.findById(request.getCitaId())
                .orElseThrow(() -> new ResourceNotFoundException("La cita con ID " + request.getCitaId() + " no existe"));

        // Validar regla de negocio: La cita debe pertenecer a la mascota especificada
        if (cita.getMascota() == null || !cita.getMascota().getMascotaId().equals(mascota.getMascotaId())) {
            throw new BadRequestException("La cita no pertenece al paciente (mascota) especificado");
        }

        // Validar regla de negocio: La cita debe estar en estado ATENDIDA para registrar su historia
        if (cita.getEstado() == null || !cita.getEstado().equalsIgnoreCase("ATENDIDA")) {
            throw new BadRequestException("La cita debe estar en estado ATENDIDA para registrar su historia médica");
        }

        // Validar regla de negocio: No debe existir ya una historia médica asociada a esta misma cita (409 Conflict)
        if (historiaMedicaRepository.existsByCitaId(request.getCitaId())) {
            throw new ConflictException("Ya existe una historia médica registrada para esta cita");
        }

        // Construir y mapear la nueva entidad de historia médica
        HistoriaMedica historia = new HistoriaMedica();
        historia.setMascota(mascota);
        historia.setCita(cita);
        historia.setMedico(medico);
        historia.setFechaHora(request.getFechaHora());
        historia.setMotivoConsulta(request.getMotivoConsulta());
        historia.setDiagnostico(request.getDiagnostico());
        historia.setObservaciones(request.getObservaciones());
        historia.setEstado("ACTIVA");

        // Guardar la entidad en la base de datos
        HistoriaMedica saved = historiaMedicaRepository.save(historia);

        // Construir la respuesta estándar del servidor
        MiRespuestaRS response = new MiRespuestaRS();
        response.setStatus(200);
        response.setMessage("Historia médica guardada exitosamente");
        response.setData(saved);
        return response;
    }
}
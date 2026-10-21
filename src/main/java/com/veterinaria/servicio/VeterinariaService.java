package com.veterinaria.servicio;

import com.veterinaria.modelo.*;
import com.veterinaria.repositorio.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class VeterinariaService{
    private final FormulaMedicaRepository formulaRepository;
    private final CitaRepository citaRepository;
    private final AnotacionHistoriaRepository anotacionRepository;
    private final HistoriaMedicaRepository historiaRepository;
    private final MedicoRepository medicoRepository;
    
    public VeterinariaService(FormulaMedicaRepository formulaRepository,
                              CitaRepository citaRepository,
                              AnotacionHistoriaRepository anotacionRepository,
                              HistoriaMedicaRepository historiaRepository,
                              MedicoRepository medicoRepository) {
        this.formulaRepository = formulaRepository;
        this.citaRepository = citaRepository;
        this.anotacionRepository = anotacionRepository;
        this.historiaRepository = historiaRepository;
        this.medicoRepository = medicoRepository;
    }

    public List<FormulaMedica> listarFormulasOrdenadas() {
        return formulaRepository.findAllByOrderByFechaCreacionDesc();
    }

    public List<Cita> filtrarCitasPorFecha(LocalDateTime inicio, LocalDateTime fin) {
        return citaRepository.findByFechaHoraBetweenOrderByFechaHoraDesc(inicio, fin);
    }

    public Cita crearCita(Cita cita) {
        return citaRepository.save(cita);
    }

    public Cita actualizarCita(Long id, Cita detalles) {
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada con ID: " + id));
        cita.setFechaHora(detalles.getFechaHora());
        cita.setMotivo(detalles.getMotivo());
        cita.setPaciente(detalles.getPaciente());
        return citaRepository.save(cita);
    }

    public AnotacionHistoria crearAnotacion(Long historiaId, AnotacionHistoria anotacion) {
        HistoriaMedica historia = historiaRepository.findById(historiaId)
                .orElseThrow(() -> new RuntimeException("Historia medica no encontrada con ID: " + historiaId));
        anotacion.setHistoriaMedica(historia);
        if (anotacion.getFechaCreacion() == null) {
            anotacion.setFechaCreacion(LocalDateTime.now());
        }
        return anotacionRepository.save(anotacion);
    }

    public AnotacionHistoria actualizarAnotacion(Long id, AnotacionHistoria detalles) {
        AnotacionHistoria anotacion = anotacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Anotacion no encontrada con ID: " + id));
        anotacion.setObservacion(detalles.getObservacion());
        return anotacionRepository.save(anotacion);
    }

    public List<AnotacionHistoria> listarAnotacionesPorFecha(LocalDateTime inicio, LocalDateTime fin) {
        return anotacionRepository.findByFechaCreacionBetweenOrderByFechaCreacionDesc(inicio, fin);
    }

    public List<Medico> listarMedicosConEspecializaciones() {
        return medicoRepository.findAll();
    }

    /**
     * @return 
     */
    public List<HistoriaMedica> listarHistoriasMedicas() {
        return historiaRepository.findAll();
    }
}
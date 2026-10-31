package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.AnotacionHistoria;
import com.uniminuto.clinica.entity.Cita;
import com.uniminuto.clinica.entity.HistoriaMedica;
import com.uniminuto.clinica.entity.Medico;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.exception.NotFoundException;
import com.uniminuto.clinica.models.AnotacionHistoriaRq;
import com.uniminuto.clinica.repository.AnotacionHistoriaRepository;
import com.uniminuto.clinica.repository.CitaRepository;
import com.uniminuto.clinica.repository.FormulaMedicaRepository;
import com.uniminuto.clinica.repository.HistoriaMedicaRepository;
import com.uniminuto.clinica.repository.MascotaRepository;
import com.uniminuto.clinica.repository.MedicoRepository;
import com.uniminuto.clinica.service.HistoriaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Pruebas de las reglas de negocio de las anotaciones.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AnotacionHistoriaServiceImplTests {

    @Mock
    private HistoriaMedicaRepository historiaMedicaRepository;
    @Mock
    private AnotacionHistoriaRepository anotacionHistoriaRepository;
    @Mock
    private MascotaRepository mascotaRepository;
    @Mock
    private MedicoRepository medicoRepository;
    @Mock
    private CitaRepository citaRepository;
    @Mock
    private FormulaMedicaRepository formulaMedicaRepository;
    @Mock
    private HistoriaService historiaService;
    @InjectMocks
    private AnotacionHistoriaServiceImpl anotacionService;

    @Test
    void creaAnotacionEnUnaHistoriaExistente() {
        Cita cita = new Cita();
        cita.setId(5L);
        HistoriaMedica historia = new HistoriaMedica();
        historia.setId(3L);
        historia.setCita(cita);
        Medico medico = new Medico();
        medico.setId(2L);
        medico.setEstado(true);
        when(historiaMedicaRepository.findById(3L)).thenReturn(Optional.of(historia));
        when(medicoRepository.findById(2L)).thenReturn(Optional.of(medico));

        anotacionService.crearAnotacionHistoria(solicitud(3L, 2L));

        ArgumentCaptor<AnotacionHistoria> captor = ArgumentCaptor.forClass(AnotacionHistoria.class);
        verify(anotacionHistoriaRepository).save(captor.capture());
        AnotacionHistoria anotacion = captor.getValue();
        assertSame(historia, anotacion.getHistoria());
        assertSame(cita, anotacion.getCita());
        assertSame(medico, anotacion.getMedico());
        assertEquals("Dermatitis", anotacion.getDiagnostico());
    }

    @Test
    void rechazaAnotacionSobreHistoriaInexistente() {
        when(historiaMedicaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> anotacionService.crearAnotacionHistoria(solicitud(99L, 2L)));
        verify(anotacionHistoriaRepository, never()).save(any(AnotacionHistoria.class));
    }

    @Test
    void rechazaAnotacionSinHistoriaNiCita() {
        AnotacionHistoriaRq rq = solicitud(null, 2L);

        assertThrows(BadRequestException.class, () -> anotacionService.crearAnotacionHistoria(rq));
    }

    @Test
    void rechazaAnotacionSinDiagnostico() {
        AnotacionHistoriaRq rq = solicitud(3L, 2L);
        rq.setDiagnostico(" ");

        assertThrows(BadRequestException.class, () -> anotacionService.crearAnotacionHistoria(rq));
    }

    private AnotacionHistoriaRq solicitud(Long historiaId, Long medicoId) {
        AnotacionHistoriaRq rq = new AnotacionHistoriaRq();
        rq.setHistoriaId(historiaId);
        rq.setMedicoId(medicoId);
        rq.setDescripcion("Consulta");
        rq.setDiagnostico("Dermatitis");
        rq.setObservaciones("Control en una semana");
        return rq;
    }
}

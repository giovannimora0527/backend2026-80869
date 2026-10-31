package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.AnotacionHistoria;
import com.uniminuto.clinica.entity.Cliente;
import com.uniminuto.clinica.entity.Mascota;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.exception.ConflictException;
import com.uniminuto.clinica.models.HistoriaMedicaRq;
import com.uniminuto.clinica.repository.CitaRepository;
import com.uniminuto.clinica.repository.ClienteRepository;
import com.uniminuto.clinica.repository.MascotaRepository;
import com.uniminuto.clinica.repository.MedicoRepository;
import com.uniminuto.clinica.utils.EstadoCita;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import com.uniminuto.clinica.entity.Cita;
import com.uniminuto.clinica.entity.FormulaMedica;
import com.uniminuto.clinica.entity.HistoriaMedica;
import com.uniminuto.clinica.entity.Medicamento;
import com.uniminuto.clinica.entity.Medico;
import com.uniminuto.clinica.models.AnotacionHistoriaRs;
import com.uniminuto.clinica.models.HistoriaMedicaRs;
import com.uniminuto.clinica.repository.AnotacionHistoriaRepository;
import com.uniminuto.clinica.repository.FormulaMedicaRepository;
import com.uniminuto.clinica.repository.HistoriaMedicaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class HistoriaServiceImplTests {
    @Mock
    private HistoriaMedicaRepository historiaMedicaRepository;
    @Mock
    private AnotacionHistoriaRepository anotacionHistoriaRepository;
    @Mock
    private FormulaMedicaRepository formulaMedicaRepository;
    @Mock
    private MascotaRepository mascotaRepository;
    @Mock
    private ClienteRepository clienteRepository;
    @Mock
    private CitaRepository citaRepository;
    @Mock
    private MedicoRepository medicoRepository;
    @InjectMocks
    private HistoriaServiceImpl historiaService;

    @Test
    void incluyeFormulasSoloDeLaCitaDeCadaAnotacion() {
        HistoriaMedica historia = new HistoriaMedica();
        historia.setId(1L);
        HistoriaMedica otraHistoria = new HistoriaMedica();
        otraHistoria.setId(2L);
        AnotacionHistoria primera = anotacion(historia, 10L);
        AnotacionHistoria segunda = anotacion(historia, 20L);
        AnotacionHistoria compartida = anotacion(otraHistoria, 10L);
        FormulaMedica formula = formula(primera.getCita());
        FormulaMedica otraFormula = formula(primera.getCita());
        FormulaMedica formulaSegunda = formula(segunda.getCita());
        when(historiaMedicaRepository.findAll()).thenReturn(List.of(historia, otraHistoria));
        when(anotacionHistoriaRepository.findByHistoria(historia)).thenReturn(List.of(primera, segunda));
        when(anotacionHistoriaRepository.findByHistoria(otraHistoria)).thenReturn(List.of(compartida));
        when(formulaMedicaRepository.findByCita_IdInOrderByFechaCreacionRegistroDesc(List.of(10L, 20L)))
                .thenReturn(List.of(formula, otraFormula, formulaSegunda));

        List<HistoriaMedicaRs> resultado = historiaService.getAllHistorias();

        AnotacionHistoriaRs anotacion = resultado.getFirst().getAnotaciones().getFirst();
        assertSame(historia, resultado.getFirst().getHistoria());
        assertEquals(primera.getId(), anotacion.getId());
        assertSame(historia, anotacion.getHistoria());
        assertSame(primera.getMedico(), anotacion.getMedico());
        assertSame(primera.getCita(), anotacion.getCita());
        assertEquals(primera.getFecha(), anotacion.getFecha());
        assertEquals(primera.getDescripcion(), anotacion.getDescripcion());
        assertEquals(primera.getDiagnostico(), anotacion.getDiagnostico());
        assertEquals(primera.getObservaciones(), anotacion.getObservaciones());
        assertEquals(List.of(formula, otraFormula), anotacion.getFormulasMedicas());
        assertSame(formula.getMedicamento(), anotacion.getFormulasMedicas().getFirst().getMedicamento());
        assertEquals(List.of(formulaSegunda), resultado.getFirst().getAnotaciones().get(1).getFormulasMedicas());
        assertEquals(List.of(formula, otraFormula), resultado.get(1).getAnotaciones().getFirst().getFormulasMedicas());
        verify(formulaMedicaRepository).findByCita_IdInOrderByFechaCreacionRegistroDesc(List.of(10L, 20L));
        verifyNoMoreInteractions(formulaMedicaRepository);
    }

    @Test
    void devuelveListaVaciaCuandoLaCitaNoTieneFormulas() {
        HistoriaMedica historia = new HistoriaMedica();
        when(historiaMedicaRepository.findAll()).thenReturn(List.of(historia));
        when(anotacionHistoriaRepository.findByHistoria(historia)).thenReturn(List.of(anotacion(historia, 10L)));
        when(formulaMedicaRepository.findByCita_IdInOrderByFechaCreacionRegistroDesc(List.of(10L)))
                .thenReturn(List.of());

        assertTrue(historiaService.getAllHistorias().getFirst().getAnotaciones().getFirst()
                .getFormulasMedicas().isEmpty());
    }

    @Test
    void conservaHistoriasSinAnotacionesSinConsultarFormulas() {
        HistoriaMedica historia = new HistoriaMedica();
        when(historiaMedicaRepository.findAll()).thenReturn(List.of(historia));
        when(anotacionHistoriaRepository.findByHistoria(historia)).thenReturn(List.of());

        List<HistoriaMedicaRs> resultado = historiaService.getAllHistorias();

        assertEquals(1, resultado.size());
        assertTrue(resultado.getFirst().getAnotaciones().isEmpty());
        verifyNoInteractions(formulaMedicaRepository);
    }

    @Test
    void devuelveListaVaciaSiNoHayHistorias() {
        when(historiaMedicaRepository.findAll()).thenReturn(List.of());

        assertTrue(historiaService.getAllHistorias().isEmpty());
        verifyNoInteractions(anotacionHistoriaRepository, formulaMedicaRepository);
    }

    @Test
    void guardaLaHistoriaYDejaLaCitaAtendida() {
        Escenario e = escenario();

        historiaService.guardarHistoria(e.solicitud);

        verify(historiaMedicaRepository).save(any(HistoriaMedica.class));
        assertEquals(EstadoCita.ATENDIDA, e.cita.getEstado());
    }

    @Test
    void rechazaCitaDeOtraMascota() {
        Escenario e = escenario();
        Mascota otra = new Mascota();
        otra.setMascotaId(2);
        e.cita.setMascota(otra);

        BadRequestException error = assertThrows(BadRequestException.class,
                () -> historiaService.guardarHistoria(e.solicitud));

        assertEquals("La cita seleccionada pertenece a otra mascota.", error.getMessage());
        verify(historiaMedicaRepository, never()).save(any(HistoriaMedica.class));
    }

    @Test
    void rechazaMascotaQueNoEsDelCliente() {
        Escenario e = escenario();
        Cliente otro = new Cliente();
        otro.setId(2L);
        e.mascota.setCliente(otro);

        assertThrows(BadRequestException.class, () -> historiaService.guardarHistoria(e.solicitud));
    }

    @Test
    void rechazaUnaSegundaHistoriaParaLaMismaCita() {
        Escenario e = escenario();
        when(historiaMedicaRepository.existsByCita_Id(5L)).thenReturn(true);

        assertThrows(ConflictException.class, () -> historiaService.guardarHistoria(e.solicitud));
    }

    @Test
    void rechazaHistoriaSobreCitaCancelada() {
        Escenario e = escenario();
        e.cita.setEstado(EstadoCita.CANCELADA);

        assertThrows(ConflictException.class, () -> historiaService.guardarHistoria(e.solicitud));
    }

    @Test
    void rechazaFechaDeConsultaAnteriorALaCita() {
        Escenario e = escenario();
        e.solicitud.setFechaHora(e.cita.getFechaHora().minusHours(1));

        assertThrows(BadRequestException.class, () -> historiaService.guardarHistoria(e.solicitud));
    }

    @Test
    void rechazaDiagnosticoVacio() {
        Escenario e = escenario();
        e.solicitud.setDiagnostico(" ");

        assertThrows(BadRequestException.class, () -> historiaService.guardarHistoria(e.solicitud));
    }

    @Test
    void creaLaHistoriaDeUnaCitaAtendidaUnaSolaVez() {
        Cita cita = new Cita();
        cita.setId(5L);
        cita.setMascota(new Mascota());
        cita.setMedico(new Medico());
        cita.setFechaHora(LocalDateTime.now().minusDays(1));
        when(historiaMedicaRepository.findByCita_Id(5L)).thenReturn(Optional.empty());
        when(historiaMedicaRepository.save(any(HistoriaMedica.class))).thenAnswer(i -> i.getArgument(0));

        HistoriaMedica creada = historiaService.asegurarHistoriaDeCita(cita);

        assertSame(cita, creada.getCita());
        HistoriaMedica existente = new HistoriaMedica();
        when(historiaMedicaRepository.findByCita_Id(5L)).thenReturn(Optional.of(existente));
        assertSame(existente, historiaService.asegurarHistoriaDeCita(cita));
    }

    /** Datos coherentes (cliente → mascota → cita → veterinario) para probar las reglas. */
    private Escenario escenario() {
        Escenario e = new Escenario();
        Cliente cliente = new Cliente();
        cliente.setId(1L);
        e.mascota = new Mascota();
        e.mascota.setMascotaId(1);
        e.mascota.setCliente(cliente);
        Medico medico = new Medico();
        medico.setId(1L);
        medico.setEstado(true);
        e.cita = new Cita();
        e.cita.setId(5L);
        e.cita.setMascota(e.mascota);
        e.cita.setMedico(medico);
        e.cita.setEstado(EstadoCita.CONFIRMADA);
        e.cita.setFechaHora(LocalDateTime.now().minusHours(2));
        e.solicitud = new HistoriaMedicaRq();
        e.solicitud.setClienteId(1L);
        e.solicitud.setMascotaId(1);
        e.solicitud.setCitaId(5L);
        e.solicitud.setMedicoId(1L);
        e.solicitud.setFechaHora(LocalDateTime.now().minusHours(1));
        e.solicitud.setMotivo("Control");
        e.solicitud.setDiagnostico("Sano");
        e.solicitud.setObservaciones("Repetir en 6 meses");
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(mascotaRepository.findById(1)).thenReturn(Optional.of(e.mascota));
        when(citaRepository.findById(5L)).thenReturn(Optional.of(e.cita));
        when(medicoRepository.findById(1L)).thenReturn(Optional.of(medico));
        return e;
    }

    /** Contenedor simple de los objetos de un escenario de prueba. */
    private static class Escenario {
        private Mascota mascota;
        private Cita cita;
        private HistoriaMedicaRq solicitud;
    }

    private AnotacionHistoria anotacion(HistoriaMedica historia, Long citaId) {
        Cita cita = new Cita();
        cita.setId(citaId);
        AnotacionHistoria anotacion = new AnotacionHistoria();
        anotacion.setId(citaId);
        anotacion.setHistoria(historia);
        anotacion.setCita(cita);
        anotacion.setMedico(new Medico());
        anotacion.setFecha(LocalDateTime.now());
        anotacion.setDescripcion("Consulta");
        anotacion.setDiagnostico("Dermatitis");
        anotacion.setObservaciones("Control en una semana");
        return anotacion;
    }

    private FormulaMedica formula(Cita cita) {
        FormulaMedica formula = new FormulaMedica();
        formula.setCita(cita);
        formula.setMedicamento(new Medicamento());
        formula.setDosis("1 tableta");
        formula.setFrecuencia("Cada 12 horas");
        formula.setDuracion("5 dias");
        formula.setIndicaciones("Con alimento");
        return formula;
    }
}

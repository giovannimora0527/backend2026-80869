package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.Cita;
import com.uniminuto.clinica.entity.Cliente;
import com.uniminuto.clinica.entity.FormulaMedica;
import com.uniminuto.clinica.entity.Mascota;
import com.uniminuto.clinica.entity.Medicamento;
import com.uniminuto.clinica.entity.Medico;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.exception.ConflictException;
import com.uniminuto.clinica.exception.NotFoundException;
import com.uniminuto.clinica.models.FormulaRq;
import com.uniminuto.clinica.repository.CitaRepository;
import com.uniminuto.clinica.repository.FormulaMedicaRepository;
import com.uniminuto.clinica.repository.HistoriaMedicaRepository;
import com.uniminuto.clinica.repository.MascotaRepository;
import com.uniminuto.clinica.repository.MedicamentoRepository;
import com.uniminuto.clinica.repository.MedicoRepository;
import com.uniminuto.clinica.utils.EstadoCita;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Pruebas de las reglas de negocio de las fórmulas médicas.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class FormulaServiceImplTests {

    @Mock
    private FormulaMedicaRepository formulaMedicaRepository;
    @Mock
    private CitaRepository citaRepository;
    @Mock
    private MedicamentoRepository medicamentoRepository;
    @Mock
    private MascotaRepository mascotaRepository;
    @Mock
    private MedicoRepository medicoRepository;
    @Mock
    private HistoriaMedicaRepository historiaMedicaRepository;
    @InjectMocks
    private FormulaServiceImpl formulaService;

    private Mascota mascota;
    private Medico medico;
    private Cita cita;
    private Medicamento medicamento;

    @BeforeEach
    void preparar() {
        Cliente cliente = new Cliente();
        cliente.setId(1L);
        mascota = new Mascota();
        mascota.setMascotaId(1);
        mascota.setCliente(cliente);
        medico = new Medico();
        medico.setId(1L);
        medico.setEstado(true);
        cita = new Cita();
        cita.setId(1L);
        cita.setMascota(mascota);
        cita.setMedico(medico);
        cita.setEstado(EstadoCita.ATENDIDA);
        cita.setFechaHora(LocalDateTime.now().minusDays(1));
        medicamento = new Medicamento();
        medicamento.setId(1L);
        medicamento.setActivo(true);
        medicamento.setFechaVence(LocalDate.now().plusYears(1));
        when(mascotaRepository.findById(1)).thenReturn(Optional.of(mascota));
        when(medicoRepository.findById(1L)).thenReturn(Optional.of(medico));
        when(citaRepository.findById(1L)).thenReturn(Optional.of(cita));
        when(medicamentoRepository.findById(1L)).thenReturn(Optional.of(medicamento));
        when(historiaMedicaRepository.findByCita_Id(1L)).thenReturn(Optional.empty());
    }

    @Test
    void guardaUnaFormulaValida() {
        formulaService.guardarFormula(solicitud());

        org.mockito.ArgumentCaptor<FormulaMedica> captor = org.mockito.ArgumentCaptor.forClass(FormulaMedica.class);
        verify(formulaMedicaRepository).save(captor.capture());
        assertEquals(mascota, captor.getValue().getMascota());
        assertEquals(medico, captor.getValue().getMedico());
        assertTrue(captor.getValue().getActivo());
    }

    @Test
    void rechazaMascotaInexistente() {
        when(mascotaRepository.findById(50)).thenReturn(Optional.empty());
        FormulaRq rq = solicitud();
        rq.setMascotaId(50);

        assertThrows(NotFoundException.class, () -> formulaService.guardarFormula(rq));
        verify(formulaMedicaRepository, never()).save(any(FormulaMedica.class));
    }

    @Test
    void rechazaMascotaSinClienteRegistrado() {
        mascota.setCliente(null);

        assertThrows(BadRequestException.class, () -> formulaService.guardarFormula(solicitud()));
    }

    @Test
    void rechazaVeterinarioInexistente() {
        when(medicoRepository.findById(9L)).thenReturn(Optional.empty());
        FormulaRq rq = solicitud();
        rq.setMedicoId(9L);

        assertThrows(NotFoundException.class, () -> formulaService.guardarFormula(rq));
    }

    @Test
    void rechazaConsultaDeOtraMascota() {
        Mascota otra = new Mascota();
        otra.setMascotaId(2);
        cita.setMascota(otra);

        BadRequestException error = assertThrows(BadRequestException.class,
                () -> formulaService.guardarFormula(solicitud()));

        assertEquals("La consulta seleccionada pertenece a otra mascota.", error.getMessage());
    }

    @Test
    void rechazaFechaDeEmisionAnteriorALaConsulta() {
        FormulaRq rq = solicitud();
        rq.setFechaEmision(cita.getFechaHora().minusHours(1));

        BadRequestException error = assertThrows(BadRequestException.class, () -> formulaService.guardarFormula(rq));

        assertEquals("La fecha de emisión no puede ser anterior a la fecha de la consulta asociada.",
                error.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"0 ml", "-1 ml", "abc", "5", "0/2 tableta"})
    void rechazaDosisInvalida(String dosis) {
        FormulaRq rq = solicitud();
        rq.setDosis(dosis);

        assertThrows(BadRequestException.class, () -> formulaService.guardarFormula(rq));
    }

    @ParameterizedTest
    @ValueSource(strings = {"0 días", "abc", "-3 días", "400 días", "2 años"})
    void rechazaDuracionInvalida(String duracion) {
        FormulaRq rq = solicitud();
        rq.setDuracion(duracion);

        assertThrows(BadRequestException.class, () -> formulaService.guardarFormula(rq));
    }

    @ParameterizedTest
    @ValueSource(strings = {"0.5 ml", "1 tableta", "1/2 tableta", "2,5 mg"})
    void aceptaDosisValida(String dosis) {
        FormulaRq rq = solicitud();
        rq.setDosis(dosis);

        formulaService.guardarFormula(rq);

        verify(formulaMedicaRepository).save(any(FormulaMedica.class));
    }

    @Test
    void rechazaMedicamentoVencido() {
        medicamento.setFechaVence(LocalDate.now().minusDays(1));

        assertThrows(ConflictException.class, () -> formulaService.guardarFormula(solicitud()));
    }

    @Test
    void rechazaFormulaSobreCitaCancelada() {
        cita.setEstado(EstadoCita.CANCELADA);

        assertThrows(ConflictException.class, () -> formulaService.guardarFormula(solicitud()));
    }

    @Test
    void inactivarEnLugarDeEliminar() {
        FormulaMedica formula = formulaExistente();
        when(formulaMedicaRepository.findById(7L)).thenReturn(Optional.of(formula));

        formulaService.cambiarEstadoFormula(7L, false);

        assertFalse(formula.getActivo());
        verify(formulaMedicaRepository).save(formula);
    }

    @Test
    void noPermiteEditarUnaFormulaInactiva() {
        FormulaMedica formula = formulaExistente();
        formula.setActivo(false);
        when(formulaMedicaRepository.findById(7L)).thenReturn(Optional.of(formula));
        FormulaRq rq = solicitud();
        rq.setId(7L);

        assertThrows(ConflictException.class, () -> formulaService.actualizarFormula(rq));
    }

    @Test
    void noPermiteCambiarLaConsultaDeUnaFormulaEmitida() {
        FormulaMedica formula = formulaExistente();
        when(formulaMedicaRepository.findById(7L)).thenReturn(Optional.of(formula));
        FormulaRq rq = solicitud();
        rq.setId(7L);
        rq.setCitaId(99L);

        assertThrows(ConflictException.class, () -> formulaService.actualizarFormula(rq));
    }

    @Test
    void editaLosDatosPermitidos() {
        FormulaMedica formula = formulaExistente();
        when(formulaMedicaRepository.findById(7L)).thenReturn(Optional.of(formula));
        FormulaRq rq = solicitud();
        rq.setId(7L);
        rq.setDosis("1 tableta");

        formulaService.actualizarFormula(rq);

        assertEquals("1 tableta", formula.getDosis());
        verify(formulaMedicaRepository).save(formula);
    }

    private FormulaMedica formulaExistente() {
        FormulaMedica formula = new FormulaMedica();
        formula.setId(7L);
        formula.setCita(cita);
        formula.setMascota(mascota);
        formula.setMedico(medico);
        formula.setMedicamento(medicamento);
        formula.setActivo(true);
        formula.setFechaEmision(LocalDateTime.now().minusHours(1));
        return formula;
    }

    private FormulaRq solicitud() {
        FormulaRq rq = new FormulaRq();
        rq.setMascotaId(1);
        rq.setCitaId(1L);
        rq.setMedicoId(1L);
        rq.setMedicamentoId(1L);
        rq.setDosis("0.5 ml");
        rq.setFrecuencia("Cada 12 horas");
        rq.setDuracion("5 días");
        rq.setIndicaciones("Administrar con comida");
        return rq;
    }
}

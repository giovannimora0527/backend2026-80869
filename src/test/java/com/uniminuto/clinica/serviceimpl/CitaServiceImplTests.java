package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.Cita;
import com.uniminuto.clinica.entity.Cliente;
import com.uniminuto.clinica.entity.Mascota;
import com.uniminuto.clinica.entity.Medico;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.exception.ConflictException;
import com.uniminuto.clinica.exception.NotFoundException;
import com.uniminuto.clinica.models.CitaRq;
import com.uniminuto.clinica.repository.CitaRepository;
import com.uniminuto.clinica.repository.ClienteRepository;
import com.uniminuto.clinica.repository.MascotaRepository;
import com.uniminuto.clinica.repository.MedicoRepository;
import com.uniminuto.clinica.service.HistoriaService;
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

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Pruebas de las reglas de negocio de las citas.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CitaServiceImplTests {

    @Mock
    private CitaRepository citaRepository;
    @Mock
    private MascotaRepository mascotaRepository;
    @Mock
    private MedicoRepository medicoRepository;
    @Mock
    private ClienteRepository clienteRepository;
    @Mock
    private HistoriaService historiaService;
    @InjectMocks
    private CitaServiceImpl citaService;

    private Cliente cliente;
    private Mascota mascota;
    private Medico medico;

    @BeforeEach
    void preparar() {
        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setActivo(true);
        mascota = new Mascota();
        mascota.setMascotaId(1);
        mascota.setCliente(cliente);
        mascota.setActivo(true);
        medico = new Medico();
        medico.setId(1L);
        medico.setEstado(true);
        when(mascotaRepository.findById(1)).thenReturn(Optional.of(mascota));
        when(medicoRepository.findById(1L)).thenReturn(Optional.of(medico));
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
    }

    @ParameterizedTest
    @ValueSource(longs = {1, 1440})
    void rechazaCrearCitaEnElPasado(long minutos) {
        CitaRq rq = solicitud(LocalDateTime.now().minusMinutes(minutos));

        BadRequestException error = assertThrows(BadRequestException.class, () -> citaService.crearCita(rq));

        assertEquals("La fecha y hora de la cita no puede ser anterior a la fecha y hora actual",
                error.getMessage());
        verify(citaRepository, never()).save(any(Cita.class));
    }

    @Test
    void creaCitaProgramadaPorDefecto() {
        CitaRq rq = solicitud(LocalDateTime.now().plusDays(1));

        citaService.crearCita(rq);

        org.mockito.ArgumentCaptor<Cita> captor = org.mockito.ArgumentCaptor.forClass(Cita.class);
        verify(citaRepository).save(captor.capture());
        assertEquals(EstadoCita.PROGRAMADA, captor.getValue().getEstado());
        assertEquals(cliente, captor.getValue().getCliente());
    }

    @Test
    void rechazaMascotaQueNoPerteneceAlCliente() {
        Cliente otro = new Cliente();
        otro.setId(2L);
        otro.setActivo(true);
        when(clienteRepository.findById(2L)).thenReturn(Optional.of(otro));
        CitaRq rq = solicitud(LocalDateTime.now().plusDays(1));
        rq.setClienteId(2L);

        BadRequestException error = assertThrows(BadRequestException.class, () -> citaService.crearCita(rq));

        assertEquals("La mascota seleccionada no pertenece al cliente seleccionado.", error.getMessage());
        verify(citaRepository, never()).save(any(Cita.class));
    }

    @Test
    void rechazaClienteInexistente() {
        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());
        CitaRq rq = solicitud(LocalDateTime.now().plusDays(1));
        rq.setClienteId(99L);

        assertThrows(NotFoundException.class, () -> citaService.crearCita(rq));
    }

    @Test
    void rechazaVeterinarioInexistente() {
        when(medicoRepository.findById(77L)).thenReturn(Optional.empty());
        CitaRq rq = solicitud(LocalDateTime.now().plusDays(1));
        rq.setMedicoId(77L);

        assertThrows(NotFoundException.class, () -> citaService.crearCita(rq));
    }

    @Test
    void rechazaVeterinarioOcupadoEnLaVentanaDe15Minutos() {
        when(citaRepository.contarPorMedicoEnRango(eq(1L), any(), any(), eq(EstadoCita.CANCELADA), anyLong()))
                .thenReturn(1L);
        CitaRq rq = solicitud(LocalDateTime.now().plusDays(1));

        assertThrows(ConflictException.class, () -> citaService.crearCita(rq));
        verify(citaRepository, never()).save(any(Cita.class));
    }

    @Test
    void rechazaMascotaConDosCitasSimultaneas() {
        when(citaRepository.contarPorMascotaEnRango(eq(1), any(), any(), eq(EstadoCita.CANCELADA), anyLong()))
                .thenReturn(1L);
        CitaRq rq = solicitud(LocalDateTime.now().plusDays(1));

        assertThrows(ConflictException.class, () -> citaService.crearCita(rq));
    }

    @Test
    void rechazaCrearCitaYaAtendida() {
        CitaRq rq = solicitud(LocalDateTime.now().plusDays(1));
        rq.setEstado(EstadoCita.ATENDIDA);

        assertThrows(BadRequestException.class, () -> citaService.crearCita(rq));
    }

    @Test
    void noPermiteEditarUnaCitaCancelada() {
        Cita existente = citaExistente(EstadoCita.CANCELADA, LocalDateTime.now().plusDays(2));
        when(citaRepository.findById(10L)).thenReturn(Optional.of(existente));
        CitaRq rq = solicitud(LocalDateTime.now().plusDays(3));
        rq.setId(10L);

        assertThrows(ConflictException.class, () -> citaService.actualizarCita(rq));
        verify(citaRepository, never()).save(any(Cita.class));
    }

    @Test
    void noPermiteEditarUnaCitaAtendida() {
        Cita existente = citaExistente(EstadoCita.ATENDIDA, LocalDateTime.now().minusDays(1));
        when(citaRepository.findById(10L)).thenReturn(Optional.of(existente));
        CitaRq rq = solicitud(LocalDateTime.now().plusDays(3));
        rq.setId(10L);

        assertThrows(ConflictException.class, () -> citaService.actualizarCita(rq));
    }

    @Test
    void permiteCambiarFechaDeUnaCitaPendiente() {
        Cita existente = citaExistente(EstadoCita.PENDIENTE, LocalDateTime.now().plusDays(2));
        when(citaRepository.findById(10L)).thenReturn(Optional.of(existente));
        CitaRq rq = solicitud(LocalDateTime.now().plusDays(5));
        rq.setId(10L);
        rq.setEstado(EstadoCita.PENDIENTE);

        citaService.actualizarCita(rq);

        assertEquals(rq.getFechaHora(), existente.getFechaHora());
        verify(citaRepository).save(existente);
    }

    @Test
    void noPermiteCambiarLaMascotaDeUnaCita() {
        Cita existente = citaExistente(EstadoCita.PENDIENTE, LocalDateTime.now().plusDays(2));
        when(citaRepository.findById(10L)).thenReturn(Optional.of(existente));
        CitaRq rq = solicitud(LocalDateTime.now().plusDays(5));
        rq.setId(10L);
        rq.setMascotaId(2);

        assertThrows(BadRequestException.class, () -> citaService.actualizarCita(rq));
    }

    @Test
    void marcarAtendidaGeneraLaHistoriaClinica() {
        Cita existente = citaExistente(EstadoCita.CONFIRMADA, LocalDateTime.now().minusHours(2));
        when(citaRepository.findById(10L)).thenReturn(Optional.of(existente));

        citaService.cambiarEstadoCita(10L, "atendida");

        assertEquals(EstadoCita.ATENDIDA, existente.getEstado());
        verify(historiaService).asegurarHistoriaDeCita(existente);
    }

    @Test
    void noPermiteAtenderUnaCitaFutura() {
        Cita existente = citaExistente(EstadoCita.CONFIRMADA, LocalDateTime.now().plusDays(1));
        when(citaRepository.findById(10L)).thenReturn(Optional.of(existente));

        assertThrows(ConflictException.class, () -> citaService.cambiarEstadoCita(10L, EstadoCita.ATENDIDA));
    }

    @Test
    void canceladaSoloVuelveConLaOperacionExplicitaDeReactivar() {
        Cita existente = citaExistente(EstadoCita.CANCELADA, LocalDateTime.now().plusDays(2));
        when(citaRepository.findById(10L)).thenReturn(Optional.of(existente));

        assertThrows(ConflictException.class,
                () -> citaService.cambiarEstadoCita(10L, EstadoCita.PROGRAMADA));

        citaService.reactivarCita(10L);

        assertEquals(EstadoCita.PROGRAMADA, existente.getEstado());
    }

    @Test
    void soloSePuedeReactivarUnaCitaCancelada() {
        Cita existente = citaExistente(EstadoCita.PROGRAMADA, LocalDateTime.now().plusDays(2));
        when(citaRepository.findById(10L)).thenReturn(Optional.of(existente));

        assertThrows(ConflictException.class, () -> citaService.reactivarCita(10L));
    }

    private Cita citaExistente(String estado, LocalDateTime fechaHora) {
        Cita cita = new Cita();
        cita.setId(10L);
        cita.setCliente(cliente);
        cita.setMascota(mascota);
        cita.setMedico(medico);
        cita.setEstado(estado);
        cita.setFechaHora(fechaHora);
        cita.setMotivo("Consulta");
        return cita;
    }

    private CitaRq solicitud(LocalDateTime fechaHora) {
        CitaRq rq = new CitaRq();
        rq.setMascotaId(1);
        rq.setMedicoId(1L);
        rq.setFechaHora(fechaHora);
        rq.setMotivo("Consulta");
        return rq;
    }
}

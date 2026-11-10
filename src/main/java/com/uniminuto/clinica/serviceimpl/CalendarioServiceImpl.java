package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.Cita;
import com.uniminuto.clinica.entity.Cliente;
import com.uniminuto.clinica.entity.Mascota;
import com.uniminuto.clinica.entity.Medico;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.CambiarEstadoCitaRq;
import com.uniminuto.clinica.models.CitaRs;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.models.ReprogramarCitaRq;
import com.uniminuto.clinica.repository.CitaRepository;
import com.uniminuto.clinica.repository.HorarioMedicoRepository;
import com.uniminuto.clinica.repository.MascotaRepository;
import com.uniminuto.clinica.repository.MedicoRepository;
import com.uniminuto.clinica.security.ContextoSeguridad;
import com.uniminuto.clinica.security.UsuarioAutenticado;
import com.uniminuto.clinica.service.CalendarioService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * Implementacion del servicio de calendario de citas.
 *
 * Determina el alcance de la informacion segun el rol del usuario autenticado
 * (un medico solo ve sus citas) y aplica las reglas de negocio para el cambio
 * de estado y la reprogramacion.
 */
@Service
public class CalendarioServiceImpl implements CalendarioService {

    private static final String ROL_MEDICO = "Medico";
    private static final int DURACION_CITA_MINUTOS = 30;
    private static final DateTimeFormatter FORMATO =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private static final String ESTADO_PROGRAMADA = "PROGRAMADA";
    private static final String ESTADO_CONFIRMADA = "CONFIRMADA";
    private static final String ESTADO_ATENDIDA = "ATENDIDA";
    private static final String ESTADO_CANCELADA = "CANCELADA";
    private static final String ESTADO_FINALIZADA = "FINALIZADA";

    private final CitaRepository citaRepository;
    private final MedicoRepository medicoRepository;
    private final MascotaRepository mascotaRepository;
    private final HorarioMedicoRepository horarioRepository;

    public CalendarioServiceImpl(CitaRepository citaRepository,
                                 MedicoRepository medicoRepository,
                                 MascotaRepository mascotaRepository,
                                 HorarioMedicoRepository horarioRepository) {
        this.citaRepository = citaRepository;
        this.medicoRepository = medicoRepository;
        this.mascotaRepository = mascotaRepository;
        this.horarioRepository = horarioRepository;
    }

    @Override
    public List<CitaRs> listarCitasCalendario(LocalDateTime inicio, LocalDateTime fin) {
        if (inicio == null || fin == null) {
            throw new BadRequestException("El rango de fechas es obligatorio.");
        }
        if (inicio.isAfter(fin)) {
            throw new BadRequestException("La fecha de inicio no puede ser posterior a la de fin.");
        }

        Medico medicoSesion = this.obtenerMedicoDeLaSesion();
        List<Cita> citas;
        if (medicoSesion != null) {
            // Un medico ve unicamente sus citas.
            citas = citaRepository.findByMedicoAndFechaHoraBetweenOrderByFechaHoraAsc(
                    medicoSesion, inicio, fin);
        } else {
            // Recepcion o administrador ven todas las citas del rango.
            citas = citaRepository.findByFechaHoraBetween(inicio, fin);
            citas.sort((a, b) -> a.getFechaHora().compareTo(b.getFechaHora()));
        }
        return citas.stream().map(this::mapear).toList();
    }

    @Override
    public List<CitaRs> historialPorMascota(Integer mascotaId) {
        if (mascotaId == null) {
            throw new BadRequestException("El identificador de la mascota es obligatorio.");
        }
        Optional<Mascota> mascota = mascotaRepository.findById(mascotaId);
        if (mascota.isEmpty()) {
            throw new BadRequestException("La mascota no existe.");
        }
        return citaRepository.findByMascotaOrderByFechaHoraDesc(mascota.get())
                .stream().map(this::mapear).toList();
    }

    @Override
    @Transactional
    public MiRespuestaRS cambiarEstado(CambiarEstadoCitaRq rq) {
        if (rq == null || rq.getCitaId() == null || rq.getEstado() == null) {
            throw new BadRequestException("La cita y el nuevo estado son obligatorios.");
        }
        Cita cita = this.buscarCita(rq.getCitaId());
        String nuevoEstado = rq.getEstado().trim().toUpperCase();

        switch (nuevoEstado) {
            case ESTADO_CONFIRMADA, ESTADO_ATENDIDA, ESTADO_CANCELADA, ESTADO_FINALIZADA -> { }
            default -> throw new BadRequestException("Estado no valido: " + rq.getEstado());
        }

        if (ESTADO_CANCELADA.equals(cita.getEstado())) {
            throw new BadRequestException("La cita esta cancelada y conserva su historial; no se puede modificar.");
        }

        cita.setEstado(nuevoEstado);
        citaRepository.save(cita);

        MiRespuestaRS rta = new MiRespuestaRS();
        rta.setStatus(200);
        rta.setMessage("La cita cambio al estado " + nuevoEstado + ".");
        return rta;
    }

    @Override
    @Transactional
    public MiRespuestaRS reprogramar(ReprogramarCitaRq rq) {
        if (rq == null || rq.getCitaId() == null || rq.getNuevaFechaHora() == null) {
            throw new BadRequestException("La cita y la nueva fecha son obligatorias.");
        }
        Cita cita = this.buscarCita(rq.getCitaId());

        if (ESTADO_CANCELADA.equals(cita.getEstado()) || ESTADO_ATENDIDA.equals(cita.getEstado())
                || ESTADO_FINALIZADA.equals(cita.getEstado())) {
            throw new BadRequestException(
                    "No se puede reprogramar una cita cancelada, atendida o finalizada.");
        }
        if (rq.getNuevaFechaHora().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("La nueva fecha no puede ser anterior a la fecha actual.");
        }

        Medico medico = cita.getMedico();
        if (!Boolean.TRUE.equals(medico.getEstado())) {
            throw new BadRequestException("No se puede reprogramar: el medico esta inactivo.");
        }
        this.validarDisponibilidadYSolapamiento(medico, rq.getNuevaFechaHora(), cita.getId());

        cita.setFechaHora(rq.getNuevaFechaHora());
        citaRepository.save(cita);

        MiRespuestaRS rta = new MiRespuestaRS();
        rta.setStatus(200);
        rta.setMessage("Cita reprogramada para el " + rq.getNuevaFechaHora().format(FORMATO) + ".");
        return rta;
    }

    @Override
    public byte[] generarReporteExcel(Long medicoId, LocalDateTime inicio, LocalDateTime fin) {
        if (inicio == null || fin == null) {
            throw new BadRequestException("El rango de fechas es obligatorio para el reporte.");
        }
        Medico medico = this.resolverMedicoReporte(medicoId);
        if (medico == null) {
            throw new BadRequestException("Debe indicar un medico para generar el reporte.");
        }

        List<Cita> citas = citaRepository
                .findByMedicoAndFechaHoraBetweenOrderByFechaHoraAsc(medico, inicio, fin);

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet hoja = workbook.createSheet("Citas");
            String[] encabezados = {"Fecha y hora", "Estado", "Mascota", "Propietario",
                    "Telefono", "Motivo"};
            Row filaEnc = hoja.createRow(0);
            for (int i = 0; i < encabezados.length; i++) {
                Cell celda = filaEnc.createCell(i);
                celda.setCellValue(encabezados[i]);
            }
            int fila = 1;
            for (Cita c : citas) {
                Row r = hoja.createRow(fila++);
                r.createCell(0).setCellValue(c.getFechaHora() != null ? c.getFechaHora().format(FORMATO) : "");
                r.createCell(1).setCellValue(c.getEstado() != null ? c.getEstado() : "");
                r.createCell(2).setCellValue(c.getMascota() != null ? c.getMascota().getNombreMascota() : "");
                Cliente cliente = c.getCliente();
                r.createCell(3).setCellValue(cliente != null
                        ? (texto(cliente.getNombres()) + " " + texto(cliente.getApellidos())).trim() : "");
                r.createCell(4).setCellValue(cliente != null ? texto(cliente.getTelefono()) : "");
                r.createCell(5).setCellValue(c.getMotivo() != null ? c.getMotivo() : "");
            }
            for (int i = 0; i < encabezados.length; i++) {
                hoja.autoSizeColumn(i);
            }
            workbook.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new BadRequestException("No fue posible generar el reporte: " + e.getMessage());
        }
    }

    // ----------------------------------------------------------------
    // Auxiliares
    // ----------------------------------------------------------------

    /**
     * Devuelve el medico asociado al usuario autenticado si su rol es Medico;
     * en caso contrario (recepcion, administrador) devuelve null, lo que indica
     * acceso a todas las citas.
     */
    private Medico obtenerMedicoDeLaSesion() {
        UsuarioAutenticado usuario = ContextoSeguridad.obtenerUsuario();
        if (usuario == null || usuario.rol() == null) {
            return null;
        }
        if (!ROL_MEDICO.equalsIgnoreCase(usuario.rol())) {
            return null;
        }
        if (usuario.usuarioId() == null) {
            throw new BadRequestException("No fue posible identificar al usuario de la sesion.");
        }
        return medicoRepository.findByUsuarioId(usuario.usuarioId())
                .orElseThrow(() -> new BadRequestException(
                        "Su usuario no esta asociado a un medico. Contacte al administrador."));
    }

    private Medico resolverMedicoReporte(Long medicoId) {
        Medico medicoSesion = this.obtenerMedicoDeLaSesion();
        if (medicoSesion != null) {
            // Un medico solo puede generar su propio reporte.
            return medicoSesion;
        }
        if (medicoId == null) {
            return null;
        }
        return medicoRepository.findById(medicoId).orElse(null);
    }

    private Cita buscarCita(Long citaId) {
        Optional<Cita> opt = citaRepository.findById(citaId);
        if (opt.isEmpty()) {
            throw new BadRequestException("La cita no existe.");
        }
        return opt.get();
    }

    /**
     * Valida que el medico este disponible (dentro de su horario, si lo tiene
     * definido) y que no exista otra cita suya que se solape con la nueva fecha.
     */
    private void validarDisponibilidadYSolapamiento(Medico medico, LocalDateTime fechaHora, Long citaIdExcluir) {
        // Solapamiento: busca citas del medico en la ventana de la nueva cita.
        LocalDateTime desde = fechaHora.minusMinutes(DURACION_CITA_MINUTOS);
        LocalDateTime hasta = fechaHora.plusMinutes(DURACION_CITA_MINUTOS);
        List<Cita> solapadas = citaRepository.findByMedicoAndFechaHoraBetween(medico, desde, hasta);
        boolean hayConflicto = solapadas.stream()
                .anyMatch(c -> !c.getId().equals(citaIdExcluir)
                        && !ESTADO_CANCELADA.equals(c.getEstado()));
        if (hayConflicto) {
            throw new BadRequestException(
                    "El medico ya tiene una cita en ese horario.");
        }

        // Disponibilidad: si el medico tiene horarios definidos, la hora debe caer dentro.
        List<com.uniminuto.clinica.entity.HorarioMedico> horarios =
                horarioRepository.findByMedicoIdAndActivoTrue(medico.getId());
        if (!horarios.isEmpty()) {
            int dia = fechaHora.getDayOfWeek().getValue();
            boolean disponible = horarios.stream().anyMatch(h ->
                    h.getDiaSemana() == dia
                            && !fechaHora.toLocalTime().isBefore(h.getHoraInicio())
                            && !fechaHora.toLocalTime().isAfter(h.getHoraFin()));
            if (!disponible) {
                throw new BadRequestException(
                        "El medico no atiende en el horario seleccionado.");
            }
        }
    }

    private CitaRs mapear(Cita c) {
        CitaRs rs = new CitaRs();
        rs.setId(c.getId());
        rs.setFechaHora(c.getFechaHora());
        rs.setFechaHoraFin(c.getFechaHora() != null
                ? c.getFechaHora().plus(Duration.ofMinutes(DURACION_CITA_MINUTOS)) : null);
        rs.setEstado(c.getEstado());
        rs.setMotivo(c.getMotivo());

        Mascota mascota = c.getMascota();
        if (mascota != null) {
            rs.setMascotaId(mascota.getMascotaId() != null ? mascota.getMascotaId().longValue() : null);
            rs.setMascotaNombre(mascota.getNombreMascota());
        }
        Cliente cliente = c.getCliente();
        if (cliente != null) {
            rs.setClienteId(cliente.getId());
            rs.setClienteNombre((texto(cliente.getNombres()) + " " + texto(cliente.getApellidos())).trim());
            rs.setClienteTelefono(cliente.getTelefono());
        }
        Medico medico = c.getMedico();
        if (medico != null) {
            rs.setMedicoId(medico.getId());
            rs.setMedicoNombre((texto(medico.getNombres()) + " " + texto(medico.getApellidos())).trim());
            if (medico.getEspecializacion() != null) {
                rs.setEspecializacion(medico.getEspecializacion().getNombre());
            }
        }
        return rs;
    }

    private String texto(String valor) {
        return valor != null ? valor : "";
    }
}

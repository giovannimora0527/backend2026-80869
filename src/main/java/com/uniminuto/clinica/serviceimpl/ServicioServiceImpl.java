package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.Especializacion;
import com.uniminuto.clinica.entity.LogTarifa;
import com.uniminuto.clinica.entity.Servicio;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.LogTarifaRs;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.models.ServicioRq;
import com.uniminuto.clinica.models.ServicioRs;
import com.uniminuto.clinica.repository.EspecializacionRepository;
import com.uniminuto.clinica.repository.LogTarifaRepository;
import com.uniminuto.clinica.repository.ServicioRepository;
import com.uniminuto.clinica.security.ContextoSeguridad;
import com.uniminuto.clinica.security.UsuarioAutenticado;
import com.uniminuto.clinica.service.ServicioService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Implementacion del servicio de servicios y tarifas.
 *
 * Al actualizar un servicio, si cambia el precio o el porcentaje de IVA, se
 * registra automaticamente una entrada en el historial de tarifas (log_tarifa).
 */
@Service
public class ServicioServiceImpl implements ServicioService {

    private final ServicioRepository servicioRepository;
    private final EspecializacionRepository especializacionRepository;
    private final LogTarifaRepository logTarifaRepository;

    public ServicioServiceImpl(ServicioRepository servicioRepository,
                               EspecializacionRepository especializacionRepository,
                               LogTarifaRepository logTarifaRepository) {
        this.servicioRepository = servicioRepository;
        this.especializacionRepository = especializacionRepository;
        this.logTarifaRepository = logTarifaRepository;
    }

    @Override
    public List<ServicioRs> listar() {
        return servicioRepository.findAllByOrderByNombreAsc().stream()
                .map(this::mapear).toList();
    }

    @Override
    public List<ServicioRs> listarActivos() {
        return servicioRepository.findByActivoTrueOrderByNombreAsc().stream()
                .map(this::mapear).toList();
    }

    @Override
    @Transactional
    public MiRespuestaRS crear(ServicioRq rq) {
        this.validar(rq);
        if (servicioRepository.existsByNombreIgnoreCase(rq.getNombre().trim())) {
            throw new BadRequestException("Ya existe un servicio con ese nombre.");
        }
        Servicio servicio = new Servicio();
        servicio.setNombre(rq.getNombre().trim());
        servicio.setDescripcion(rq.getDescripcion());
        servicio.setEspecializacion(this.resolverEspecializacion(rq.getEspecializacionId()));
        servicio.setPrecio(rq.getPrecio() != null ? rq.getPrecio() : BigDecimal.ZERO);
        servicio.setIvaPorcentaje(rq.getIvaPorcentaje() != null ? rq.getIvaPorcentaje() : BigDecimal.ZERO);
        servicio.setActivo(rq.getActivo() != null ? rq.getActivo() : Boolean.TRUE);
        servicio.setFechaCreacion(LocalDateTime.now());
        servicioRepository.save(servicio);

        MiRespuestaRS rta = new MiRespuestaRS();
        rta.setStatus(200);
        rta.setMessage("Servicio creado correctamente.");
        return rta;
    }

    @Override
    @Transactional
    public MiRespuestaRS actualizar(ServicioRq rq) {
        this.validar(rq);
        if (rq.getId() == null) {
            throw new BadRequestException("El identificador del servicio es obligatorio.");
        }
        Optional<Servicio> opt = servicioRepository.findById(rq.getId());
        if (opt.isEmpty()) {
            throw new BadRequestException("El servicio no existe.");
        }
        if (servicioRepository.existsByNombreIgnoreCaseAndIdNot(rq.getNombre().trim(), rq.getId())) {
            throw new BadRequestException("Ya existe otro servicio con ese nombre.");
        }

        Servicio servicio = opt.get();
        BigDecimal precioAnterior = servicio.getPrecio();
        BigDecimal ivaAnterior = servicio.getIvaPorcentaje();
        BigDecimal precioNuevo = rq.getPrecio() != null ? rq.getPrecio() : BigDecimal.ZERO;
        BigDecimal ivaNuevo = rq.getIvaPorcentaje() != null ? rq.getIvaPorcentaje() : BigDecimal.ZERO;

        servicio.setNombre(rq.getNombre().trim());
        servicio.setDescripcion(rq.getDescripcion());
        servicio.setEspecializacion(this.resolverEspecializacion(rq.getEspecializacionId()));
        servicio.setPrecio(precioNuevo);
        servicio.setIvaPorcentaje(ivaNuevo);
        if (rq.getActivo() != null) {
            servicio.setActivo(rq.getActivo());
        }
        servicio.setFechaModificacion(LocalDateTime.now());
        servicioRepository.save(servicio);

        // Registrar en el historial si cambio el precio o el IVA.
        if (precioAnterior.compareTo(precioNuevo) != 0 || ivaAnterior.compareTo(ivaNuevo) != 0) {
            LogTarifa log = new LogTarifa();
            log.setServicioId(servicio.getId());
            log.setPrecioAnterior(precioAnterior);
            log.setPrecioNuevo(precioNuevo);
            log.setIvaAnterior(ivaAnterior);
            log.setIvaNuevo(ivaNuevo);
            log.setFecha(LocalDateTime.now());
            log.setUsuarioResponsable(this.usuarioResponsable());
            logTarifaRepository.save(log);
        }

        MiRespuestaRS rta = new MiRespuestaRS();
        rta.setStatus(200);
        rta.setMessage("Servicio actualizado correctamente.");
        return rta;
    }

    @Override
    public List<LogTarifaRs> historialTarifas(Long servicioId) {
        if (servicioId == null) {
            throw new BadRequestException("El identificador del servicio es obligatorio.");
        }
        return logTarifaRepository.findByServicioIdOrderByFechaDesc(servicioId).stream()
                .map(log -> {
                    LogTarifaRs rs = new LogTarifaRs();
                    rs.setId(log.getId());
                    rs.setServicioId(log.getServicioId());
                    rs.setPrecioAnterior(log.getPrecioAnterior());
                    rs.setPrecioNuevo(log.getPrecioNuevo());
                    rs.setIvaAnterior(log.getIvaAnterior());
                    rs.setIvaNuevo(log.getIvaNuevo());
                    rs.setFecha(log.getFecha());
                    rs.setUsuarioResponsable(log.getUsuarioResponsable());
                    return rs;
                }).toList();
    }

    // ----------------------------------------------------------------

    private Especializacion resolverEspecializacion(Long especializacionId) {
        if (especializacionId == null) {
            return null;
        }
        return especializacionRepository.findById(especializacionId).orElse(null);
    }

    private String usuarioResponsable() {
        UsuarioAutenticado usuario = ContextoSeguridad.obtenerUsuario();
        return usuario != null ? usuario.username() : "sistema";
    }

    private void validar(ServicioRq rq) {
        if (rq == null) {
            throw new BadRequestException("Los datos del servicio son obligatorios.");
        }
        if (rq.getNombre() == null || rq.getNombre().isBlank()) {
            throw new BadRequestException("El nombre del servicio es obligatorio.");
        }
        if (rq.getPrecio() != null && rq.getPrecio().signum() < 0) {
            throw new BadRequestException("El precio no puede ser negativo.");
        }
        if (rq.getIvaPorcentaje() != null
                && (rq.getIvaPorcentaje().signum() < 0
                || rq.getIvaPorcentaje().compareTo(new BigDecimal("100")) > 0)) {
            throw new BadRequestException("El IVA debe estar entre 0 y 100.");
        }
    }

    private ServicioRs mapear(Servicio s) {
        ServicioRs rs = new ServicioRs();
        rs.setId(s.getId());
        rs.setNombre(s.getNombre());
        rs.setDescripcion(s.getDescripcion());
        if (s.getEspecializacion() != null) {
            rs.setEspecializacionId(s.getEspecializacion().getId());
            rs.setEspecializacionNombre(s.getEspecializacion().getNombre());
        }
        rs.setPrecio(s.getPrecio());
        rs.setIvaPorcentaje(s.getIvaPorcentaje());
        BigDecimal iva = s.getPrecio().multiply(s.getIvaPorcentaje())
                .divide(new BigDecimal("100"));
        rs.setPrecioConIva(s.getPrecio().add(iva));
        rs.setActivo(s.getActivo());
        return rs;
    }
}

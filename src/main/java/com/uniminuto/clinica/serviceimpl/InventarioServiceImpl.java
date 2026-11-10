package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.LoteMedicamento;
import com.uniminuto.clinica.entity.Medicamento;
import com.uniminuto.clinica.entity.MovimientoInventario;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.LoteRq;
import com.uniminuto.clinica.models.LoteRs;
import com.uniminuto.clinica.models.MedicamentoRq;
import com.uniminuto.clinica.models.MedicamentoRs;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.models.MovimientoInventarioRs;
import com.uniminuto.clinica.repository.LoteMedicamentoRepository;
import com.uniminuto.clinica.repository.MedicamentoRepository;
import com.uniminuto.clinica.repository.MovimientoInventarioRepository;
import com.uniminuto.clinica.security.ContextoSeguridad;
import com.uniminuto.clinica.security.UsuarioAutenticado;
import com.uniminuto.clinica.service.InventarioService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementacion del servicio de inventario.
 *
 * Gestiona el catalogo de medicamentos, sus lotes, los indicadores de estado
 * (agotado, stock bajo, proximo a vencer, vencido) y los movimientos de
 * inventario. La existencia de un medicamento se calcula como la suma de las
 * cantidades de sus lotes.
 */
@Service
public class InventarioServiceImpl implements InventarioService {

    /** Dias de antelacion para considerar un lote como proximo a vencer. */
    private static final int DIAS_PROXIMO_VENCIMIENTO = 30;

    private final MedicamentoRepository medicamentoRepository;
    private final LoteMedicamentoRepository loteRepository;
    private final MovimientoInventarioRepository movimientoRepository;

    public InventarioServiceImpl(MedicamentoRepository medicamentoRepository,
                                 LoteMedicamentoRepository loteRepository,
                                 MovimientoInventarioRepository movimientoRepository) {
        this.medicamentoRepository = medicamentoRepository;
        this.loteRepository = loteRepository;
        this.movimientoRepository = movimientoRepository;
    }

    @Override
    public List<MedicamentoRs> listarMedicamentos() {
        return medicamentoRepository.findAllByOrderByNombreAsc().stream()
                .map(this::mapearMedicamento)
                .toList();
    }

    @Override
    public MedicamentoRs obtenerMedicamento(Long id) {
        Medicamento medicamento = this.buscarMedicamento(id);
        return this.mapearMedicamento(medicamento);
    }

    @Override
    @Transactional
    public MiRespuestaRS crearMedicamento(MedicamentoRq rq) {
        this.validarMedicamento(rq, false);

        if (medicamentoRepository.existsByNombreIgnoreCase(rq.getNombre().trim())) {
            throw new BadRequestException("Ya existe un medicamento con ese nombre.");
        }
        if (rq.getCodigo() != null && !rq.getCodigo().isBlank()
                && medicamentoRepository.existsByCodigo(rq.getCodigo().trim())) {
            throw new BadRequestException("Ya existe un medicamento con ese codigo.");
        }

        Medicamento medicamento = new Medicamento();
        this.aplicarDatos(medicamento, rq);
        medicamento.setFechaCreacionRegistro(LocalDateTime.now());
        medicamento = medicamentoRepository.save(medicamento);

        // Lotes iniciales (opcionales).
        if (rq.getLotes() != null) {
            for (LoteRq loteRq : rq.getLotes()) {
                this.crearLote(medicamento, loteRq);
            }
        }

        MiRespuestaRS rta = new MiRespuestaRS();
        rta.setStatus(200);
        rta.setMessage("Medicamento creado correctamente.");
        return rta;
    }

    @Override
    @Transactional
    public MiRespuestaRS actualizarMedicamento(MedicamentoRq rq) {
        this.validarMedicamento(rq, true);
        Medicamento medicamento = this.buscarMedicamento(rq.getId());

        if (medicamentoRepository.existsByNombreIgnoreCaseAndIdNot(rq.getNombre().trim(), rq.getId())) {
            throw new BadRequestException("Ya existe otro medicamento con ese nombre.");
        }
        if (rq.getCodigo() != null && !rq.getCodigo().isBlank()
                && medicamentoRepository.existsByCodigoAndIdNot(rq.getCodigo().trim(), rq.getId())) {
            throw new BadRequestException("Ya existe otro medicamento con ese codigo.");
        }

        this.aplicarDatos(medicamento, rq);
        medicamento.setFechaModificacionRegistro(LocalDateTime.now());
        medicamentoRepository.save(medicamento);

        MiRespuestaRS rta = new MiRespuestaRS();
        rta.setStatus(200);
        rta.setMessage("Medicamento actualizado correctamente.");
        return rta;
    }

    @Override
    @Transactional
    public MiRespuestaRS agregarLote(Long medicamentoId, LoteRq loteRq) {
        Medicamento medicamento = this.buscarMedicamento(medicamentoId);
        this.crearLote(medicamento, loteRq);
        MiRespuestaRS rta = new MiRespuestaRS();
        rta.setStatus(200);
        rta.setMessage("Lote agregado correctamente al inventario.");
        return rta;
    }

    @Override
    public List<MedicamentoRs> listarAgotados() {
        return this.listarMedicamentos().stream()
                .filter(MedicamentoRs::getAgotado)
                .toList();
    }

    @Override
    public List<MedicamentoRs> listarStockBajo() {
        return this.listarMedicamentos().stream()
                .filter(MedicamentoRs::getStockBajo)
                .toList();
    }

    @Override
    public List<MedicamentoRs> listarProximosAVencer() {
        return this.listarMedicamentos().stream()
                .filter(MedicamentoRs::getTieneProximosAVencer)
                .toList();
    }

    @Override
    public List<MedicamentoRs> listarVencidos() {
        return this.listarMedicamentos().stream()
                .filter(MedicamentoRs::getTieneVencidos)
                .toList();
    }

    @Override
    public List<MovimientoInventarioRs> listarMovimientos(Long medicamentoId) {
        Medicamento medicamento = this.buscarMedicamento(medicamentoId);
        return movimientoRepository
                .findByMedicamentoIdOrderByFechaDesc(medicamentoId).stream()
                .map(mov -> {
                    MovimientoInventarioRs rs = new MovimientoInventarioRs();
                    rs.setId(mov.getId());
                    rs.setMedicamentoId(mov.getMedicamentoId());
                    rs.setMedicamentoNombre(medicamento.getNombre());
                    rs.setLoteId(mov.getLoteId());
                    rs.setTipo(mov.getTipo());
                    rs.setCantidad(mov.getCantidad());
                    rs.setFecha(mov.getFecha());
                    rs.setUsuarioResponsable(mov.getUsuarioResponsable());
                    rs.setReferencia(mov.getReferencia());
                    rs.setObservacion(mov.getObservacion());
                    return rs;
                })
                .toList();
    }

    // ----------------------------------------------------------------
    // Metodos auxiliares
    // ----------------------------------------------------------------

    private Medicamento buscarMedicamento(Long id) {
        if (id == null) {
            throw new BadRequestException("El identificador del medicamento es obligatorio.");
        }
        Optional<Medicamento> opt = medicamentoRepository.findById(id);
        if (opt.isEmpty()) {
            throw new BadRequestException("El medicamento no existe.");
        }
        return opt.get();
    }

    /**
     * Crea un lote para un medicamento y registra el movimiento de entrada
     * correspondiente. Este metodo es publico a nivel de paquete para poder
     * reutilizarse desde la carga masiva.
     */
    LoteMedicamento crearLote(Medicamento medicamento, LoteRq loteRq) {
        if (loteRq.getNumeroLote() == null || loteRq.getNumeroLote().isBlank()) {
            throw new BadRequestException("El numero de lote es obligatorio.");
        }
        if (loteRq.getCantidad() == null || loteRq.getCantidad() < 0) {
            throw new BadRequestException("La cantidad del lote no puede ser negativa.");
        }
        if (loteRq.getFechaVencimiento() == null) {
            throw new BadRequestException("La fecha de vencimiento del lote es obligatoria.");
        }

        LoteMedicamento lote = new LoteMedicamento();
        lote.setMedicamento(medicamento);
        lote.setNumeroLote(loteRq.getNumeroLote().trim());
        lote.setCantidad(loteRq.getCantidad());
        lote.setPrecioCompra(loteRq.getPrecioCompra() != null
                ? loteRq.getPrecioCompra() : BigDecimal.ZERO);
        lote.setFechaIngreso(loteRq.getFechaIngreso() != null
                ? loteRq.getFechaIngreso() : LocalDate.now());
        lote.setFechaVencimiento(loteRq.getFechaVencimiento());
        lote.setFechaCreacionRegistro(LocalDateTime.now());
        lote = loteRepository.save(lote);

        // Movimiento de entrada por la cantidad del lote.
        if (lote.getCantidad() > 0) {
            this.registrarMovimiento(medicamento.getId(), lote.getId(),
                    MovimientoInventario.TIPO_ENTRADA, lote.getCantidad(),
                    "Ingreso de lote " + lote.getNumeroLote());
        }
        return lote;
    }

    void registrarMovimiento(Long medicamentoId, Long loteId, String tipo,
                             int cantidad, String referencia) {
        MovimientoInventario mov = new MovimientoInventario();
        mov.setMedicamentoId(medicamentoId);
        mov.setLoteId(loteId);
        mov.setTipo(tipo);
        mov.setCantidad(cantidad);
        mov.setFecha(LocalDateTime.now());
        mov.setUsuarioResponsable(this.obtenerUsuarioResponsable());
        mov.setReferencia(referencia);
        movimientoRepository.save(mov);
    }

    private String obtenerUsuarioResponsable() {
        UsuarioAutenticado usuario = ContextoSeguridad.obtenerUsuario();
        return usuario != null ? usuario.username() : "sistema";
    }

    private void aplicarDatos(Medicamento medicamento, MedicamentoRq rq) {
        medicamento.setNombre(rq.getNombre().trim());
        medicamento.setCodigo(rq.getCodigo() != null && !rq.getCodigo().isBlank()
                ? rq.getCodigo().trim() : null);
        medicamento.setDescripcion(rq.getDescripcion());
        medicamento.setPresentacion(rq.getPresentacion());
        medicamento.setRequiereInvima(rq.getRequiereInvima() != null ? rq.getRequiereInvima() : Boolean.TRUE);
        medicamento.setRegistroInvima(rq.getRegistroInvima());
        medicamento.setPrecioCompra(rq.getPrecioCompra() != null ? rq.getPrecioCompra() : BigDecimal.ZERO);
        medicamento.setPrecioVenta(rq.getPrecioVenta() != null ? rq.getPrecioVenta() : BigDecimal.ZERO);
        medicamento.setStockMinimo(rq.getStockMinimo() != null ? rq.getStockMinimo() : 0);
        medicamento.setFechaProximoPedido(rq.getFechaProximoPedido());
        medicamento.setFechaCompra(rq.getFechaCompra() != null ? rq.getFechaCompra() : LocalDate.now());
        medicamento.setFechaVence(rq.getFechaVence() != null ? rq.getFechaVence() : LocalDate.now());
    }

    private void validarMedicamento(MedicamentoRq rq, boolean esActualizacion) {
        if (rq == null) {
            throw new BadRequestException("Los datos del medicamento son obligatorios.");
        }
        if (esActualizacion && rq.getId() == null) {
            throw new BadRequestException("El identificador del medicamento es obligatorio.");
        }
        if (rq.getNombre() == null || rq.getNombre().isBlank()) {
            throw new BadRequestException("El nombre del medicamento es obligatorio.");
        }
        if (rq.getCodigo() != null && !rq.getCodigo().isBlank()
                && rq.getCodigo().trim().length() != 8) {
            throw new BadRequestException("El codigo debe tener exactamente 8 caracteres.");
        }
        // Regla Invima: obligatorio solo cuando el producto lo requiere.
        boolean requiere = rq.getRequiereInvima() != null ? rq.getRequiereInvima() : true;
        if (requiere && (rq.getRegistroInvima() == null || rq.getRegistroInvima().isBlank())) {
            throw new BadRequestException(
                    "El registro Invima es obligatorio para los medicamentos.");
        }
        if (rq.getPrecioVenta() != null && rq.getPrecioVenta().signum() < 0) {
            throw new BadRequestException("El precio de venta no puede ser negativo.");
        }
        if (rq.getPrecioCompra() != null && rq.getPrecioCompra().signum() < 0) {
            throw new BadRequestException("El precio de compra no puede ser negativo.");
        }
    }

    private MedicamentoRs mapearMedicamento(Medicamento m) {
        MedicamentoRs rs = new MedicamentoRs();
        rs.setId(m.getId());
        rs.setNombre(m.getNombre());
        rs.setCodigo(m.getCodigo());
        rs.setDescripcion(m.getDescripcion());
        rs.setPresentacion(m.getPresentacion());
        rs.setRequiereInvima(m.getRequiereInvima());
        rs.setRegistroInvima(m.getRegistroInvima());
        rs.setPrecioCompra(m.getPrecioCompra());
        rs.setPrecioVenta(m.getPrecioVenta());
        rs.setStockMinimo(m.getStockMinimo());
        rs.setFechaProximoPedido(m.getFechaProximoPedido());
        rs.setFechaCompra(m.getFechaCompra());
        rs.setFechaVence(m.getFechaVence());

        List<LoteMedicamento> lotes = loteRepository
                .findByMedicamentoIdOrderByFechaVencimientoAsc(m.getId());
        LocalDate hoy = LocalDate.now();
        LocalDate umbral = hoy.plusDays(DIAS_PROXIMO_VENCIMIENTO);

        int existencia = 0;
        boolean tieneProximos = false;
        boolean tieneVencidos = false;
        List<LoteRs> lotesRs = new ArrayList<>();

        for (LoteMedicamento lote : lotes) {
            existencia += lote.getCantidad() != null ? lote.getCantidad() : 0;
            boolean vencido = lote.getFechaVencimiento().isBefore(hoy);
            boolean proximo = !vencido
                    && !lote.getFechaVencimiento().isAfter(umbral);
            if (vencido) {
                tieneVencidos = true;
            }
            if (proximo) {
                tieneProximos = true;
            }
            LoteRs loteRs = new LoteRs();
            loteRs.setId(lote.getId());
            loteRs.setNumeroLote(lote.getNumeroLote());
            loteRs.setCantidad(lote.getCantidad());
            loteRs.setPrecioCompra(lote.getPrecioCompra());
            loteRs.setFechaIngreso(lote.getFechaIngreso());
            loteRs.setFechaVencimiento(lote.getFechaVencimiento());
            loteRs.setVencido(vencido);
            loteRs.setProximoAVencer(proximo);
            lotesRs.add(loteRs);
        }

        rs.setExistenciaTotal(existencia);
        rs.setAgotado(existencia == 0);
        rs.setStockBajo(existencia > 0 && existencia <= (m.getStockMinimo() != null ? m.getStockMinimo() : 0));
        rs.setTieneProximosAVencer(tieneProximos);
        rs.setTieneVencidos(tieneVencidos);
        rs.setLotes(lotesRs);
        return rs;
    }
}

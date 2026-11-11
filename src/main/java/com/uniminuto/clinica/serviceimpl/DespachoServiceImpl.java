package com.uniminuto.clinica.serviceimpl;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.uniminuto.clinica.entity.Cita;
import com.uniminuto.clinica.entity.Cliente;
import com.uniminuto.clinica.entity.Despacho;
import com.uniminuto.clinica.entity.DespachoDetalle;
import com.uniminuto.clinica.entity.FormulaMedica;
import com.uniminuto.clinica.entity.FormulaMedicamento;
import com.uniminuto.clinica.entity.LogDespacho;
import com.uniminuto.clinica.entity.LoteMedicamento;
import com.uniminuto.clinica.entity.Mascota;
import com.uniminuto.clinica.entity.Medicamento;
import com.uniminuto.clinica.entity.Medico;
import com.uniminuto.clinica.entity.MovimientoInventario;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.DespachoRq;
import com.uniminuto.clinica.models.DespachoRs;
import com.uniminuto.clinica.models.FormulaPendienteRs;
import com.uniminuto.clinica.models.MedicamentoDespachoRs;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.repository.CitaRepository;
import com.uniminuto.clinica.repository.DespachoDetalleRepository;
import com.uniminuto.clinica.repository.DespachoRepository;
import com.uniminuto.clinica.repository.FormulaMedicaRepository;
import com.uniminuto.clinica.repository.FormulaMedicamentoRepository;
import com.uniminuto.clinica.repository.LogDespachoRepository;
import com.uniminuto.clinica.repository.LoteMedicamentoRepository;
import com.uniminuto.clinica.repository.MedicamentoRepository;
import com.uniminuto.clinica.repository.MedicoRepository;
import com.uniminuto.clinica.repository.MovimientoInventarioRepository;
import com.uniminuto.clinica.security.ContextoSeguridad;
import com.uniminuto.clinica.security.UsuarioAutenticado;
import com.uniminuto.clinica.service.DespachoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementacion del modulo de despacho.
 *
 * Al confirmar un despacho, por cada medicamento se descuenta la cantidad
 * entregada de los lotes no vencidos, priorizando el vencimiento mas proximo
 * (FEFO). Se registra un movimiento de salida por cada lote afectado, ligado a
 * la formula y al despacho. Si la cantidad entregada es menor a la prescrita, el
 * despacho queda como parcial. Toda la operacion es transaccional: si algo
 * falla, no se aplica ningun descuento.
 */
@Service
public class DespachoServiceImpl implements DespachoService {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final DespachoRepository despachoRepository;
    private final DespachoDetalleRepository despachoDetalleRepository;
    private final LogDespachoRepository logDespachoRepository;
    private final FormulaMedicaRepository formulaRepository;
    private final FormulaMedicamentoRepository formulaDetalleRepository;
    private final LoteMedicamentoRepository loteRepository;
    private final MovimientoInventarioRepository movimientoRepository;
    private final MedicamentoRepository medicamentoRepository;
    private final CitaRepository citaRepository;
    private final MedicoRepository medicoRepository;

    public DespachoServiceImpl(DespachoRepository despachoRepository,
                               DespachoDetalleRepository despachoDetalleRepository,
                               LogDespachoRepository logDespachoRepository,
                               FormulaMedicaRepository formulaRepository,
                               FormulaMedicamentoRepository formulaDetalleRepository,
                               LoteMedicamentoRepository loteRepository,
                               MovimientoInventarioRepository movimientoRepository,
                               MedicamentoRepository medicamentoRepository,
                               CitaRepository citaRepository,
                               MedicoRepository medicoRepository) {
        this.despachoRepository = despachoRepository;
        this.despachoDetalleRepository = despachoDetalleRepository;
        this.logDespachoRepository = logDespachoRepository;
        this.formulaRepository = formulaRepository;
        this.formulaDetalleRepository = formulaDetalleRepository;
        this.loteRepository = loteRepository;
        this.movimientoRepository = movimientoRepository;
        this.medicamentoRepository = medicamentoRepository;
        this.citaRepository = citaRepository;
        this.medicoRepository = medicoRepository;
    }

    @Override
    public List<FormulaPendienteRs> listarFormulasPendientes() {
        List<FormulaPendienteRs> resultado = new ArrayList<>();
        for (FormulaMedica formula : formulaRepository.findAllByOrderByFechaCreacionRegistroDesc()) {
            // Se considera pendiente si no tiene un despacho ya despachado.
            boolean yaDespachado = despachoRepository
                    .existsByFormulaIdAndEstadoNot(formula.getId(), Despacho.ESTADO_CANCELADO)
                    && this.tieneDespachoFinal(formula.getId());
            if (!yaDespachado) {
                resultado.add(this.mapearFormulaPendiente(formula));
            }
        }
        return resultado;
    }

    private boolean tieneDespachoFinal(Long formulaId) {
        return despachoRepository.findAllByOrderByFechaDesc().stream()
                .anyMatch(d -> d.getFormulaId().equals(formulaId)
                        && Despacho.ESTADO_DESPACHADO.equals(d.getEstado()));
    }

    @Override
    public FormulaPendienteRs obtenerFormulaParaDespacho(Long formulaId) {
        FormulaMedica formula = formulaRepository.findById(formulaId)
                .orElseThrow(() -> new BadRequestException("La formula no existe."));
        return this.mapearFormulaPendiente(formula);
    }

    @Override
    @Transactional
    public MiRespuestaRS confirmarDespacho(DespachoRq rq) {
        if (rq == null || rq.getFormulaId() == null) {
            throw new BadRequestException("La formula es obligatoria para el despacho.");
        }
        if (rq.getItems() == null || rq.getItems().isEmpty()) {
            throw new BadRequestException("Debe indicar las cantidades a entregar.");
        }
        FormulaMedica formula = formulaRepository.findById(rq.getFormulaId())
                .orElseThrow(() -> new BadRequestException("La formula no existe."));

        // Regla: no despachar dos veces una formula ya despachada por completo.
        if (this.tieneDespachoFinal(formula.getId())) {
            throw new BadRequestException("Esta formula ya fue despachada en su totalidad.");
        }

        List<FormulaMedicamento> detallesFormula =
                formulaDetalleRepository.findByFormulaId(formula.getId());

        String usuario = this.usuarioResponsable();
        Despacho despacho = new Despacho();
        despacho.setFormulaId(formula.getId());
        despacho.setFecha(LocalDateTime.now());
        despacho.setUsuarioResponsable(usuario);
        despacho.setObservacion(rq.getObservacion());
        despacho.setEstado(Despacho.ESTADO_EN_PREPARACION);
        despacho = despachoRepository.save(despacho);

        boolean huboEntrega = false;
        boolean entregaCompleta = true;

        for (FormulaMedicamento itemFormula : detallesFormula) {
            int cantidadEntregar = this.cantidadSolicitada(rq, itemFormula.getMedicamentoId());
            int prescrita = itemFormula.getCantidad() != null ? itemFormula.getCantidad() : 0;

            if (cantidadEntregar <= 0) {
                entregaCompleta = false;
                this.guardarDetalle(despacho.getId(), itemFormula.getMedicamentoId(), null, prescrita, 0);
                continue;
            }

            // Lotes no vencidos ordenados por vencimiento mas proximo (FEFO).
            List<LoteMedicamento> lotes = loteRepository
                    .findByMedicamentoIdOrderByFechaVencimientoAsc(itemFormula.getMedicamentoId());
            LocalDate hoy = LocalDate.now();

            int pendiente = cantidadEntregar;
            int entregadoReal = 0;
            for (LoteMedicamento lote : lotes) {
                if (pendiente <= 0) {
                    break;
                }
                if (lote.getFechaVencimiento().isBefore(hoy)) {
                    continue; // no dispensar vencidos
                }
                int disponible = lote.getCantidad() != null ? lote.getCantidad() : 0;
                if (disponible <= 0) {
                    continue;
                }
                int tomar = Math.min(disponible, pendiente);
                lote.setCantidad(disponible - tomar);
                lote.setFechaModificacionRegistro(LocalDateTime.now());
                loteRepository.save(lote);

                // Movimiento de salida ligado al despacho y la formula.
                this.registrarSalida(itemFormula.getMedicamentoId(), lote.getId(), tomar,
                        "Despacho " + despacho.getId() + " / Formula " + formula.getId(), usuario);

                this.guardarDetalle(despacho.getId(), itemFormula.getMedicamentoId(),
                        lote.getId(), prescrita, tomar);

                entregadoReal += tomar;
                pendiente -= tomar;
            }

            if (entregadoReal > 0) {
                huboEntrega = true;
            }
            if (entregadoReal < prescrita) {
                entregaCompleta = false;
            }
            // Si no se pudo entregar nada de este medicamento, registrar el detalle en cero.
            if (entregadoReal == 0) {
                this.guardarDetalle(despacho.getId(), itemFormula.getMedicamentoId(), null, prescrita, 0);
            }
        }

        if (!huboEntrega) {
            throw new BadRequestException(
                    "No hay existencias vigentes para entregar los medicamentos de esta formula.");
        }

        String estadoFinal = entregaCompleta
                ? Despacho.ESTADO_DESPACHADO : Despacho.ESTADO_PARCIAL;
        this.cambiarEstado(despacho, estadoFinal, usuario);

        MiRespuestaRS rta = new MiRespuestaRS();
        rta.setStatus(200);
        rta.setMessage(entregaCompleta
                ? "Despacho completado. Inventario actualizado."
                : "Despacho parcial registrado. Algunos medicamentos no se entregaron en su totalidad.");
        return rta;
    }

    @Override
    public List<DespachoRs> listarDespachos() {
        return despachoRepository.findAllByOrderByFechaDesc().stream()
                .map(this::mapearDespacho).toList();
    }

    @Override
    public byte[] generarComprobantePdf(Long despachoId) {
        DespachoRs d = this.mapearDespacho(despachoRepository.findById(despachoId)
                .orElseThrow(() -> new BadRequestException("El despacho no existe.")));
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document doc = new Document();
            PdfWriter.getInstance(doc, out);
            doc.open();

            Font titulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, new Color(10, 42, 74));
            Font subtitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, new Color(11, 90, 140));
            Font normal = FontFactory.getFont(FontFactory.HELVETICA, 10);

            Paragraph tit = new Paragraph("Clinica Veterinaria Uniminuto", titulo);
            tit.setAlignment(Element.ALIGN_CENTER);
            doc.add(tit);
            Paragraph st = new Paragraph("Comprobante de despacho No. " + d.getId(), subtitulo);
            st.setAlignment(Element.ALIGN_CENTER);
            st.setSpacingAfter(12);
            doc.add(st);

            doc.add(new Paragraph("Fecha: " + (d.getFecha() != null ? d.getFecha().format(FORMATO) : ""), normal));
            doc.add(new Paragraph("Formula No.: " + d.getFormulaId(), normal));
            doc.add(new Paragraph("Estado: " + texto(d.getEstado()), normal));
            doc.add(new Paragraph("Responsable: " + texto(d.getUsuarioResponsable()), normal));
            doc.add(new Paragraph("Propietario: " + texto(d.getClienteNombre()), normal));
            doc.add(new Paragraph("Mascota: " + texto(d.getMascotaNombre()), normal));
            doc.add(new Paragraph("Medico: " + texto(d.getMedicoNombre()), normal));

            Paragraph tablaTit = new Paragraph("Medicamentos entregados", subtitulo);
            tablaTit.setSpacingBefore(12);
            tablaTit.setSpacingAfter(6);
            doc.add(tablaTit);

            PdfPTable tabla = new PdfPTable(new float[]{4, 2, 2, 2});
            tabla.setWidthPercentage(100);
            this.encabezado(tabla, "Medicamento", "Lote", "Solicitado", "Entregado");
            for (DespachoRs.DespachoDetalleRs it : d.getItems()) {
                tabla.addCell(celda(texto(it.getMedicamentoNombre())));
                tabla.addCell(celda(texto(it.getNumeroLote())));
                tabla.addCell(celda(String.valueOf(it.getCantidadSolicitada())));
                tabla.addCell(celda(String.valueOf(it.getCantidadEntregada())));
            }
            doc.add(tabla);

            doc.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new BadRequestException("No fue posible generar el comprobante: " + e.getMessage());
        }
    }

    // ----------------------------------------------------------------

    private int cantidadSolicitada(DespachoRq rq, Long medicamentoId) {
        return rq.getItems().stream()
                .filter(i -> medicamentoId.equals(i.getMedicamentoId()))
                .map(i -> i.getCantidadEntregada() != null ? i.getCantidadEntregada() : 0)
                .findFirst().orElse(0);
    }

    private void guardarDetalle(Long despachoId, Long medicamentoId, Long loteId,
                                int solicitada, int entregada) {
        DespachoDetalle detalle = new DespachoDetalle();
        detalle.setDespachoId(despachoId);
        detalle.setMedicamentoId(medicamentoId);
        detalle.setLoteId(loteId);
        detalle.setCantidadSolicitada(solicitada);
        detalle.setCantidadEntregada(entregada);
        despachoDetalleRepository.save(detalle);
    }

    private void registrarSalida(Long medicamentoId, Long loteId, int cantidad,
                                 String referencia, String usuario) {
        MovimientoInventario mov = new MovimientoInventario();
        mov.setMedicamentoId(medicamentoId);
        mov.setLoteId(loteId);
        mov.setTipo(MovimientoInventario.TIPO_SALIDA);
        mov.setCantidad(cantidad);
        mov.setFecha(LocalDateTime.now());
        mov.setUsuarioResponsable(usuario);
        mov.setReferencia(referencia);
        movimientoRepository.save(mov);
    }

    private void cambiarEstado(Despacho despacho, String nuevoEstado, String usuario) {
        String anterior = despacho.getEstado();
        despacho.setEstado(nuevoEstado);
        despachoRepository.save(despacho);

        LogDespacho log = new LogDespacho();
        log.setDespachoId(despacho.getId());
        log.setEstadoAnterior(anterior);
        log.setEstadoNuevo(nuevoEstado);
        log.setFecha(LocalDateTime.now());
        log.setUsuarioResponsable(usuario);
        logDespachoRepository.save(log);
    }

    private FormulaPendienteRs mapearFormulaPendiente(FormulaMedica formula) {
        FormulaPendienteRs rs = new FormulaPendienteRs();
        rs.setFormulaId(formula.getId());
        rs.setFechaGeneracion(formula.getFechaCreacion() != null
                ? formula.getFechaCreacion() : formula.getFechaCreacionRegistro());

        Cita cita = formula.getCita();
        if (cita != null) {
            Mascota mascota = cita.getMascota();
            if (mascota != null) {
                rs.setMascotaNombre(mascota.getNombreMascota());
            }
            Cliente cliente = cita.getCliente();
            if (cliente != null) {
                rs.setClienteNombre((texto(cliente.getNombres()) + " "
                        + texto(cliente.getApellidos())).trim());
            }
        }
        Long medicoId = formula.getMedicoId();
        if (medicoId == null && cita != null && cita.getMedico() != null) {
            medicoId = cita.getMedico().getId();
        }
        if (medicoId != null) {
            medicoRepository.findById(medicoId).ifPresent(m ->
                    rs.setMedicoNombre((texto(m.getNombres()) + " " + texto(m.getApellidos())).trim()));
        }

        List<MedicamentoDespachoRs> meds = new ArrayList<>();
        LocalDate hoy = LocalDate.now();
        for (FormulaMedicamento det : formulaDetalleRepository.findByFormulaId(formula.getId())) {
            MedicamentoDespachoRs m = new MedicamentoDespachoRs();
            m.setMedicamentoId(det.getMedicamentoId());
            medicamentoRepository.findById(det.getMedicamentoId())
                    .ifPresent(me -> m.setMedicamentoNombre(me.getNombre()));
            m.setCantidadPrescrita(det.getCantidad());
            m.setDosis(det.getDosis());
            m.setFrecuencia(det.getFrecuencia());
            m.setDuracion(det.getDuracion());
            int disponible = loteRepository
                    .findByMedicamentoIdOrderByFechaVencimientoAsc(det.getMedicamentoId()).stream()
                    .filter(l -> !l.getFechaVencimiento().isBefore(hoy))
                    .mapToInt(l -> l.getCantidad() != null ? l.getCantidad() : 0)
                    .sum();
            m.setExistenciaDisponible(disponible);
            m.setSuficiente(det.getCantidad() != null && disponible >= det.getCantidad());
            meds.add(m);
        }
        rs.setMedicamentos(meds);
        return rs;
    }

    private DespachoRs mapearDespacho(Despacho d) {
        DespachoRs rs = new DespachoRs();
        rs.setId(d.getId());
        rs.setFormulaId(d.getFormulaId());
        rs.setEstado(d.getEstado());
        rs.setFecha(d.getFecha());
        rs.setUsuarioResponsable(d.getUsuarioResponsable());

        formulaRepository.findById(d.getFormulaId()).ifPresent(formula -> {
            Cita cita = formula.getCita();
            if (cita != null) {
                if (cita.getMascota() != null) {
                    rs.setMascotaNombre(cita.getMascota().getNombreMascota());
                }
                Cliente cliente = cita.getCliente();
                if (cliente != null) {
                    rs.setClienteNombre((texto(cliente.getNombres()) + " "
                            + texto(cliente.getApellidos())).trim());
                }
            }
            Long medicoId = formula.getMedicoId();
            if (medicoId == null && cita != null && cita.getMedico() != null) {
                medicoId = cita.getMedico().getId();
            }
            if (medicoId != null) {
                medicoRepository.findById(medicoId).ifPresent(m ->
                        rs.setMedicoNombre((texto(m.getNombres()) + " " + texto(m.getApellidos())).trim()));
            }
        });

        List<DespachoRs.DespachoDetalleRs> items = new ArrayList<>();
        for (DespachoDetalle det : despachoDetalleRepository.findByDespachoId(d.getId())) {
            DespachoRs.DespachoDetalleRs it = new DespachoRs.DespachoDetalleRs();
            medicamentoRepository.findById(det.getMedicamentoId())
                    .ifPresent(me -> it.setMedicamentoNombre(me.getNombre()));
            it.setLoteId(det.getLoteId());
            if (det.getLoteId() != null) {
                loteRepository.findById(det.getLoteId())
                        .ifPresent(l -> it.setNumeroLote(l.getNumeroLote()));
            }
            it.setCantidadSolicitada(det.getCantidadSolicitada());
            it.setCantidadEntregada(det.getCantidadEntregada());
            items.add(it);
        }
        rs.setItems(items);
        return rs;
    }

    private String usuarioResponsable() {
        UsuarioAutenticado usuario = ContextoSeguridad.obtenerUsuario();
        return usuario != null ? usuario.username() : "sistema";
    }

    private void encabezado(PdfPTable tabla, String... titulos) {
        Font f = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.WHITE);
        for (String t : titulos) {
            PdfPCell c = new PdfPCell(new Phrase(t, f));
            c.setBackgroundColor(new Color(11, 90, 140));
            c.setPadding(5);
            tabla.addCell(c);
        }
    }

    private PdfPCell celda(String texto) {
        PdfPCell c = new PdfPCell(new Phrase(texto, FontFactory.getFont(FontFactory.HELVETICA, 9)));
        c.setPadding(4);
        return c;
    }

    private String texto(String valor) {
        return valor != null ? valor : "";
    }
}

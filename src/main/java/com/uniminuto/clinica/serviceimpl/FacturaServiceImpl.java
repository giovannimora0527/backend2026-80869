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
import com.uniminuto.clinica.entity.DatosClinica;
import com.uniminuto.clinica.entity.Factura;
import com.uniminuto.clinica.entity.FacturaDetalle;
import com.uniminuto.clinica.entity.Mascota;
import com.uniminuto.clinica.entity.Medicamento;
import com.uniminuto.clinica.entity.Medico;
import com.uniminuto.clinica.entity.Pago;
import com.uniminuto.clinica.entity.Servicio;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.FacturaDetalleRq;
import com.uniminuto.clinica.models.FacturaDetalleRs;
import com.uniminuto.clinica.models.FacturaRq;
import com.uniminuto.clinica.models.FacturaRs;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.models.PagoRq;
import com.uniminuto.clinica.repository.CitaRepository;
import com.uniminuto.clinica.repository.DatosClinicaRepository;
import com.uniminuto.clinica.repository.FacturaDetalleRepository;
import com.uniminuto.clinica.repository.FacturaRepository;
import com.uniminuto.clinica.repository.MedicamentoRepository;
import com.uniminuto.clinica.repository.MedicoRepository;
import com.uniminuto.clinica.repository.PagoRepository;
import com.uniminuto.clinica.repository.ServicioRepository;
import com.uniminuto.clinica.security.ContextoSeguridad;
import com.uniminuto.clinica.security.UsuarioAutenticado;
import com.uniminuto.clinica.service.FacturaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementacion del servicio de facturacion.
 *
 * Genera facturas a partir de una cita y sus items (servicios y medicamentos),
 * calculando subtotal, IVA y total, y conservando los valores historicamente en
 * el detalle. Registra pagos y actualiza el estado (pendiente, pago parcial,
 * pagada). La anulacion no elimina registros.
 */
@Service
public class FacturaServiceImpl implements FacturaService {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final BigDecimal CIEN = new BigDecimal("100");

    private final FacturaRepository facturaRepository;
    private final FacturaDetalleRepository detalleRepository;
    private final PagoRepository pagoRepository;
    private final DatosClinicaRepository datosClinicaRepository;
    private final CitaRepository citaRepository;
    private final ServicioRepository servicioRepository;
    private final MedicamentoRepository medicamentoRepository;
    private final MedicoRepository medicoRepository;

    public FacturaServiceImpl(FacturaRepository facturaRepository,
                              FacturaDetalleRepository detalleRepository,
                              PagoRepository pagoRepository,
                              DatosClinicaRepository datosClinicaRepository,
                              CitaRepository citaRepository,
                              ServicioRepository servicioRepository,
                              MedicamentoRepository medicamentoRepository,
                              MedicoRepository medicoRepository) {
        this.facturaRepository = facturaRepository;
        this.detalleRepository = detalleRepository;
        this.pagoRepository = pagoRepository;
        this.datosClinicaRepository = datosClinicaRepository;
        this.citaRepository = citaRepository;
        this.servicioRepository = servicioRepository;
        this.medicamentoRepository = medicamentoRepository;
        this.medicoRepository = medicoRepository;
    }

    @Override
    public List<FacturaRs> listar() {
        return facturaRepository.findAllByOrderByFechaDesc().stream().map(this::mapear).toList();
    }

    @Override
    public List<FacturaRs> listarPorCliente(Long clienteId) {
        if (clienteId == null) {
            throw new BadRequestException("El identificador del cliente es obligatorio.");
        }
        return facturaRepository.findByClienteIdOrderByFechaDesc(clienteId).stream()
                .map(this::mapear).toList();
    }

    @Override
    public FacturaRs obtener(Long id) {
        return this.mapear(this.buscar(id));
    }

    @Override
    @Transactional
    public MiRespuestaRS generar(FacturaRq rq) {
        if (rq == null || rq.getCitaId() == null) {
            throw new BadRequestException("La cita es obligatoria para generar la factura.");
        }
        if (rq.getItems() == null || rq.getItems().isEmpty()) {
            throw new BadRequestException("La factura debe tener al menos un item.");
        }
        Cita cita = citaRepository.findById(rq.getCitaId())
                .orElseThrow(() -> new BadRequestException("La cita no existe."));

        // Regla: una cita no se factura dos veces (salvo que la anterior este anulada).
        if (facturaRepository.existsByCitaIdAndEstadoPagoNot(rq.getCitaId(), Factura.ESTADO_ANULADA)) {
            throw new BadRequestException("Esta cita ya tiene una factura vigente.");
        }

        Factura factura = new Factura();
        factura.setCitaId(cita.getId());
        if (cita.getCliente() != null) {
            factura.setClienteId(cita.getCliente().getId());
        }
        if (cita.getMascota() != null) {
            factura.setMascotaId(cita.getMascota().getMascotaId() != null
                    ? cita.getMascota().getMascotaId().longValue() : null);
        }
        if (cita.getMedico() != null) {
            factura.setMedicoId(cita.getMedico().getId());
        }
        factura.setFecha(LocalDateTime.now());
        factura.setObservacion(rq.getObservacion());
        factura.setEstadoPago(Factura.ESTADO_PENDIENTE);
        factura = facturaRepository.save(factura);

        BigDecimal subtotalTotal = BigDecimal.ZERO;
        BigDecimal ivaTotal = BigDecimal.ZERO;

        for (FacturaDetalleRq item : rq.getItems()) {
            FacturaDetalle detalle = this.construirDetalle(factura.getId(), item);
            detalleRepository.save(detalle);
            subtotalTotal = subtotalTotal.add(detalle.getSubtotal());
            BigDecimal ivaItem = detalle.getSubtotal()
                    .multiply(detalle.getIvaPorcentaje()).divide(CIEN, 2, RoundingMode.HALF_UP);
            ivaTotal = ivaTotal.add(ivaItem);
        }

        BigDecimal descuento = rq.getDescuento() != null ? rq.getDescuento() : BigDecimal.ZERO;
        if (descuento.signum() < 0) {
            throw new BadRequestException("El descuento no puede ser negativo.");
        }
        BigDecimal total = subtotalTotal.add(ivaTotal).subtract(descuento);
        if (total.signum() < 0) {
            total = BigDecimal.ZERO;
        }

        factura.setSubtotal(subtotalTotal.setScale(2, RoundingMode.HALF_UP));
        factura.setIva(ivaTotal.setScale(2, RoundingMode.HALF_UP));
        factura.setDescuento(descuento.setScale(2, RoundingMode.HALF_UP));
        factura.setTotal(total.setScale(2, RoundingMode.HALF_UP));
        facturaRepository.save(factura);

        MiRespuestaRS rta = new MiRespuestaRS();
        rta.setStatus(200);
        rta.setMessage("Factura generada correctamente. Total: " + factura.getTotal());
        return rta;
    }

    private FacturaDetalle construirDetalle(Long facturaId, FacturaDetalleRq item) {
        if (item.getTipo() == null || item.getReferenciaId() == null) {
            throw new BadRequestException("Cada item debe indicar tipo y referencia.");
        }
        int cantidad = item.getCantidad() != null && item.getCantidad() > 0 ? item.getCantidad() : 1;
        FacturaDetalle detalle = new FacturaDetalle();
        detalle.setFacturaId(facturaId);
        detalle.setCantidad(cantidad);
        detalle.setReferenciaId(item.getReferenciaId());

        if (FacturaDetalle.TIPO_SERVICIO.equalsIgnoreCase(item.getTipo())) {
            Servicio servicio = servicioRepository.findById(item.getReferenciaId())
                    .orElseThrow(() -> new BadRequestException("El servicio no existe."));
            detalle.setTipo(FacturaDetalle.TIPO_SERVICIO);
            detalle.setDescripcion(servicio.getNombre());
            detalle.setPrecioUnitario(servicio.getPrecio());
            detalle.setIvaPorcentaje(servicio.getIvaPorcentaje());
        } else if (FacturaDetalle.TIPO_MEDICAMENTO.equalsIgnoreCase(item.getTipo())) {
            Medicamento med = medicamentoRepository.findById(item.getReferenciaId())
                    .orElseThrow(() -> new BadRequestException("El medicamento no existe."));
            detalle.setTipo(FacturaDetalle.TIPO_MEDICAMENTO);
            detalle.setDescripcion(med.getNombre());
            detalle.setPrecioUnitario(med.getPrecioVenta());
            detalle.setIvaPorcentaje(BigDecimal.ZERO);
        } else {
            throw new BadRequestException("Tipo de item no valido: " + item.getTipo());
        }

        BigDecimal subtotal = detalle.getPrecioUnitario()
                .multiply(new BigDecimal(cantidad)).setScale(2, RoundingMode.HALF_UP);
        detalle.setSubtotal(subtotal);
        return detalle;
    }

    @Override
    @Transactional
    public MiRespuestaRS registrarPago(PagoRq rq) {
        if (rq == null || rq.getFacturaId() == null || rq.getMonto() == null) {
            throw new BadRequestException("La factura y el monto del pago son obligatorios.");
        }
        if (rq.getMonto().signum() <= 0) {
            throw new BadRequestException("El monto del pago debe ser mayor que cero.");
        }
        Factura factura = this.buscar(rq.getFacturaId());
        if (Factura.ESTADO_ANULADA.equals(factura.getEstadoPago())) {
            throw new BadRequestException("No se puede registrar un pago sobre una factura anulada.");
        }
        if (Factura.ESTADO_PAGADA.equals(factura.getEstadoPago())) {
            throw new BadRequestException("La factura ya esta pagada.");
        }

        BigDecimal pagadoActual = this.totalPagado(factura.getId());
        BigDecimal saldo = factura.getTotal().subtract(pagadoActual);
        if (rq.getMonto().compareTo(saldo) > 0) {
            throw new BadRequestException("El monto excede el saldo pendiente (" + saldo + ").");
        }

        Pago pago = new Pago();
        pago.setFacturaId(factura.getId());
        pago.setMonto(rq.getMonto());
        pago.setFecha(LocalDateTime.now());
        pago.setMetodo(rq.getMetodo());
        pago.setUsuarioResponsable(this.usuarioResponsable());
        pagoRepository.save(pago);

        BigDecimal nuevoPagado = pagadoActual.add(rq.getMonto());
        if (nuevoPagado.compareTo(factura.getTotal()) >= 0) {
            factura.setEstadoPago(Factura.ESTADO_PAGADA);
        } else {
            factura.setEstadoPago(Factura.ESTADO_PAGO_PARCIAL);
        }
        facturaRepository.save(factura);

        MiRespuestaRS rta = new MiRespuestaRS();
        rta.setStatus(200);
        rta.setMessage("Pago registrado. Estado: " + factura.getEstadoPago());
        return rta;
    }

    @Override
    @Transactional
    public MiRespuestaRS anular(Long facturaId) {
        Factura factura = this.buscar(facturaId);
        if (Factura.ESTADO_ANULADA.equals(factura.getEstadoPago())) {
            throw new BadRequestException("La factura ya esta anulada.");
        }
        // Anular conserva los registros (no se elimina nada).
        factura.setEstadoPago(Factura.ESTADO_ANULADA);
        facturaRepository.save(factura);

        MiRespuestaRS rta = new MiRespuestaRS();
        rta.setStatus(200);
        rta.setMessage("Factura anulada. Los registros se conservan.");
        return rta;
    }

    @Override
    public byte[] generarPdf(Long facturaId) {
        FacturaRs factura = this.mapear(this.buscar(facturaId));
        DatosClinica datos = datosClinicaRepository.findAll().stream().findFirst().orElse(null);
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document doc = new Document();
            PdfWriter.getInstance(doc, out);
            doc.open();

            Font titulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, new Color(10, 42, 74));
            Font subtitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, new Color(11, 90, 140));
            Font normal = FontFactory.getFont(FontFactory.HELVETICA, 10);

            String nombreClinica = datos != null ? datos.getNombre() : "Clinica Veterinaria";
            Paragraph tit = new Paragraph(nombreClinica, titulo);
            tit.setAlignment(Element.ALIGN_CENTER);
            doc.add(tit);
            if (datos != null) {
                Paragraph info = new Paragraph(
                        "NIT: " + texto(datos.getNit()) + "  |  Tel: " + texto(datos.getTelefono())
                                + "\n" + texto(datos.getDireccion()), normal);
                info.setAlignment(Element.ALIGN_CENTER);
                doc.add(info);
            }
            Paragraph st = new Paragraph("Factura No. " + factura.getId(), subtitulo);
            st.setAlignment(Element.ALIGN_CENTER);
            st.setSpacingBefore(8);
            st.setSpacingAfter(12);
            doc.add(st);

            doc.add(new Paragraph("Fecha: "
                    + (factura.getFecha() != null ? factura.getFecha().format(FORMATO) : ""), normal));
            doc.add(new Paragraph("Propietario: " + texto(factura.getClienteNombre()), normal));
            doc.add(new Paragraph("Mascota: " + texto(factura.getMascotaNombre()), normal));
            doc.add(new Paragraph("Medico: " + texto(factura.getMedicoNombre()), normal));
            doc.add(new Paragraph("Estado de pago: " + texto(factura.getEstadoPago()), normal));

            Paragraph tablaTit = new Paragraph("Detalle", subtitulo);
            tablaTit.setSpacingBefore(12);
            tablaTit.setSpacingAfter(6);
            doc.add(tablaTit);

            PdfPTable tabla = new PdfPTable(new float[]{4, 1, 2, 1.2f, 2});
            tabla.setWidthPercentage(100);
            this.encabezado(tabla, "Descripcion", "Cant.", "Precio unit.", "IVA %", "Subtotal");
            for (FacturaDetalleRs d : factura.getItems()) {
                tabla.addCell(celda(texto(d.getDescripcion())));
                tabla.addCell(celda(String.valueOf(d.getCantidad())));
                tabla.addCell(celda(money(d.getPrecioUnitario())));
                tabla.addCell(celda(d.getIvaPorcentaje() + "%"));
                tabla.addCell(celda(money(d.getSubtotal())));
            }
            doc.add(tabla);

            PdfPTable totales = new PdfPTable(new float[]{3, 2});
            totales.setWidthPercentage(45);
            totales.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totales.setSpacingBefore(10);
            this.filaTotal(totales, "Subtotal", money(factura.getSubtotal()));
            this.filaTotal(totales, "IVA", money(factura.getIva()));
            this.filaTotal(totales, "Descuento", money(factura.getDescuento()));
            this.filaTotal(totales, "Total", money(factura.getTotal()));
            this.filaTotal(totales, "Pagado", money(factura.getTotalPagado()));
            this.filaTotal(totales, "Saldo", money(factura.getSaldoPendiente()));
            doc.add(totales);

            doc.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new BadRequestException("No fue posible generar el PDF de la factura: " + e.getMessage());
        }
    }

    // ----------------------------------------------------------------

    private Factura buscar(Long id) {
        if (id == null) {
            throw new BadRequestException("El identificador de la factura es obligatorio.");
        }
        return facturaRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("La factura no existe."));
    }

    private BigDecimal totalPagado(Long facturaId) {
        return pagoRepository.findByFacturaIdOrderByFechaAsc(facturaId).stream()
                .map(Pago::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private FacturaRs mapear(Factura f) {
        FacturaRs rs = new FacturaRs();
        rs.setId(f.getId());
        rs.setCitaId(f.getCitaId());
        rs.setFecha(f.getFecha());
        rs.setSubtotal(f.getSubtotal());
        rs.setIva(f.getIva());
        rs.setDescuento(f.getDescuento());
        rs.setTotal(f.getTotal());
        rs.setEstadoPago(f.getEstadoPago());
        rs.setObservacion(f.getObservacion());

        BigDecimal pagado = this.totalPagado(f.getId());
        rs.setTotalPagado(pagado);
        rs.setSaldoPendiente(f.getTotal().subtract(pagado));

        if (f.getClienteId() != null && f.getCitaId() != null) {
            citaRepository.findById(f.getCitaId()).ifPresent(cita -> {
                Cliente cliente = cita.getCliente();
                if (cliente != null) {
                    rs.setClienteNombre((texto(cliente.getNombres()) + " "
                            + texto(cliente.getApellidos())).trim());
                }
                Mascota mascota = cita.getMascota();
                if (mascota != null) {
                    rs.setMascotaNombre(mascota.getNombreMascota());
                }
            });
        }
        if (f.getMedicoId() != null) {
            medicoRepository.findById(f.getMedicoId()).ifPresent(m ->
                    rs.setMedicoNombre((texto(m.getNombres()) + " " + texto(m.getApellidos())).trim()));
        }

        List<FacturaDetalleRs> items = new ArrayList<>();
        for (FacturaDetalle d : detalleRepository.findByFacturaId(f.getId())) {
            FacturaDetalleRs dr = new FacturaDetalleRs();
            dr.setId(d.getId());
            dr.setTipo(d.getTipo());
            dr.setReferenciaId(d.getReferenciaId());
            dr.setDescripcion(d.getDescripcion());
            dr.setCantidad(d.getCantidad());
            dr.setPrecioUnitario(d.getPrecioUnitario());
            dr.setIvaPorcentaje(d.getIvaPorcentaje());
            dr.setSubtotal(d.getSubtotal());
            items.add(dr);
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

    private void filaTotal(PdfPTable tabla, String etiqueta, String valor) {
        Font fe = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9);
        Font fv = FontFactory.getFont(FontFactory.HELVETICA, 9);
        PdfPCell c1 = new PdfPCell(new Phrase(etiqueta, fe));
        c1.setPadding(4);
        PdfPCell c2 = new PdfPCell(new Phrase(valor, fv));
        c2.setPadding(4);
        c2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        tabla.addCell(c1);
        tabla.addCell(c2);
    }

    private PdfPCell celda(String texto) {
        PdfPCell c = new PdfPCell(new Phrase(texto, FontFactory.getFont(FontFactory.HELVETICA, 9)));
        c.setPadding(4);
        return c;
    }

    private String money(BigDecimal valor) {
        return valor != null ? valor.setScale(2, RoundingMode.HALF_UP).toPlainString() : "0.00";
    }

    private String texto(String valor) {
        return valor != null ? valor : "";
    }
}

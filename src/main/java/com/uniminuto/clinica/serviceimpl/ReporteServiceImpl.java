package com.uniminuto.clinica.serviceimpl;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.uniminuto.clinica.entity.Cita;
import com.uniminuto.clinica.entity.Cliente;
import com.uniminuto.clinica.entity.Especializacion;
import com.uniminuto.clinica.entity.Factura;
import com.uniminuto.clinica.entity.FacturaDetalle;
import com.uniminuto.clinica.entity.LoteMedicamento;
import com.uniminuto.clinica.entity.Mascota;
import com.uniminuto.clinica.entity.Medicamento;
import com.uniminuto.clinica.entity.Medico;
import com.uniminuto.clinica.entity.MovimientoInventario;
import com.uniminuto.clinica.entity.Pago;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.ReporteRs;
import com.uniminuto.clinica.repository.CitaRepository;
import com.uniminuto.clinica.repository.EspecializacionRepository;
import com.uniminuto.clinica.repository.FacturaDetalleRepository;
import com.uniminuto.clinica.repository.FacturaRepository;
import com.uniminuto.clinica.repository.LoteMedicamentoRepository;
import com.uniminuto.clinica.repository.MedicamentoRepository;
import com.uniminuto.clinica.repository.MedicoRepository;
import com.uniminuto.clinica.repository.MovimientoInventarioRepository;
import com.uniminuto.clinica.repository.PagoRepository;
import com.uniminuto.clinica.service.ReporteService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementacion del modulo de reportes.
 *
 * Cada reporte se construye como un objeto {@link ReporteRs} (titulo, columnas,
 * filas y totales). La misma estructura se usa para la vista previa (JSON) y
 * para las exportaciones a PDF y Excel, que son completamente genericas.
 *
 * Los datos provienen de las facturas, pagos, movimientos de inventario, lotes
 * y citas ya registrados en el sistema, de modo que los reportes son coherentes
 * con la operacion real (facturacion, dispensacion y agenda).
 */
@Service
public class ReporteServiceImpl implements ReporteService {

    private static final DateTimeFormatter F_FECHA = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter F_FECHA_HORA = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final Color AZUL = new Color(11, 90, 140);
    private static final Color AZUL_OSCURO = new Color(10, 42, 74);

    private final FacturaRepository facturaRepository;
    private final FacturaDetalleRepository facturaDetalleRepository;
    private final PagoRepository pagoRepository;
    private final MedicamentoRepository medicamentoRepository;
    private final LoteMedicamentoRepository loteRepository;
    private final MovimientoInventarioRepository movimientoRepository;
    private final CitaRepository citaRepository;
    private final MedicoRepository medicoRepository;
    private final EspecializacionRepository especializacionRepository;

    public ReporteServiceImpl(FacturaRepository facturaRepository,
                              FacturaDetalleRepository facturaDetalleRepository,
                              PagoRepository pagoRepository,
                              MedicamentoRepository medicamentoRepository,
                              LoteMedicamentoRepository loteRepository,
                              MovimientoInventarioRepository movimientoRepository,
                              CitaRepository citaRepository,
                              MedicoRepository medicoRepository,
                              EspecializacionRepository especializacionRepository) {
        this.facturaRepository = facturaRepository;
        this.facturaDetalleRepository = facturaDetalleRepository;
        this.pagoRepository = pagoRepository;
        this.medicamentoRepository = medicamentoRepository;
        this.loteRepository = loteRepository;
        this.movimientoRepository = movimientoRepository;
        this.citaRepository = citaRepository;
        this.medicoRepository = medicoRepository;
        this.especializacionRepository = especializacionRepository;
    }

    // ================================================================
    // API publica
    // ================================================================

    @Override
    public ReporteRs generar(String categoria, String tipo, LocalDate desde, LocalDate hasta) {
        if (categoria == null || tipo == null) {
            throw new BadRequestException("La categoria y el tipo de reporte son obligatorios.");
        }
        this.validarRango(desde, hasta);
        return switch (categoria.toLowerCase()) {
            case "financiero" -> this.generarFinanciero(tipo, desde, hasta);
            case "inventario" -> this.generarInventario(tipo, desde, hasta);
            case "gestion" -> this.generarGestion(tipo, desde, hasta);
            default -> throw new BadRequestException("Categoria de reporte no valida: " + categoria);
        };
    }

    @Override
    public byte[] exportarPdf(String categoria, String tipo, LocalDate desde, LocalDate hasta) {
        ReporteRs reporte = this.generar(categoria, tipo, desde, hasta);
        return this.aPdf(reporte);
    }

    @Override
    public byte[] exportarExcel(String categoria, String tipo, LocalDate desde, LocalDate hasta) {
        ReporteRs reporte = this.generar(categoria, tipo, desde, hasta);
        return this.aExcel(reporte);
    }

    // ================================================================
    // Reportes financieros
    // ================================================================

    private ReporteRs generarFinanciero(String tipo, LocalDate desde, LocalDate hasta) {
        List<Factura> facturas = this.facturasEnRango(desde, hasta);
        String sub = this.subtituloRango(desde, hasta);

        return switch (tipo) {
            case "ingresos-periodo" -> this.ingresosPorPeriodo(facturas, sub);
            case "ingresos-consultas" -> this.ingresosPorTipoDetalle(facturas, FacturaDetalle.TIPO_SERVICIO,
                    "Ingresos por consultas medicas (servicios)", sub);
            case "ingresos-medicamentos" -> this.ingresosPorTipoDetalle(facturas, FacturaDetalle.TIPO_MEDICAMENTO,
                    "Ingresos por venta de medicamentos", sub);
            case "ingresos-especialidad" -> this.ingresosPorEspecialidad(facturas, sub);
            case "ingresos-veterinario" -> this.ingresosPorVeterinario(facturas, sub);
            case "facturas-estado" -> this.facturasPorEstado(facturas, sub);
            case "pagos-parciales" -> this.pagosParciales(facturas, sub);
            case "impuestos-descuentos" -> this.impuestosYDescuentos(facturas, sub);
            case "resumen-dia-mes-anio" -> this.resumenDiaMesAnio(facturas, sub);
            default -> throw new BadRequestException("Reporte financiero no valido: " + tipo);
        };
    }

    private ReporteRs ingresosPorPeriodo(List<Factura> facturas, String sub) {
        ReporteRs r = this.base("Ingresos por periodo", sub,
                "Factura", "Fecha", "Cliente", "Subtotal", "IVA", "Descuento", "Total");
        BigDecimal totSub = BigDecimal.ZERO, totIva = BigDecimal.ZERO,
                totDesc = BigDecimal.ZERO, totTotal = BigDecimal.ZERO;
        for (Factura f : facturas) {
            r.agregarFila("No. " + f.getId(), fecha(f.getFecha()), this.nombreCliente(f.getClienteId()),
                    money(f.getSubtotal()), money(f.getIva()), money(f.getDescuento()), money(f.getTotal()));
            totSub = totSub.add(nz(f.getSubtotal()));
            totIva = totIva.add(nz(f.getIva()));
            totDesc = totDesc.add(nz(f.getDescuento()));
            totTotal = totTotal.add(nz(f.getTotal()));
        }
        r.agregarTotal("Facturas", String.valueOf(facturas.size()));
        r.agregarTotal("Subtotal", money(totSub));
        r.agregarTotal("IVA", money(totIva));
        r.agregarTotal("Descuentos", money(totDesc));
        r.agregarTotal("Total ingresos", money(totTotal));
        return r;
    }

    private ReporteRs ingresosPorTipoDetalle(List<Factura> facturas, String tipoDetalle,
                                             String titulo, String sub) {
        // Agrupa el importe de los detalles del tipo indicado por descripcion.
        Map<String, BigDecimal> porItem = new LinkedHashMap<>();
        Map<String, Integer> cantidades = new LinkedHashMap<>();
        BigDecimal total = BigDecimal.ZERO;
        for (Factura f : facturas) {
            for (FacturaDetalle d : facturaDetalleRepository.findByFacturaId(f.getId())) {
                if (!tipoDetalle.equals(d.getTipo())) {
                    continue;
                }
                String clave = d.getDescripcion() != null ? d.getDescripcion() : "(sin descripcion)";
                porItem.merge(clave, nz(d.getSubtotal()), BigDecimal::add);
                cantidades.merge(clave, d.getCantidad() != null ? d.getCantidad() : 0, Integer::sum);
                total = total.add(nz(d.getSubtotal()));
            }
        }
        ReporteRs r = this.base(titulo, sub, "Descripcion", "Cantidad", "Importe");
        for (Map.Entry<String, BigDecimal> e : porItem.entrySet()) {
            r.agregarFila(e.getKey(), String.valueOf(cantidades.getOrDefault(e.getKey(), 0)), money(e.getValue()));
        }
        r.agregarTotal("Total", money(total));
        return r;
    }

    private ReporteRs ingresosPorEspecialidad(List<Factura> facturas, String sub) {
        Map<String, BigDecimal> porEsp = new LinkedHashMap<>();
        BigDecimal total = BigDecimal.ZERO;
        for (Factura f : facturas) {
            String esp = this.especialidadDeMedico(f.getMedicoId());
            porEsp.merge(esp, nz(f.getTotal()), BigDecimal::add);
            total = total.add(nz(f.getTotal()));
        }
        ReporteRs r = this.base("Ingresos por especialidad", sub, "Especialidad", "Total ingresos");
        for (Map.Entry<String, BigDecimal> e : porEsp.entrySet()) {
            r.agregarFila(e.getKey(), money(e.getValue()));
        }
        r.agregarTotal("Total ingresos", money(total));
        return r;
    }

    private ReporteRs ingresosPorVeterinario(List<Factura> facturas, String sub) {
        Map<String, BigDecimal> porVet = new LinkedHashMap<>();
        BigDecimal total = BigDecimal.ZERO;
        for (Factura f : facturas) {
            String vet = this.nombreMedico(f.getMedicoId());
            porVet.merge(vet, nz(f.getTotal()), BigDecimal::add);
            total = total.add(nz(f.getTotal()));
        }
        ReporteRs r = this.base("Ingresos por veterinario", sub, "Veterinario", "Total ingresos");
        for (Map.Entry<String, BigDecimal> e : porVet.entrySet()) {
            r.agregarFila(e.getKey(), money(e.getValue()));
        }
        r.agregarTotal("Total ingresos", money(total));
        return r;
    }

    private ReporteRs facturasPorEstado(List<Factura> facturas, String sub) {
        Map<String, Integer> conteo = new LinkedHashMap<>();
        Map<String, BigDecimal> montos = new LinkedHashMap<>();
        for (String estado : List.of(Factura.ESTADO_PENDIENTE, Factura.ESTADO_PAGO_PARCIAL,
                Factura.ESTADO_PAGADA, Factura.ESTADO_ANULADA)) {
            conteo.put(estado, 0);
            montos.put(estado, BigDecimal.ZERO);
        }
        BigDecimal totalGeneral = BigDecimal.ZERO;
        for (Factura f : facturas) {
            String estado = f.getEstadoPago() != null ? f.getEstadoPago() : Factura.ESTADO_PENDIENTE;
            conteo.merge(estado, 1, Integer::sum);
            montos.merge(estado, nz(f.getTotal()), BigDecimal::add);
            totalGeneral = totalGeneral.add(nz(f.getTotal()));
        }
        ReporteRs r = this.base("Facturas por estado", sub, "Estado de pago", "Cantidad", "Monto total");
        for (String estado : conteo.keySet()) {
            r.agregarFila(estado, String.valueOf(conteo.get(estado)), money(montos.get(estado)));
        }
        r.agregarTotal("Total facturas", String.valueOf(facturas.size()));
        r.agregarTotal("Monto total", money(totalGeneral));
        return r;
    }

    private ReporteRs pagosParciales(List<Factura> facturas, String sub) {
        ReporteRs r = this.base("Pagos parciales", sub,
                "Factura", "Cliente", "Total", "Pagado", "Saldo");
        BigDecimal totalSaldo = BigDecimal.ZERO;
        int cantidad = 0;
        for (Factura f : facturas) {
            if (!Factura.ESTADO_PAGO_PARCIAL.equals(f.getEstadoPago())) {
                continue;
            }
            BigDecimal pagado = this.totalPagado(f.getId());
            BigDecimal saldo = nz(f.getTotal()).subtract(pagado);
            r.agregarFila("No. " + f.getId(), this.nombreCliente(f.getClienteId()),
                    money(f.getTotal()), money(pagado), money(saldo));
            totalSaldo = totalSaldo.add(saldo);
            cantidad++;
        }
        r.agregarTotal("Facturas con pago parcial", String.valueOf(cantidad));
        r.agregarTotal("Saldo pendiente total", money(totalSaldo));
        return r;
    }

    private ReporteRs impuestosYDescuentos(List<Factura> facturas, String sub) {
        ReporteRs r = this.base("Impuestos recaudados y descuentos aplicados", sub,
                "Factura", "Fecha", "Base (subtotal)", "IVA", "Descuento");
        BigDecimal totIva = BigDecimal.ZERO, totDesc = BigDecimal.ZERO, totBase = BigDecimal.ZERO;
        for (Factura f : facturas) {
            if (Factura.ESTADO_ANULADA.equals(f.getEstadoPago())) {
                continue;
            }
            r.agregarFila("No. " + f.getId(), fecha(f.getFecha()), money(f.getSubtotal()),
                    money(f.getIva()), money(f.getDescuento()));
            totBase = totBase.add(nz(f.getSubtotal()));
            totIva = totIva.add(nz(f.getIva()));
            totDesc = totDesc.add(nz(f.getDescuento()));
        }
        r.agregarTotal("Base gravable", money(totBase));
        r.agregarTotal("IVA recaudado", money(totIva));
        r.agregarTotal("Descuentos aplicados", money(totDesc));
        return r;
    }

    private ReporteRs resumenDiaMesAnio(List<Factura> facturas, String sub) {
        // Resumen de ingresos agrupado por dia, con subtotales por mes y total general.
        Map<String, BigDecimal> porDia = new LinkedHashMap<>();
        for (Factura f : facturas) {
            if (Factura.ESTADO_ANULADA.equals(f.getEstadoPago()) || f.getFecha() == null) {
                continue;
            }
            porDia.merge(f.getFecha().toLocalDate().format(F_FECHA), nz(f.getTotal()), BigDecimal::add);
        }
        ReporteRs r = this.base("Resumen de ingresos por dia, mes y anio", sub, "Fecha (dia)", "Ingresos del dia");
        BigDecimal total = BigDecimal.ZERO;
        Map<String, BigDecimal> porMes = new LinkedHashMap<>();
        Map<String, BigDecimal> porAnio = new LinkedHashMap<>();
        for (Map.Entry<String, BigDecimal> e : porDia.entrySet()) {
            r.agregarFila(e.getKey(), money(e.getValue()));
            total = total.add(e.getValue());
            String mes = e.getKey().substring(0, 7);
            String anio = e.getKey().substring(0, 4);
            porMes.merge(mes, e.getValue(), BigDecimal::add);
            porAnio.merge(anio, e.getValue(), BigDecimal::add);
        }
        porMes.forEach((k, v) -> r.agregarTotal("Mes " + k, money(v)));
        porAnio.forEach((k, v) -> r.agregarTotal("Anio " + k, money(v)));
        r.agregarTotal("Total general", money(total));
        return r;
    }

    // ================================================================
    // Reportes de inventario
    // ================================================================

    private ReporteRs generarInventario(String tipo, LocalDate desde, LocalDate hasta) {
        return switch (tipo) {
            case "existencias" -> this.existencias();
            case "valor-inventario" -> this.valorInventario();
            case "agotados" -> this.filtradoPorEstado("agotados");
            case "stock-minimo" -> this.filtradoPorEstado("stock-minimo");
            case "por-vencer" -> this.filtradoPorEstado("por-vencer");
            case "vencidos" -> this.filtradoPorEstado("vencidos");
            case "movimientos" -> this.movimientos(desde, hasta);
            default -> throw new BadRequestException("Reporte de inventario no valido: " + tipo);
        };
    }

    private ReporteRs existencias() {
        ReporteRs r = this.base("Existencias actuales", "Estado actual del inventario",
                "Codigo", "Medicamento", "Existencia", "Stock minimo", "Estado");
        int totalUnidades = 0;
        for (Medicamento m : medicamentoRepository.findAllByOrderByNombreAsc()) {
            int existencia = this.existenciaTotal(m.getId());
            r.agregarFila(texto(m.getCodigo()), m.getNombre(), String.valueOf(existencia),
                    String.valueOf(m.getStockMinimo()), this.estadoStock(existencia, m.getStockMinimo()));
            totalUnidades += existencia;
        }
        r.agregarTotal("Unidades totales en inventario", String.valueOf(totalUnidades));
        return r;
    }

    private ReporteRs valorInventario() {
        ReporteRs r = this.base("Valor del inventario", "Estado actual del inventario",
                "Codigo", "Medicamento", "Existencia", "Costo unitario", "Valor costo",
                "Precio venta", "Valor venta");
        BigDecimal totalCosto = BigDecimal.ZERO, totalVenta = BigDecimal.ZERO;
        for (Medicamento m : medicamentoRepository.findAllByOrderByNombreAsc()) {
            int existencia = this.existenciaTotal(m.getId());
            BigDecimal valorCosto = nz(m.getPrecioCompra()).multiply(BigDecimal.valueOf(existencia));
            BigDecimal valorVenta = nz(m.getPrecioVenta()).multiply(BigDecimal.valueOf(existencia));
            r.agregarFila(texto(m.getCodigo()), m.getNombre(), String.valueOf(existencia),
                    money(m.getPrecioCompra()), money(valorCosto),
                    money(m.getPrecioVenta()), money(valorVenta));
            totalCosto = totalCosto.add(valorCosto);
            totalVenta = totalVenta.add(valorVenta);
        }
        r.agregarTotal("Valor del inventario (costo de adquisicion)", money(totalCosto));
        r.agregarTotal("Valor potencial de venta", money(totalVenta));
        r.agregarTotal("Utilidad potencial", money(totalVenta.subtract(totalCosto)));
        return r;
    }

    private ReporteRs filtradoPorEstado(String estado) {
        String titulo = switch (estado) {
            case "agotados" -> "Medicamentos agotados";
            case "stock-minimo" -> "Medicamentos con stock minimo";
            case "por-vencer" -> "Medicamentos proximos a vencer (30 dias)";
            case "vencidos" -> "Medicamentos vencidos";
            default -> "Medicamentos";
        };
        LocalDate hoy = LocalDate.now();
        LocalDate limite = hoy.plusDays(30);

        if ("por-vencer".equals(estado) || "vencidos".equals(estado)) {
            ReporteRs r = this.base(titulo, "Estado actual del inventario",
                    "Medicamento", "Lote", "Cantidad", "Fecha vencimiento", "Dias restantes");
            int totalLotes = 0;
            for (Medicamento m : medicamentoRepository.findAllByOrderByNombreAsc()) {
                for (LoteMedicamento lote : loteRepository.findByMedicamentoId(m.getId())) {
                    boolean incluir = "vencidos".equals(estado)
                            ? lote.getFechaVencimiento().isBefore(hoy)
                            : (!lote.getFechaVencimiento().isBefore(hoy)
                               && !lote.getFechaVencimiento().isAfter(limite));
                    if (!incluir || nz(lote.getCantidad()) == 0) {
                        continue;
                    }
                    r.agregarFila(m.getNombre(), lote.getNumeroLote(),
                            String.valueOf(lote.getCantidad()),
                            lote.getFechaVencimiento().format(F_FECHA),
                            String.valueOf(java.time.temporal.ChronoUnit.DAYS.between(hoy, lote.getFechaVencimiento())));
                    totalLotes++;
                }
            }
            r.agregarTotal("Lotes encontrados", String.valueOf(totalLotes));
            return r;
        }

        // Agotados y stock minimo trabajan sobre la existencia total del medicamento.
        ReporteRs r = this.base(titulo, "Estado actual del inventario",
                "Codigo", "Medicamento", "Existencia", "Stock minimo");
        int cantidad = 0;
        for (Medicamento m : medicamentoRepository.findAllByOrderByNombreAsc()) {
            int existencia = this.existenciaTotal(m.getId());
            boolean incluir = "agotados".equals(estado)
                    ? existencia == 0
                    : (existencia > 0 && existencia <= nz(m.getStockMinimo()));
            if (!incluir) {
                continue;
            }
            r.agregarFila(texto(m.getCodigo()), m.getNombre(), String.valueOf(existencia),
                    String.valueOf(m.getStockMinimo()));
            cantidad++;
        }
        r.agregarTotal("Medicamentos encontrados", String.valueOf(cantidad));
        return r;
    }

    private ReporteRs movimientos(LocalDate desde, LocalDate hasta) {
        LocalDateTime ini = desde.atStartOfDay();
        LocalDateTime fin = hasta.atTime(23, 59, 59);
        ReporteRs r = this.base("Entradas y salidas de inventario", this.subtituloRango(desde, hasta),
                "Fecha", "Medicamento", "Tipo", "Cantidad", "Responsable", "Referencia");
        int entradas = 0, salidas = 0;
        for (Medicamento m : medicamentoRepository.findAllByOrderByNombreAsc()) {
            for (MovimientoInventario mov : movimientoRepository.findByMedicamentoIdOrderByFechaDesc(m.getId())) {
                if (mov.getFecha() == null || mov.getFecha().isBefore(ini) || mov.getFecha().isAfter(fin)) {
                    continue;
                }
                r.agregarFila(mov.getFecha().format(F_FECHA_HORA), m.getNombre(), texto(mov.getTipo()),
                        String.valueOf(mov.getCantidad()), texto(mov.getUsuarioResponsable()),
                        texto(mov.getReferencia()));
                if (MovimientoInventario.TIPO_ENTRADA.equals(mov.getTipo())) {
                    entradas += nz(mov.getCantidad());
                } else {
                    salidas += nz(mov.getCantidad());
                }
            }
        }
        r.agregarTotal("Unidades ingresadas", String.valueOf(entradas));
        r.agregarTotal("Unidades despachadas", String.valueOf(salidas));
        return r;
    }

    // ================================================================
    // Reportes de gestion medica
    // ================================================================

    private ReporteRs generarGestion(String tipo, LocalDate desde, LocalDate hasta) {
        List<Cita> citas = this.citasEnRango(desde, hasta);
        String sub = this.subtituloRango(desde, hasta);
        return switch (tipo) {
            case "citas-periodo" -> this.citasPorPeriodo(citas, sub);
            case "citas-estado" -> this.citasPorEstado(citas, sub);
            case "consultas-veterinario" -> this.consultasPorVeterinario(citas, sub);
            case "consultas-especialidad" -> this.consultasPorEspecialidad(citas, sub);
            case "mascotas-atendidas" -> this.mascotasAtendidas(citas, sub);
            default -> throw new BadRequestException("Reporte de gestion medica no valido: " + tipo);
        };
    }

    private ReporteRs citasPorPeriodo(List<Cita> citas, String sub) {
        ReporteRs r = this.base("Citas por periodo", sub,
                "Fecha y hora", "Estado", "Mascota", "Propietario", "Veterinario", "Motivo");
        for (Cita c : citas) {
            r.agregarFila(c.getFechaHora() != null ? c.getFechaHora().format(F_FECHA_HORA) : "",
                    texto(c.getEstado()),
                    c.getMascota() != null ? texto(c.getMascota().getNombreMascota()) : "",
                    this.nombreClienteEntidad(c.getCliente()),
                    c.getMedico() != null ? (texto(c.getMedico().getNombres()) + " "
                            + texto(c.getMedico().getApellidos())).trim() : "",
                    texto(c.getMotivo()));
        }
        r.agregarTotal("Total de citas en el periodo", String.valueOf(citas.size()));
        return r;
    }

    private ReporteRs citasPorEstado(List<Cita> citas, String sub) {
        Map<String, Integer> conteo = new LinkedHashMap<>();
        for (Cita c : citas) {
            String estado = c.getEstado() != null ? c.getEstado() : "(sin estado)";
            conteo.merge(estado, 1, Integer::sum);
        }
        ReporteRs r = this.base("Citas por estado (atendidas, canceladas, pendientes)", sub,
                "Estado", "Cantidad");
        for (Map.Entry<String, Integer> e : conteo.entrySet()) {
            r.agregarFila(e.getKey(), String.valueOf(e.getValue()));
        }
        r.agregarTotal("Total de citas", String.valueOf(citas.size()));
        return r;
    }

    private ReporteRs consultasPorVeterinario(List<Cita> citas, String sub) {
        Map<String, Integer> conteo = new LinkedHashMap<>();
        for (Cita c : citas) {
            String vet = c.getMedico() != null
                    ? (texto(c.getMedico().getNombres()) + " " + texto(c.getMedico().getApellidos())).trim()
                    : "(sin veterinario)";
            conteo.merge(vet, 1, Integer::sum);
        }
        ReporteRs r = this.base("Consultas realizadas por veterinario", sub, "Veterinario", "Numero de citas");
        for (Map.Entry<String, Integer> e : conteo.entrySet()) {
            r.agregarFila(e.getKey(), String.valueOf(e.getValue()));
        }
        r.agregarTotal("Total de citas", String.valueOf(citas.size()));
        return r;
    }

    private ReporteRs consultasPorEspecialidad(List<Cita> citas, String sub) {
        Map<String, Integer> conteo = new LinkedHashMap<>();
        for (Cita c : citas) {
            String esp = "(sin especialidad)";
            if (c.getMedico() != null && c.getMedico().getEspecializacion() != null) {
                esp = texto(c.getMedico().getEspecializacion().getNombre());
            }
            conteo.merge(esp, 1, Integer::sum);
        }
        ReporteRs r = this.base("Consultas realizadas por especialidad", sub, "Especialidad", "Numero de citas");
        for (Map.Entry<String, Integer> e : conteo.entrySet()) {
            r.agregarFila(e.getKey(), String.valueOf(e.getValue()));
        }
        r.agregarTotal("Total de citas", String.valueOf(citas.size()));
        return r;
    }

    private ReporteRs mascotasAtendidas(List<Cita> citas, String sub) {
        // Mascotas distintas con cita atendida/finalizada en el periodo.
        Map<Integer, String> mascotas = new LinkedHashMap<>();
        Map<Integer, Integer> visitas = new LinkedHashMap<>();
        for (Cita c : citas) {
            if (c.getMascota() == null) {
                continue;
            }
            Integer id = c.getMascota().getMascotaId();
            mascotas.putIfAbsent(id, texto(c.getMascota().getNombreMascota()));
            visitas.merge(id, 1, Integer::sum);
        }
        ReporteRs r = this.base("Mascotas atendidas por periodo", sub, "Mascota", "Numero de citas");
        for (Map.Entry<Integer, String> e : mascotas.entrySet()) {
            r.agregarFila(e.getValue(), String.valueOf(visitas.get(e.getKey())));
        }
        r.agregarTotal("Mascotas distintas atendidas", String.valueOf(mascotas.size()));
        return r;
    }

    // ================================================================
    // Exportacion a PDF (OpenPDF)
    // ================================================================

    private byte[] aPdf(ReporteRs reporte) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document doc = new Document(reporte.getColumnas().size() > 5
                    ? PageSize.A4.rotate() : PageSize.A4);
            PdfWriter.getInstance(doc, out);
            doc.open();

            Font fTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, AZUL_OSCURO);
            Font fSub = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.DARK_GRAY);
            Font fTotal = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, AZUL_OSCURO);

            Paragraph clinica = new Paragraph("Clinica Veterinaria Uniminuto",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, AZUL));
            clinica.setAlignment(Element.ALIGN_CENTER);
            doc.add(clinica);

            Paragraph tit = new Paragraph(reporte.getTitulo(), fTitulo);
            tit.setAlignment(Element.ALIGN_CENTER);
            tit.setSpacingBefore(4);
            doc.add(tit);

            if (reporte.getSubtitulo() != null && !reporte.getSubtitulo().isEmpty()) {
                Paragraph s = new Paragraph(reporte.getSubtitulo(), fSub);
                s.setAlignment(Element.ALIGN_CENTER);
                doc.add(s);
            }
            Paragraph gen = new Paragraph("Generado el " + LocalDateTime.now().format(F_FECHA_HORA), fSub);
            gen.setAlignment(Element.ALIGN_CENTER);
            gen.setSpacingAfter(12);
            doc.add(gen);

            int columnas = reporte.getColumnas().size();
            PdfPTable tabla = new PdfPTable(columnas);
            tabla.setWidthPercentage(100);
            for (String col : reporte.getColumnas()) {
                tabla.addCell(this.celdaEncabezado(col));
            }
            if (reporte.getFilas().isEmpty()) {
                PdfPCell vacio = new PdfPCell(new Phrase("No hay datos para los filtros seleccionados.",
                        FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 9)));
                vacio.setColspan(columnas);
                vacio.setPadding(6);
                vacio.setHorizontalAlignment(Element.ALIGN_CENTER);
                tabla.addCell(vacio);
            } else {
                for (List<String> fila : reporte.getFilas()) {
                    for (String celda : fila) {
                        tabla.addCell(this.celdaDato(celda));
                    }
                }
            }
            doc.add(tabla);

            if (!reporte.getTotales().isEmpty()) {
                Paragraph totTit = new Paragraph("Totales", fTotal);
                totTit.setSpacingBefore(12);
                totTit.setSpacingAfter(4);
                doc.add(totTit);
                PdfPTable tabTot = new PdfPTable(2);
                tabTot.setWidthPercentage(60);
                tabTot.setHorizontalAlignment(Element.ALIGN_LEFT);
                for (Map.Entry<String, String> e : reporte.getTotales().entrySet()) {
                    tabTot.addCell(this.celdaDato(e.getKey()));
                    PdfPCell valor = this.celdaDato(e.getValue());
                    valor.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    tabTot.addCell(valor);
                }
                doc.add(tabTot);
            }

            doc.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new BadRequestException("No fue posible generar el PDF del reporte: " + e.getMessage());
        }
    }

    private PdfPCell celdaEncabezado(String texto) {
        PdfPCell c = new PdfPCell(new Phrase(texto,
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.WHITE)));
        c.setBackgroundColor(AZUL);
        c.setPadding(5);
        return c;
    }

    private PdfPCell celdaDato(String texto) {
        PdfPCell c = new PdfPCell(new Phrase(texto != null ? texto : "",
                FontFactory.getFont(FontFactory.HELVETICA, 9)));
        c.setPadding(4);
        return c;
    }

    // ================================================================
    // Exportacion a Excel (Apache POI)
    // ================================================================

    private byte[] aExcel(ReporteRs reporte) {
        try (Workbook wb = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet hoja = wb.createSheet(this.nombreHoja(reporte.getTitulo()));

            CellStyle estiloTitulo = wb.createCellStyle();
            org.apache.poi.ss.usermodel.Font fontTitulo = wb.createFont();
            fontTitulo.setBold(true);
            fontTitulo.setFontHeightInPoints((short) 14);
            estiloTitulo.setFont(fontTitulo);

            CellStyle estiloEnc = wb.createCellStyle();
            org.apache.poi.ss.usermodel.Font fontEnc = wb.createFont();
            fontEnc.setBold(true);
            fontEnc.setColor(org.apache.poi.ss.usermodel.IndexedColors.WHITE.getIndex());
            estiloEnc.setFont(fontEnc);
            estiloEnc.setFillForegroundColor(org.apache.poi.ss.usermodel.IndexedColors.DARK_BLUE.getIndex());
            estiloEnc.setFillPattern(org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);

            int fila = 0;
            Row filaTitulo = hoja.createRow(fila++);
            Cell celdaTitulo = filaTitulo.createCell(0);
            celdaTitulo.setCellValue("Clinica Veterinaria Uniminuto - " + reporte.getTitulo());
            celdaTitulo.setCellStyle(estiloTitulo);

            if (reporte.getSubtitulo() != null && !reporte.getSubtitulo().isEmpty()) {
                hoja.createRow(fila++).createCell(0).setCellValue(reporte.getSubtitulo());
            }
            hoja.createRow(fila++).createCell(0)
                    .setCellValue("Generado el " + LocalDateTime.now().format(F_FECHA_HORA));
            fila++; // fila en blanco

            Row filaEnc = hoja.createRow(fila++);
            List<String> columnas = reporte.getColumnas();
            for (int i = 0; i < columnas.size(); i++) {
                Cell c = filaEnc.createCell(i);
                c.setCellValue(columnas.get(i));
                c.setCellStyle(estiloEnc);
            }
            for (List<String> datos : reporte.getFilas()) {
                Row r = hoja.createRow(fila++);
                for (int i = 0; i < datos.size(); i++) {
                    r.createCell(i).setCellValue(datos.get(i));
                }
            }

            if (!reporte.getTotales().isEmpty()) {
                fila++; // separador
                Row filaTotTit = hoja.createRow(fila++);
                Cell cTot = filaTotTit.createCell(0);
                cTot.setCellValue("Totales");
                cTot.setCellStyle(estiloTitulo);
                for (Map.Entry<String, String> e : reporte.getTotales().entrySet()) {
                    Row r = hoja.createRow(fila++);
                    r.createCell(0).setCellValue(e.getKey());
                    r.createCell(1).setCellValue(e.getValue());
                }
            }

            int totalColumnas = Math.max(columnas.size(), 2);
            for (int i = 0; i < totalColumnas; i++) {
                hoja.autoSizeColumn(i);
            }
            wb.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new BadRequestException("No fue posible generar el Excel del reporte: " + e.getMessage());
        }
    }

    private String nombreHoja(String titulo) {
        // Excel limita el nombre de hoja a 31 caracteres y prohibe algunos simbolos.
        String limpio = titulo.replaceAll("[\\\\/?*\\[\\]:]", " ");
        return limpio.length() > 31 ? limpio.substring(0, 31) : limpio;
    }

    // ================================================================
    // Auxiliares
    // ================================================================

    private void validarRango(LocalDate desde, LocalDate hasta) {
        if (desde == null || hasta == null) {
            throw new BadRequestException("El rango de fechas (desde y hasta) es obligatorio.");
        }
        if (desde.isAfter(hasta)) {
            throw new BadRequestException("La fecha inicial no puede ser posterior a la fecha final.");
        }
    }

    private List<Factura> facturasEnRango(LocalDate desde, LocalDate hasta) {
        LocalDateTime ini = desde.atStartOfDay();
        LocalDateTime fin = hasta.atTime(23, 59, 59);
        List<Factura> resultado = new ArrayList<>();
        for (Factura f : facturaRepository.findAllByOrderByFechaDesc()) {
            if (f.getFecha() != null && !f.getFecha().isBefore(ini) && !f.getFecha().isAfter(fin)) {
                resultado.add(f);
            }
        }
        return resultado;
    }

    private List<Cita> citasEnRango(LocalDate desde, LocalDate hasta) {
        LocalDateTime ini = desde.atStartOfDay();
        LocalDateTime fin = hasta.atTime(23, 59, 59);
        List<Cita> citas = citaRepository.findByFechaHoraBetween(ini, fin);
        citas.sort((a, b) -> {
            if (a.getFechaHora() == null || b.getFechaHora() == null) {
                return 0;
            }
            return a.getFechaHora().compareTo(b.getFechaHora());
        });
        return citas;
    }

    private int existenciaTotal(Long medicamentoId) {
        return loteRepository.findByMedicamentoId(medicamentoId).stream()
                .mapToInt(l -> l.getCantidad() != null ? l.getCantidad() : 0)
                .sum();
    }

    private String estadoStock(int existencia, Integer stockMinimo) {
        int min = stockMinimo != null ? stockMinimo : 0;
        if (existencia == 0) {
            return "AGOTADO";
        }
        if (existencia <= min) {
            return "STOCK BAJO";
        }
        return "DISPONIBLE";
    }

    private BigDecimal totalPagado(Long facturaId) {
        BigDecimal total = BigDecimal.ZERO;
        for (Pago p : pagoRepository.findByFacturaIdOrderByFechaAsc(facturaId)) {
            total = total.add(nz(p.getMonto()));
        }
        return total;
    }

    private String nombreMedico(Long medicoId) {
        if (medicoId == null) {
            return "(sin veterinario)";
        }
        return medicoRepository.findById(medicoId)
                .map(m -> (texto(m.getNombres()) + " " + texto(m.getApellidos())).trim())
                .orElse("(veterinario " + medicoId + ")");
    }

    private String especialidadDeMedico(Long medicoId) {
        if (medicoId == null) {
            return "(sin especialidad)";
        }
        return medicoRepository.findById(medicoId)
                .map(Medico::getEspecializacion)
                .map(Especializacion::getNombre)
                .orElse("(sin especialidad)");
    }

    private String nombreCliente(Long clienteId) {
        // El cliente se resuelve a traves de las citas/facturas; aqui solo
        // devolvemos el identificador cuando no hay una relacion directa cargada.
        return clienteId != null ? "Cliente " + clienteId : "(sin cliente)";
    }

    private String nombreClienteEntidad(Cliente cliente) {
        if (cliente == null) {
            return "";
        }
        return (texto(cliente.getNombres()) + " " + texto(cliente.getApellidos())).trim();
    }

    private String subtituloRango(LocalDate desde, LocalDate hasta) {
        return "Periodo: " + desde.format(F_FECHA) + " a " + hasta.format(F_FECHA);
    }

    private ReporteRs base(String titulo, String subtitulo, String... columnas) {
        ReporteRs r = new ReporteRs();
        r.setTitulo(titulo);
        r.setSubtitulo(subtitulo);
        r.setColumnas(new ArrayList<>(List.of(columnas)));
        return r;
    }

    private String fecha(LocalDateTime f) {
        return f != null ? f.format(F_FECHA) : "";
    }

    private String money(BigDecimal valor) {
        BigDecimal v = valor != null ? valor.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        return "$ " + String.format("%,.2f", v);
    }

    private BigDecimal nz(BigDecimal v) {
        return v != null ? v : BigDecimal.ZERO;
    }

    private int nz(Integer v) {
        return v != null ? v : 0;
    }

    private String texto(String v) {
        return v != null ? v : "";
    }
}

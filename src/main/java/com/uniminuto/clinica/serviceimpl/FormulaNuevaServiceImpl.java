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
import com.uniminuto.clinica.entity.FormulaMedica;
import com.uniminuto.clinica.entity.FormulaMedicamento;
import com.uniminuto.clinica.entity.Mascota;
import com.uniminuto.clinica.entity.Medicamento;
import com.uniminuto.clinica.entity.Medico;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.FormulaDetalleRq;
import com.uniminuto.clinica.models.FormulaDetalleRs;
import com.uniminuto.clinica.models.FormulaNuevaRq;
import com.uniminuto.clinica.models.FormulaNuevaRs;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.repository.CitaRepository;
import com.uniminuto.clinica.repository.FormulaMedicaRepository;
import com.uniminuto.clinica.repository.FormulaMedicamentoRepository;
import com.uniminuto.clinica.repository.MedicamentoRepository;
import com.uniminuto.clinica.repository.MedicoRepository;
import com.uniminuto.clinica.service.FormulaNuevaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementacion de las formulas con varios medicamentos y su PDF.
 *
 * Al crear una formula se valida que cada medicamento exista; se consulta su
 * existencia como informacion, pero NO se descuenta el inventario (eso ocurre
 * en la dispensacion del modulo de despacho).
 */
@Service
public class FormulaNuevaServiceImpl implements FormulaNuevaService {

    private static final DateTimeFormatter FORMATO =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final FormulaMedicaRepository formulaRepository;
    private final FormulaMedicamentoRepository detalleRepository;
    private final CitaRepository citaRepository;
    private final MedicoRepository medicoRepository;
    private final MedicamentoRepository medicamentoRepository;

    public FormulaNuevaServiceImpl(FormulaMedicaRepository formulaRepository,
                                   FormulaMedicamentoRepository detalleRepository,
                                   CitaRepository citaRepository,
                                   MedicoRepository medicoRepository,
                                   MedicamentoRepository medicamentoRepository) {
        this.formulaRepository = formulaRepository;
        this.detalleRepository = detalleRepository;
        this.citaRepository = citaRepository;
        this.medicoRepository = medicoRepository;
        this.medicamentoRepository = medicamentoRepository;
    }

    @Override
    public List<FormulaNuevaRs> listar() {
        return formulaRepository.findAllByOrderByFechaCreacionRegistroDesc().stream()
                .map(this::mapear).toList();
    }

    @Override
    public FormulaNuevaRs obtener(Long id) {
        return this.mapear(this.buscarFormula(id));
    }

    @Override
    @Transactional
    public MiRespuestaRS crear(FormulaNuevaRq rq) {
        if (rq == null || rq.getCitaId() == null) {
            throw new BadRequestException("La cita es obligatoria para la formula.");
        }
        if (rq.getMedicamentos() == null || rq.getMedicamentos().isEmpty()) {
            throw new BadRequestException("La formula debe tener al menos un medicamento.");
        }
        Cita cita = citaRepository.findById(rq.getCitaId())
                .orElseThrow(() -> new BadRequestException("La cita no existe."));

        FormulaMedica formula = new FormulaMedica();
        formula.setCita(cita);
        formula.setMedicoId(rq.getMedicoId());
        formula.setIndicaciones(rq.getIndicaciones());
        formula.setFechaCreacion(LocalDateTime.now());
        formula.setFechaCreacionRegistro(LocalDateTime.now());
        formula = formulaRepository.save(formula);

        for (FormulaDetalleRq det : rq.getMedicamentos()) {
            if (det.getMedicamentoId() == null) {
                throw new BadRequestException("Cada item de la formula debe indicar el medicamento.");
            }
            Medicamento med = medicamentoRepository.findById(det.getMedicamentoId())
                    .orElseThrow(() -> new BadRequestException(
                            "El medicamento con id " + det.getMedicamentoId() + " no existe."));
            if (det.getDosis() == null || det.getDosis().isBlank()) {
                throw new BadRequestException("La dosis es obligatoria para " + med.getNombre() + ".");
            }
            FormulaMedicamento detalle = new FormulaMedicamento();
            detalle.setFormulaId(formula.getId());
            detalle.setMedicamentoId(med.getId());
            detalle.setDosis(det.getDosis());
            detalle.setFrecuencia(det.getFrecuencia());
            detalle.setDuracion(det.getDuracion());
            detalle.setCantidad(det.getCantidad() != null && det.getCantidad() > 0 ? det.getCantidad() : 1);
            detalle.setViaAdministracion(det.getViaAdministracion());
            detalle.setIndicaciones(det.getIndicaciones());
            detalleRepository.save(detalle);
        }

        MiRespuestaRS rta = new MiRespuestaRS();
        rta.setStatus(200);
        rta.setMessage("Formula medica creada correctamente.");
        return rta;
    }

    @Override
    public byte[] generarPdf(Long id) {
        FormulaNuevaRs formula = this.mapear(this.buscarFormula(id));
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
            Paragraph st = new Paragraph("Formula medica", subtitulo);
            st.setAlignment(Element.ALIGN_CENTER);
            st.setSpacingAfter(12);
            doc.add(st);

            doc.add(new Paragraph("Numero de formula: " + formula.getId(), normal));
            doc.add(new Paragraph("Fecha: "
                    + (formula.getFechaCreacion() != null ? formula.getFechaCreacion().format(FORMATO) : ""), normal));
            doc.add(new Paragraph("Mascota: " + texto(formula.getMascotaNombre()), normal));
            doc.add(new Paragraph("Propietario: " + texto(formula.getClienteNombre()), normal));
            doc.add(new Paragraph("Medico: " + texto(formula.getMedicoNombre()), normal));
            if (formula.getIndicaciones() != null && !formula.getIndicaciones().isBlank()) {
                doc.add(new Paragraph("Indicaciones generales: " + formula.getIndicaciones(), normal));
            }

            Paragraph tablaTit = new Paragraph("Medicamentos prescritos", subtitulo);
            tablaTit.setSpacingBefore(12);
            tablaTit.setSpacingAfter(6);
            doc.add(tablaTit);

            PdfPTable tabla = new PdfPTable(new float[]{3, 2, 2, 2, 1.2f, 2});
            tabla.setWidthPercentage(100);
            this.encabezado(tabla, "Medicamento", "Dosis", "Frecuencia", "Duracion", "Cant.", "Via");
            for (FormulaDetalleRs d : formula.getMedicamentos()) {
                tabla.addCell(celda(texto(d.getMedicamentoNombre())));
                tabla.addCell(celda(texto(d.getDosis())));
                tabla.addCell(celda(texto(d.getFrecuencia())));
                tabla.addCell(celda(texto(d.getDuracion())));
                tabla.addCell(celda(String.valueOf(d.getCantidad())));
                tabla.addCell(celda(texto(d.getViaAdministracion())));
            }
            doc.add(tabla);

            Paragraph pie = new Paragraph(
                    "\n\nEste documento es una formula medica generada por el sistema.", normal);
            pie.setSpacingBefore(20);
            doc.add(pie);

            doc.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new BadRequestException("No fue posible generar el PDF de la formula: " + e.getMessage());
        }
    }

    // ----------------------------------------------------------------

    private FormulaMedica buscarFormula(Long id) {
        if (id == null) {
            throw new BadRequestException("El identificador de la formula es obligatorio.");
        }
        Optional<FormulaMedica> opt = formulaRepository.findById(id);
        if (opt.isEmpty()) {
            throw new BadRequestException("La formula no existe.");
        }
        return opt.get();
    }

    private FormulaNuevaRs mapear(FormulaMedica formula) {
        FormulaNuevaRs rs = new FormulaNuevaRs();
        rs.setId(formula.getId());
        rs.setIndicaciones(formula.getIndicaciones());
        rs.setFechaCreacion(formula.getFechaCreacion() != null
                ? formula.getFechaCreacion() : formula.getFechaCreacionRegistro());

        Cita cita = formula.getCita();
        if (cita != null) {
            rs.setCitaId(cita.getId());
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
        // Medico: por el medicoId de la cabecera o, si no, el de la cita.
        Long medicoId = formula.getMedicoId();
        if (medicoId == null && cita != null && cita.getMedico() != null) {
            medicoId = cita.getMedico().getId();
        }
        if (medicoId != null) {
            medicoRepository.findById(medicoId).ifPresent(m ->
                    rs.setMedicoNombre((texto(m.getNombres()) + " " + texto(m.getApellidos())).trim()));
        }

        List<FormulaDetalleRs> detalles = new ArrayList<>();
        for (FormulaMedicamento det : detalleRepository.findByFormulaId(formula.getId())) {
            FormulaDetalleRs d = new FormulaDetalleRs();
            d.setId(det.getId());
            d.setMedicamentoId(det.getMedicamentoId());
            medicamentoRepository.findById(det.getMedicamentoId())
                    .ifPresent(m -> d.setMedicamentoNombre(m.getNombre()));
            d.setDosis(det.getDosis());
            d.setFrecuencia(det.getFrecuencia());
            d.setDuracion(det.getDuracion());
            d.setCantidad(det.getCantidad());
            d.setViaAdministracion(det.getViaAdministracion());
            d.setIndicaciones(det.getIndicaciones());
            detalles.add(d);
        }
        rs.setMedicamentos(detalles);
        return rs;
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

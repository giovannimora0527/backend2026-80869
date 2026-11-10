package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.LogCargaInventario;
import com.uniminuto.clinica.entity.Medicamento;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.CargaMasivaRs;
import com.uniminuto.clinica.models.FilaCargaRs;
import com.uniminuto.clinica.models.LoteRq;
import com.uniminuto.clinica.repository.LogCargaInventarioRepository;
import com.uniminuto.clinica.repository.MedicamentoRepository;
import com.uniminuto.clinica.security.ContextoSeguridad;
import com.uniminuto.clinica.security.UsuarioAutenticado;
import com.uniminuto.clinica.service.CargaMasivaService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Implementacion de la carga masiva de medicamentos desde Excel o CSV.
 *
 * Encabezados esperados (primera fila del archivo), en este orden:
 *   nombre | codigo | presentacion | requiere_invima | registro_invima |
 *   precio_compra | precio_venta | stock_minimo | numero_lote | cantidad |
 *   fecha_vencimiento
 *
 * Reglas de validacion por fila:
 *   - nombre obligatorio.
 *   - codigo, si se indica, debe tener 8 caracteres.
 *   - registro_invima obligatorio cuando requiere_invima es verdadero.
 *   - precios y cantidad numericos y no negativos.
 *   - fecha_vencimiento valida (formato yyyy-MM-dd) y no anterior a hoy.
 *   - sin nombres ni codigos duplicados (dentro del archivo y contra la base).
 *
 * Un error en una fila no afecta el procesamiento de las demas.
 */
@Service
public class CargaMasivaServiceImpl implements CargaMasivaService {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final int COLUMNAS_ESPERADAS = 11;

    private final MedicamentoRepository medicamentoRepository;
    private final LogCargaInventarioRepository logRepository;
    private final InventarioServiceImpl inventarioService;

    public CargaMasivaServiceImpl(MedicamentoRepository medicamentoRepository,
                                  LogCargaInventarioRepository logRepository,
                                  InventarioServiceImpl inventarioService) {
        this.medicamentoRepository = medicamentoRepository;
        this.logRepository = logRepository;
        this.inventarioService = inventarioService;
    }

    @Override
    public CargaMasivaRs previsualizar(MultipartFile archivo) {
        List<FilaCargaRs> filas = this.leerYValidar(archivo);
        return this.construirResultado(filas, false, null);
    }

    @Override
    @Transactional
    public CargaMasivaRs confirmar(MultipartFile archivo) {
        List<FilaCargaRs> filas = this.leerYValidar(archivo);

        int persistidos = 0;
        for (FilaCargaRs fila : filas) {
            if (Boolean.TRUE.equals(fila.getValida())) {
                this.persistirFila(fila);
                persistidos++;
            }
        }

        CargaMasivaRs resultado = this.construirResultado(filas, true, persistidos
                + " registro(s) importado(s) correctamente.");
        this.registrarLog(archivo, resultado);
        return resultado;
    }

    // ----------------------------------------------------------------
    // Lectura y validacion
    // ----------------------------------------------------------------

    private List<FilaCargaRs> leerYValidar(MultipartFile archivo) {
        if (archivo == null || archivo.isEmpty()) {
            throw new BadRequestException("Debe adjuntar un archivo con datos.");
        }
        String nombre = archivo.getOriginalFilename() != null
                ? archivo.getOriginalFilename().toLowerCase() : "";

        List<String[]> filasCrudas;
        if (nombre.endsWith(".xlsx")) {
            filasCrudas = this.leerExcel(archivo);
        } else if (nombre.endsWith(".csv")) {
            filasCrudas = this.leerCsv(archivo);
        } else {
            throw new BadRequestException("Formato no soportado. Use un archivo .xlsx o .csv.");
        }

        if (filasCrudas.isEmpty()) {
            throw new BadRequestException("El archivo no contiene encabezados.");
        }
        // Validar encabezados (primera fila).
        String[] encabezados = filasCrudas.get(0);
        if (encabezados.length < COLUMNAS_ESPERADAS) {
            throw new BadRequestException(
                    "La estructura del archivo no es valida. Se esperan "
                            + COLUMNAS_ESPERADAS + " columnas.");
        }

        List<FilaCargaRs> resultado = new ArrayList<>();
        Set<String> nombresEnArchivo = new HashSet<>();
        Set<String> codigosEnArchivo = new HashSet<>();

        for (int i = 1; i < filasCrudas.size(); i++) {
            String[] cols = filasCrudas.get(i);
            FilaCargaRs fila = this.validarFila(i, cols, nombresEnArchivo, codigosEnArchivo);
            resultado.add(fila);
        }
        return resultado;
    }

    private FilaCargaRs validarFila(int indice, String[] cols,
                                    Set<String> nombresEnArchivo,
                                    Set<String> codigosEnArchivo) {
        FilaCargaRs fila = new FilaCargaRs();
        fila.setFila(indice);

        String nombre = valor(cols, 0);
        String codigo = valor(cols, 1);
        String presentacion = valor(cols, 2);
        String requiereInvimaTexto = valor(cols, 3);
        String registroInvima = valor(cols, 4);
        String precioCompra = valor(cols, 5);
        String precioVenta = valor(cols, 6);
        String stockMinimo = valor(cols, 7);
        String numeroLote = valor(cols, 8);
        String cantidad = valor(cols, 9);
        String fechaVencimiento = valor(cols, 10);

        fila.setNombre(nombre);
        fila.setCodigo(codigo);
        fila.setPresentacion(presentacion);
        fila.setRegistroInvima(registroInvima);
        fila.setPrecioCompra(precioCompra);
        fila.setPrecioVenta(precioVenta);
        fila.setStockMinimo(stockMinimo);
        fila.setNumeroLote(numeroLote);
        fila.setCantidad(cantidad);
        fila.setFechaVencimiento(fechaVencimiento);

        boolean requiereInvima = this.interpretarBooleano(requiereInvimaTexto);
        fila.setRequiereInvima(requiereInvima);

        String error = this.obtenerError(nombre, codigo, requiereInvima, registroInvima,
                precioCompra, precioVenta, stockMinimo, numeroLote, cantidad,
                fechaVencimiento, nombresEnArchivo, codigosEnArchivo);

        if (error == null) {
            fila.setValida(Boolean.TRUE);
            nombresEnArchivo.add(nombre.trim().toLowerCase());
            if (codigo != null && !codigo.isBlank()) {
                codigosEnArchivo.add(codigo.trim());
            }
        } else {
            fila.setValida(Boolean.FALSE);
            fila.setMotivoError(error);
        }
        return fila;
    }

    private String obtenerError(String nombre, String codigo, boolean requiereInvima,
                                String registroInvima, String precioCompra,
                                String precioVenta, String stockMinimo, String numeroLote,
                                String cantidad, String fechaVencimiento,
                                Set<String> nombresEnArchivo, Set<String> codigosEnArchivo) {
        if (nombre == null || nombre.isBlank()) {
            return "El nombre es obligatorio.";
        }
        if (nombresEnArchivo.contains(nombre.trim().toLowerCase())) {
            return "Nombre duplicado dentro del archivo.";
        }
        if (medicamentoRepository.existsByNombreIgnoreCase(nombre.trim())) {
            return "Ya existe un medicamento con ese nombre en el sistema.";
        }
        if (codigo != null && !codigo.isBlank()) {
            if (codigo.trim().length() != 8) {
                return "El codigo debe tener 8 caracteres.";
            }
            if (codigosEnArchivo.contains(codigo.trim())) {
                return "Codigo duplicado dentro del archivo.";
            }
            if (medicamentoRepository.existsByCodigo(codigo.trim())) {
                return "Ya existe un medicamento con ese codigo en el sistema.";
            }
        }
        if (requiereInvima && (registroInvima == null || registroInvima.isBlank())) {
            return "El registro Invima es obligatorio para medicamentos.";
        }
        if (this.numeroInvalidoOnegativo(precioCompra)) {
            return "El precio de compra debe ser un numero no negativo.";
        }
        if (this.numeroInvalidoOnegativo(precioVenta)) {
            return "El precio de venta debe ser un numero no negativo.";
        }
        if (stockMinimo != null && !stockMinimo.isBlank() && this.enteroInvalidoNegativo(stockMinimo)) {
            return "El stock minimo debe ser un entero no negativo.";
        }
        if (numeroLote == null || numeroLote.isBlank()) {
            return "El numero de lote es obligatorio.";
        }
        if (cantidad == null || this.enteroInvalidoNegativo(cantidad)) {
            return "La cantidad debe ser un entero no negativo.";
        }
        LocalDate fecha = this.parsearFecha(fechaVencimiento);
        if (fecha == null) {
            return "La fecha de vencimiento no es valida (formato esperado: yyyy-MM-dd).";
        }
        if (fecha.isBefore(LocalDate.now())) {
            return "La fecha de vencimiento no puede ser anterior a hoy.";
        }
        return null;
    }

    // ----------------------------------------------------------------
    // Persistencia de una fila valida
    // ----------------------------------------------------------------

    private void persistirFila(FilaCargaRs fila) {
        Medicamento medicamento = new Medicamento();
        medicamento.setNombre(fila.getNombre().trim());
        medicamento.setCodigo(fila.getCodigo() != null && !fila.getCodigo().isBlank()
                ? fila.getCodigo().trim() : null);
        medicamento.setPresentacion(fila.getPresentacion());
        medicamento.setRequiereInvima(fila.getRequiereInvima());
        medicamento.setRegistroInvima(fila.getRegistroInvima());
        medicamento.setPrecioCompra(this.aBigDecimal(fila.getPrecioCompra()));
        medicamento.setPrecioVenta(this.aBigDecimal(fila.getPrecioVenta()));
        medicamento.setStockMinimo(this.aEntero(fila.getStockMinimo()));
        medicamento.setFechaCompra(LocalDate.now());
        medicamento.setFechaVence(this.parsearFecha(fila.getFechaVencimiento()));
        medicamento.setFechaCreacionRegistro(LocalDateTime.now());
        medicamento = medicamentoRepository.save(medicamento);

        LoteRq loteRq = new LoteRq();
        loteRq.setNumeroLote(fila.getNumeroLote().trim());
        loteRq.setCantidad(this.aEntero(fila.getCantidad()));
        loteRq.setPrecioCompra(this.aBigDecimal(fila.getPrecioCompra()));
        loteRq.setFechaIngreso(LocalDate.now());
        loteRq.setFechaVencimiento(this.parsearFecha(fila.getFechaVencimiento()));
        inventarioService.crearLote(medicamento, loteRq);
    }

    // ----------------------------------------------------------------
    // Lectura de archivos
    // ----------------------------------------------------------------

    private List<String[]> leerExcel(MultipartFile archivo) {
        List<String[]> filas = new ArrayList<>();
        DataFormatter formatter = new DataFormatter();
        try (InputStream is = archivo.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {
            Sheet hoja = workbook.getSheetAt(0);
            for (Row row : hoja) {
                String[] cols = new String[COLUMNAS_ESPERADAS];
                for (int c = 0; c < COLUMNAS_ESPERADAS; c++) {
                    Cell celda = row.getCell(c);
                    cols[c] = celda != null ? formatter.formatCellValue(celda).trim() : "";
                }
                filas.add(cols);
            }
        } catch (Exception e) {
            throw new BadRequestException("No fue posible leer el archivo Excel: " + e.getMessage());
        }
        return filas;
    }

    private List<String[]> leerCsv(MultipartFile archivo) {
        List<String[]> filas = new ArrayList<>();
        try (InputStream is = archivo.getInputStream();
             BufferedReader reader = new BufferedReader(
                     new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                if (linea.isBlank()) {
                    continue;
                }
                String[] partes = linea.split(";|,", -1);
                String[] cols = new String[COLUMNAS_ESPERADAS];
                for (int c = 0; c < COLUMNAS_ESPERADAS; c++) {
                    cols[c] = c < partes.length ? partes[c].trim() : "";
                }
                filas.add(cols);
            }
        } catch (Exception e) {
            throw new BadRequestException("No fue posible leer el archivo CSV: " + e.getMessage());
        }
        return filas;
    }

    // ----------------------------------------------------------------
    // Utilidades
    // ----------------------------------------------------------------

    private CargaMasivaRs construirResultado(List<FilaCargaRs> filas, boolean confirmado, String mensaje) {
        int validos = (int) filas.stream().filter(f -> Boolean.TRUE.equals(f.getValida())).count();
        CargaMasivaRs rs = new CargaMasivaRs();
        rs.setTotalRegistros(filas.size());
        rs.setRegistrosValidos(validos);
        rs.setRegistrosConError(filas.size() - validos);
        rs.setConfirmado(confirmado);
        rs.setFilas(filas);
        rs.setMensaje(mensaje != null ? mensaje
                : "Vista previa generada. Revise los registros antes de confirmar.");
        return rs;
    }

    private void registrarLog(MultipartFile archivo, CargaMasivaRs resultado) {
        LogCargaInventario log = new LogCargaInventario();
        log.setFecha(LocalDateTime.now());
        log.setUsuarioResponsable(this.obtenerUsuarioResponsable());
        log.setNombreArchivo(archivo.getOriginalFilename());
        log.setTotalRegistros(resultado.getTotalRegistros());
        log.setRegistrosValidos(resultado.getRegistrosValidos());
        log.setRegistrosConError(resultado.getRegistrosConError());
        log.setDetalle("Carga confirmada. Validos: " + resultado.getRegistrosValidos()
                + ", con error: " + resultado.getRegistrosConError() + ".");
        logRepository.save(log);
    }

    private String obtenerUsuarioResponsable() {
        UsuarioAutenticado usuario = ContextoSeguridad.obtenerUsuario();
        return usuario != null ? usuario.username() : "sistema";
    }

    private String valor(String[] cols, int indice) {
        if (cols == null || indice >= cols.length || cols[indice] == null) {
            return "";
        }
        return cols[indice].trim();
    }

    private boolean interpretarBooleano(String texto) {
        if (texto == null) {
            return true;
        }
        String t = texto.trim().toLowerCase();
        // Por defecto se asume que requiere Invima (es medicamento).
        return !(t.equals("no") || t.equals("false") || t.equals("0") || t.equals("n"));
    }

    private boolean numeroInvalidoOnegativo(String texto) {
        if (texto == null || texto.isBlank()) {
            return false; // opcional; se interpreta como 0
        }
        try {
            return new BigDecimal(texto.trim().replace(",", ".")).signum() < 0;
        } catch (NumberFormatException e) {
            return true;
        }
    }

    private boolean enteroInvalidoNegativo(String texto) {
        if (texto == null || texto.isBlank()) {
            return true;
        }
        try {
            return Integer.parseInt(texto.trim()) < 0;
        } catch (NumberFormatException e) {
            return true;
        }
    }

    private LocalDate parsearFecha(String texto) {
        if (texto == null || texto.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(texto.trim(), FORMATO_FECHA);
        } catch (Exception e) {
            return null;
        }
    }

    private BigDecimal aBigDecimal(String texto) {
        if (texto == null || texto.isBlank()) {
            return BigDecimal.ZERO;
        }
        try {
            return new BigDecimal(texto.trim().replace(",", "."));
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }

    private int aEntero(String texto) {
        if (texto == null || texto.isBlank()) {
            return 0;
        }
        try {
            return Integer.parseInt(texto.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}

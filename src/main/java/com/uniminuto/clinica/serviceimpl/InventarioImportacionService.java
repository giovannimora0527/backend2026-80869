package com.uniminuto.clinica.serviceimpl;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PushbackReader;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Service
public class InventarioImportacionService {

    private static final List<String> COLUMNAS = List.of(
            "codigo", "nombre", "tipoProducto", "presentacion", "registroInvima",
            "precioVenta", "stockMinimo", "numeroLote", "fechaCompra",
            "fechaIngreso", "fechaVence", "precioCompra", "cantidadIngresada"
    );

    private static final BigDecimal PRECIO_MAXIMO = new BigDecimal("9999999999.99");

    private final JdbcTemplate jdbc;

    public InventarioImportacionService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public record FilaVista(
            long fila,
            Map<String, String> datos,
            boolean valida,
            List<String> errores
    ) {}

    public record VistaPrevia(
            int total,
            long validas,
            long conErrores,
            List<FilaVista> filas
    ) {}

    public VistaPrevia revisar(MultipartFile archivo) {
        if (archivo == null || archivo.isEmpty()) {
            throw new IllegalArgumentException("Selecciona un archivo CSV con datos.");
        }

        Set<String> codigosExistentes = new HashSet<>();
        Set<String> nombresExistentes = new HashSet<>();

        jdbc.query("SELECT codigo, nombre FROM medicamento", rs -> {
            codigosExistentes.add(normalizar(rs.getString("codigo")));
            nombresExistentes.add(normalizar(rs.getString("nombre")));
        });

        Set<String> codigosDelArchivo = new HashSet<>();
        Set<String> nombresDelArchivo = new HashSet<>();
        List<FilaVista> filas = new ArrayList<>();
        LocalDate hoy = LocalDate.now();

        CSVFormat formato = CSVFormat.DEFAULT.builder()
                .setDelimiter(';')
                .setIgnoreEmptyLines(false)
                .build();

        try (PushbackReader lector = new PushbackReader(
                new InputStreamReader(archivo.getInputStream(), StandardCharsets.UTF_8), 1)) {

            // Windows PowerShell puede generar una marca UTF-8 antes del encabezado.
            int primero = lector.read();
            if (primero != -1 && primero != '\uFEFF') {
                lector.unread(primero);
            }

            try (CSVParser parser = formato.parse(lector)) {
                var registros = parser.iterator();

                if (!registros.hasNext()) {
                    throw new IllegalArgumentException("El archivo está vacío.");
                }

                CSVRecord encabezado = registros.next();
                List<String> nombresColumnas = new ArrayList<>();
                encabezado.forEach(valor -> nombresColumnas.add(valor.trim()));

                if (nombresColumnas.size() != COLUMNAS.size()
                        || !new HashSet<>(nombresColumnas).equals(new HashSet<>(COLUMNAS))) {
                    throw new IllegalArgumentException(
                            "Revisa los encabezados y el separador (;). Se esperan: "
                                    + String.join("; ", COLUMNAS)
                    );
                }

                while (registros.hasNext()) {
                    CSVRecord registro = registros.next();
                    Map<String, String> datos = new LinkedHashMap<>();
                    List<String> errores = new ArrayList<>();

                    for (int i = 0; i < nombresColumnas.size(); i++) {
                        datos.put(nombresColumnas.get(i),
                                i < registro.size() ? registro.get(i).trim() : "");
                    }

                    if (registro.size() != nombresColumnas.size()) {
                        errores.add("La cantidad de columnas no coincide con el encabezado.");
                    }

                    validarFila(datos, hoy, errores);

                    String codigo = normalizar(datos.get("codigo"));
                    String nombre = normalizar(datos.get("nombre"));

                    if (!codigo.isEmpty()) {
                        if (codigosExistentes.contains(codigo)) {
                            errores.add("El código ya existe en el inventario.");
                        }
                        if (!codigosDelArchivo.add(codigo)) {
                            errores.add("El código se repite en el archivo.");
                        }
                    }

                    if (!nombre.isEmpty()) {
                        if (nombresExistentes.contains(nombre)) {
                            errores.add("El nombre ya existe en el inventario.");
                        }
                        if (!nombresDelArchivo.add(nombre)) {
                            errores.add("El nombre se repite en el archivo.");
                        }
                    }

                    filas.add(new FilaVista(
                            registro.getRecordNumber(), datos, errores.isEmpty(), errores
                    ));
                }
            }
        } catch (IOException | UncheckedIOException ex) {
            throw new IllegalArgumentException(
                    "No se pudo leer el CSV. Revisa las comillas y guárdalo en UTF-8.", ex
            );
        }

        if (filas.isEmpty()) {
            throw new IllegalArgumentException("El archivo tiene encabezados, pero no tiene productos.");
        }

        long validas = filas.stream().filter(FilaVista::valida).count();
        return new VistaPrevia(filas.size(), validas, filas.size() - validas, filas);
    }

    private void validarFila(Map<String, String> datos, LocalDate hoy, List<String> errores) {
        if (!datos.get("codigo").matches("[A-Za-z0-9]{8}")) {
            errores.add("El código debe tener exactamente 8 letras o números.");
        }

        validarTexto(datos, "nombre", 100, true, errores);
        validarTexto(datos, "presentacion", 100, false, errores);
        validarTexto(datos, "numeroLote", 60, true, errores);

        String tipo = datos.get("tipoProducto").toUpperCase(Locale.ROOT);
        if (!tipo.equals("MEDICAMENTO") && !tipo.equals("UTENSILIO")) {
            errores.add("El tipo debe ser MEDICAMENTO o UTENSILIO.");
        }

        validarTexto(datos, "registroInvima", 100, tipo.equals("MEDICAMENTO"), errores);
        validarPrecio(datos, "precioVenta", errores);
        validarPrecio(datos, "precioCompra", errores);
        validarEntero(datos, "stockMinimo", 0, errores);
        validarEntero(datos, "cantidadIngresada", 1, errores);

        LocalDate compra = leerFecha(datos, "fechaCompra", true, errores);
        LocalDate ingreso = leerFecha(datos, "fechaIngreso", true, errores);
        LocalDate vence = leerFecha(datos, "fechaVence", tipo.equals("MEDICAMENTO"), errores);

        if (compra != null && compra.isAfter(hoy)) {
            errores.add("La compra no puede tener fecha futura.");
        }
        if (ingreso != null && ingreso.isAfter(hoy)) {
            errores.add("El ingreso no puede tener fecha futura.");
        }
        if (compra != null && ingreso != null && ingreso.isBefore(compra)) {
            errores.add("El ingreso no puede ser anterior a la compra.");
        }
        if (vence != null && vence.isBefore(hoy)) {
            errores.add("El lote está vencido.");
        }
    }

    private void validarTexto(
            Map<String, String> datos, String campo, int maximo,
            boolean obligatorio, List<String> errores
    ) {
        String valor = datos.get(campo);
        if (obligatorio && valor.isBlank()) {
            errores.add("El campo " + campo + " es obligatorio.");
        } else if (valor.length() > maximo) {
            errores.add("El campo " + campo + " supera los " + maximo + " caracteres.");
        }
    }

    private void validarPrecio(Map<String, String> datos, String campo, List<String> errores) {
        String valor = datos.get(campo);
        if (!valor.matches("[0-9]+([.,][0-9]{1,2})?")) {
            errores.add(campo + " debe ser un precio mayor o igual a cero con máximo dos decimales.");
            return;
        }

        BigDecimal precio = new BigDecimal(valor.replace(',', '.'));
        if (precio.compareTo(PRECIO_MAXIMO) > 0) {
            errores.add(campo + " supera el valor permitido.");
        }
    }

    private void validarEntero(
            Map<String, String> datos, String campo, int minimo, List<String> errores
    ) {
        try {
            int numero = Integer.parseInt(datos.get(campo));
            if (numero < minimo) {
                errores.add(campo + " debe ser mayor o igual a " + minimo + ".");
            }
        } catch (NumberFormatException ex) {
            errores.add(campo + " debe ser un número entero válido.");
        }
    }

    private LocalDate leerFecha(
            Map<String, String> datos, String campo, boolean obligatoria, List<String> errores
    ) {
        String valor = datos.get(campo);
        if (valor.isBlank()) {
            if (obligatoria) {
                errores.add("El campo " + campo + " es obligatorio.");
            }
            return null;
        }

        try {
            return LocalDate.parse(valor);
        } catch (DateTimeParseException ex) {
            errores.add(campo + " debe tener el formato AAAA-MM-DD.");
            return null;
        }
    }

    private static String normalizar(String valor) {
        if (valor == null) {
            return "";
        }
        return Normalizer.normalize(valor.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .toUpperCase(Locale.ROOT);
    }
}

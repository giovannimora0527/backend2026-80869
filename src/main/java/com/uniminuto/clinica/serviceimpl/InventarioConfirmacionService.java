package com.uniminuto.clinica.serviceimpl;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.Savepoint;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@Service
public class InventarioConfirmacionService {

    private final JdbcTemplate jdbc;
    private final InventarioImportacionService vistaPrevia;

    public InventarioConfirmacionService(
            JdbcTemplate jdbc,
            InventarioImportacionService vistaPrevia
    ) {
        this.jdbc = jdbc;
        this.vistaPrevia = vistaPrevia;
    }

    public record Resultado(
            long importacionId,
            int total,
            int importadas,
            int rechazadas,
            boolean yaConfirmada
    ) {}

    private record UsuarioCarga(long id, String perfil) {}

    @Transactional
    public Resultado confirmar(String autorizacion, MultipartFile archivo) {
        UsuarioCarga usuario = obtenerAdministrador(autorizacion);

        if (archivo == null || archivo.isEmpty()) {
            throw new IllegalArgumentException("Selecciona un archivo CSV con datos.");
        }

        String clave = claveOperacion(usuario.id(), archivo);
        String nombreArchivo = archivo.getOriginalFilename();
        if (nombreArchivo == null || nombreArchivo.isBlank()) {
            nombreArchivo = "inventario.csv";
        }
        if (nombreArchivo.length() > 255) {
            throw new IllegalArgumentException("El nombre del archivo supera los 255 caracteres.");
        }

        // La clave única hace que dos confirmaciones simultáneas del mismo
        // usuario y archivo compartan una sola operación.
        jdbc.update("""
                INSERT INTO inventario_importacion
                    (clave_operacion, usuario_id, nombre_archivo,
                     total_filas, filas_importadas, filas_rechazadas)
                VALUES (?, ?, ?, 0, 0, 0)
                ON DUPLICATE KEY UPDATE id = id
                """, clave, usuario.id(), nombreArchivo);

        Resultado anterior = jdbc.queryForObject("""
                SELECT id, total_filas, filas_importadas, filas_rechazadas
                FROM inventario_importacion
                WHERE clave_operacion = ? AND usuario_id = ?
                FOR UPDATE
                """,
                (rs, fila) -> new Resultado(
                        rs.getLong("id"),
                        rs.getInt("total_filas"),
                        rs.getInt("filas_importadas"),
                        rs.getInt("filas_rechazadas"),
                        true
                ), clave, usuario.id());

        if (anterior == null) {
            throw new IllegalStateException("No se pudo identificar la importación.");
        }
        if (anterior.total() > 0) {
            return anterior;
        }

        // Se revisa otra vez en el servidor: el catálogo pudo cambiar
        // desde que el usuario abrió la vista previa.
        var vista = vistaPrevia.revisar(archivo);
        long importacionId = anterior.importacionId();
        int importadas = 0;

        for (var fila : vista.filas()) {
            if (!fila.valida()) {
                registrarFila(importacionId, fila, null, String.join(" / ", fila.errores()));
                continue;
            }

            boolean guardada = guardarFilaValida(importacionId, fila);
            if (guardada) {
                importadas++;
            }
        }

        int rechazadas = vista.total() - importadas;
        jdbc.update("""
                UPDATE inventario_importacion
                SET total_filas = ?, filas_importadas = ?, filas_rechazadas = ?
                WHERE id = ?
                """, vista.total(), importadas, rechazadas, importacionId);

        return new Resultado(importacionId, vista.total(), importadas, rechazadas, false);
    }

    private UsuarioCarga obtenerAdministrador(String autorizacion) {
        if (autorizacion == null || !autorizacion.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Inicia sesión.");
        }

        String token = autorizacion.substring(7).trim();
        if (token.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Inicia sesión.");
        }

        List<UsuarioCarga> usuarios = jdbc.query("""
                SELECT u.id, p.nombre
                FROM `session` s
                JOIN usuario u ON u.id = s.user_id
                JOIN perfil p ON p.perfil_id = u.rol
                WHERE BINARY s.token = BINARY ?
                  AND s.fecha_expiracion > CURRENT_TIMESTAMP
                  AND u.activo = 1
                  AND p.activo = 1
                ORDER BY s.session_id DESC
                LIMIT 1
                """,
                (rs, fila) -> new UsuarioCarga(rs.getLong("id"), rs.getString("nombre")),
                token);

        if (usuarios.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "La sesión expiró. Vuelve a iniciar sesión."
            );
        }

        UsuarioCarga usuario = usuarios.get(0);
        if (!"Administrador".equalsIgnoreCase(usuario.perfil())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "Se requiere perfil Administrador para importar."
            );
        }
        return usuario;
    }

    private boolean guardarFilaValida(
            long importacionId, InventarioImportacionService.FilaVista fila
    ) {
        Boolean guardada = jdbc.execute((ConnectionCallback<Boolean>) conexion -> {
            Savepoint inicioFila = conexion.setSavepoint();
            Long productoId = null;
            String motivo = null;

            try {
                productoId = insertarProductoYLote(fila.datos());
            } catch (DuplicateKeyException ex) {
                conexion.rollback(inicioFila);
                motivo = "El código, nombre o lote ya existe al confirmar.";
            } catch (DataIntegrityViolationException ex) {
                conexion.rollback(inicioFila);
                motivo = "Los datos de esta fila incumplen una restricción de la base de datos.";
            }

            conexion.releaseSavepoint(inicioFila);
            registrarFila(importacionId, fila, productoId, motivo);
            return motivo == null;
        });

        return Boolean.TRUE.equals(guardada);
    }

    private long insertarProductoYLote(Map<String, String> d) {
        String codigo = d.get("codigo").toUpperCase(Locale.ROOT);
        String tipo = d.get("tipoProducto").toUpperCase(Locale.ROOT);
        String vence = opcional(d.get("fechaVence"));
        int cantidad = Integer.parseInt(d.get("cantidadIngresada"));

        jdbc.update("""
                INSERT INTO medicamento (
                    codigo, nombre, tipo_producto, presentacion, registro_invima,
                    precio_venta, stock_minimo, fecha_compra, fecha_vence,
                    fecha_creacion_registro
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)
                """,
                codigo, d.get("nombre"), tipo, opcional(d.get("presentacion")),
                opcional(d.get("registroInvima")), precio(d.get("precioVenta")),
                Integer.parseInt(d.get("stockMinimo")), d.get("fechaCompra"), vence);

        Long productoId = jdbc.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
        if (productoId == null) {
            throw new IllegalStateException("No se obtuvo el ID del producto.");
        }

        jdbc.update("""
                INSERT INTO medicamento_lote (
                    medicamento_id, numero_lote, fecha_ingreso, fecha_compra,
                    fecha_vence, precio_compra, cantidad_ingresada, cantidad_disponible
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """,
                productoId, d.get("numeroLote"), d.get("fechaIngreso"),
                d.get("fechaCompra"), vence, precio(d.get("precioCompra")),
                cantidad, cantidad);

        return productoId;
    }

    private void registrarFila(
            long importacionId,
            InventarioImportacionService.FilaVista fila,
            Long productoId,
            String motivo
    ) {
        jdbc.update("""
                INSERT INTO inventario_importacion_fila (
                    importacion_id, numero_fila, codigo_leido, nombre_leido,
                    estado, medicamento_id, motivo
                )
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """,
                importacionId, fila.fila(), fila.datos().get("codigo"),
                fila.datos().get("nombre"), motivo == null ? "IMPORTADA" : "RECHAZADA",
                productoId, motivo);
    }

    private String claveOperacion(long usuarioId, MultipartFile archivo) {
        try {
            UUID huellaArchivo = UUID.nameUUIDFromBytes(archivo.getBytes());
            String operacion = usuarioId + ":" + huellaArchivo;
            return UUID.nameUUIDFromBytes(
                    operacion.getBytes(StandardCharsets.UTF_8)
            ).toString();
        } catch (IOException ex) {
            throw new IllegalArgumentException("No se pudo leer el archivo CSV.", ex);
        }
    }

    private BigDecimal precio(String valor) {
        return new BigDecimal(valor.replace(',', '.'));
    }

    private String opcional(String valor) {
        return valor == null || valor.isBlank() ? null : valor;
    }
}
package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.entity.Medicamento;
import com.uniminuto.clinica.repository.MedicamentoRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/medicamento")
public class AltaProductoController {

    private final MedicamentoRepository productos;

    public AltaProductoController(MedicamentoRepository productos) {
        this.productos = productos;
    }

    public record DatosProducto(
            String codigo,
            String nombre,
            Medicamento.TipoProducto tipoProducto,
            String registroInvima,
            String presentacion,
            String descripcion,
            BigDecimal precioVenta,
            Integer stockMinimo,
            LocalDate fechaProximoPedido
    ) {
        static DatosProducto desde(Medicamento producto) {
            return new DatosProducto(
                    producto.getCodigo(),
                    producto.getNombre(),
                    producto.getTipoProducto(),
                    producto.getRegistroInvima(),
                    producto.getPresentacion(),
                    producto.getDescripcion(),
                    producto.getPrecioVenta(),
                    producto.getStockMinimo(),
                    producto.getFechaProximoPedido()
            );
        }
    }

    @GetMapping(value = "/{id}/detalle", produces = "application/json")
    public ResponseEntity<DatosProducto> detalle(@PathVariable("id") Long id) {
        return productos.findById(id)
                .map(producto -> ResponseEntity.ok(DatosProducto.desde(producto)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping(value = "/crear", consumes = "application/json")
    public ResponseEntity<?> crear(@RequestBody DatosProducto datos) {
        return guardar(new Medicamento(), datos);
    }

    @PutMapping(value = "/{id}/detalle", consumes = "application/json")
    public ResponseEntity<?> editar(
            @PathVariable("id") Long id,
            @RequestBody DatosProducto datos
    ) {
        Medicamento producto = productos.findById(id).orElse(null);
        if (producto == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "El producto ya no existe."));
        }
        return guardar(producto, datos);
    }

    private ResponseEntity<?> guardar(Medicamento producto, DatosProducto datos) {
        String codigo = limpiar(datos.codigo());
        String nombre = limpiar(datos.nombre());
        String invima = limpiar(datos.registroInvima());
        String presentacion = limpiar(datos.presentacion());
        BigDecimal precio = datos.precioVenta();

        if (codigo == null || !codigo.matches("[a-zA-Z0-9]{8}")) {
            return error("El código debe tener exactamente 8 letras o números.");
        }
        if (nombre == null || nombre.length() > 100) {
            return error("El nombre es obligatorio y admite hasta 100 caracteres.");
        }
        if (datos.tipoProducto() == null) {
            return error("Selecciona el tipo de producto.");
        }
        if (datos.tipoProducto() == Medicamento.TipoProducto.MEDICAMENTO && invima == null) {
            return error("El registro INVIMA es obligatorio para medicamentos.");
        }
        if (invima != null && invima.length() > 100) {
            return error("El registro INVIMA admite hasta 100 caracteres.");
        }
        if (presentacion != null && presentacion.length() > 100) {
            return error("La presentación admite hasta 100 caracteres.");
        }
        if (precio == null || precio.signum() < 0
                || precio.compareTo(new BigDecimal("9999999999.99")) > 0
                || precio.stripTrailingZeros().scale() > 2) {
            return error("Escribe un precio válido con máximo dos decimales.");
        }
        if (datos.stockMinimo() == null || datos.stockMinimo() < 0) {
            return error("El stock mínimo debe ser un entero mayor o igual a cero.");
        }

        boolean nuevo = producto.getId() == null;
        producto.setCodigo(codigo.toUpperCase(Locale.ROOT));
        producto.setNombre(nombre);
        producto.setTipoProducto(datos.tipoProducto());
        producto.setRegistroInvima(invima);
        producto.setPresentacion(presentacion);
        producto.setDescripcion(limpiar(datos.descripcion()));
        producto.setPrecioVenta(precio);
        producto.setStockMinimo(datos.stockMinimo());
        producto.setFechaProximoPedido(datos.fechaProximoPedido());

        if (nuevo) {
            producto.setFechaCreacionRegistro(LocalDateTime.now());
        } else {
            producto.setFechaModificacionRegistro(LocalDateTime.now());
        }

        try {
            Medicamento guardado = productos.saveAndFlush(producto);
            return ResponseEntity.status(nuevo ? HttpStatus.CREATED : HttpStatus.OK)
                    .body(Map.of("id", guardado.getId()));
        } catch (DataIntegrityViolationException ex) {
            for (Throwable causa = ex; causa != null; causa = causa.getCause()) {
                if (causa instanceof SQLException sql && sql.getErrorCode() == 1062) {
                    return ResponseEntity.status(HttpStatus.CONFLICT)
                            .body(Map.of("message", "Ya existe un producto con ese nombre o código."));
                }
            }
            throw ex;
        }
    }

    private static ResponseEntity<Map<String, String>> error(String mensaje) {
        return ResponseEntity.badRequest().body(Map.of("message", mensaje));
    }

    private static String limpiar(String texto) {
        return texto == null || texto.isBlank() ? null : texto.trim();
    }
}
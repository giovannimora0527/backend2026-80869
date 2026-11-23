package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.repository.MedicamentoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/medicamento")
public class MedicamentoLoteConsultaApiController {

    private final JdbcClient jdbc;
    private final MedicamentoRepository medicamentoRepository;

    public MedicamentoLoteConsultaApiController(
            JdbcClient jdbc,
            MedicamentoRepository medicamentoRepository
    ) {
        this.jdbc = jdbc;
        this.medicamentoRepository = medicamentoRepository;
    }

    public record LoteResumen(
            Long id,
            String numeroLote,
            LocalDate fechaIngreso,
            LocalDate fechaCompra,
            LocalDate fechaVence,
            BigDecimal precioCompra,
            Long cantidadIngresada,
            Long cantidadDisponible
    ) {}

    @GetMapping(value = "/{id}/lotes", produces = "application/json")
    public ResponseEntity<List<LoteResumen>> listarLotes(
            @PathVariable("id") Long id
    ) {
        if (!medicamentoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        List<LoteResumen> lotes = jdbc.sql("""
                SELECT id,
                       numero_lote,
                       fecha_ingreso,
                       fecha_compra,
                       fecha_vence,
                       precio_compra,
                       cantidad_ingresada,
                       cantidad_disponible
                FROM medicamento_lote
                WHERE medicamento_id = :id
                ORDER BY fecha_vence IS NULL, fecha_vence, id
                """)
                .param("id", id)
                .query((rs, fila) -> new LoteResumen(
                        rs.getLong("id"),
                        rs.getString("numero_lote"),
                        rs.getObject("fecha_ingreso", LocalDate.class),
                        rs.getObject("fecha_compra", LocalDate.class),
                        rs.getObject("fecha_vence", LocalDate.class),
                        rs.getBigDecimal("precio_compra"),
                        rs.getLong("cantidad_ingresada"),
                        rs.getLong("cantidad_disponible")
                ))
                .list();

        return ResponseEntity.ok(lotes);
    }
}

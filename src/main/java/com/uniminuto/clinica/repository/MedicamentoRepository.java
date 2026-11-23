package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.Medicamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface MedicamentoRepository extends JpaRepository<Medicamento, Long> {

    interface InventarioResumen {
        Long getId();
        String getCodigo();
        String getNombre();
        String getPresentacion();
        String getTipoProducto();
        String getRegistroInvima();
        BigDecimal getPrecioVenta();
        Integer getStockMinimo();
        Long getCantidadDisponible();
        LocalDate getProximoVencimiento();
    }

    @Query(value = """
            SELECT
                m.id AS id,
                m.codigo AS codigo,
                m.nombre AS nombre,
                m.presentacion AS presentacion,
                m.tipo_producto AS tipoProducto,
                m.registro_invima AS registroInvima,
                m.precio_venta AS precioVenta,
                m.stock_minimo AS stockMinimo,
                COALESCE(SUM(l.cantidad_disponible), 0) AS cantidadDisponible,
                MIN(l.fecha_vence) AS proximoVencimiento
            FROM medicamento m
            LEFT JOIN medicamento_lote l
                ON l.medicamento_id = m.id
                AND l.cantidad_disponible > 0
                AND l.fecha_ingreso <= CURRENT_DATE
                AND (
                    l.fecha_vence >= CURRENT_DATE
                    OR (m.tipo_producto = 'UTENSILIO' AND l.fecha_vence IS NULL)
                )
            GROUP BY m.id, m.codigo, m.nombre, m.presentacion,
                     m.tipo_producto, m.registro_invima, m.precio_venta, m.stock_minimo
            ORDER BY m.nombre, m.id
            """, nativeQuery = true)
    List<InventarioResumen> listarInventario();

    @Query(value = """
            SELECT COUNT(*)
            FROM medicamento_lote
            WHERE medicamento_id = :medicamentoId
              AND cantidad_disponible > 0
              AND fecha_ingreso <= CURRENT_DATE
              AND fecha_vence >= CURRENT_DATE
            """, nativeQuery = true)
    long tieneLoteVigenteParaFormula(@Param("medicamentoId") Long medicamentoId);

    @Modifying
    @Query(value = """
            INSERT INTO medicamento_lote (
                medicamento_id,
                numero_lote,
                fecha_ingreso,
                fecha_compra,
                fecha_vence,
                precio_compra,
                cantidad_ingresada,
                cantidad_disponible
            )
            VALUES (
                :medicamentoId,
                :numeroLote,
                :fechaIngreso,
                :fechaCompra,
                :fechaVence,
                :precioCompra,
                :cantidad,
                :cantidad
            )
            """, nativeQuery = true)
    void insertarLote(
            @Param("medicamentoId") Long medicamentoId,
            @Param("numeroLote") String numeroLote,
            @Param("fechaIngreso") LocalDate fechaIngreso,
            @Param("fechaCompra") LocalDate fechaCompra,
            @Param("fechaVence") LocalDate fechaVence,
            @Param("precioCompra") BigDecimal precioCompra,
            @Param("cantidad") Integer cantidad
    );
}
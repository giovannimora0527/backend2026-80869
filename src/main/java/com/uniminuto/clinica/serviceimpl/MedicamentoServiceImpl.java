package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.Medicamento;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.InventarioPreciosRq;
import com.uniminuto.clinica.models.MedicamentoLoteRq;
import com.uniminuto.clinica.repository.MedicamentoRepository;
import com.uniminuto.clinica.repository.MedicamentoRepository.InventarioResumen;
import com.uniminuto.clinica.service.MedicamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class MedicamentoServiceImpl implements MedicamentoService {

    private static final BigDecimal PRECIO_MAXIMO = new BigDecimal("9999999999.99");

    @Autowired
    private MedicamentoRepository medicamentoRepository;

    @Override
    public List<Medicamento> listarMedicamentos() throws BadRequestException {
        return medicamentoRepository.findAll().stream()
                .sorted((m1, m2) -> m1.getNombre().compareToIgnoreCase(m2.getNombre()))
                .toList();
    }

    @Override
    public List<InventarioResumen> listarInventario() {
        return medicamentoRepository.listarInventario();
    }

    @Override
    @Transactional
    public void actualizarPrecioYStockMinimo(Long id, InventarioPreciosRq datos)
            throws BadRequestException {

        if (id == null || id <= 0) {
            throw new BadRequestException("Selecciona un producto válido.");
        }

        if (datos == null || datos.getPrecioVenta() == null) {
            throw new BadRequestException("El precio de venta es obligatorio.");
        }

        BigDecimal precio = datos.getPrecioVenta();

        if (precio.signum() < 0 || precio.compareTo(PRECIO_MAXIMO) > 0) {
            throw new BadRequestException(
                    "El precio de venta debe estar entre 0 y 9999999999.99."
            );
        }

        if (precio.stripTrailingZeros().scale() > 2) {
            throw new BadRequestException(
                    "El precio de venta admite máximo dos decimales."
            );
        }

        if (datos.getStockMinimo() == null || datos.getStockMinimo() < 0) {
            throw new BadRequestException(
                    "El stock mínimo es obligatorio y debe ser mayor o igual a cero."
            );
        }

        Medicamento producto = medicamentoRepository.findById(id)
                .orElseThrow(() -> new BadRequestException(
                        "El producto seleccionado no existe."
                ));

        producto.setPrecioVenta(precio);
        producto.setStockMinimo(datos.getStockMinimo());
        producto.setFechaModificacionRegistro(LocalDateTime.now());
    }

    @Override
    @Transactional
    public void registrarLote(Long id, MedicamentoLoteRq datos)
            throws BadRequestException {

        if (id == null || id <= 0) {
            throw new BadRequestException("Selecciona un producto existente.");
        }

        Medicamento producto = medicamentoRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("El producto no existe."));

        if (datos == null) {
            throw new BadRequestException("Los datos del lote son obligatorios.");
        }

        String numeroLote = datos.getNumeroLote();

        if (numeroLote == null || numeroLote.isBlank() || numeroLote.trim().length() > 60) {
            throw new BadRequestException(
                    "El número de lote debe tener entre 1 y 60 caracteres."
            );
        }

        LocalDate hoy = LocalDate.now();
        LocalDate fechaCompra = datos.getFechaCompra();
        LocalDate fechaVence = datos.getFechaVence();

        if (fechaCompra == null || fechaCompra.isAfter(hoy)) {
            throw new BadRequestException(
                    "Indica una fecha de compra igual o anterior a hoy."
            );
        }

        if (fechaVence == null && producto.getTipoProducto() != Medicamento.TipoProducto.UTENSILIO) {
            throw new BadRequestException("La fecha de vencimiento es obligatoria para medicamentos.");
        }

        if (fechaVence != null && fechaVence.isBefore(hoy)) {
            throw new BadRequestException(
                    "El lote debe estar vigente al ingresarlo al inventario."
            );
        }

        Integer cantidad = datos.getCantidadIngresada();

        if (cantidad == null || cantidad <= 0) {
            throw new BadRequestException(
                    "La cantidad ingresada debe ser mayor que cero."
            );
        }

        BigDecimal precioCompra = datos.getPrecioCompra();

        if (precioCompra == null || precioCompra.signum() < 0
                || precioCompra.compareTo(PRECIO_MAXIMO) > 0
                || precioCompra.stripTrailingZeros().scale() > 2) {
            throw new BadRequestException(
                    "El precio de compra debe estar entre 0 y 9999999999.99 "
                            + "y tener máximo dos decimales."
            );
        }

        medicamentoRepository.insertarLote(
                id,
                numeroLote.trim(),
                hoy,
                fechaCompra,
                fechaVence,
                precioCompra,
                cantidad
        );
    }
}
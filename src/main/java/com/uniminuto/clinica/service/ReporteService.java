package com.uniminuto.clinica.service;

import com.uniminuto.clinica.models.ReporteRs;

import java.time.LocalDate;

/**
 * Servicio del modulo de reportes administrativos y contables.
 *
 * Expone la construccion de cada reporte (vista previa en formato JSON) y la
 * exportacion del mismo reporte a PDF y Excel. Los reportes se agrupan en tres
 * categorias: financieros, de inventario y de gestion medica.
 *
 * Todos los reportes reciben un rango de fechas. Los reportes de inventario que
 * reflejan el estado actual (existencias, valor, agotados, por vencer,
 * vencidos) ignoran el rango porque corresponden a una foto del momento; el
 * rango solo es relevante para los reportes por periodo (movimientos, ingresos,
 * citas).
 */
public interface ReporteService {

    /**
     * Construye el reporte solicitado como estructura generica para la vista
     * previa.
     *
     * @param categoria "financiero", "inventario" o "gestion".
     * @param tipo      identificador del reporte dentro de la categoria.
     * @param desde     fecha inicial del rango (inclusive).
     * @param hasta     fecha final del rango (inclusive).
     */
    ReporteRs generar(String categoria, String tipo, LocalDate desde, LocalDate hasta);

    /** Exporta el reporte indicado a un documento PDF. */
    byte[] exportarPdf(String categoria, String tipo, LocalDate desde, LocalDate hasta);

    /** Exporta el reporte indicado a un libro de Excel (.xlsx). */
    byte[] exportarExcel(String categoria, String tipo, LocalDate desde, LocalDate hasta);
}

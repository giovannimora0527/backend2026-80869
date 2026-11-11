package com.uniminuto.clinica.models;

import lombok.Data;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Respuesta generica para todos los reportes del modulo de reportes.
 *
 * Se disenio una estructura uniforme (titulo, columnas, filas y totales) para
 * que tanto la vista previa del frontend como la exportacion a PDF y Excel
 * puedan procesar cualquier reporte sin conocer su contenido especifico. De
 * este modo, agregar un nuevo reporte solo implica construir este objeto en el
 * servicio, sin tocar la logica de presentacion ni la de exportacion.
 *
 * - titulo: nombre descriptivo del reporte (se usa como encabezado del PDF,
 *   nombre de la hoja de Excel y titulo de la vista previa).
 * - subtitulo: informacion del rango de fechas o filtros aplicados.
 * - columnas: encabezados de la tabla, en orden.
 * - filas: cada fila es una lista de celdas (texto ya formateado) con la misma
 *   cantidad de elementos que columnas.
 * - totales: pares etiqueta-valor que se muestran al pie del reporte (por
 *   ejemplo "Total ingresos" o "Valor del inventario").
 */
@Data
public class ReporteRs {

    private String titulo;
    private String subtitulo;
    private List<String> columnas = new ArrayList<>();
    private List<List<String>> filas = new ArrayList<>();
    private Map<String, String> totales = new LinkedHashMap<>();

    public void agregarFila(String... celdas) {
        this.filas.add(List.of(celdas));
    }

    public void agregarTotal(String etiqueta, String valor) {
        this.totales.put(etiqueta, valor);
    }
}

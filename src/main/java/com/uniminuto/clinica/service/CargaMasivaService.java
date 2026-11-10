package com.uniminuto.clinica.service;

import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.CargaMasivaRs;
import org.springframework.web.multipart.MultipartFile;

/**
 * Servicio de carga masiva de medicamentos mediante archivos Excel (.xlsx) o
 * CSV (.csv).
 */
public interface CargaMasivaService {

    /**
     * Valida el archivo y devuelve una vista previa con el resultado por fila,
     * sin persistir ningun registro.
     */
    CargaMasivaRs previsualizar(MultipartFile archivo) throws BadRequestException;

    /**
     * Valida el archivo y persiste unicamente las filas validas. Registra el
     * log de la carga. Un error en una fila no impide procesar las demas.
     */
    CargaMasivaRs confirmar(MultipartFile archivo) throws BadRequestException;
}

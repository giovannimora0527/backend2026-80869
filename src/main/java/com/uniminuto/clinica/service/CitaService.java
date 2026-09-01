package com.uniminuto.clinica.service;

import com.uniminuto.clinica.entity.Cita;
import org.apache.coyote.BadRequestException;

import java.util.List;

public interface CitaService {

    List<Cita> listarCitas() throws BadRequestException;
}

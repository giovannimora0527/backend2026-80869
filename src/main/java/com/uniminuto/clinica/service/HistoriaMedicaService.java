
package com.uniminuto.clinica.service;

import com.uniminuto.clinica.entity.HistoriaMedica;
import com.uniminuto.clinica.models.HistoriaMedicaRq;
import com.uniminuto.clinica.models.MiRespuestaRS;

import java.util.List;

public interface HistoriaMedicaService {

    MiRespuestaRS crearHistoriaMedica(HistoriaMedicaRq rq);

    List<HistoriaMedica> listarHistoriasMedicas();
}

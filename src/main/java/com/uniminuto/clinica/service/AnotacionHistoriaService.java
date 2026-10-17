
package com.uniminuto.clinica.service;

import com.uniminuto.clinica.entity.AnotacionHistoria;
import com.uniminuto.clinica.models.AnotacionHistoriaRq;
import com.uniminuto.clinica.models.MiRespuestaRS;

import java.util.List;

public interface AnotacionHistoriaService {

    MiRespuestaRS crearAnotacionHistoria(
            AnotacionHistoriaRq anotacionHistoriaRq
    );

    List<AnotacionHistoria> listarPorHistoria(Long historiaId);
}

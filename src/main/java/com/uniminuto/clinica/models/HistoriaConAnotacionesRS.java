package com.uniminuto.clinica.models;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.uniminuto.clinica.entity.AnotacionHistoria;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@JsonPropertyOrder({
        "historiaId",
        "mascotaId",
        "fechaCreacionHistoria",
        "anotaciones"
})
public class HistoriaConAnotacionesRS {
    private Integer historiaId;
    private Integer mascotaId;
    private LocalDateTime fechaCreacionHistoria;
    private List<AnotacionHistoria> anotaciones;
}

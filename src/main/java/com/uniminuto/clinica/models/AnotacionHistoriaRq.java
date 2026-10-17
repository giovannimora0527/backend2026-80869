
package com.uniminuto.clinica.models;

import lombok.Data;

@Data
public class AnotacionHistoriaRq {
    private Integer mascotaId;
    private Long historiaId;
    private Long medicoId;
    private String descripcion;
}

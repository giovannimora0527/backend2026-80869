package com.uniminuto.clinica.mapper;

import com.uniminuto.clinica.entity.AnotacionHistoria;
import com.uniminuto.clinica.models.AnotacionRs;
import com.uniminuto.clinica.util.NombreUtil;

/** Convierte la entidad {@link AnotacionHistoria} en su representación de respuesta. */
public final class AnotacionHistoriaMapper {

    /** Clase utilitaria: no se instancia. */
    private AnotacionHistoriaMapper() {
    }

    /**
     * Convierte una anotación en su DTO.
     *
     * @param a Anotación a convertir.
     * @return DTO de la anotación.
     */
    public static AnotacionRs aRs(AnotacionHistoria a) {
        AnotacionRs rs = new AnotacionRs();
        rs.setId(a.getId());
        rs.setHistoriaId(a.getHistoria() != null ? a.getHistoria().getId() : null);
        rs.setFecha(a.getFecha());
        rs.setDescripcion(a.getDescripcion());
        rs.setObservacion(a.getObservacion());
        rs.setDiagnosticoEvolucion(a.getDiagnosticoEvolucion());
        if (a.getMedico() != null) {
            rs.setMedicoId(a.getMedico().getId());
            rs.setMedicoNombre(NombreUtil.completo(a.getMedico()));
        }
        return rs;
    }
}

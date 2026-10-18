package com.uniminuto.clinica.mapper;

import com.uniminuto.clinica.entity.HistoriaMedica;
import com.uniminuto.clinica.models.HistoriaDetalleRs;
import com.uniminuto.clinica.models.HistoriaResumenRs;
import com.uniminuto.clinica.util.NombreUtil;

/** Convierte la entidad {@link HistoriaMedica} en sus representaciones de listado y detalle. */
public final class HistoriaMedicaMapper {

    /** Clase utilitaria: no se instancia. */
    private HistoriaMedicaMapper() {
    }

    /**
     * Copia los campos del resumen en el DTO indicado (compartido por resumen y detalle).
     *
     * @param h  Historia origen.
     * @param rs DTO destino.
     */
    private static void llenarResumen(HistoriaMedica h, HistoriaResumenRs rs) {
        rs.setId(h.getId());
        rs.setFechaHora(h.getFechaHora());
        rs.setMotivoConsulta(h.getMotivoConsulta());
        rs.setEstado(h.getEstado());
        rs.setCitaId(h.getCita() != null ? h.getCita().getId() : null);
        if (h.getMascota() != null) {
            rs.setMascotaId(h.getMascota().getMascotaId());
            rs.setMascotaNombre(h.getMascota().getNombreMascota());
            if (h.getMascota().getCliente() != null) {
                rs.setClienteId(h.getMascota().getCliente().getId());
                rs.setClienteNombre(NombreUtil.completo(h.getMascota().getCliente()));
            }
        }
        if (h.getMedico() != null) {
            rs.setMedicoId(h.getMedico().getId());
            rs.setMedicoNombre(NombreUtil.completo(h.getMedico()));
        }
    }

    /**
     * Convierte una historia en su resumen para el listado.
     *
     * @param h Historia a convertir.
     * @return Resumen de la historia.
     */
    public static HistoriaResumenRs aResumen(HistoriaMedica h) {
        HistoriaResumenRs rs = new HistoriaResumenRs();
        llenarResumen(h, rs);
        return rs;
    }

    /**
     * Convierte una historia en su detalle completo.
     *
     * @param h                Historia a convertir.
     * @param totalAnotaciones Cantidad de anotaciones registradas.
     * @return Detalle de la historia.
     */
    public static HistoriaDetalleRs aDetalle(HistoriaMedica h, long totalAnotaciones) {
        HistoriaDetalleRs rs = new HistoriaDetalleRs();
        llenarResumen(h, rs);
        rs.setDiagnostico(h.getDiagnostico());
        rs.setObservaciones(h.getObservaciones());
        rs.setFechaCreacion(h.getFechaCreacion());
        rs.setTotalAnotaciones(totalAnotaciones);
        return rs;
    }
}

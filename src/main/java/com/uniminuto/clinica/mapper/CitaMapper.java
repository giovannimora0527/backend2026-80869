package com.uniminuto.clinica.mapper;

import com.uniminuto.clinica.entity.Cita;
import com.uniminuto.clinica.models.CitaRs;
import com.uniminuto.clinica.util.NombreUtil;

/** Convierte la entidad {@link Cita} en su representación de respuesta. */
public final class CitaMapper {

    /** Clase utilitaria: no se instancia. */
    private CitaMapper() {
    }

    /**
     * Convierte una cita en su DTO.
     *
     * @param c          Cita a convertir.
     * @param historiaId Historia asociada (solo en el detalle; puede ser nulo).
     * @return DTO de la cita.
     */
    public static CitaRs aRs(Cita c, Long historiaId) {
        CitaRs rs = new CitaRs();
        rs.setId(c.getId());
        rs.setFechaHora(c.getFechaHora());
        rs.setFecha(c.getFechaHora().toLocalDate());
        rs.setHora(c.getFechaHora().toLocalTime());
        rs.setEstado(c.getEstado());
        rs.setMotivo(c.getMotivo());
        rs.setHistoriaId(historiaId);
        if (c.getCliente() != null) {
            rs.setClienteId(c.getCliente().getId());
            rs.setClienteNombre(NombreUtil.completo(c.getCliente()));
        }
        if (c.getMascota() != null) {
            rs.setMascotaId(c.getMascota().getMascotaId());
            rs.setMascotaNombre(c.getMascota().getNombreMascota());
        }
        if (c.getMedico() != null) {
            rs.setMedicoId(c.getMedico().getId());
            rs.setMedicoNombre(NombreUtil.completo(c.getMedico()));
        }
        return rs;
    }
}

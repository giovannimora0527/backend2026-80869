package com.uniminuto.clinica.mapper;

import com.uniminuto.clinica.entity.FormulaMedica;
import com.uniminuto.clinica.entity.Mascota;
import com.uniminuto.clinica.models.FormulaMedicaDetalleRs;
import com.uniminuto.clinica.models.FormulaMedicaResumenRs;
import com.uniminuto.clinica.util.NombreUtil;

/**
 * Convierte la entidad {@link FormulaMedica} en sus representaciones de listado (resumida)
 * y de detalle (completa). Es tolerante a registros antiguos con relaciones nulas.
 */
public final class FormulaMedicaMapper {

    /** Clase utilitaria: no se instancia. */
    private FormulaMedicaMapper() {
    }

    /**
     * Convierte una fórmula en su resumen para el listado.
     *
     * @param f Fórmula a convertir.
     * @return Resumen con solo los campos necesarios para la tabla.
     */
    public static FormulaMedicaResumenRs aResumen(FormulaMedica f) {
        FormulaMedicaResumenRs rs = new FormulaMedicaResumenRs();
        rs.setId(f.getId());
        rs.setFechaEmision(f.getFechaEmision());
        rs.setDosis(f.getDosis());
        rs.setFrecuencia(f.getFrecuencia());
        rs.setDuracionDias(f.getDuracionDias());
        rs.setActivo(f.getActivo());
        rs.setFechaCreacionRegistro(f.getFechaCreacionRegistro());
        rs.setMedicamentoNombre(f.getMedicamento() != null ? f.getMedicamento().getNombre() : f.getNombreMedicamento());
        Mascota m = f.getMascota();
        if (m != null) {
            rs.setMascotaId(m.getMascotaId());
            rs.setMascotaNombre(m.getNombreMascota());
            rs.setClienteNombre(NombreUtil.completo(m.getCliente()));
        }
        rs.setMedicoNombre(NombreUtil.completo(f.getMedico()));
        return rs;
    }

    /**
     * Convierte una fórmula en su detalle completo.
     *
     * @param f Fórmula a convertir.
     * @return Detalle con todos los campos.
     */
    public static FormulaMedicaDetalleRs aDetalle(FormulaMedica f) {
        FormulaMedicaDetalleRs rs = new FormulaMedicaDetalleRs();
        rs.setId(f.getId());
        rs.setFechaEmision(f.getFechaEmision());
        rs.setPresentacion(f.getPresentacion());
        rs.setDescripcion(f.getDescripcion());
        rs.setDosis(f.getDosis());
        rs.setFrecuencia(f.getFrecuencia());
        rs.setDuracionDias(f.getDuracionDias());
        rs.setIndicaciones(f.getIndicaciones());
        rs.setActivo(f.getActivo());
        rs.setFechaCreacionRegistro(f.getFechaCreacionRegistro());
        rs.setFechaActualizacionRegistro(f.getFechaActualizacionRegistro());
        if (f.getCita() != null) {
            rs.setCitaId(f.getCita().getId());
            rs.setCitaFechaHora(f.getCita().getFechaHora());
        }
        rs.setHistoriaId(f.getHistoria() != null ? f.getHistoria().getId() : null);
        if (f.getMedicamento() != null) {
            rs.setMedicamentoId(f.getMedicamento().getId());
            rs.setMedicamentoNombre(f.getMedicamento().getNombre());
        } else {
            rs.setMedicamentoNombre(f.getNombreMedicamento());
        }
        Mascota m = f.getMascota();
        if (m != null) {
            rs.setMascotaId(m.getMascotaId());
            rs.setMascotaNombre(m.getNombreMascota());
            if (m.getCliente() != null) {
                rs.setClienteId(m.getCliente().getId());
                rs.setClienteNombre(NombreUtil.completo(m.getCliente()));
            }
        }
        if (f.getMedico() != null) {
            rs.setMedicoId(f.getMedico().getId());
            rs.setMedicoNombre(NombreUtil.completo(f.getMedico()));
        }
        return rs;
    }
}

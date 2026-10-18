package com.uniminuto.clinica.specification;

import com.uniminuto.clinica.entity.Cliente;
import com.uniminuto.clinica.entity.HistoriaMedica;
import com.uniminuto.clinica.entity.Mascota;
import com.uniminuto.clinica.entity.Medico;
import com.uniminuto.clinica.models.HistoriaFiltroRq;
import com.uniminuto.clinica.util.ValidacionUtil;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.uniminuto.clinica.specification.SpecificationUtil.contiene;
import static com.uniminuto.clinica.specification.SpecificationUtil.nombreCompleto;

/** Filtros dinámicos del listado de historias clínicas. */
public final class HistoriaMedicaSpecs {

    /** Clase utilitaria: no se instancia. */
    private HistoriaMedicaSpecs() {
    }

    /**
     * Construye la especificación con los filtros informados (los nulos se ignoran).
     *
     * @param f Filtros del cliente.
     * @return Especificación JPA.
     */
    public static Specification<HistoriaMedica> conFiltros(HistoriaFiltroRq f) {
        return (root, query, cb) -> {
            List<Predicate> p = new ArrayList<>();
            if (f.getClienteId() != null) {
                p.add(cb.equal(root.get("mascota").get("cliente").get("id"), f.getClienteId()));
            }
            if (f.getMascotaId() != null) {
                p.add(cb.equal(root.get("mascota").get("mascotaId"), f.getMascotaId()));
            }
            if (f.getMedicoId() != null) {
                p.add(cb.equal(root.get("medico").get("id"), f.getMedicoId()));
            }
            if (ValidacionUtil.tieneTexto(f.getEstado())) {
                p.add(cb.equal(root.get("estado"), f.getEstado().trim().toUpperCase()));
            }
            if (f.getFechaDesde() != null) {
                p.add(cb.greaterThanOrEqualTo(root.<LocalDateTime>get("fechaHora"), f.getFechaDesde().atStartOfDay()));
            }
            if (f.getFechaHasta() != null) {
                p.add(cb.lessThan(root.<LocalDateTime>get("fechaHora"), f.getFechaHasta().plusDays(1).atStartOfDay()));
            }
            if (ValidacionUtil.tieneTexto(f.getTexto())) {
                Join<HistoriaMedica, Mascota> mascota = root.join("mascota");
                Join<Mascota, Cliente> cliente = mascota.join("cliente");
                Join<HistoriaMedica, Medico> medico = root.join("medico");
                String t = f.getTexto();
                p.add(cb.or(
                        contiene(cb, nombreCompleto(cb, cliente.<String>get("nombres"), cliente.<String>get("apellidos")), t),
                        contiene(cb, mascota.<String>get("nombreMascota"), t),
                        contiene(cb, nombreCompleto(cb, medico.<String>get("nombres"), medico.<String>get("apellidos")), t),
                        contiene(cb, root.<String>get("motivoConsulta"), t),
                        contiene(cb, root.<String>get("diagnostico"), t)));
            }
            return cb.and(p.toArray(new Predicate[0]));
        };
    }
}

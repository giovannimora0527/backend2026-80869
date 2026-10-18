package com.uniminuto.clinica.specification;

import com.uniminuto.clinica.entity.Cita;
import com.uniminuto.clinica.entity.Cliente;
import com.uniminuto.clinica.entity.Mascota;
import com.uniminuto.clinica.entity.Medico;
import com.uniminuto.clinica.models.CitaFiltroRq;
import com.uniminuto.clinica.util.EstadoCita;
import com.uniminuto.clinica.util.ValidacionUtil;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.uniminuto.clinica.specification.SpecificationUtil.contiene;
import static com.uniminuto.clinica.specification.SpecificationUtil.nombreCompleto;

/** Filtros dinámicos del listado de citas. */
public final class CitaSpecs {

    /** Clase utilitaria: no se instancia. */
    private CitaSpecs() {
    }

    /**
     * Construye la especificación con los filtros informados (los nulos se ignoran).
     *
     * @param f Filtros del cliente.
     * @return Especificación JPA.
     */
    public static Specification<Cita> conFiltros(CitaFiltroRq f) {
        return (root, query, cb) -> {
            List<Predicate> p = new ArrayList<>();
            if (f.getClienteId() != null) {
                p.add(cb.equal(root.get("cliente").get("id"), f.getClienteId()));
            }
            if (f.getMascotaId() != null) {
                p.add(cb.equal(root.get("mascota").get("mascotaId"), f.getMascotaId()));
            }
            if (f.getMedicoId() != null) {
                p.add(cb.equal(root.get("medico").get("id"), f.getMedicoId()));
            }
            String estado = EstadoCita.normalizar(f.getEstado());
            if (estado != null) {
                p.add(cb.equal(root.get("estado"), estado));
            }
            if (f.getFechaDesde() != null) {
                p.add(cb.greaterThanOrEqualTo(root.<LocalDateTime>get("fechaHora"), f.getFechaDesde().atStartOfDay()));
            }
            if (f.getFechaHasta() != null) {
                p.add(cb.lessThan(root.<LocalDateTime>get("fechaHora"), f.getFechaHasta().plusDays(1).atStartOfDay()));
            }
            if (ValidacionUtil.tieneTexto(f.getTexto())) {
                Join<Cita, Cliente> cliente = root.join("cliente");
                Join<Cita, Mascota> mascota = root.join("mascota");
                Join<Cita, Medico> medico = root.join("medico");
                String t = f.getTexto();
                p.add(cb.or(
                        contiene(cb, nombreCompleto(cb, cliente.<String>get("nombres"), cliente.<String>get("apellidos")), t),
                        contiene(cb, cliente.<String>get("numeroDocumento"), t),
                        contiene(cb, mascota.<String>get("nombreMascota"), t),
                        contiene(cb, nombreCompleto(cb, medico.<String>get("nombres"), medico.<String>get("apellidos")), t),
                        contiene(cb, root.<String>get("motivo"), t)));
            }
            return cb.and(p.toArray(new Predicate[0]));
        };
    }
}

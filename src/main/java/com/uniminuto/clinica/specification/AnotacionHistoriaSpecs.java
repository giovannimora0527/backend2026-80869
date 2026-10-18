package com.uniminuto.clinica.specification;

import com.uniminuto.clinica.entity.AnotacionHistoria;
import com.uniminuto.clinica.models.AnotacionFiltroRq;
import com.uniminuto.clinica.util.ValidacionUtil;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.uniminuto.clinica.specification.SpecificationUtil.contiene;

/** Filtros dinámicos del histórico de anotaciones de una historia. */
public final class AnotacionHistoriaSpecs {

    /** Clase utilitaria: no se instancia. */
    private AnotacionHistoriaSpecs() {
    }

    /**
     * Construye la especificación para las anotaciones de una historia concreta.
     *
     * @param historiaId Historia a la que pertenecen las anotaciones (obligatorio).
     * @param f          Filtros adicionales del cliente.
     * @return Especificación JPA.
     */
    public static Specification<AnotacionHistoria> deHistoria(Long historiaId, AnotacionFiltroRq f) {
        return (root, query, cb) -> {
            List<Predicate> p = new ArrayList<>();
            p.add(cb.equal(root.get("historia").get("id"), historiaId));
            if (f.getMedicoId() != null) {
                p.add(cb.equal(root.get("medico").get("id"), f.getMedicoId()));
            }
            if (f.getFechaDesde() != null) {
                p.add(cb.greaterThanOrEqualTo(root.<LocalDateTime>get("fecha"), f.getFechaDesde().atStartOfDay()));
            }
            if (f.getFechaHasta() != null) {
                p.add(cb.lessThan(root.<LocalDateTime>get("fecha"), f.getFechaHasta().plusDays(1).atStartOfDay()));
            }
            if (ValidacionUtil.tieneTexto(f.getTexto())) {
                String t = f.getTexto();
                p.add(cb.or(
                        contiene(cb, root.<String>get("descripcion"), t),
                        contiene(cb, root.<String>get("observacion"), t),
                        contiene(cb, root.<String>get("diagnosticoEvolucion"), t)));
            }
            return cb.and(p.toArray(new Predicate[0]));
        };
    }
}

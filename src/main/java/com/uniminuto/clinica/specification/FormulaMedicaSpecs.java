package com.uniminuto.clinica.specification;

import com.uniminuto.clinica.entity.Cliente;
import com.uniminuto.clinica.entity.FormulaMedica;
import com.uniminuto.clinica.entity.Mascota;
import com.uniminuto.clinica.entity.Medicamento;
import com.uniminuto.clinica.entity.Medico;
import com.uniminuto.clinica.models.FormulaFiltroRq;
import com.uniminuto.clinica.util.ValidacionUtil;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.uniminuto.clinica.specification.SpecificationUtil.contiene;
import static com.uniminuto.clinica.specification.SpecificationUtil.nombreCompleto;

/** Filtros dinámicos del listado de fórmulas médicas. */
public final class FormulaMedicaSpecs {

    /** Clase utilitaria: no se instancia. */
    private FormulaMedicaSpecs() {
    }

    /**
     * Construye la especificación con los filtros informados (los nulos se ignoran).
     *
     * @param f Filtros del cliente.
     * @return Especificación JPA.
     */
    public static Specification<FormulaMedica> conFiltros(FormulaFiltroRq f) {
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
            if (f.getMedicamentoId() != null) {
                p.add(cb.equal(root.get("medicamento").get("id"), f.getMedicamentoId()));
            }
            if (f.getCitaId() != null) {
                p.add(cb.equal(root.get("cita").get("id"), f.getCitaId()));
            }
            if (f.getActivo() != null) {
                p.add(cb.equal(root.get("activo"), f.getActivo()));
            }
            if (f.getFechaDesde() != null) {
                p.add(cb.greaterThanOrEqualTo(root.<LocalDate>get("fechaEmision"), f.getFechaDesde()));
            }
            if (f.getFechaHasta() != null) {
                p.add(cb.lessThanOrEqualTo(root.<LocalDate>get("fechaEmision"), f.getFechaHasta()));
            }
            if (ValidacionUtil.tieneTexto(f.getTexto())) {
                Join<FormulaMedica, Mascota> mascota = root.join("mascota");
                Join<Mascota, Cliente> cliente = mascota.join("cliente");
                Join<FormulaMedica, Medico> medico = root.join("medico");
                Join<FormulaMedica, Medicamento> medicamento = root.join("medicamento");
                String t = f.getTexto();
                p.add(cb.or(
                        contiene(cb, mascota.<String>get("nombreMascota"), t),
                        contiene(cb, nombreCompleto(cb, cliente.<String>get("nombres"), cliente.<String>get("apellidos")), t),
                        contiene(cb, nombreCompleto(cb, medico.<String>get("nombres"), medico.<String>get("apellidos")), t),
                        contiene(cb, medicamento.<String>get("nombre"), t)));
            }
            return cb.and(p.toArray(new Predicate[0]));
        };
    }
}

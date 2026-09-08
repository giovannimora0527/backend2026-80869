package com.uniminuto.clinica.entity;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.*;
import lombok.Data;

@JsonPropertyOrder({
        "usuarioId",
        "nombres",
        "apellidos",
        "tipoDocumento",
        "numeroDocumento",
        "telefono",
        "especializacionId",
        "registroProfesional",
})
@Entity
@Table(name = "medico")
@Data
public class Medico {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int usuarioId;

    @Column(name = "tipo_documento")
    private String tipoDocumento;

    @Column(name = "numero_documento")
    private String numeroDocumento;

    @Column(name = "nombres")
    private String nombres;

    @Column(name = "apellidos")
    private String apellidos;

    @Column(name = "telefono")
    private String telefono;

    @Column(name = "registro_profesional")
    private String registroProfesional;

    @ManyToOne
    @JoinColumn(name = "especializacion_id")
    private Especializacion especializacion;

}

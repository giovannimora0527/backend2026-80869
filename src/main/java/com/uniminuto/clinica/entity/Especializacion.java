package com.uniminuto.clinica.entity;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "especializacion")
@Data
public class Especializacion {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int especializacionId;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "codigo_especializacion")
    private String codigoEspecializacion;
}

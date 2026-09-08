package com.uniminuto.clinica.entity;

import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "cliente")
@Data
public class Cliente {
    @Id
    private Integer usuarioId;

    private String tipoDocumento;

    private String numeroDocumento;

    private String nombres;

    private String apellidos;

    private LocalDate fechaNacimiento;

    private String genero;

    private String telefono;

    private String direccion;

}

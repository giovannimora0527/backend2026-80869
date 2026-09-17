package com.uniminuto.clinica.entity;

// Importamos las anotaciones de JPA (Java Persistence API)
// JPA es lo que permite que Java "hable" con la base de datos
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Entidad que representa una formula medica en el sistema.
 *
 * ¿Qué significa esto?
 * - @Entity le dice a Spring: "Esta clase es una tabla en la BD"
 * - @Table(name="formula_medica") le dice el nombre EXACTO de la tabla
 */
@Entity
@Table(name = "formula_medica")  // ← Nombre de la tabla en la BD
@Data  // ← Lombok genera automáticamente getters/setters
@NoArgsConstructor  // ← Constructor vacío
@AllArgsConstructor  // ← Constructor con todos los campos
public class FormulaMedica {

    /**
     * Identificador único de la fórmula médica.
     *
     * ¿Por qué usamos @Id y @GeneratedValue?
     * - @Id: Este campo es la LLAVE PRIMARIA (primary key)
     * - @GeneratedValue(strategy = GenerationType.IDENTITY):
     *   El ID se genera AUTOMÁTICAMENTE (1, 2, 3...) sin que tú lo digas
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * Descripción de la fórmula médica.
     *
     * ¿Por qué columnDefinition="TEXT"?
     * - Para permitir textos largos (más de 255 caracteres)
     * - nullable=false significa que es OBLIGATORIO (no puede ser null)
     */
    @Column(name = "descripcion", nullable = false, columnDefinition = "TEXT")
    private String descripcion;

    /**
     * Fecha y hora de creación de la fórmula.
     */
    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    /**
     * Método que se ejecuta ANTES de guardar en la base de datos.
     *
     * ¿Para qué sirve @PrePersist?
     * - Es un "hook" o gancho de JPA
     * - Se ejecuta automáticamente antes del INSERT
     * - Aquí asignamos la fecha actual SI el cliente no la envió
     *
     * Ejemplo: Si el usuario no envía fecha_creacion,
     * automáticamente usamos LocalDateTime.now()
     */
    @PrePersist
    public void prePersist() {
        if (this.fechaCreacion == null) {
            this.fechaCreacion = LocalDateTime.now();
        }
    }
}
USE clinica;

CREATE TABLE IF NOT EXISTS cita_servicio (
    cita_id BIGINT UNSIGNED NOT NULL,
    tarifa_vigencia_id BIGINT UNSIGNED NOT NULL,
    PRIMARY KEY (cita_id, tarifa_vigencia_id),
    FOREIGN KEY (cita_id) REFERENCES cita(id),
    FOREIGN KEY (tarifa_vigencia_id) REFERENCES tarifa_vigencia(id)
) ENGINE=InnoDB;

ALTER TABLE factura_detalle
    DROP CHECK chk_factura_detalle_origen,
    ADD CONSTRAINT chk_factura_detalle_origen CHECK (
        (tipo IN ('CONSULTA', 'SERVICIO') AND cantidad = 1
            AND tarifa_vigencia_id IS NOT NULL AND despacho_detalle_id IS NULL)
        OR (tipo = 'MEDICAMENTO' AND despacho_detalle_id IS NOT NULL
            AND tarifa_vigencia_id IS NULL)
    );

INSERT INTO tarifa_servicio (especializacion_id, nombre_servicio)
SELECT e.id, catalogo.nombre
FROM especializacion e
CROSS JOIN (
    SELECT 'Consulta general' AS nombre
    UNION ALL SELECT 'Radiografía diagnóstica'
    UNION ALL SELECT 'Orquiectomía (castración)'
    UNION ALL SELECT 'Ovariohisterectomía (esterilización)'
    UNION ALL SELECT 'Profilaxis dental'
    UNION ALL SELECT 'Hemograma completo'
    UNION ALL SELECT 'Aplicación de vacuna'
    UNION ALL SELECT 'Eutanasia veterinaria'
) catalogo
WHERE NOT EXISTS (
    SELECT 1 FROM tarifa_servicio t
    WHERE t.especializacion_id = e.id AND t.nombre_servicio = catalogo.nombre
);

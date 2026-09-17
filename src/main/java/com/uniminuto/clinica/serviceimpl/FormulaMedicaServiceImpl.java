package com.uniminuto.clinica.serviceimpl;

// Importamos la entidad que vamos a manipular
import com.uniminuto.clinica.entity.FormulaMedica;
// Importamos el repository para acceder a la base de datos
import com.uniminuto.clinica.repository.FormulaMedicaRepository;
// Importamos la interfaz del servicio que vamos a implementar
import com.uniminuto.clinica.service.FormulaMedicaService;
// @RequiredArgsConstructor genera automáticamente un constructor con los campos final
import lombok.RequiredArgsConstructor;
// @Service le dice a Spring que esta clase es un componente de servicio
import org.springframework.stereotype.Service;
// @Transactional(readOnly=true) optimiza la consulta indicando que es solo lectura
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * Implementacion del servicio de formulas medicas.
 *
 * ¿Qué significa @Service?
 * - Es una anotación de Spring que marca esta clase como componente de servicio
 * - Spring la detecta automáticamente y la inyecta donde se necesite
 * - Permite usar @Transactional para manejar transacciones de base de datos
 */
@Service
@RequiredArgsConstructor  // ← Genera constructor con todos los campos final automáticamente
public class FormulaMedicaServiceImpl implements FormulaMedicaService {

    /**
     * Repositorio inyectado para la persistencia de FormulaMedica.
     *
     * ¿Por qué usamos 'final' y no @Autowired?
     * - 'final' garantiza que el repositorio nunca sea null después de la construcción
     * - @RequiredArgsConstructor genera el constructor automáticamente
     * - Es más seguro que @Autowired porque evita la inyección por campo (field injection)
     * - Facilita el testing (puedes pasar un mock en el constructor)
     */
    private final FormulaMedicaRepository formulaMedicaRepository;

    /**
     * {@inheritDoc}
     *
     * ¿Qué significa @Transactional(readOnly=true)?
     * - Le dice a Spring que este método es SOLO LECTURA
     * - Spring optimiza la transacción (no necesita bloqueo de escritura)
     * - Mejora el rendimiento de la consulta
     * - Si intentas hacer un INSERT/UPDATE dentro, lanzará error
     */
    @Override
    @Transactional(readOnly = true)
    public List<FormulaMedica> obtenerTodasOrdenadas() {
        // Delegamos la consulta al repository
        return formulaMedicaRepository.findAllByOrderByFechaCreacionDesc();
    }
}
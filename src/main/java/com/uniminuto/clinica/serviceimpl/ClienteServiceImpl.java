package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.Cliente;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.exception.ConflictException;
import com.uniminuto.clinica.exception.NotFoundException;
import com.uniminuto.clinica.models.ClienteRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.repository.CitaRepository;
import com.uniminuto.clinica.repository.ClienteRepository;
import com.uniminuto.clinica.service.ClienteService;
import com.uniminuto.clinica.utils.EstadoCita;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Implementación de las reglas de negocio de clientes.
 * <p>
 * Principio general: un cliente nunca se borra, solo se desactiva. Así las
 * mascotas, citas e historias que dependen de él no quedan "huérfanas".
 */
@Service
public class ClienteServiceImpl implements ClienteService {

    /** Tipos de documento aceptados. */
    private static final Set<String> TIPOS_DOCUMENTO = Set.of("CC", "CE", "TI", "PA");

    /** Valores de género aceptados. */
    private static final Set<String> GENEROS = Set.of("M", "F", "O");

    /** Documento: entre 5 y 20 letras o números, sin espacios ni símbolos. */
    private static final Pattern PATRON_DOCUMENTO = Pattern.compile("^[A-Za-z0-9]{5,20}$");

    /** Teléfono: entre 7 y 20 caracteres entre dígitos, +, - y espacios. */
    private static final Pattern PATRON_TELEFONO = Pattern.compile("^[0-9+\\-\\s]{7,20}$");

    /** Edad máxima razonable para una fecha de nacimiento (evita errores de digitación). */
    private static final int EDAD_MAXIMA_ANIOS = 120;

    /** Largo máximo de la dirección. */
    private static final int LARGO_MAXIMO_DIRECCION = 500;

    private final ClienteRepository clienteRepository;
    private final CitaRepository citaRepository;

    /**
     * Crea el servicio con sus dependencias.
     *
     * @param clienteRepository repositorio de clientes.
     * @param citaRepository    repositorio de citas (para validar la desactivación).
     */
    public ClienteServiceImpl(ClienteRepository clienteRepository, CitaRepository citaRepository) {
        this.clienteRepository = clienteRepository;
        this.citaRepository = citaRepository;
    }

    @Override
    public List<Cliente> listarClientes() throws BadRequestException {
        return clienteRepository.findAll().stream()
                .sorted((c1, c2) -> c1.getNombres().compareToIgnoreCase(c2.getNombres()))
                .toList();
    }

    @Override
    public List<Cliente> listarClientesActivos() {
        return clienteRepository.findByActivoOrderByNombresAscApellidosAsc(Boolean.TRUE);
    }

    @Override
    public Cliente buscarClientesPorDocumento(String documento) throws BadRequestException {
        if (documento == null || documento.trim().isEmpty()) {
            throw new BadRequestException("El número de documento es obligatorio.");
        }
        return clienteRepository.findByNumeroDocumento(documento.trim())
                .orElseThrow(() -> new NotFoundException(
                        "No existe un cliente con el documento " + documento.trim() + "."));
    }

    @Override
    public Cliente buscarClientePorId(Long id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("El id del cliente debe ser un número mayor a cero.");
        }
        return clienteRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("No existe un cliente con el id " + id + "."));
    }

    @Override
    @Transactional
    public MiRespuestaRS crearCliente(ClienteRq rq) {
        validar(rq);
        String documento = rq.getNumeroDocumento().trim();
        // Regla: el documento identifica a la persona; dos clientes con el mismo documento
        // harían ambiguas las búsquedas y mezclarían mascotas de personas distintas.
        if (clienteRepository.existsByNumeroDocumento(documento)) {
            throw new ConflictException("Ya existe un cliente con el número de documento " + documento + ".");
        }
        Cliente cliente = new Cliente();
        mapear(cliente, rq);
        cliente.setActivo(Boolean.TRUE);
        clienteRepository.save(cliente);
        return respuesta(201, "Cliente creado correctamente.");
    }

    @Override
    @Transactional
    public MiRespuestaRS actualizarCliente(Long id, ClienteRq rq) {
        Cliente cliente = buscarClientePorId(id);
        validar(rq);
        String documento = rq.getNumeroDocumento().trim();
        // Al editar se permite conservar el propio documento, pero no tomar el de otro cliente.
        if (clienteRepository.existsByNumeroDocumentoAndIdNot(documento, id)) {
            throw new ConflictException("Otro cliente ya tiene el número de documento " + documento + ".");
        }
        mapear(cliente, rq);
        clienteRepository.save(cliente);
        return respuesta(200, "Cliente actualizado correctamente.");
    }

    @Override
    @Transactional
    public MiRespuestaRS cambiarEstadoCliente(Long id, Boolean activo) {
        if (activo == null) {
            throw new BadRequestException("Debes indicar si el cliente queda activo o inactivo.");
        }
        Cliente cliente = buscarClientePorId(id);
        if (activo.equals(cliente.getActivo())) {
            return respuesta(200, "El cliente ya estaba " + (activo ? "activo." : "inactivo."));
        }
        // Regla: no se desactiva a un cliente con citas vivas por delante (pendientes, programadas
        // o confirmadas). Si se permitiera, quedarían citas a nombre de alguien que ya no puede
        // gestionarse. Solo se miran citas futuras para no bloquear para siempre por citas viejas
        // que nunca se cerraron.
        if (!activo && citaRepository.existsByCliente_IdAndEstadoInAndFechaHoraAfter(
                id, EstadoCita.ACTIVAS, LocalDateTime.now())) {
            throw new ConflictException("No se puede desactivar al cliente porque tiene citas pendientes, "
                    + "programadas o confirmadas. Cancélalas o atiéndelas primero.");
        }
        cliente.setActivo(activo);
        clienteRepository.save(cliente);
        return respuesta(200, activo ? "Cliente activado correctamente." : "Cliente desactivado correctamente.");
    }

    /**
     * Valida todos los datos de entrada del cliente antes de guardar.
     * Las reglas viven aquí (backend); el formulario del frontend solo las refleja.
     */
    private void validar(ClienteRq rq) {
        if (rq == null) {
            throw new BadRequestException("La información del cliente es obligatoria.");
        }
        String tipo = limpiar(rq.getTipoDocumento());
        if (tipo == null || !TIPOS_DOCUMENTO.contains(tipo.toUpperCase())) {
            throw new BadRequestException("El tipo de documento debe ser CC, CE, TI o PA.");
        }
        String documento = limpiar(rq.getNumeroDocumento());
        if (documento == null) {
            throw new BadRequestException("El número de documento es obligatorio.");
        }
        if (!PATRON_DOCUMENTO.matcher(documento).matches()) {
            throw new BadRequestException(
                    "El número de documento debe tener entre 5 y 20 letras o números, sin espacios ni símbolos.");
        }
        validarTexto(rq.getNombres(), "Los nombres", 100);
        validarTexto(rq.getApellidos(), "Los apellidos", 100);

        LocalDate nacimiento = rq.getFechaNacimiento();
        if (nacimiento == null) {
            throw new BadRequestException("La fecha de nacimiento es obligatoria.");
        }
        if (nacimiento.isAfter(LocalDate.now())) {
            throw new BadRequestException("La fecha de nacimiento no puede ser futura.");
        }
        if (nacimiento.isBefore(LocalDate.now().minusYears(EDAD_MAXIMA_ANIOS))) {
            throw new BadRequestException("La fecha de nacimiento no es válida.");
        }
        String genero = limpiar(rq.getGenero());
        if (genero != null && !GENEROS.contains(genero.toUpperCase())) {
            throw new BadRequestException("El género debe ser M, F u O.");
        }
        String telefono = limpiar(rq.getTelefono());
        if (telefono != null && !PATRON_TELEFONO.matcher(telefono).matches()) {
            throw new BadRequestException(
                    "El teléfono debe tener entre 7 y 20 caracteres (números, +, - o espacios).");
        }
        String direccion = limpiar(rq.getDireccion());
        if (direccion != null && direccion.length() > LARGO_MAXIMO_DIRECCION) {
            throw new BadRequestException("La dirección no puede superar " + LARGO_MAXIMO_DIRECCION + " caracteres.");
        }
    }

    /** Valida que un texto obligatorio no esté vacío ni exceda el largo máximo. */
    private void validarTexto(String valor, String etiqueta, int largoMaximo) {
        String limpio = limpiar(valor);
        if (limpio == null) {
            throw new BadRequestException(etiqueta + " son obligatorios.");
        }
        if (limpio.length() > largoMaximo) {
            throw new BadRequestException(etiqueta + " no pueden superar " + largoMaximo + " caracteres.");
        }
    }

    /** Copia los datos validados del Rq a la entidad, limpiando espacios sobrantes. */
    private void mapear(Cliente cliente, ClienteRq rq) {
        cliente.setTipoDocumento(limpiar(rq.getTipoDocumento()).toUpperCase());
        cliente.setNumeroDocumento(limpiar(rq.getNumeroDocumento()));
        cliente.setNombres(limpiar(rq.getNombres()));
        cliente.setApellidos(limpiar(rq.getApellidos()));
        cliente.setFechaNacimiento(rq.getFechaNacimiento());
        String genero = limpiar(rq.getGenero());
        cliente.setGenero(genero == null ? null : genero.toUpperCase());
        cliente.setTelefono(limpiar(rq.getTelefono()));
        cliente.setDireccion(limpiar(rq.getDireccion()));
    }

    /** Quita espacios al inicio y al final; devuelve {@code null} si queda vacío. */
    private String limpiar(String valor) {
        if (valor == null) {
            return null;
        }
        String recortado = valor.trim();
        return recortado.isEmpty() ? null : recortado;
    }

    private MiRespuestaRS respuesta(int status, String mensaje) {
        MiRespuestaRS rta = new MiRespuestaRS();
        rta.setStatus(status);
        rta.setMessage(mensaje);
        return rta;
    }
}

package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.Raza;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.models.RazaRq;
import com.uniminuto.clinica.repository.RazaRepository;
import com.uniminuto.clinica.service.RazaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Implementación de la lógica de negocio para la gestión de Razas.
 *
 * @author Yulian Estefanía Montealegre
 */
@Service
public class RazaServiceImpl implements RazaService {

    @Autowired
    private RazaRepository razaRepository;

    /**
     * Crea una nueva raza validando que no exista previamente para la misma especie.
     *
     * @param razaRq DTO con los datos de la raza.
     * @return Respuesta estandarizada con el resultado.
     * @throws BadRequestException Si los datos son inválidos o la raza ya existe.
     */
    @Override
    public MiRespuestaRS crearRazaNueva(RazaRq razaRq) throws BadRequestException {
        this.validarRazaRq(razaRq);

        Optional<Raza> optRaza = this.razaRepository.findByEspecieAndNombre(razaRq.getEspecie(), razaRq.getNombre());

        if (optRaza.isPresent()) {
            throw new BadRequestException("La raza ya existe para la especie especificada.");
        }

        Raza nuevaRaza = new Raza();
        nuevaRaza.setEspecie(razaRq.getEspecie());
        nuevaRaza.setNombre(razaRq.getNombre());

        this.razaRepository.save(nuevaRaza);

        MiRespuestaRS respuesta = new MiRespuestaRS();
        respuesta.setStatus(200);
        respuesta.setMessage("Raza creada correctamente.");
        return respuesta;
    }

    /**
     * Obtiene el listado completo de razas ordenadas alfabéticamente.
     *
     * @return Lista de razas.
     * @throws BadRequestException Si ocurre un error en la consulta.
     */
    @Override
    public List<Raza> listarRazas() throws BadRequestException {
        return razaRepository.findAll().stream()
                .sorted((r1, r2) -> r1.getNombre().compareToIgnoreCase(r2.getNombre()))
                .toList();
    }

    /**
     * Valida los campos obligatorios del objeto DTO de raza.
     *
     * @param razaRq DTO a validar.
     * @throws BadRequestException Si algún campo está vacío o es nulo.
     */
    private void validarRazaRq(RazaRq razaRq) throws BadRequestException {
        if (razaRq == null) {
            throw new BadRequestException("Los datos de la raza no pueden ser nulos.");
        }
        if (razaRq.getNombre() == null || razaRq.getNombre().trim().isEmpty()) {
            throw new BadRequestException("El nombre de la raza es obligatorio.");
        }
        if (razaRq.getEspecie() == null || razaRq.getEspecie().trim().isEmpty()) {
            throw new BadRequestException("La especie de la raza es obligatoria.");
        }
    }
}
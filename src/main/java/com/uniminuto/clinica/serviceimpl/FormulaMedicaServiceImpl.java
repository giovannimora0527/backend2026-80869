package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.FormulaMedica;
import com.uniminuto.clinica.repository.FormulaMedicaRepository;
import com.uniminuto.clinica.service.FormulaMedicaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor // Sustituye el @Autowired (inyección por constructor segura)
public class FormulaMedicaServiceImpl implements FormulaMedicaService {

    // Al usar final con @RequiredArgsConstructor, Spring inyecta la dependencia automáticamente
    private final FormulaMedicaRepository formulaMedicaRepository;

    @Override
    @Transactional(readOnly = true) // Le indica a la BD que es una consulta rápida de solo lectura
    public List<FormulaMedica> obtenerTodasOrdenadas() {
        return formulaMedicaRepository.findAllByOrderByFechaCreacionDesc();
    }
}
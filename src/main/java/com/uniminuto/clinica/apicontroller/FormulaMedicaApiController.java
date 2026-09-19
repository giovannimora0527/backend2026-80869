package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.api.FormulaMedicaApi;
import com.uniminuto.clinica.models.FormulaMedicaRS;
import com.uniminuto.clinica.service.FormulaMedicaService;
import com.uniminuto.clinica.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
public class FormulaMedicaApiController implements FormulaMedicaApi {


    @Autowired
    private FormulaMedicaService formulaMedicaService;


    @Override
    public ResponseEntity<List<FormulaMedicaRS>> listar() throws BadRequestException {
        return ResponseEntity.ok(formulaMedicaService.listar());
    }
}
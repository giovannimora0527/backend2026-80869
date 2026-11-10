package com.uniminuto.clinica.api;

import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.MenuRs;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.models.RolRq;
import com.uniminuto.clinica.models.RolRs;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * Endpoints de gestion de roles (perfiles) y sus menus. Restringido al rol
 * Administrador mediante el filtro de seguridad.
 */
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/rol")
public interface RolApi {

    @GetMapping(value = "/listar", produces = {"application/json"})
    ResponseEntity<List<RolRs>> listar() throws BadRequestException;

    @GetMapping(value = "/menus", produces = {"application/json"})
    ResponseEntity<List<MenuRs>> listarMenus() throws BadRequestException;

    @PostMapping(value = "/guardar", produces = {"application/json"}, consumes = {"application/json"})
    ResponseEntity<MiRespuestaRS> guardar(@RequestBody RolRq rolRq) throws BadRequestException;

    @PostMapping(value = "/actualizar", produces = {"application/json"}, consumes = {"application/json"})
    ResponseEntity<MiRespuestaRS> actualizar(@RequestBody RolRq rolRq) throws BadRequestException;
}

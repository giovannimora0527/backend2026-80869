package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.api.RolApi;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.MenuRs;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.models.RolRq;
import com.uniminuto.clinica.models.RolRs;
import com.uniminuto.clinica.service.RolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class RolApiController implements RolApi {

    @Autowired
    private RolService rolService;

    @Override
    public ResponseEntity<List<RolRs>> listar() throws BadRequestException {
        return ResponseEntity.ok(this.rolService.listarRoles());
    }

    @Override
    public ResponseEntity<List<MenuRs>> listarMenus() throws BadRequestException {
        return ResponseEntity.ok(this.rolService.listarMenus());
    }

    @Override
    public ResponseEntity<MiRespuestaRS> guardar(RolRq rolRq) throws BadRequestException {
        return ResponseEntity.ok(this.rolService.crearRol(rolRq));
    }

    @Override
    public ResponseEntity<MiRespuestaRS> actualizar(RolRq rolRq) throws BadRequestException {
        return ResponseEntity.ok(this.rolService.actualizarRol(rolRq));
    }
}

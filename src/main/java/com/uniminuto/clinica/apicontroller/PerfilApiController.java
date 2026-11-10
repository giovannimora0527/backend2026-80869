package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.api.PerfilApi;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.CambiarPasswordRq;
import com.uniminuto.clinica.models.EditarPerfilRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.models.UsuarioRs;
import com.uniminuto.clinica.service.PerfilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PerfilApiController implements PerfilApi {

    @Autowired
    private PerfilService perfilService;

    @Override
    public ResponseEntity<UsuarioRs> obtenerPerfil(Long usuarioId) throws BadRequestException {
        return ResponseEntity.ok(this.perfilService.obtenerPerfil(usuarioId));
    }

    @Override
    public ResponseEntity<MiRespuestaRS> editarPerfil(EditarPerfilRq editarPerfilRq)
            throws BadRequestException {
        return ResponseEntity.ok(this.perfilService.editarPerfil(editarPerfilRq));
    }

    @Override
    public ResponseEntity<MiRespuestaRS> cambiarPassword(CambiarPasswordRq cambiarPasswordRq)
            throws BadRequestException {
        return ResponseEntity.ok(this.perfilService.cambiarPassword(cambiarPasswordRq));
    }
}

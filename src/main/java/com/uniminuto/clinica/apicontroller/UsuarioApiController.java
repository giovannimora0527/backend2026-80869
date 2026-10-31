package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.api.UsuarioApi;
import com.uniminuto.clinica.models.EstadoRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.models.UsuarioRS;
import com.uniminuto.clinica.models.UsuarioRq;
import com.uniminuto.clinica.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UsuarioApiController implements UsuarioApi {

    @Autowired
    private UsuarioService usuarioService;

    @Override
    public ResponseEntity<List<UsuarioRS>> listarUsuarios() {
        return ResponseEntity.ok(this.usuarioService.listarUsuarios());
    }

    @Override
    public ResponseEntity<UsuarioRS> buscarUsuarioPorId(Long id) {
        return ResponseEntity.ok(this.usuarioService.buscarUsuarioPorId(id));
    }

    @Override
    public ResponseEntity<MiRespuestaRS> crearUsuario(UsuarioRq usuarioRq) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.usuarioService.crearUsuario(usuarioRq));
    }

    @Override
    public ResponseEntity<MiRespuestaRS> actualizarUsuario(Long id, UsuarioRq usuarioRq) {
        return ResponseEntity.ok(this.usuarioService.actualizarUsuario(id, usuarioRq));
    }

    @Override
    public ResponseEntity<MiRespuestaRS> cambiarEstadoUsuario(Long id, EstadoRq estadoRq) {
        return ResponseEntity.ok(this.usuarioService.cambiarEstadoUsuario(id, estadoRq));
    }
}

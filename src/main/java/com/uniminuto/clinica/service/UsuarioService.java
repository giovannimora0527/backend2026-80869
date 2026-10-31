package com.uniminuto.clinica.service;

import com.uniminuto.clinica.models.EstadoRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.models.UsuarioRS;
import com.uniminuto.clinica.models.UsuarioRq;

import java.util.List;

public interface UsuarioService {

    List<UsuarioRS> listarUsuarios();

    UsuarioRS buscarUsuarioPorId(Long id);

    MiRespuestaRS crearUsuario(UsuarioRq rq);

    MiRespuestaRS actualizarUsuario(Long id, UsuarioRq rq);

    MiRespuestaRS cambiarEstadoUsuario(Long id, EstadoRq estadoRq);
}

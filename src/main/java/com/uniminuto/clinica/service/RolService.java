package com.uniminuto.clinica.service;

import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.MenuRs;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.models.RolRq;
import com.uniminuto.clinica.models.RolRs;

import java.util.List;

/**
 * Servicio de gestion de roles (perfiles) y de los menus asignados a cada uno.
 *
 * Permite que el administrador cree y configure roles desde la interfaz, sin
 * necesidad de modificar la base de datos manualmente.
 */
public interface RolService {

    /** Lista todos los roles con los identificadores de sus menus asignados. */
    List<RolRs> listarRoles() throws BadRequestException;

    /** Lista todos los menus (modulos) disponibles del sistema. */
    List<MenuRs> listarMenus() throws BadRequestException;

    /** Crea un rol y le asigna los menus indicados. */
    MiRespuestaRS crearRol(RolRq rolRq) throws BadRequestException;

    /** Actualiza un rol y reasigna sus menus. */
    MiRespuestaRS actualizarRol(RolRq rolRq) throws BadRequestException;
}

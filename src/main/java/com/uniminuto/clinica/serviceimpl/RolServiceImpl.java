package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.Menu;
import com.uniminuto.clinica.entity.Perfil;
import com.uniminuto.clinica.entity.PerfilMenu;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.MenuRs;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.models.RolRq;
import com.uniminuto.clinica.models.RolRs;
import com.uniminuto.clinica.repository.MenuRepository;
import com.uniminuto.clinica.repository.PerfilMenuRepository;
import com.uniminuto.clinica.repository.PerfilRepository;
import com.uniminuto.clinica.service.RolService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Implementacion del servicio de gestion de roles (perfiles) y sus menus.
 *
 * La asignacion de menus a un rol se guarda en la tabla perfil_menu. Al
 * actualizar un rol se eliminan sus asignaciones anteriores y se crean las
 * nuevas dentro de una transaccion, garantizando consistencia.
 */
@Service
public class RolServiceImpl implements RolService {

    private final PerfilRepository perfilRepository;
    private final PerfilMenuRepository perfilMenuRepository;
    private final MenuRepository menuRepository;

    public RolServiceImpl(PerfilRepository perfilRepository,
                          PerfilMenuRepository perfilMenuRepository,
                          MenuRepository menuRepository) {
        this.perfilRepository = perfilRepository;
        this.perfilMenuRepository = perfilMenuRepository;
        this.menuRepository = menuRepository;
    }

    @Override
    public List<RolRs> listarRoles() {
        return perfilRepository.findAllByOrderByNombreAsc().stream().map(perfil -> {
            RolRs rs = new RolRs();
            rs.setPerfilId(perfil.getPerfilId());
            rs.setNombre(perfil.getNombre());
            rs.setActivo(perfil.getActivo());
            List<Integer> menuIds = perfilMenuRepository.findByPerfil(perfil).stream()
                    .map(pm -> pm.getMenu().getMenuId())
                    .toList();
            rs.setMenuIds(menuIds);
            return rs;
        }).toList();
    }

    @Override
    public List<MenuRs> listarMenus() {
        return menuRepository.findAllByOrderByTitleAsc().stream().map(menu -> {
            MenuRs rs = new MenuRs();
            rs.setMenuId(menu.getMenuId());
            rs.setCode(menu.getCode());
            rs.setTitle(menu.getTitle());
            rs.setUrl(menu.getUrl());
            rs.setIcon(menu.getIcon());
            rs.setActivo(menu.getActivo());
            return rs;
        }).toList();
    }

    @Override
    @Transactional
    public MiRespuestaRS crearRol(RolRq rq) {
        this.validar(rq);
        if (perfilRepository.existsByNombreIgnoreCase(rq.getNombre().trim())) {
            throw new BadRequestException("Ya existe un rol con ese nombre.");
        }
        Perfil perfil = new Perfil();
        perfil.setNombre(rq.getNombre().trim());
        perfil.setActivo(rq.getActivo() != null ? rq.getActivo() : Boolean.TRUE);
        perfil.setFechaCreacion(LocalDateTime.now());
        perfil = perfilRepository.save(perfil);

        this.asignarMenus(perfil, rq.getMenuIds());

        MiRespuestaRS rta = new MiRespuestaRS();
        rta.setStatus(200);
        rta.setMessage("Rol creado correctamente.");
        return rta;
    }

    @Override
    @Transactional
    public MiRespuestaRS actualizarRol(RolRq rq) {
        this.validar(rq);
        if (rq.getPerfilId() == null) {
            throw new BadRequestException("El identificador del rol es obligatorio.");
        }
        Optional<Perfil> opt = perfilRepository.findById(rq.getPerfilId());
        if (opt.isEmpty()) {
            throw new BadRequestException("El rol no existe.");
        }
        if (perfilRepository.existsByNombreIgnoreCaseAndPerfilIdNot(rq.getNombre().trim(), rq.getPerfilId())) {
            throw new BadRequestException("Ya existe otro rol con ese nombre.");
        }
        Perfil perfil = opt.get();
        perfil.setNombre(rq.getNombre().trim());
        if (rq.getActivo() != null) {
            perfil.setActivo(rq.getActivo());
        }
        perfil.setFechaModificacion(LocalDateTime.now());
        perfilRepository.save(perfil);

        // Reasigna los menus: elimina los anteriores y crea los nuevos.
        perfilMenuRepository.deleteByPerfil(perfil);
        this.asignarMenus(perfil, rq.getMenuIds());

        MiRespuestaRS rta = new MiRespuestaRS();
        rta.setStatus(200);
        rta.setMessage("Rol actualizado correctamente.");
        return rta;
    }

    private void asignarMenus(Perfil perfil, List<Integer> menuIds) {
        if (menuIds == null) {
            return;
        }
        for (Integer menuId : menuIds) {
            Optional<Menu> menu = menuRepository.findById(menuId);
            if (menu.isPresent()) {
                PerfilMenu pm = new PerfilMenu();
                pm.setPerfil(perfil);
                pm.setMenu(menu.get());
                perfilMenuRepository.save(pm);
            }
        }
    }

    private void validar(RolRq rq) {
        if (rq == null) {
            throw new BadRequestException("Los datos del rol son obligatorios.");
        }
        if (rq.getNombre() == null || rq.getNombre().isBlank()) {
            throw new BadRequestException("El nombre del rol es obligatorio.");
        }
    }
}

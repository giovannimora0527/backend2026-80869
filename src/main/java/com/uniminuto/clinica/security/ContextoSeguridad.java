package com.uniminuto.clinica.security;

/**
 * Almacena la informacion del usuario autenticado durante el ciclo de vida de
 * una peticion HTTP, usando una variable asociada al hilo de ejecucion
 * (ThreadLocal).
 *
 * El filtro {@link JwtAutenticacionFilter} coloca el usuario al inicio de la
 * peticion y lo limpia al finalizar. Cualquier servicio puede consultar el
 * usuario responsable de la operacion mediante {@link #obtenerUsuario()}.
 */
public final class ContextoSeguridad {

    private static final ThreadLocal<UsuarioAutenticado> USUARIO_ACTUAL = new ThreadLocal<>();

    private ContextoSeguridad() {
    }

    public static void establecerUsuario(UsuarioAutenticado usuario) {
        USUARIO_ACTUAL.set(usuario);
    }

    public static UsuarioAutenticado obtenerUsuario() {
        return USUARIO_ACTUAL.get();
    }

    public static void limpiar() {
        USUARIO_ACTUAL.remove();
    }
}

package com.example.smarthouse.ui.utils;

import com.example.smarthouse.data.models.Usuario;

public class UsuarioSesion {
    private static Usuario usuarioActual = null;

    public static void establecerUsuario(Usuario usuario) {
        usuarioActual = usuario;
    }

    public static Usuario obtenerUsuario() {
        return usuarioActual;
    }

    public static void cerrarSesion() {
        usuarioActual = null;
    }

    public static boolean haySesionActiva() {
        return usuarioActual != null;
    }
}

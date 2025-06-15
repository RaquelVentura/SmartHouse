package com.example.smarthouse.data.services;

import com.example.smarthouse.data.models.Usuario;
import com.google.android.gms.tasks.Task;

import java.util.List;

public interface UsuarioServicio {
    Task<Void> crearUsuarioPendiente(Usuario usuario, String token);
    Task<Void> loginConCorreo(String correo, String contrasenna);
    Task<Void> loginConGoogle(String idToken);
    Task<Void> validarCuentaGoogleConToken(String idToken, String token);
    Task<List<Usuario>> obtenerTodosLosUsuarios();
    Task<Usuario> obtenerUsuarioPorId(String uid);
    Task<Void> actualizarUsuario(Usuario usuario);
    Task<Void> eliminarUsuario(String uid);
}

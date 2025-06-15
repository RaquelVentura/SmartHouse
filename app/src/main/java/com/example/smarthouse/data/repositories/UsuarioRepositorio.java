package com.example.smarthouse.data.repositories;

import com.example.smarthouse.data.models.Usuario;
import com.google.android.gms.tasks.Task;

import java.util.List;

public interface UsuarioRepositorio {
    Task<Void> crearUsuario(Usuario usuario);
    Task<Void> crearUsuarioPendiente(Usuario usuario, String token);
    Task<Usuario> obtenerUsuarioActual();
    Task<Usuario> obtenerUsuarioPorId(String uid);
    Task<List<Usuario>> obtenerTodosLosUsuarios();
    Task<Void> actualizarUsuario(Usuario usuario);
    Task<Void> eliminarUsuario(String uid);
}

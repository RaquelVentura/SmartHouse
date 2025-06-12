package com.example.smarthouse.data.repositories;

import com.example.smarthouse.data.models.Usuario;
import com.google.android.gms.tasks.Task;

public interface UsuarioRepositorio {
    Task<Void> crearUsuario(Usuario usuario);
    Task<Usuario> obtenerUsuarioActual();
    Task<Usuario> obtenerUsuarioPorId(String uid);
}

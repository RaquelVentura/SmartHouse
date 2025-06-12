package com.example.smarthouse.data.services;

import com.google.android.gms.tasks.Task;

public interface UsuarioServicio {
    public Task<Void> loginConCorreo(String correo, String contrasenna);
    Task<Void> loginConGoogle(String idToken);
    Task<Void> validarCuentaGoogleConToken(String idToken, String token);
}

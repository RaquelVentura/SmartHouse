package com.example.smarthouse.data.services;

import android.os.Build;

import com.example.smarthouse.data.models.Usuario;
import com.example.smarthouse.data.repositories.UsuarioRepoImplement;
import com.example.smarthouse.data.repositories.UsuarioRepositorio;
import com.example.smarthouse.ui.utils.UsuarioSesion;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.LocalDateTime;

public class UsuarioSerImplement implements UsuarioServicio{
    @Override
    public Task<Void> loginConCorreo(String correo, String contrasenna) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        UsuarioRepositorio repo = new UsuarioRepoImplement();

        return auth.signInWithEmailAndPassword(correo, contrasenna)
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) return Tasks.forException(task.getException());

                    FirebaseUser user = auth.getCurrentUser();
                    if (user == null || !user.isEmailVerified())
                        return Tasks.forException(new Exception("Debe verificar su correo."));

                    return repo.obtenerUsuarioActual()
                            .continueWith(usuarioTask -> {
                                Usuario usuario = usuarioTask.getResult();
                                if (usuario == null)
                                    throw new Exception("No se encontró información del usuario.");
                                UsuarioSesion.establecerUsuario(usuario);
                                return null;
                            });
                });
    }

    @Override
    public Task<Void> loginConGoogle(String idToken) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        UsuarioRepositorio repo = new UsuarioRepoImplement();

        return auth.signInWithCredential(credential)
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        return Tasks.forException(task.getException());
                    }

                    FirebaseUser firebaseUser = auth.getCurrentUser();
                    if (firebaseUser == null) {
                        return Tasks.forException(new Exception("No se obtuvo el usuario de Firebase"));
                    }

                    // Intentar obtener el usuario desde la base de datos
                    return repo.obtenerUsuarioPorId(firebaseUser.getUid())
                            .continueWith(usuarioTask -> {
                                Usuario existente = usuarioTask.getResult();
                                if (existente == null) {
                                    throw new Exception("Este usuario no está registrado en la base de datos. Contacte al administrador.");
                                }

                                UsuarioSesion.establecerUsuario(existente);
                                return null;
                            });
                });
    }

    @Override
    public Task<Void> validarCuentaGoogleConToken(String idToken, String token) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        UsuarioRepositorio repo = new UsuarioRepoImplement();

        return auth.signInWithCredential(credential).continueWithTask(task -> {
            if (!task.isSuccessful()) return Tasks.forException(task.getException());

            FirebaseUser user = auth.getCurrentUser();
            if (user == null) return Tasks.forException(new Exception("No se obtuvo el usuario"));

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usuarios_pendientes").child(token);
            return ref.get().continueWithTask(snapshotTask -> {
                if (!snapshotTask.getResult().exists())
                    return Tasks.forException(new Exception("Token no válido o expirado"));

                String correoEsperado = snapshotTask.getResult().child("correo").getValue(String.class);
                if (!correoEsperado.equals(user.getEmail()))
                    return Tasks.forException(new Exception("Correo no coincide con el token"));

                // Crear usuario definitivo
                Usuario nuevo = snapshotTask.getResult().getValue(Usuario.class);
                nuevo.setId(user.getUid());

                return repo.crearUsuario(nuevo).continueWithTask(t -> {
                    if (!t.isSuccessful()) return Tasks.forException(t.getException());
                    UsuarioSesion.establecerUsuario(nuevo);
                    return ref.removeValue(); // Elimina el registro pendiente
                });
            });
        });
    }
}

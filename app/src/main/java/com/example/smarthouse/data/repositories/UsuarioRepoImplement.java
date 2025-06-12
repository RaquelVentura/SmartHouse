package com.example.smarthouse.data.repositories;

import com.example.smarthouse.data.models.Usuario;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuth;

public class UsuarioRepoImplement implements UsuarioRepositorio {
    private final DatabaseReference db = FirebaseDatabase.getInstance().getReference("usuarios");
    private final String uid = FirebaseAuth.getInstance().getUid();

    @Override
    public Task<Void> crearUsuario(Usuario usuario) {
        return db.child(uid).setValue(usuario);
    }

    @Override
    public Task<Usuario> obtenerUsuarioActual() {
        return db.child(uid).get().continueWith(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                return task.getResult().getValue(Usuario.class);
            } else {
                return null;
            }
        });
    }

    @Override
    public Task<Usuario> obtenerUsuarioPorId(String uid) {
        return db.child(uid).get()
                .continueWith(task -> {
                    if (!task.isSuccessful()) throw task.getException();
                    return task.getResult().getValue(Usuario.class);
                });
    }
}

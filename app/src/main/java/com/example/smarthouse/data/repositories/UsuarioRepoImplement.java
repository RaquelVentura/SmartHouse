package com.example.smarthouse.data.repositories;

import com.example.smarthouse.data.models.Usuario;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class UsuarioRepoImplement implements UsuarioRepositorio {
    private final DatabaseReference db = FirebaseDatabase.getInstance().getReference("usuarios");
    private final DatabaseReference dbPendientes = FirebaseDatabase.getInstance().getReference("usuarios_pendientes");
    private final String uid = FirebaseAuth.getInstance().getUid();

    @Override
    public Task<Void> crearUsuario(Usuario usuario) {
        return db.child(uid).setValue(usuario);
    }

    @Override
    public Task<Void> crearUsuarioPendiente(Usuario usuario, String token) {
        return dbPendientes.child(token).setValue(usuario);
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

    @Override
    public Task<List<Usuario>> obtenerTodosLosUsuarios()
    {
        return db.get()
                .continueWith(task -> {
                    List<Usuario> lista = new ArrayList<>();
                    if (task.isSuccessful() && task.getResult().exists()) {
                        for (DataSnapshot snapshot : task.getResult().getChildren()) {
                            Usuario usuario = snapshot.getValue(Usuario.class);
                            if (usuario != null) {
                                lista.add(usuario);
                            }
                        }
                    }
                    return lista;
                });
    }

    @Override
    public Task<Void> actualizarUsuario(Usuario usuario) {
        return db.child(usuario.getId()).setValue(usuario);
    }

    @Override
    public Task<Void> eliminarUsuario(String uid) {
        return db.child(uid).removeValue();
    }
}

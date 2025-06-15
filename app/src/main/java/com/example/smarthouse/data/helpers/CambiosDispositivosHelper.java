package com.example.smarthouse.data.helpers;

import com.example.smarthouse.data.models.CambioDispositivo;
import com.example.smarthouse.data.models.UnidadDeSalida;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class CambiosDispositivosHelper {
    private static final DatabaseReference cambiosRef =
            FirebaseDatabase.getInstance().getReference("cambiosDispositivos");

    public static void registrarCambioInmediato(UnidadDeSalida dispositivo, boolean nuevoEstado) {
        String cambioId = cambiosRef.push().getKey();
        String fecha = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        String hora = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        long timestamp = System.currentTimeMillis();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String usuarioId = "anonimo";
        String usuarioNombre = "Anónimo";
        String usuarioEmail = "";

        if (currentUser != null) {
            usuarioId = currentUser.getUid();
            usuarioNombre = currentUser.getDisplayName() != null ?
                    currentUser.getDisplayName() : "Usuario";
            usuarioEmail = currentUser.getEmail() != null ?
                    currentUser.getEmail() : "";
        }

        CambioDispositivo cambio = new CambioDispositivo(
                cambioId,
                "inmediato",
                fecha,
                hora,
                nuevoEstado,
                dispositivo.getId(),
                dispositivo.getTipo(),
                dispositivo.getUbicacion(),
                usuarioId,
                usuarioNombre,
                timestamp,
                true
        );

        Map<String, Object> cambioValues = cambio.toMap();
        if (!usuarioEmail.isEmpty()) {
            cambioValues.put("usuarioEmail", usuarioEmail);
        }

        cambiosRef.child(cambioId).setValue(cambioValues);
    }

    public static Query obtenerCambiosRecientes() {
        return cambiosRef.orderByChild("timestamp").limitToLast(50);
    }

    public static Query obtenerCambiosProgramadosPendientes() {
        return cambiosRef.orderByChild("ejecutado").equalTo(false);
    }

    public static void marcarComoEjecutado(String cambioId) {
        cambiosRef.child(cambioId).child("ejecutado").setValue(true);
    }
}
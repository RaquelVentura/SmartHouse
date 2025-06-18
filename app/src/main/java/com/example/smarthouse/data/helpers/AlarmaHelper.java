package com.example.smarthouse.data.helpers;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.smarthouse.data.models.HistorialAcceso;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AlarmaHelper {
    private static final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("alarmas");
    public static void activarAlarma(Context context, String tipoEvento, @Nullable String ubicacion) {
        dbRef.child("configuracion/activada").setValue(true);
        crearRegistroHistorial(context, tipoEvento, ubicacion != null ? ubicacion : "No especificada", true);
        Toast.makeText(context, "ALARMA ACTIVADA: " + tipoEvento, Toast.LENGTH_LONG).show();
    }

    public static void crearRegistroHistorial(Context context, String tipoEvento, String ubicacion, boolean activado) {
        DatabaseReference historialRef = FirebaseDatabase.getInstance().getReference("alarmas/historial");
        String id = historialRef.push().getKey();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userEmail = user != null ? user.getEmail() : "Sistema";

        HistorialAcceso registro = new HistorialAcceso(
                id,
                new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date()),
                new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date()),
                tipoEvento,
                userEmail,
                activado ? "Activada" : "Desactivada",
                ubicacion
        );

        historialRef.child(id).setValue(registro.toMap())
                .addOnFailureListener(e -> {
                    Log.e("AlarmaHelper", "Error al guardar historial", e);
                    Toast.makeText(context, "Error registrando evento", Toast.LENGTH_SHORT).show();
                });
    }

    public static void desactivarAlarma(Context context) {
        dbRef.child("configuracion/activada").setValue(false);
        dbRef.child("configuracion/intentos_fallidos").setValue(0);
        crearRegistroHistorial(context, "DESACTIVACION_MANUAL", "Sistema", false);
        Toast.makeText(context, "Alarma desactivada", Toast.LENGTH_SHORT).show();
    }

    public static void registrarIntentoFallido(Context context) {
        DatabaseReference intentosRef = dbRef.child("configuracion/intentos_fallidos");
        intentosRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Integer intentos = task.getResult().getValue(Integer.class);
                int nuevosIntentos = (intentos != null ? intentos : 0) + 1;
                intentosRef.setValue(nuevosIntentos);

                if (nuevosIntentos >= 3) {
                    activarAlarma(context, "INTENTOS_FALLIDOS", "Keypad");
                }
            }
        });
    }

    public static void inicializarMonitoreoSensores(Context context) {
        DatabaseReference sensoresRef = FirebaseDatabase.getInstance().getReference("sensores");
        sensoresRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot sensor : snapshot.getChildren()) {
                    Boolean activado = sensor.child("estado").getValue(Boolean.class);
                    if (activado != null && activado) {
                        dbRef.child("configuracion/modo_seguro").get().addOnCompleteListener(task -> {
                            if (task.isSuccessful() && Boolean.TRUE.equals(task.getResult().getValue(Boolean.class))) {
                                String ubicacion = sensor.child("ubicacion").getValue(String.class);
                                activarAlarma(context, "SENSOR_ACTIVADO",
                                        ubicacion != null ? ubicacion : sensor.getKey());
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("AlarmaHelper", "Error monitoreando sensores", error.toException());
            }
        });
    }
    public static void setModoSeguro(Context context, boolean activar) {
        dbRef.child("configuracion/modo_seguro").setValue(activar)
                .addOnSuccessListener(aVoid -> {
                    String evento = activar ? "MODO_SEGURO_ACTIVADO" : "MODO_SEGURO_DESACTIVADO";
                    crearRegistroHistorial(context, evento, "Sistema", activar);
                    Toast.makeText(context,
                            activar ? "Modo seguro activado" : "Modo seguro desactivado",
                            Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context,
                            "Error al " + (activar ? "activar" : "desactivar") + " modo seguro: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }
}
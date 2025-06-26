package com.example.smarthouse.data.helpers;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.smarthouse.data.models.Alarma;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AlarmaHelper {
    private static final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("alarmas");

    public static void activarAlarma(Context context, String tipoEvento, @Nullable String ubicacion) {
        dbRef.child("configuracion/activada").setValue(true);
        registrarAlarma(context, tipoEvento, ubicacion != null ? ubicacion : "No especificada");
        Toast.makeText(context, "ALARMA ACTIVADA: " + tipoEvento, Toast.LENGTH_LONG).show();
    }

    public static void desactivarAlarma(Context context) {
        dbRef.child("configuracion/activada").setValue(false);
        dbRef.child("configuracion/intentos_fallidos").setValue(0);
        registrarAlarma(context, "DESACTIVACION_MANUAL", "Sistema");
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
        DatabaseReference sensoresRef = FirebaseDatabase.getInstance().getReference("unidadesSalida");
        DatabaseReference modoSeguroRef = FirebaseDatabase.getInstance().getReference("modoSeguro");

        sensoresRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                modoSeguroRef.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful() && Boolean.TRUE.equals(task.getResult().getValue(Boolean.class))) {
                        for (DataSnapshot sensor : snapshot.getChildren()) {
                            String tipo = sensor.child("tipo").getValue(String.class);
                            Boolean estado = sensor.child("estado").getValue(Boolean.class);

                            if ((tipo != null && (tipo.equalsIgnoreCase("SERVO") || tipo.equalsIgnoreCase("Lamina")))
                                    && Boolean.TRUE.equals(estado)) {

                                String ubicacion = sensor.child("ubicacion").getValue(String.class);

                                registrarAlarma(
                                        context,
                                        "APERTURA_MODO_SEGURO",
                                        ubicacion != null ? ubicacion : sensor.getKey()
                                );

                                Toast.makeText(context, "¡Intrusión detectada en modo seguro!", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("AlarmaHelper", "Error monitoreando sensores", error.toException());
            }
        });
    }

    public static void setModoSeguro(Context context, boolean activar) {
        FirebaseDatabase.getInstance().getReference("modoSeguro").setValue(activar)
                .addOnSuccessListener(aVoid -> {
                    String evento = activar ? "MODO_SEGURO_ACTIVADO" : "MODO_SEGURO_DESACTIVADO";
                    registrarAlarma(context, evento, "Sistema");
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

    private static void registrarAlarma(Context context, String tipoEvento, String ubicacion) {
        DatabaseReference alarmasRef = FirebaseDatabase.getInstance().getReference("alarmas");
        String id = alarmasRef.push().getKey();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userEmail = user != null ? user.getEmail() : "Sistema";

        Alarma alarma = new Alarma(
                id,
                new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date()),
                new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date()),
                tipoEvento,
                userEmail,
                "Activada",
                ubicacion
        );

        alarmasRef.child(id).setValue(alarma.toMap())
                .addOnSuccessListener(aVoid -> Log.d("AlarmaHelper", "Alarma registrada correctamente"))
                .addOnFailureListener(e -> {
                    Log.e("AlarmaHelper", "Error al registrar la alarma", e);
                    Toast.makeText(context, "Error al registrar alarma", Toast.LENGTH_SHORT).show();
                });
    }
    public static void crearAlarma(Context context, String tipoEvento, String ubicacion) {
        DatabaseReference alarmasRef = FirebaseDatabase.getInstance().getReference("alarmas").push();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userEmail = user != null ? user.getEmail() : "Sistema";

        Alarma alarma = new Alarma(
                alarmasRef.getKey(),
                new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date()),
                new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date()),
                tipoEvento,
                userEmail,
                "Activada",
                ubicacion
        );

        alarmasRef.setValue(alarma.toMap())
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Alarma registrada: " + tipoEvento, Toast.LENGTH_SHORT).show();
                    Log.d("AlarmaHelper", "Alarma creada en ubicación: " + ubicacion);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Error al crear alarma", Toast.LENGTH_SHORT).show();
                    Log.e("AlarmaHelper", "Error creando alarma", e);
                });
    }

}

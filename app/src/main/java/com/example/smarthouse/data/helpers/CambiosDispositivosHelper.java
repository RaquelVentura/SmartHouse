package com.example.smarthouse.data.helpers;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.smarthouse.data.models.CambioDispositivo;
import com.example.smarthouse.data.models.UnidadDeSalida;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CambiosDispositivosHelper {

        private static final DatabaseReference cambiosRef =
                FirebaseDatabase.getInstance().getReference("cambiosDispositivos");
        private static final DatabaseReference dispositivosRef =
                FirebaseDatabase.getInstance().getReference("unidadesSalida");
        private static final DatabaseReference dht11Ref = FirebaseDatabase.getInstance().getReference("DHT11");
        private static ValueEventListener cambiosListener;

        public static void iniciarEscuchaCambiosProgramados() {
            if (cambiosListener != null) {
                cambiosRef.removeEventListener(cambiosListener);
            }

            cambiosListener = cambiosRef.orderByChild("ejecutado").equalTo(false)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            long now = System.currentTimeMillis();
                            for (DataSnapshot cambioSnap : snapshot.getChildren()) {
                                CambioDispositivo cambio = cambioSnap.getValue(CambioDispositivo.class);
                                if (cambio != null && !cambio.isEjecutado() && cambio.getTimestamp() <= now) {
                                    ejecutarCambioProgramado(cambio);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e("Firebase", "Error en escucha cambios", error.toException());
                        }
                    });
        }
        public static void detenerEscuchaCambios() {
            if (cambiosListener != null) {
                cambiosRef.removeEventListener(cambiosListener);
                cambiosListener = null;
            }
        }

        public static void registrarCambio(UnidadDeSalida dispositivo, boolean nuevoEstado,
                                           String tipoCambio, long timestamp) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            CambioDispositivo cambio = new CambioDispositivo(
                    cambiosRef.push().getKey(),
                    tipoCambio,
                    new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date()),
                    new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date()),
                    nuevoEstado,
                    dispositivo.getId(),
                    dispositivo.getTipo(),
                    dispositivo.getUbicacion(),
                    user != null ? user.getUid() : "sistema",
                    user != null ? user.getDisplayName() : "Sistema Automático",
                    timestamp,
                    tipoCambio.equals("inmediato")
            );

            cambiosRef.child(cambio.getId()).setValue(cambio.toMap());

            if (tipoCambio.equals("inmediato")) {
                dispositivosRef.child(dispositivo.getId()).child("estado").setValue(nuevoEstado);
            }
        }


    public static void verificarYEjecutarCambiosProgramados() {
        cambiosRef.orderByChild("ejecutado").equalTo(false)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        long now = System.currentTimeMillis();
                        for (DataSnapshot cambioSnap : snapshot.getChildren()) {
                            CambioDispositivo cambio = cambioSnap.getValue(CambioDispositivo.class);
                            if (cambio != null && !cambio.isEjecutado() && cambio.getTimestamp() <= now) {
                                ejecutarCambioProgramado(cambio);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("CambiosHelper", "Error al verificar cambios", error.toException());
                    }
                });
    }

    private static void ejecutarCambioProgramado(CambioDispositivo cambio) {
        DatabaseReference dispositivoRef;

        if ("DHT11".equalsIgnoreCase(cambio.getTipoDispositivo())) {
            dispositivoRef = dht11Ref.child(cambio.getIdUnidadSalida());
        } else {
            dispositivoRef = dispositivosRef.child(cambio.getIdUnidadSalida());
        }

        dispositivoRef.child("estado").setValue(cambio.isEstado())
                .addOnSuccessListener(aVoid -> {
                    Log.d("CambioDispositivo", "Estado del dispositivo actualizado correctamente");

                    cambiosRef.child(cambio.getId()).child("ejecutado").setValue(true)
                            .addOnSuccessListener(aVoid1 -> {
                                Log.d("CambioDispositivo", "Cambio marcado como ejecutado");
                            })
                            .addOnFailureListener(e -> {
                                Log.e("CambioDispositivo", "Error al marcar cambio como ejecutado", e);
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e("CambioDispositivo", "Error al actualizar estado del dispositivo", e);
                });
    }
    public static Query obtenerCambiosRecientes() {
        return cambiosRef.orderByChild("timestamp").limitToLast(50);
    }
}
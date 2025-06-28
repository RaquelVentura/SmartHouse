package com.example.smarthouse.ui.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EjecutarCambioReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String idCambio = intent.getStringExtra("idCambio");

        if (idCambio == null) {
            Log.e("EjecutarCambio", "ID de cambio no recibido");
            return;
        }

        DatabaseReference refCambio = FirebaseDatabase.getInstance().getReference("cambiosDispositivos").child(idCambio);

        refCambio.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String idUnidad = snapshot.child("idUnidadSalida").getValue(String.class);
                String tipoDispositivo = snapshot.child("tipoDispositivo").getValue(String.class);

                if (idUnidad != null && tipoDispositivo != null) {
                    String ruta = tipoDispositivo.equalsIgnoreCase("DHT11") ? "DHT11" : "unidadesSalida";
                    DatabaseReference unidadRef = FirebaseDatabase.getInstance()
                            .getReference(ruta)
                            .child(idUnidad)
                            .child("estado");

                    unidadRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot estadoSnapshot) {
                            Boolean estadoActual = estadoSnapshot.getValue(Boolean.class);

                            if (estadoActual != null) {
                                boolean nuevoEstado = !estadoActual;

                                unidadRef.setValue(nuevoEstado);
                                refCambio.child("estado").setValue(nuevoEstado);
                                refCambio.child("ejecutado").setValue(true);

                                Log.d("EjecutarCambio", "Estado invertido y actualizado exitosamente.");
                            } else {
                                Log.w("EjecutarCambio", "No se pudo obtener el estado actual.");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e("EjecutarCambio", "Error al leer el estado actual: " + error.getMessage());
                        }
                    });
                } else {
                    Log.w("EjecutarCambio", "ID de unidad o tipo de dispositivo nulo.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("EjecutarCambio", "Error al leer Firebase: " + error.getMessage());
            }
        });
    }
}

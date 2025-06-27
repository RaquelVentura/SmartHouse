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

    public static void crearAlarma(Context context, String tipoEvento, String ubicacion) {
        DatabaseReference alarmasRef = FirebaseDatabase.getInstance().getReference("alarmas").push();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userEmail = user != null ? user.getEmail() : "Sistema";

        Alarma alarma = new Alarma(
                new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date()),
                new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date()),
                tipoEvento,
                "Activada",
                ubicacion
        );
        alarmasRef.setValue(alarma)
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

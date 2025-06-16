package com.example.smarthouse.ui.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarthouse.R;
import com.example.smarthouse.data.models.CambioDispositivo;
import com.example.smarthouse.data.models.UnidadDeSalida;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class adaptadorServo extends RecyclerView.Adapter<adaptadorServo.ServoViewHolder> {

    private List<UnidadDeSalida> puertas;
    private Context context;

    public adaptadorServo(Context context, List<UnidadDeSalida> todasLasUnidades) {
        this.context = context;
        this.puertas = filtrarPuertas(todasLasUnidades);
    }

    private List<UnidadDeSalida> filtrarPuertas(List<UnidadDeSalida> unidades) {
        List<UnidadDeSalida> resultado = new ArrayList<>();
        for (UnidadDeSalida unidad : unidades) {
            if ("SERVO".equalsIgnoreCase(unidad.getTipo())) {
                resultado.add(unidad);
            }
        }
        return resultado;
    }

    @NonNull
    @Override
    public ServoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_puerta, parent, false);
        return new ServoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServoViewHolder holder, int position) {
        UnidadDeSalida puerta = puertas.get(position);
        actualizarVista(holder, puerta);

        holder.btnCambiarEstado.setOnClickListener(v -> {
            boolean nuevoEstado = !puerta.getEstado();
            puerta.setEstado(nuevoEstado);
            actualizarVista(holder, puerta);

            DatabaseReference dbRef = FirebaseDatabase.getInstance()
                    .getReference("unidadesSalida")
                    .child(puerta.getId());

            dbRef.child("estado").setValue(nuevoEstado)
                    .addOnSuccessListener(aVoid -> {
                        registrarCambioDispositivo(puerta, nuevoEstado, "inmediato");
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(context, "Error al cambiar estado", Toast.LENGTH_SHORT).show();
                        puerta.setEstado(!nuevoEstado);
                        actualizarVista(holder, puerta);
                        Log.e("Firebase", "Error al actualizar estado", e);
                    });
        });
    }

    private void actualizarVista(ServoViewHolder holder, UnidadDeSalida puerta) {
        if (puerta.getEstado()) {
            holder.imgPuerta.setImageResource(R.drawable.puerta_abierta);
            holder.lbEstado.setText("Estado: Abierta");
            holder.btnCambiarEstado.setText("CERRAR PUERTA");
        } else {
            holder.imgPuerta.setImageResource(R.drawable.puerta_cerrada);
            holder.lbEstado.setText("Estado: Cerrada");
            holder.btnCambiarEstado.setText("ABRIR PUERTA");
        }
        holder.lbUbicacion.setText("Ubicación: " + puerta.getUbicacion());
    }

    private void registrarCambioDispositivo(UnidadDeSalida puerta, boolean nuevoEstado, String tipoCambio) {
        DatabaseReference cambiosRef = FirebaseDatabase.getInstance()
                .getReference("cambiosDispositivos")
                .push();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String usuarioId = currentUser != null ? currentUser.getUid() : "anonimo";
        String usuarioNombre = currentUser != null ?
                (currentUser.getDisplayName() != null ? currentUser.getDisplayName() : "Usuario") :
                "Anónimo";

        CambioDispositivo cambio = new CambioDispositivo(
                cambiosRef.getKey(),
                tipoCambio,
                obtenerFechaActual(),
                obtenerHoraActual(),
                nuevoEstado,
                puerta.getId(),
                puerta.getTipo(),
                puerta.getUbicacion(),
                usuarioId,
                usuarioNombre,
                System.currentTimeMillis(),
                true
        );

        cambiosRef.setValue(cambio)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firebase", "Cambio de puerta registrado: " + puerta.getUbicacion());
                })
                .addOnFailureListener(e -> {
                    Log.e("Firebase", "Error al registrar cambio de puerta", e);
                });
    }

    private String obtenerFechaActual() {
        return new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
    }

    private String obtenerHoraActual() {
        return new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
    }

    @Override
    public int getItemCount() {
        return puertas.size();
    }

    public static class ServoViewHolder extends RecyclerView.ViewHolder {
        ImageView imgPuerta;
        TextView lbUbicacion, lbEstado;
        Button btnCambiarEstado;

        public ServoViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPuerta = itemView.findViewById(R.id.imgPuerta);
            lbUbicacion = itemView.findViewById(R.id.lbUbicacionPuerta);
            lbEstado = itemView.findViewById(R.id.lbEstadoPuerta);
            btnCambiarEstado = itemView.findViewById(R.id.btnCambiarEstadoPuerta);
        }
    }
}
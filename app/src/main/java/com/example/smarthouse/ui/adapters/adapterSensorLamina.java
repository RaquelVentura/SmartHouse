package com.example.smarthouse.ui.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarthouse.R;
import com.example.smarthouse.data.models.CambioDispositivo;
import com.example.smarthouse.data.models.UnidadDeSalida;
import com.example.smarthouse.ui.views.dialogs.DispositivosDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class adapterSensorLamina extends RecyclerView.Adapter<adapterSensorLamina.LaminaViewHolder> {
    private List<UnidadDeSalida> dataLamina;
    private Context context;

    public adapterSensorLamina(Context context, List<UnidadDeSalida> todasLasUnidades) {
        this.context = context;
        this.dataLamina = new ArrayList<>();
        for (UnidadDeSalida unidad : todasLasUnidades) {
            if ("Lamina".equalsIgnoreCase(unidad.getTipo())) {
                this.dataLamina.add(unidad);
            }
        }
    }

    @NonNull
    @Override
    public LaminaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_lamina, parent, false);
        return new LaminaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LaminaViewHolder holder, int position) {
        UnidadDeSalida lamina = dataLamina.get(position);
        holder.lbNombre.setText(lamina.getUbicacion());
        holder.lbHabitacion.setText("Ubicación: "+lamina.getUbicacion());

        if (lamina.getEstado()) {
            holder.imgLamina.setImageResource(R.drawable.ventana_abierta);
            holder.lbEstado.setText("Estado: Abierto");
            holder.btnCambiarEstado.setText("CERRAR");
        } else {
            holder.imgLamina.setImageResource(R.drawable.ventana_cerrada);
            holder.lbEstado.setText("Estado: Cerrado");
            holder.btnCambiarEstado.setText("ABRIR");
        }

        holder.btnCambiarEstado.setOnClickListener(v -> {
            boolean nuevoEstado = !lamina.getEstado();
            lamina.setEstado(nuevoEstado);

            if (nuevoEstado) {
                holder.imgLamina.setImageResource(R.drawable.ventana_abierta);
                holder.lbEstado.setText("Estado: Abierto");
                holder.btnCambiarEstado.setText("CERRAR");
            } else {
                holder.imgLamina.setImageResource(R.drawable.ventana_cerrada);
                holder.lbEstado.setText("Estado: Cerrado");
                holder.btnCambiarEstado.setText("ABRIR");
            }

            DatabaseReference dbRef = FirebaseDatabase.getInstance()
                    .getReference("unidadesSalida")
                    .child(lamina.getId());

            dbRef.child("estado").setValue(nuevoEstado)
                    .addOnSuccessListener(aVoid -> {
                        registrarCambioDispositivo(lamina, nuevoEstado, "inmediato");
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(context, "Error al cambiar estado", Toast.LENGTH_SHORT).show();
                        Log.e("Firebase", "Error al actualizar estado", e);
                    });
        });

        holder.btnProgramarCambio.setOnClickListener(v -> {
            FragmentManager fragmentManager = ((AppCompatActivity) v.getContext()).getSupportFragmentManager();
            DispositivosDialogFragment dialog = DispositivosDialogFragment.newInstance(
                    lamina.getId(),
                    lamina.getTipo(),
                    lamina.getUbicacion(),
                    lamina.getEstado()
            );
            dialog.show(fragmentManager, "DispositivosDialog");
        });
    }

    private void registrarCambioDispositivo(UnidadDeSalida lamina, boolean nuevoEstado, String tipoCambio) {
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
                lamina.getId(),
                lamina.getTipo(),
                lamina.getUbicacion(),
                usuarioId,
                usuarioNombre,
                System.currentTimeMillis(),
                true
        );

        cambiosRef.setValue(cambio)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firebase", "Cambio registrado: " + lamina.getUbicacion());
                })
                .addOnFailureListener(e -> {
                    Log.e("Firebase", "Error al registrar cambio", e);
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
        return dataLamina.size();
    }

    public class LaminaViewHolder extends RecyclerView.ViewHolder {
        ImageView imgLamina;
        TextView lbNombre, lbHabitacion, lbEstado;
        Button btnCambiarEstado;
        ImageButton btnProgramarCambio;

        public LaminaViewHolder(@NonNull View itemView) {
            super(itemView);
            imgLamina = itemView.findViewById(R.id.imgLamina);
            lbNombre = itemView.findViewById(R.id.lbNombreLamina);
            lbHabitacion = itemView.findViewById(R.id.lbHabitacionLamina);
            lbEstado = itemView.findViewById(R.id.lbEstadoLamina);
            btnCambiarEstado = itemView.findViewById(R.id.btnCambiarEstadoLamina);
            btnProgramarCambio = itemView.findViewById(R.id.btnAbrirDialogProgramarCambioLamina);
        }
    }
}
package com.example.smarthouse.ui.adapters;

import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class adaptadorLuces extends RecyclerView.Adapter<adaptadorLuces.LucesViewHolder> {

    private List<UnidadDeSalida> dataLuces;
    private Context context;

    public adaptadorLuces(Context context, List<UnidadDeSalida> todasLasUnidades) {
        this.context = context;
        this.dataLuces = new ArrayList<>();

        for (UnidadDeSalida unidad : todasLasUnidades) {
            if ("LED".equalsIgnoreCase(unidad.getTipo())) {
                this.dataLuces.add(unidad);
            }
        }
    }

    @NonNull
    @Override
    public LucesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_luces, parent, false);
        return new LucesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LucesViewHolder holder, int position) {
        UnidadDeSalida luz = dataLuces.get(position);
        configurarVista(holder, luz);

        holder.btnCambiarEstadoDispositivo.setOnClickListener(v -> {
            boolean nuevoEstado = !luz.getEstado();
            luz.setEstado(nuevoEstado);

            configurarVista(holder, luz);
            actualizarEstadoDispositivo(luz, nuevoEstado);
            registrarCambioDispositivo(luz, nuevoEstado, "inmediato");
        });

        holder.btnAbrirDialogProgramarCambio.setOnClickListener(v -> {
            abrirDialogProgramacion(v, luz);
        });
    }

    private void configurarVista(LucesViewHolder holder, UnidadDeSalida luz) {
        if (luz.getEstado()) {
            holder.imgDispositivo.setImageResource(R.drawable.foco_encendido);
            holder.lbEstadoDispositivo.setText("Estado: Encendido");
            holder.btnCambiarEstadoDispositivo.setText("APAGAR");
        } else {
            holder.imgDispositivo.setImageResource(R.drawable.img_1);
            holder.lbEstadoDispositivo.setText("Estado: Apagado");
            holder.btnCambiarEstadoDispositivo.setText("ENCENDER");
        }
        holder.lbHabitacionDispositivo.setText("Ubicación: " + luz.getUbicacion());
    }

    private void actualizarEstadoDispositivo(UnidadDeSalida luz, boolean nuevoEstado) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance()
                .getReference("unidadesSalida")
                .child(luz.getId());
        dbRef.child("estado").setValue(nuevoEstado);
    }

    private void registrarCambioDispositivo(UnidadDeSalida luz, boolean nuevoEstado, String tipoCambio) {
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
                luz.getId(),
                luz.getTipo(),
                luz.getUbicacion(),
                usuarioId,
                usuarioNombre,
                System.currentTimeMillis(),
                tipoCambio.equals("inmediato")
        );

        cambiosRef.setValue(cambio.toMap())
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firebase", "Cambio registrado exitosamente");
                })
                .addOnFailureListener(e -> {
                    Log.e("Firebase", "Error al registrar cambio", e);
                });
    }

    private void abrirDialogProgramacion(View v, UnidadDeSalida luz) {
        verificarPermisoAlarmas();
        FragmentManager fragmentManager = ((AppCompatActivity) v.getContext()).getSupportFragmentManager();

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(v.getContext(), "Debes iniciar sesión para programar cambios", Toast.LENGTH_SHORT).show();
            return;
        }

        DispositivosDialogFragment dialog = DispositivosDialogFragment.newInstance(
                luz.getId(),
                luz.getTipo(),
                luz.getUbicacion(),
                luz.getEstado()
        );

        dialog.show(fragmentManager, "DispositivosDialog");
    }

    private String obtenerFechaActual() {
        return new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
    }

    private String obtenerHoraActual() {
        return new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
    }

    @Override
    public int getItemCount() {
        return dataLuces.size();
    }

    public static class LucesViewHolder extends RecyclerView.ViewHolder {
        ImageView imgDispositivo;
        TextView lbHabitacionDispositivo, lbEstadoDispositivo;
        Button btnCambiarEstadoDispositivo;
        ImageButton btnAbrirDialogProgramarCambio;

        public LucesViewHolder(@NonNull View itemView) {
            super(itemView);
            imgDispositivo = itemView.findViewById(R.id.imgDispositivo);
            lbHabitacionDispositivo = itemView.findViewById(R.id.lbHabitacionDispositivo);
            lbEstadoDispositivo = itemView.findViewById(R.id.lbEstadoDispositivo);
            btnCambiarEstadoDispositivo = itemView.findViewById(R.id.btnCambiarEstadoDispositivo);
            btnAbrirDialogProgramarCambio = itemView.findViewById(R.id.btnAbrirDialogProgramarCambio);
        }
    }
    private void verificarPermisoAlarmas() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null && !alarmManager.canScheduleExactAlarms()) {
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        }
    }
}
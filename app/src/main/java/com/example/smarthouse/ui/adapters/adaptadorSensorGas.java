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
import com.example.smarthouse.data.helpers.AlarmaHelper;
import com.example.smarthouse.data.models.CambioDispositivo;
import com.example.smarthouse.data.models.MQ2;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class adaptadorSensorGas extends RecyclerView.Adapter<adaptadorSensorGas.SensorGasViewHolder> {
    private List<MQ2> dataMQ2;
    private Context context;
    private Map<String, Boolean> estadosPrevios = new HashMap<>();

    public adaptadorSensorGas(List<MQ2> dataMQ2, Context context) {
        this.dataMQ2 = dataMQ2;
        this.context = context;
    }

    @NonNull
    @Override
    public SensorGasViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_sensor_gas, parent, false);
        return new SensorGasViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SensorGasViewHolder holder, int position) {
        MQ2 mq2 = dataMQ2.get(position);

        if (!estadosPrevios.containsKey(mq2.getId())) {
            estadosPrevios.put(mq2.getId(), mq2.getEstado());
        }

        actualizarVista(holder, mq2);

        holder.btnCambiarEstadoSensorGas.setOnClickListener(v -> {
            boolean nuevoEstado = !mq2.getEstado();
            boolean estadoAnterior = estadosPrevios.getOrDefault(mq2.getId(), false);

            mq2.setEstado(nuevoEstado);
            actualizarVista(holder, mq2);

            DatabaseReference dbRef = FirebaseDatabase.getInstance()
                    .getReference("mq2")
                    .child(mq2.getId());

            dbRef.child("estado").setValue(nuevoEstado)
                    .addOnSuccessListener(aVoid -> {
                        registrarCambioDispositivo(mq2, nuevoEstado, "inmediato");
                        if (!estadoAnterior && nuevoEstado) {
                            AlarmaHelper.crearAlarma(context, "GAS_DETECTADO", mq2.getUbicacion());
                        }
                        estadosPrevios.put(mq2.getId(), nuevoEstado);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(context, "Error al cambiar estado", Toast.LENGTH_SHORT).show();
                        mq2.setEstado(!nuevoEstado);
                        actualizarVista(holder, mq2);
                        Log.e("Firebase", "Error al actualizar estado", e);
                    });
        });
    }

    @Override
    public int getItemCount() {
        return dataMQ2 != null ? dataMQ2.size() : 0;
    }

    public void actualizarDatos(List<MQ2> nuevosDatos) {
        this.dataMQ2 = nuevosDatos;
        notifyDataSetChanged();
    }

    public static class SensorGasViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgMQ2;
        private TextView lbUbicacion, lbEstado;
        private Button btnCambiarEstadoSensorGas;

        public SensorGasViewHolder(@NonNull View itemView) {
            super(itemView);
            imgMQ2 = itemView.findViewById(R.id.imgSensorGas);
            lbUbicacion = itemView.findViewById(R.id.lbUbicacionMQ2);
            lbEstado = itemView.findViewById(R.id.lbEstadoMQ2);
            btnCambiarEstadoSensorGas = itemView.findViewById(R.id.btnCambiarEstadoSensorGas);
        }
    }

    private void actualizarVista(SensorGasViewHolder holder, MQ2 mq2) {
        if (mq2.getEstado()) {
            holder.imgMQ2.setImageResource(R.drawable.alerta);
            holder.lbEstado.setText("PELIGRO!");
            holder.lbEstado.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
            holder.btnCambiarEstadoSensorGas.setText("APAGAR ALARMA");
        } else {
            holder.imgMQ2.setImageResource(R.drawable.img_7);
            holder.lbEstado.setText("Normal");
            holder.lbEstado.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
            holder.btnCambiarEstadoSensorGas.setText("ENCENDER ALARMA");
        }
        holder.lbUbicacion.setText(mq2.getUbicacion());
    }

    private void registrarCambioDispositivo(MQ2 mq2, boolean nuevoEstado, String tipoCambio) {
        DatabaseReference cambiosRef = FirebaseDatabase.getInstance().getReference("cambiosDispositivos").push();
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
                mq2.getId(),
                "MQ2",
                mq2.getUbicacion(),
                usuarioId,
                usuarioNombre,
                System.currentTimeMillis(),
                true
        );

        cambiosRef.setValue(cambio)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firebase", "Cambio de sensor de gas registrado: " + mq2.getUbicacion());
                })
                .addOnFailureListener(e -> {
                    Log.e("Firebase", "Error al registrar cambio de sensor de gas", e);
                });
    }

    private String obtenerFechaActual() {
        return new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
    }

    private String obtenerHoraActual() {
        return new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
    }
}

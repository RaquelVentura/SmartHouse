// adaptadorHistorialAcciones.java
package com.example.smarthouse.ui.views.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarthouse.R;
import com.example.smarthouse.data.models.CambioDispositivo;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class adaptadorHistorialAcciones extends RecyclerView.Adapter<adaptadorHistorialAcciones.HistorialAccionesViewHolder> {

    private List<CambioDispositivo> historial;
    private List<String> keys;
    private Context context;
    private String usuarioLogueado;

    public adaptadorHistorialAcciones(Context context, List<CambioDispositivo> historial, List<String> keys, String usuarioLogueado) {
        this.context = context;
        this.historial = historial;
        this.keys = keys;
        this.usuarioLogueado = usuarioLogueado;
    }

    @NonNull
    @Override
    public HistorialAccionesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_historial_acciones, parent, false);
        return new HistorialAccionesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistorialAccionesViewHolder holder, int position) {
        CambioDispositivo accion = historial.get(position);

        // 1. Tipo de acción
        String tipoCambio = accion.getTipoCambio() != null ? accion.getTipoCambio() : "manual";
        holder.tvTipoAccion.setText(tipoCambio.equals("programado") ? "Programado" : "Manual");

        // 2. Estado e ícono
        String tipoDispositivo = accion.getTipoDispositivo() != null ? accion.getTipoDispositivo() : "";
        if (accion.isEstado()) {
            holder.ivEstadoIcon.setImageResource(R.drawable.ic_toogle_on);
            holder.tvEstado.setText(tipoDispositivo.equals("LED") ? "Encendido" : "Abierto");
            holder.tvEstado.setTextColor(ContextCompat.getColor(context, R.color.green));
        } else {
            holder.ivEstadoIcon.setImageResource(R.drawable.ic_toggle_off);
            holder.tvEstado.setText(tipoDispositivo.equals("LED") ? "Apagado" : "Cerrado");
            holder.tvEstado.setTextColor(ContextCompat.getColor(context, R.color.red));
        }

        // 3. Dispositivo
        String nombreDispositivo = accion.getNombreDispositivo() != null ? accion.getNombreDispositivo() : "";
        holder.tvDispositivo.setText(String.format("%s • %s", tipoDispositivo, nombreDispositivo));

        // 4. Fecha y hora
        holder.tvFecha.setText(accion.getFecha() != null ? accion.getFecha() : "");
        holder.tvHora.setText(accion.getHora() != null ? accion.getHora() : "");

        // 5. Usuario - mostramos el usuario logueado aquí
        holder.tvUsuario.setText(accion.getUsuarioNombre() != null ? accion.getUsuarioNombre() : "Desconocido");

        // 6. Ejecución
        if (accion.isEjecutado()) {
            holder.tvEjecucion.setText("Completado");
            holder.tvEjecucion.setBackgroundResource(R.drawable.bg_status_completed);
            holder.tvEjecucion.setTextColor(ContextCompat.getColor(context, R.color.green));
        } else {
            holder.tvEjecucion.setText("Pendiente");
            holder.tvEjecucion.setBackgroundResource(R.drawable.bg_status_pending);
            holder.tvEjecucion.setTextColor(ContextCompat.getColor(context, R.color.orange));
        }

        holder.btnEliminar.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Confirmar eliminación")
                    .setMessage("¿Deseas eliminar este registro?")
                    .setPositiveButton("Sí", (dialog, which) -> {
                        int pos = holder.getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION && pos < keys.size()) {
                            String keyToDelete = keys.get(pos);
                            FirebaseDatabase.getInstance()
                                    .getReference("cambiosDispositivos")
                                    .child(keyToDelete)
                                    .removeValue()
                                    .addOnSuccessListener(aVoid -> {
                                        historial.remove(pos);
                                        keys.remove(pos);
                                        notifyItemRemoved(pos);
                                        notifyItemRangeChanged(pos, historial.size());
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(context, "Error al eliminar", Toast.LENGTH_SHORT).show();
                                    });
                        }
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return historial != null ? historial.size() : 0;
    }

    public static class HistorialAccionesViewHolder extends RecyclerView.ViewHolder {
        TextView tvTipoAccion, tvEstado, tvDispositivo, tvFecha, tvHora, tvUsuario, tvEjecucion;
        ImageView ivEstadoIcon;
        ImageButton btnEliminar;

        public HistorialAccionesViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTipoAccion = itemView.findViewById(R.id.tvTipoAccion);
            tvEstado = itemView.findViewById(R.id.tvEstado);
            tvDispositivo = itemView.findViewById(R.id.tvDispositivo);
            tvFecha = itemView.findViewById(R.id.tvFecha);
            tvHora = itemView.findViewById(R.id.tvHora);
            tvUsuario = itemView.findViewById(R.id.tvUsuario);
            tvEjecucion = itemView.findViewById(R.id.tvEjecucion);
            ivEstadoIcon = itemView.findViewById(R.id.ivEstadoIcon);
            btnEliminar = itemView.findViewById(R.id.btnEliminarRegitroAcciones);
        }
    }

    public void actualizarDatos(List<CambioDispositivo> nuevosDatos, List<String> nuevasKeys) {
        this.historial = nuevosDatos;
        this.keys = nuevasKeys;
        notifyDataSetChanged();
    }
}

package com.example.smarthouse.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarthouse.R;
import com.example.smarthouse.data.models.CambioDispositivo;

import java.util.List;

public class adaptadorHistorialAcciones extends RecyclerView.Adapter<adaptadorHistorialAcciones.HistorialAccionesViewHolder> {

    private List<CambioDispositivo> historial;
    private Context context;

    public adaptadorHistorialAcciones(Context context, List<CambioDispositivo> historial) {
        this.context = context;
        this.historial = historial;
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

        // Validaciones de null para todos los campos críticos
        String tipoCambio = accion.getTipoCambio() != null ? accion.getTipoCambio() : "manual";
        String tipoDispositivo = accion.getTipoDispositivo() != null ? accion.getTipoDispositivo() : "";
        String nombreDispositivo = accion.getNombreDispositivo() != null ? accion.getNombreDispositivo() : "";
        String usuarioNombre = accion.getUsuarioNombre() != null ? accion.getUsuarioNombre() : "";

        // 1. Tipo de acción
        holder.tvTipoAccion.setText(tipoCambio.equals("programado") ? "Programado" : "Manual");

        // 2. Icono y texto de estado
        if (accion.isEstado()) {
            holder.ivEstadoIcon.setImageResource(R.drawable.ic_toogle_on);
            holder.tvEstado.setText(tipoDispositivo.equals("LED") ? "Encendido" : "Abierto");
            holder.tvEstado.setTextColor(ContextCompat.getColor(context, R.color.green));
        } else {
            holder.ivEstadoIcon.setImageResource(R.drawable.ic_toggle_off);
            holder.tvEstado.setText(tipoDispositivo.equals("LED") ? "Apagado" : "Cerrado");
            holder.tvEstado.setTextColor(ContextCompat.getColor(context, R.color.red));
        }

        // 3. Dispositivo afectado
        holder.tvDispositivo.setText(String.format("%s • %s", tipoDispositivo, nombreDispositivo));

        // 4. Fecha y hora (también deberías validar estos campos)
        holder.tvFecha.setText(accion.getFecha() != null ? accion.getFecha() : "");
        holder.tvHora.setText(accion.getHora() != null ? accion.getHora() : "");

        // 5. Usuario y estado de ejecución
        holder.tvUsuario.setText(usuarioNombre);

        if (accion.isEjecutado()) {
            holder.tvEjecucion.setText("Completado");
            holder.tvEjecucion.setBackgroundResource(R.drawable.bg_status_completed);
        } else {
            holder.tvEjecucion.setText("Pendiente");
            holder.tvEjecucion.setBackgroundResource(R.drawable.bg_status_pending);
        }
        holder.tvEjecucion.setTextColor(ContextCompat.getColor(context,
                accion.isEjecutado() ? R.color.green : R.color.orange));
    }

    @Override
    public int getItemCount() {
        return historial != null ? historial.size() : 0;
    }

    public class HistorialAccionesViewHolder extends RecyclerView.ViewHolder {
        TextView tvTipoAccion, tvEstado, tvDispositivo, tvFecha, tvHora, tvUsuario, tvEjecucion;
        ImageView ivEstadoIcon;

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
        }
    }

    // Método para actualizar datos
    public void actualizarDatos(List<CambioDispositivo> nuevosDatos) {
        this.historial = nuevosDatos;
        notifyDataSetChanged();
    }
}
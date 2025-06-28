package com.example.smarthouse.ui.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarthouse.R;
import com.example.smarthouse.data.models.Alarma;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class adaptadorHistorialAlarma extends RecyclerView.Adapter<adaptadorHistorialAlarma.HistorialAccesoViewHolder> implements Filterable {
    private List<Alarma> listaAlarmas;
    private List<Alarma> listaFiltrada;
    private List<String> listaKeys;
    private Context context;

    public adaptadorHistorialAlarma(List<Alarma> listaAlarmas, List<String> listaKeys, Context context) {
        this.listaAlarmas = listaAlarmas;
        this.listaFiltrada = new ArrayList<>(listaAlarmas);
        this.listaKeys = new ArrayList<>(listaKeys);
        this.context = context;
    }

    @NonNull
    @Override
    public HistorialAccesoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_alarmas, parent, false);
        return new HistorialAccesoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistorialAccesoViewHolder holder, int position) {
        Alarma alarma = listaFiltrada.get(position);

        holder.lbFecha.setText(alarma.getFecha() != null ? alarma.getFecha() : "--/--/----");
        holder.lbHora.setText(alarma.getHora() != null ? alarma.getHora() : "--:--");

        String tipo = alarma.getTipoEvento() != null ? formatearTipoEvento(alarma.getTipoEvento()) : "Desconocido";
        holder.lbTipo.setText("Tipo: " + tipo);

        String ubicacion = alarma.getUbicacion() != null ? alarma.getUbicacion() : "No especificada";
        holder.lbUbicacion.setText("Ubicación: " + ubicacion);

        holder.btnEliminar.setOnClickListener(v -> {
            new android.app.AlertDialog.Builder(context)
                    .setTitle("Confirmar eliminación")
                    .setMessage("¿Deseas eliminar este registro de alarma?")
                    .setPositiveButton("Sí", (dialog, which) -> {
                        int index = holder.getAdapterPosition();
                        if (index != RecyclerView.NO_POSITION) {
                            String key = listaKeys.get(index);
                            FirebaseDatabase.getInstance()
                                    .getReference("alarmas")
                                    .child(key)
                                    .removeValue()
                                    .addOnSuccessListener(aVoid -> {
                                        listaAlarmas.remove(index);
                                        listaFiltrada.remove(index);
                                        listaKeys.remove(index);
                                        notifyItemRemoved(index);
                                        notifyItemRangeChanged(index, listaFiltrada.size());
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(context, "Error al eliminar en Firebase", Toast.LENGTH_SHORT).show();
                                    });
                        }
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });
    }

    private String formatearTipoEvento(String tipoCrudo) {
        switch (tipoCrudo) {
            case "APERTURA_PUERTA":
                return "Apertura de puerta";
            case "SENSOR_GAS":
                return "Detección de gas";
            case "MODO_SEGURO":
                return "Modo seguro";
            case "Intento_de_acceso_fallido":
                return "Intento de acceso fallido";
            default:
                return tipoCrudo.replace("_", " ").toLowerCase()
                        .replaceFirst(".", tipoCrudo.substring(0,1).toUpperCase());
        }
    }

    @Override
    public int getItemCount() {
        return listaFiltrada != null ? listaFiltrada.size() : 0;
    }

    public void actualizarDatos(List<Alarma> nuevasAlarmas, List<String> nuevasKeys) {
        this.listaAlarmas = new ArrayList<>(nuevasAlarmas);
        this.listaFiltrada = new ArrayList<>(nuevasAlarmas);
        this.listaKeys = new ArrayList<>(nuevasKeys);
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<Alarma> filtrada = new ArrayList<>();

                if (constraint == null || constraint.length() == 0) {
                    filtrada.addAll(listaAlarmas);
                } else {
                    String patron = constraint.toString().toLowerCase().trim();
                    for (Alarma item : listaAlarmas) {
                        if ((item.getFecha() != null && item.getFecha().toLowerCase().contains(patron)) ||
                                (item.getHora() != null && item.getHora().toLowerCase().contains(patron)) ||
                                (item.getTipoEvento() != null && item.getTipoEvento().toLowerCase().contains(patron)) ||
                                (item.getUbicacion() != null && item.getUbicacion().toLowerCase().contains(patron))) {
                            filtrada.add(item);
                        }
                    }
                }

                FilterResults results = new FilterResults();
                results.values = filtrada;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                listaFiltrada.clear();
                listaFiltrada.addAll((List<Alarma>) results.values);
                notifyDataSetChanged();
            }
        };
    }

    public static class HistorialAccesoViewHolder extends RecyclerView.ViewHolder {
        TextView lbFecha, lbHora, lbTipo, lbUbicacion;
        ImageButton btnEliminar;

        public HistorialAccesoViewHolder(@NonNull View itemView) {
            super(itemView);
            lbFecha = itemView.findViewById(R.id.lbFechaAlarma);
            lbHora = itemView.findViewById(R.id.lbHoraAlarma);
            lbTipo = itemView.findViewById(R.id.lbTipoAlarma);
            lbUbicacion = itemView.findViewById(R.id.lbUbicacionAlarma);
            btnEliminar = itemView.findViewById(R.id.btnEliminarAlarma);
        }
    }
}

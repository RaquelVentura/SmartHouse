package com.example.smarthouse.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarthouse.R;
import com.example.smarthouse.data.models.HistorialAcceso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class adaptadorHistorialAcceso extends RecyclerView.Adapter<adaptadorHistorialAcceso.HistorialAccesoViewHolder> implements Filterable {

    private List<HistorialAcceso> listaHistorialAccesos;
    private List<HistorialAcceso> listaHistorialAccesosFiltrada;
    private Context context;

    public adaptadorHistorialAcceso(List<HistorialAcceso> listaHistorialAccesos, Context context) {
        this.listaHistorialAccesos = listaHistorialAccesos;
        this.listaHistorialAccesosFiltrada = listaHistorialAccesosFiltrada;
        this.context = context;
    }

    @NonNull
    @Override
    public HistorialAccesoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_accesos_alarmas, parent, false);
        return new HistorialAccesoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistorialAccesoViewHolder holder, int position) {
        HistorialAcceso historialAcceso = listaHistorialAccesosFiltrada.get(position);
        if (historialAcceso == null) {
            holder.lbFechaHistorialAcceso.setText("--/--/----");
            holder.lbHoraHistorialAcceso.setText("--:--");
            holder.lbResultadoHistorialAcceso.setText("Evento no disponible");
            return;
        }
        holder.lbFechaHistorialAcceso.setText(historialAcceso.getFecha() != null ?
                historialAcceso.getFecha() : "--/--/----");

        holder.lbHoraHistorialAcceso.setText(historialAcceso.getHora() != null ?
                historialAcceso.getHora() : "--:--");

        String estadoTexto = "Activada".equals(historialAcceso.getResultado()) ?
                "Alarma activada" : "Alarma desactivada";

        String tipoTexto = "";
        if (historialAcceso.getTipoEvento() != null) {
            switch(historialAcceso.getTipoEvento()) {
                case "MODO_SEGURO":
                    tipoTexto = " (Modo seguro)";
                    break;
                default:
                    tipoTexto = " (" + historialAcceso.getTipoEvento() + ")";
            }
        }

        String ubicacionTexto = historialAcceso.getUbicacion() != null ?
                "\nUbicación: " + historialAcceso.getUbicacion() : "";

        holder.lbResultadoHistorialAcceso.setText(estadoTexto + tipoTexto + ubicacionTexto);
        holder.lbUsuarioActivador.setText(historialAcceso.getUsuarioEmail() != null ?
                historialAcceso.getUsuarioEmail() : "Sistema");
    }

    @Override
    public int getItemCount() {
        return listaHistorialAccesosFiltrada != null ? listaHistorialAccesosFiltrada.size() : 0;
    }

    public void actualizarDatos(List<HistorialAcceso> nuevasHistorialAccesos) {
        List<HistorialAcceso> listaOrdenada = new ArrayList<>(nuevasHistorialAccesos);

        Collections.sort(listaOrdenada, (o1, o2) -> {
            String fechaHora1 = o1.getFecha() + o1.getHora();
            String fechaHora2 = o2.getFecha() + o2.getHora();
            return fechaHora2.compareTo(fechaHora1);
        });

        this.listaHistorialAccesos = listaOrdenada;
        this.listaHistorialAccesosFiltrada = new ArrayList<>(listaOrdenada);
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<HistorialAcceso> filteredList = new ArrayList<>();

                if (constraint == null || constraint.length() == 0) {
                    filteredList.addAll(listaHistorialAccesos);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();

                    for (HistorialAcceso item : listaHistorialAccesos) {
                        if (item.getFecha().toLowerCase().contains(filterPattern) ||
                                item.getHora().toLowerCase().contains(filterPattern) ||
                                (item.getUsuarioEmail() != null &&
                                        item.getUsuarioEmail().toLowerCase().contains(filterPattern)) ||
                                (item.getTipoEvento() != null &&
                                        item.getTipoEvento().toLowerCase().contains(filterPattern)) ||
                                (item.getUbicacion() != null &&
                                        item.getUbicacion().toLowerCase().contains(filterPattern))) {
                            filteredList.add(item);
                        }
                    }
                }

                Collections.sort(filteredList, (o1, o2) -> {
                    String fechaHora1 = o1.getFecha() + o1.getHora();
                    String fechaHora2 = o2.getFecha() + o2.getHora();
                    return fechaHora2.compareTo(fechaHora1);
                });

                FilterResults results = new FilterResults();
                results.values = filteredList;
                return results;
            }

            @Override
            @SuppressWarnings("unchecked")
            protected void publishResults(CharSequence constraint, FilterResults results) {
                listaHistorialAccesosFiltrada.clear();
                listaHistorialAccesosFiltrada.addAll((List<HistorialAcceso>) results.values);
                notifyDataSetChanged();
            }
        };
    }

    public class HistorialAccesoViewHolder extends RecyclerView.ViewHolder {
        TextView lbFechaHistorialAcceso, lbHoraHistorialAcceso,
                lbResultadoHistorialAcceso, lbUsuarioActivador;

        public HistorialAccesoViewHolder(@NonNull View itemView) {
            super(itemView);
            lbFechaHistorialAcceso = itemView.findViewById(R.id.lbFechaAlarma);
            lbHoraHistorialAcceso = itemView.findViewById(R.id.lbHoraAlarma);
            lbResultadoHistorialAcceso = itemView.findViewById(R.id.lbResultadoAlarma);
            lbUsuarioActivador = itemView.findViewById(R.id.lbUsuarioActivador);
        }
    }
}
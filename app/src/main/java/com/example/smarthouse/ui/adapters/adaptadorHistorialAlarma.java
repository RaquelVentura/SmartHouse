package com.example.smarthouse.ui.adapters;

import static java.security.AccessController.getContext;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarthouse.R;
import com.example.smarthouse.data.models.Alarma;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class adaptadorHistorialAlarma extends RecyclerView.Adapter<adaptadorHistorialAlarma.HistorialAccesoViewHolder> implements Filterable {
    private List<Alarma> listaAlarmas;
    private List<Alarma> listaHistorialAccesosFiltrada;
    private Context context;

    public adaptadorHistorialAlarma(List<Alarma> listaAlarmas, Context context) {
        this.listaAlarmas = listaAlarmas;
        this.listaHistorialAccesosFiltrada = new ArrayList<>(listaAlarmas);
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
        Alarma alarma = listaHistorialAccesosFiltrada.get(position);

        holder.lbFecha.setText(alarma.getFecha() != null ? alarma.getFecha() : "--/--/----");
        holder.lbHora.setText(alarma.getHora() != null ? alarma.getHora() : "--:--");

        String tipo = alarma.getTipoEvento() != null ? formatearTipoEvento(alarma.getTipoEvento()) : "Desconocido";
        holder.lbTipo.setText("Tipo: " + tipo);

        String ubicacion = alarma.getUbicacion() != null ? alarma.getUbicacion() : "No especificada";
        holder.lbUbicacion.setText("Ubicación: " + ubicacion);
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
        return listaHistorialAccesosFiltrada != null ? listaHistorialAccesosFiltrada.size() : 0;
    }


    public void actualizarDatos(List<Alarma> nuevasHistorialAccesos) {
        List<Alarma> listaOrdenada = new ArrayList<>(nuevasHistorialAccesos);
        Collections.sort(listaOrdenada, (o1, o2) -> (o2.getFecha() + o2.getHora()).compareTo(o1.getFecha() + o1.getHora()));
        this.listaAlarmas = listaOrdenada;
        this.listaHistorialAccesosFiltrada = new ArrayList<>(listaOrdenada);
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<Alarma> filteredList = new ArrayList<>();

                if (constraint == null || constraint.length() == 0) {
                    filteredList.addAll(listaAlarmas);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();

                    for (Alarma item : listaAlarmas) {
                        if ((item.getFecha() != null && item.getFecha().toLowerCase().contains(filterPattern)) ||
                                (item.getHora() != null && item.getHora().toLowerCase().contains(filterPattern)) ||
                                (item.getTipoEvento() != null && item.getTipoEvento().toLowerCase().contains(filterPattern)) ||
                                (item.getUbicacion() != null && item.getUbicacion().toLowerCase().contains(filterPattern))) {
                            filteredList.add(item);
                        }
                    }
                }

                Collections.sort(filteredList, (o1, o2) -> (o2.getFecha() + o2.getHora()).compareTo(o1.getFecha() + o1.getHora()));

                FilterResults results = new FilterResults();
                results.values = filteredList;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                listaHistorialAccesosFiltrada.clear();
                listaHistorialAccesosFiltrada.addAll((List<Alarma>) results.values);
                notifyDataSetChanged();
            }
        };
    }

    public static class HistorialAccesoViewHolder extends RecyclerView.ViewHolder {
        TextView lbFecha, lbHora, lbTipo, lbUbicacion;

        public HistorialAccesoViewHolder(@NonNull View itemView) {
            super(itemView);
            lbFecha = itemView.findViewById(R.id.lbFechaAlarma);
            lbHora = itemView.findViewById(R.id.lbHoraAlarma);
            lbTipo = itemView.findViewById(R.id.lbTipoAlarma);
            lbUbicacion = itemView.findViewById(R.id.lbUbicacionAlarma);
        }
    }
}

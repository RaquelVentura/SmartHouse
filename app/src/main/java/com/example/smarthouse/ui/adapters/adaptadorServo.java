package com.example.smarthouse.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarthouse.R;
import com.example.smarthouse.data.models.UnidadDeSalida;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

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

        holder.btnCambiarEstado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean nuevoEstado = !puerta.getEstado();
                puerta.setEstado(nuevoEstado);
                actualizarVista(holder, puerta);

                DatabaseReference dbRef = FirebaseDatabase.getInstance()
                        .getReference("unidadesSalida")
                        .child(puerta.getId());

                dbRef.child("estado").setValue(nuevoEstado)
                        .addOnSuccessListener(aVoid -> {
                        })
                        .addOnFailureListener(e -> {
                            puerta.setEstado(!nuevoEstado);
                            actualizarVista(holder, puerta);
                        });
            }
        });
    }

    private void actualizarVista(ServoViewHolder holder, UnidadDeSalida puerta) {
        if (puerta.getEstado()) {
            holder.imgPuerta.setImageResource(R.drawable.puerta_abierta);
            holder.lbEstado.setText("Estado: " +"Abierta");
            holder.btnCambiarEstado.setText("Cerrar puerta");
        } else {
            holder.imgPuerta.setImageResource(R.drawable.puerta_cerrada);
            holder.lbEstado.setText("Cerrada");
            holder.btnCambiarEstado.setText("Abrir puerta");
        }
        holder.lbUbicacion.setText(puerta.getUbicacion());
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
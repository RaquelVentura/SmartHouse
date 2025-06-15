package com.example.smarthouse.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarthouse.R;
import com.example.smarthouse.data.models.MQ2;

import java.util.List;

public class adaptadorSensorGas extends RecyclerView.Adapter<adaptadorSensorGas.SensorGasViewHolder> {
    private List<MQ2> dataMQ2;
    private Context context;
    private static final double UMBRAL_NORMAL = 300;
    private static final double UMBRAL_ALERTA = 600;

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

        try {
            double valor = Double.parseDouble(mq2.getValor().replace(" ppm", ""));

            if (valor > UMBRAL_ALERTA) {
                holder.imgMQ2.setImageResource(R.drawable.alerta);
                holder.lbEstado.setText("PELIGRO!");
                holder.lbEstado.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
            } else if (valor > UMBRAL_NORMAL) {

                holder.imgMQ2.setImageResource(R.drawable.advertencia);
                holder.lbEstado.setText("Advertencia");
                holder.lbEstado.setTextColor(context.getResources().getColor(android.R.color.holo_orange_dark));
            } else {
                holder.imgMQ2.setImageResource(R.drawable.img_7);
                holder.lbEstado.setText("Normal");
                holder.lbEstado.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
            }
        } catch (NumberFormatException e) {
            holder.imgMQ2.setImageResource(R.drawable.img_4);
            holder.lbEstado.setText("Error lectura");
            holder.lbEstado.setTextColor(context.getResources().getColor(android.R.color.darker_gray));
        }

        holder.lbUbicacion.setText(mq2.getUbicacion());
        holder.lbValor.setText(mq2.getValor());
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
        private TextView lbUbicacion, lbValor, lbEstado;

        public SensorGasViewHolder(@NonNull View itemView) {
            super(itemView);
            imgMQ2 = itemView.findViewById(R.id.imgSensorGas);
            lbUbicacion = itemView.findViewById(R.id.lbUbicacionMQ2);
            lbValor = itemView.findViewById(R.id.lbValorMq2);
            lbEstado = itemView.findViewById(R.id.lbEstadoMQ2);
        }
    }
}
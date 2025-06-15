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
import com.example.smarthouse.data.models.DHT11;

import java.util.List;

public class adaptadorDHT11 extends RecyclerView.Adapter<adaptadorDHT11.DHT11ViewHolder> {
    private List<DHT11> listaDHT11;
    private Context context;

    public adaptadorDHT11(Context context, List<DHT11> listaDHT11) {
        this.context = context;
        this.listaDHT11 = listaDHT11;
    }

    @NonNull
    @Override
    public DHT11ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_dht11, parent, false);
        return new DHT11ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull adaptadorDHT11.DHT11ViewHolder holder, int position) {
        DHT11 dht11 = listaDHT11.get(position);

        String humedad = dht11.getHumedad() != null ? dht11.getHumedad() + "%" : "--%";
        String temperatura = dht11.getTemperatura() != null ? dht11.getTemperatura() + "°C" : "--°C";
        String ubicacion = dht11.getUbicacion() != null ? dht11.getUbicacion() : "Sin ubicación";

        holder.lbHumedad.setText(humedad);
        holder.lbTemperatura.setText(temperatura);
        holder.lbUbicacion.setText(ubicacion);

        if (dht11.getTemperatura() != null) {
            try {
                double temp = Double.parseDouble(dht11.getTemperatura());

                if (temp <= 15) {
                    holder.imgDHT11.setImageResource(R.drawable.img_frio);
                } else if (temp > 15 && temp <= 25) {
                    holder.imgDHT11.setImageResource(R.drawable.img_buen_clima);
                } else {
                    holder.imgDHT11.setImageResource(R.drawable.img_calor);
                }
            } catch (NumberFormatException e) {
                holder.imgDHT11.setImageResource(R.drawable.img_3);
                holder.lbTemperatura.setText("--°C");
            }
        } else {
            holder.imgDHT11.setImageResource(R.drawable.img_3);
        }
    }

    @Override
    public int getItemCount() {
        return listaDHT11.size();
    }

    public static class DHT11ViewHolder extends RecyclerView.ViewHolder {
        TextView lbHumedad, lbTemperatura, lbUbicacion;
        ImageView imgDHT11;

        public DHT11ViewHolder(@NonNull View itemView) {
            super(itemView);
            lbHumedad = itemView.findViewById(R.id.lbHumedad);
            lbTemperatura = itemView.findViewById(R.id.lbTemperatura);
            lbUbicacion = itemView.findViewById(R.id.lbUbicacion);
            imgDHT11 = itemView.findViewById(R.id.imgDTH11);
        }
    }

    public void actualizarDatos(List<DHT11> nuevosDatos) {
        this.listaDHT11.clear();
        this.listaDHT11.addAll(nuevosDatos);
        notifyDataSetChanged();
    }
}
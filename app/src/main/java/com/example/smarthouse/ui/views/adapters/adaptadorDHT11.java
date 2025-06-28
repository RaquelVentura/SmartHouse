package com.example.smarthouse.ui.views.adapters;

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
import com.example.smarthouse.data.models.CambioDispositivo;
import com.example.smarthouse.data.models.DHT11;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
    public void onBindViewHolder(@NonNull DHT11ViewHolder holder, int position) {
        DHT11 dht11 = listaDHT11.get(position);
        actualizarVista(holder, dht11);

        holder.btnCambiarEstadoVentilador.setOnClickListener(v -> {
            boolean nuevoEstado = !dht11.getEstado();
            dht11.setEstado(nuevoEstado);
            actualizarVista(holder, dht11);

            DatabaseReference dbRef = FirebaseDatabase.getInstance()
                    .getReference("DHT11")
                    .child(dht11.getId());

            dbRef.child("estado").setValue(nuevoEstado)
                    .addOnSuccessListener(aVoid -> registrarCambioDispositivo(dht11, nuevoEstado, "inmediato"))
                    .addOnFailureListener(e -> {
                        Toast.makeText(context, "Error al cambiar estado del ventilador", Toast.LENGTH_SHORT).show();
                        dht11.setEstado(!nuevoEstado);
                        actualizarVista(holder, dht11);
                        Log.e("Firebase", "Error al actualizar estado", e);
                    });
        });

        holder.btnModoAutomatico.setOnClickListener(v -> {
            String modoActual = dht11.getModo();
            String nuevoModo = "manual".equalsIgnoreCase(modoActual) ? "automatico" : "manual";

            dht11.setModo(nuevoModo);
            notifyItemChanged(holder.getAdapterPosition());

            DatabaseReference dbRef = FirebaseDatabase.getInstance()
                    .getReference("DHT11")
                    .child(dht11.getId());

            dbRef.child("modo").setValue(nuevoModo)
                    .addOnSuccessListener(aVoid ->
                            Toast.makeText(context, "Modo cambiado a " + nuevoModo, Toast.LENGTH_SHORT).show()
                    )
                    .addOnFailureListener(e -> {
                        Toast.makeText(context, "Error al cambiar el modo", Toast.LENGTH_SHORT).show();
                        dht11.setModo(modoActual);
                        notifyItemChanged(holder.getAdapterPosition());
                        Log.e("Firebase", "Error al cambiar modo", e);
                    });
        });
    }

    @Override
    public int getItemCount() {
        return listaDHT11 != null ? listaDHT11.size() : 0;
    }

    public void actualizarDatos(List<DHT11> nuevosDatos) {
        this.listaDHT11 = nuevosDatos;
        notifyDataSetChanged();
    }

    public static class DHT11ViewHolder extends RecyclerView.ViewHolder {
        TextView lbHumedad, lbTemperatura, lbUbicacion;
        ImageView imgDHT11;
        Button btnCambiarEstadoVentilador, btnModoAutomatico;

        public DHT11ViewHolder(@NonNull View itemView) {
            super(itemView);
            lbHumedad = itemView.findViewById(R.id.lbHumedad);
            lbTemperatura = itemView.findViewById(R.id.lbTemperatura);
            lbUbicacion = itemView.findViewById(R.id.lbUbicacion);
            imgDHT11 = itemView.findViewById(R.id.imgDTH11);
            btnCambiarEstadoVentilador = itemView.findViewById(R.id.btnCambiarEstadoVentilador);
            btnModoAutomatico = itemView.findViewById(R.id.btnModoAutomaticoVentiladores);
        }
    }

    private void actualizarVista(DHT11ViewHolder holder, DHT11 dht11) {
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
                } else if (temp <= 25) {
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

        String modo = dht11.getModo();
        if (modo != null && modo.trim().equalsIgnoreCase("automatico")) {
            //aqui con esa propiedad GONE se oculta el boton que no estara dispponible si esta en modo automatico
            holder.btnCambiarEstadoVentilador.setVisibility(View.GONE);
            holder.btnModoAutomatico.setText("Activar modo manual");
        } else {
            holder.btnCambiarEstadoVentilador.setVisibility(View.VISIBLE);

            Boolean estado = dht11.getEstado();
            holder.btnCambiarEstadoVentilador.setText(
                    estado != null && estado ? "Apagar ventilador" : "Encender ventilador"
            );

            holder.btnModoAutomatico.setText("Activar modo automático");
        }
    }


    private void registrarCambioDispositivo(DHT11 dht11, boolean nuevoEstado, String tipoCambio) {
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
                dht11.getId(),
                "DHT11",
                dht11.getUbicacion(),
                usuarioId,
                usuarioNombre,
                System.currentTimeMillis(),
                true
        );

        cambiosRef.setValue(cambio)
                .addOnSuccessListener(aVoid ->
                        Log.d("Firebase", "Cambio de ventilador registrado: " + dht11.getUbicacion()))
                .addOnFailureListener(e ->
                        Log.e("Firebase", "Error al registrar cambio de ventilador", e));
    }

    private String obtenerFechaActual() {
        return new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
    }

    private String obtenerHoraActual() {
        return new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
    }
}

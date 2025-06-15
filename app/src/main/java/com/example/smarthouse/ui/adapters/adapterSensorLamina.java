package com.example.smarthouse.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarthouse.R;
import com.example.smarthouse.data.models.UnidadDeSalida;
import com.example.smarthouse.ui.views.dialogs.DispositivosDialogFragment;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

public class adapterSensorLamina extends RecyclerView.Adapter<adapterSensorLamina.LaminaViewHolder> {
    private List<UnidadDeSalida> dataLamina;
    private Context context;

    public adapterSensorLamina(Context context, List<UnidadDeSalida> todasLasUnidades) {
        this.context = context;
        this.dataLamina = new ArrayList<>();
        for (UnidadDeSalida unidad : todasLasUnidades) {
            if ("Lamina".equalsIgnoreCase(unidad.getTipo())) {
                this.dataLamina.add(unidad);
            }
        }
    }

    @NonNull
    @Override
    public LaminaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_lamina, parent, false);
        return new LaminaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LaminaViewHolder holder, int position) {
        UnidadDeSalida lamina = dataLamina.get(position);
        holder.lbHabitacion.setText("Ubicación: "+lamina.getUbicacion());
        if (lamina.getEstado()) {
            holder.imgLamina.setImageResource(R.drawable.ventana_abierta);
            holder.lbEstado.setText("Estado: "+"Abierto");
        } else {
            holder.imgLamina.setImageResource(R.drawable.ventana_cerrada);
            holder.lbEstado.setText("Estado: " + "Cerrado");
        }

        holder.btnCambiarEstado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean nuevoEstado = !lamina.getEstado();
                lamina.setEstado(nuevoEstado);

                if (nuevoEstado) {
                    holder.imgLamina.setImageResource(R.drawable.ventana_abierta);
                    holder.lbEstado.setText("Estado: " +"Abierto");
                } else {
                    holder.imgLamina.setImageResource(R.drawable.ventana_cerrada);
                    holder.lbEstado.setText("Estado: " +"Cerrado");
                }

                holder.lbHabitacion.setText("Ubicación: "+lamina.getUbicacion());

                DatabaseReference dbRef = com.google.firebase.database.FirebaseDatabase.getInstance()
                        .getReference("unidadesSalida")
                        .child(lamina.getId());

                dbRef.child("estado").setValue(nuevoEstado);
            }
        });

        holder.btnProgramarCambio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = ((AppCompatActivity) v.getContext()).getSupportFragmentManager();
                DispositivosDialogFragment dialog = new DispositivosDialogFragment();
                dialog.show(fragmentManager, "DispositivosDialog");
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataLamina.size();
    }

    public class LaminaViewHolder extends RecyclerView.ViewHolder {
        ImageView imgLamina;
        TextView lbNombre, lbHabitacion, lbEstado;
        Button btnCambiarEstado;
        ImageButton btnProgramarCambio;

        public LaminaViewHolder(@NonNull View itemView) {
            super(itemView);
            imgLamina = itemView.findViewById(R.id.imgLamina);
            lbNombre = itemView.findViewById(R.id.lbNombreLamina);
            lbHabitacion = itemView.findViewById(R.id.lbHabitacionLamina);
            lbEstado = itemView.findViewById(R.id.lbEstadoLamina);
            btnCambiarEstado = itemView.findViewById(R.id.btnCambiarEstadoLamina);
            btnProgramarCambio = itemView.findViewById(R.id.btnAbrirDialogProgramarCambioLamina);
        }
    }
}

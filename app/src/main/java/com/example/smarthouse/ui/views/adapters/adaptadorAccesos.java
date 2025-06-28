package com.example.smarthouse.ui.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarthouse.R;
import com.example.smarthouse.data.models.Seguridad;

import java.util.List;

public class adaptadorAccesos extends RecyclerView.Adapter<adaptadorAccesos.ViewHolderAcceso> {

    private final List<Seguridad> lista;
    private final Context context;

    public adaptadorAccesos(List<Seguridad> lista, Context context) {
        this.lista = lista;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolderAcceso onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_acceso, parent, false);
        return new ViewHolderAcceso(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderAcceso holder, int position) {
        Seguridad acceso = lista.get(position);

        holder.lbFecha.setText("Fecha: " + acceso.getFecha());
        holder.lbHora.setText("Hora: " + acceso.getHora());
        holder.lbResultado.setText("Resultado: " + acceso.getResultado());

        if (acceso.getResultado() != null && acceso.getResultado().trim().equalsIgnoreCase("fallido")) {
            holder.imgAcceso.setImageResource(R.drawable.close);
        } else {
            holder.imgAcceso.setImageResource(R.drawable.check);
        }
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class ViewHolderAcceso extends RecyclerView.ViewHolder {
        TextView lbFecha, lbHora, lbResultado;
        ImageView imgAcceso;

        public ViewHolderAcceso(@NonNull View itemView) {
            super(itemView);
            lbFecha = itemView.findViewById(R.id.tvFecha);
            lbHora = itemView.findViewById(R.id.tvHora);
            lbResultado = itemView.findViewById(R.id.tvResultado);
            imgAcceso = itemView.findViewById(R.id.imgAcceso);
        }
    }
}

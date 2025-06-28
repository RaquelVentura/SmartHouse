package com.example.smarthouse.ui.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarthouse.R;
import com.example.smarthouse.data.models.Seguridad;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
public class adaptadorAccesos extends RecyclerView.Adapter<adaptadorAccesos.ViewHolderAcceso> {

    private final List<Seguridad> lista;
    private final List<String> keys;
    private final Context context;

    public adaptadorAccesos(List<Seguridad> lista, List<String> keys, Context context) {
        this.lista = lista;
        this.keys = keys;
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

        holder.btnEliminarAcceso.setOnClickListener(v -> {
            new androidx.appcompat.app.AlertDialog.Builder(context)
                    .setTitle("Confirmar eliminación")
                    .setMessage("¿Deseas eliminar este registro de acceso?")
                    .setPositiveButton("Sí", (dialog, which) -> {
                        int pos = holder.getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION && pos < keys.size()) {
                            String keyToDelete = keys.get(pos);
                            eliminarRegistroFirebase(keyToDelete, pos);
                        }
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });
    }

    private void eliminarRegistroFirebase(String key, int position) {
        FirebaseDatabase.getInstance()
                .getReference("seguridad")
                .child(key)
                .removeValue()
                .addOnSuccessListener(aVoid -> {
                    lista.remove(position);
                    keys.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, lista.size());
                })
                .addOnFailureListener(e -> {
                    android.widget.Toast.makeText(context, "Error al eliminar registro", android.widget.Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class ViewHolderAcceso extends RecyclerView.ViewHolder {
        TextView lbFecha, lbHora, lbResultado;
        ImageView imgAcceso;
        ImageButton btnEliminarAcceso;

        public ViewHolderAcceso(@NonNull View itemView) {
            super(itemView);
            lbFecha = itemView.findViewById(R.id.tvFecha);
            lbHora = itemView.findViewById(R.id.tvHora);
            lbResultado = itemView.findViewById(R.id.tvResultado);
            imgAcceso = itemView.findViewById(R.id.imgAcceso);
            btnEliminarAcceso = itemView.findViewById(R.id.btnEliminarAcceso);
        }
    }
}

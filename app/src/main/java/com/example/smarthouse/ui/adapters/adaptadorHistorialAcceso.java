package com.example.smarthouse.ui.adapters;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class adaptadorHistorialAcceso extends RecyclerView.Adapter<adaptadorHistorialAcceso.HistorialAccesoViewHolder> {
    @NonNull
    @Override
    public adaptadorHistorialAcceso.HistorialAccesoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull adaptadorHistorialAcceso.HistorialAccesoViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class HistorialAccesoViewHolder extends RecyclerView.ViewHolder {
        public HistorialAccesoViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}

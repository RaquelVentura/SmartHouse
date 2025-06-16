package com.example.smarthouse.ui.views.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarthouse.R;
import com.example.smarthouse.data.models.CambioDispositivo;
import com.example.smarthouse.ui.adapters.adaptadorHistorialAcciones;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HistorialAccionesFragment extends Fragment {

    private RecyclerView rvHistorial;
    private ProgressBar progressBar;
    private adaptadorHistorialAcciones adapter;
    private List<CambioDispositivo> listaCambios = new ArrayList<>();

    public HistorialAccionesFragment() {
        // Constructor vacío requerido
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_historial_acciones, container, false);

        // Inicializar vistas
        rvHistorial = view.findViewById(R.id.rvHistorial);
        progressBar = view.findViewById(R.id.progressBar);

        // Configurar RecyclerView
        setupRecyclerView();

        // Cargar datos de Firebase
        cargarHistorialDesdeFirebase();

        return view;
    }

    private void setupRecyclerView() {
        adapter = new adaptadorHistorialAcciones(requireContext(), listaCambios);
        rvHistorial.setLayoutManager(new LinearLayoutManager(getContext()));
        rvHistorial.setAdapter(adapter);
    }

    private void cargarHistorialDesdeFirebase() {
        progressBar.setVisibility(View.VISIBLE);

        DatabaseReference cambiosRef = FirebaseDatabase.getInstance()
                .getReference("cambiosDispositivos");

        cambiosRef.orderByChild("timestamp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaCambios.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    CambioDispositivo cambio = ds.getValue(CambioDispositivo.class);
                    if (cambio != null) {
                        listaCambios.add(cambio);
                    }
                }

                // Ordenar por timestamp (más reciente primero)
                Collections.reverse(listaCambios);

                adapter.actualizarDatos(listaCambios);
                progressBar.setVisibility(View.GONE);

                if (listaCambios.isEmpty()) {
                    Toast.makeText(getContext(), "No hay historial disponible", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Error al cargar historial: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
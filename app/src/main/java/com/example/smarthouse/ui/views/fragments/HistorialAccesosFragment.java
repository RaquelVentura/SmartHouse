package com.example.smarthouse.ui.views.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.smarthouse.R;
import com.example.smarthouse.data.models.Seguridad;
import com.example.smarthouse.ui.views.adapters.adaptadorAccesos;

import java.util.ArrayList;
import java.util.List;
import androidx.annotation.NonNull;
import com.google.firebase.database.FirebaseDatabase;

public class HistorialAccesosFragment extends Fragment {
    private RecyclerView recyclerAccesos;
    private adaptadorAccesos adapter;
    private List<Seguridad> listaAccesos = new ArrayList<>();

    public HistorialAccesosFragment() {
    }
public static HistorialAccesosFragment newInstance(String param1, String param2) {
        HistorialAccesosFragment fragment = new HistorialAccesosFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_historial_accesos, container, false);

        recyclerAccesos = view.findViewById(R.id.recyclerAccesosPuerta);
        adapter = new adaptadorAccesos(listaAccesos, requireContext());
        recyclerAccesos.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerAccesos.setAdapter(adapter);

        cargarAccesosDesdeFirebase();

        return view;
    }

    private void cargarAccesosDesdeFirebase() {
        FirebaseDatabase.getInstance().getReference("seguridad")
                .addValueEventListener(new com.google.firebase.database.ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot snapshot) {
                        listaAccesos.clear();

                        for (com.google.firebase.database.DataSnapshot ds : snapshot.getChildren()) {
                            String fecha = ds.child("Fecha").getValue(String.class);
                            String hora = ds.child("Hora").getValue(String.class);
                            String resultado = ds.child("Resultado").getValue(String.class);

                            Seguridad acceso = new Seguridad(fecha, hora, resultado);
                            listaAccesos.add(acceso);
                        }

                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull com.google.firebase.database.DatabaseError error) {

                    }
                });
    }

}
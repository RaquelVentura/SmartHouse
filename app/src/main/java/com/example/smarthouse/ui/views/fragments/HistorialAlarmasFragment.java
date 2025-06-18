package com.example.smarthouse.ui.views.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.smarthouse.R;
import com.example.smarthouse.data.models.HistorialAcceso;
import com.example.smarthouse.ui.adapters.adaptadorHistorialAcceso;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HistorialAlarmasFragment extends Fragment {
    private adaptadorHistorialAcceso adaptador;
    private DatabaseReference alarmasRef;
    private ValueEventListener valueEventListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_historial_alarmas, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerAlarmasAccesos);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adaptador = new adaptadorHistorialAcceso(new ArrayList<>(),getContext());
        recyclerView.setAdapter(adaptador);

        SearchView searchView = view.findViewById(R.id.searchViewAlarmasAccesos);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) { return false; }

            @Override
            public boolean onQueryTextChange(String newText) {
                adaptador.getFilter().filter(newText);
                return true;
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        cargarHistorialAlarmas();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (valueEventListener != null) {
            alarmasRef.removeEventListener(valueEventListener);
        }
    }
    private void cargarHistorialAlarmas() {
        alarmasRef = FirebaseDatabase.getInstance().getReference("alarmas/historial");
        valueEventListener = alarmasRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<HistorialAcceso> historial = new ArrayList<>();
                for (DataSnapshot item : snapshot.getChildren()) {
                    HistorialAcceso acceso = item.getValue(HistorialAcceso.class);
                    if (acceso != null) {
                        acceso.setId(item.getKey());
                        if (acceso.getFecha() == null) acceso.setFecha("--/--/----");
                        if (acceso.getHora() == null) acceso.setHora("--:--");
                        if (acceso.getTipoEvento() == null) acceso.setTipoEvento("EVENTO_DESCONOCIDO");
                        historial.add(acceso);
                    }
                }
                adaptador.actualizarDatos(historial);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("HistorialAlarmas", "Error en Firebase", error.toException());
            }
        });
    }
}
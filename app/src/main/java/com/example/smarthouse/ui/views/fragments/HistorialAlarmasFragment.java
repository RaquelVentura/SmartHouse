package com.example.smarthouse.ui.views.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.smarthouse.R;
import com.example.smarthouse.data.models.Alarma;
import com.example.smarthouse.ui.views.adapters.adaptadorHistorialAlarma;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HistorialAlarmasFragment extends Fragment {

    private RecyclerView recyclerAlarmas;
    private SearchView searchView;
    private adaptadorHistorialAlarma adaptador;
    private List<Alarma> listaAlarmas = new ArrayList<>();
    private List<String> listaKeys = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_historial_alarmas, container, false);

        recyclerAlarmas = view.findViewById(R.id.recyclerAlarmasAccesos);
        searchView = view.findViewById(R.id.searchViewAlarmasAccesos);

        recyclerAlarmas.setLayoutManager(new LinearLayoutManager(getContext()));
        adaptador = new adaptadorHistorialAlarma(listaAlarmas, listaKeys, getContext());
        recyclerAlarmas.setAdapter(adaptador);

        cargarAlarmasDesdeFirebase();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adaptador.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adaptador.getFilter().filter(newText);
                return false;
            }
        });

        return view;
    }

    private void cargarAlarmasDesdeFirebase() {
        FirebaseDatabase.getInstance().getReference("alarmas")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        listaAlarmas.clear();
                        listaKeys.clear();

                        for (DataSnapshot alarmaSnapshot : snapshot.getChildren()) {
                            Alarma alarma = alarmaSnapshot.getValue(Alarma.class);
                            if (alarma != null) {
                                listaAlarmas.add(alarma);
                                listaKeys.add(alarmaSnapshot.getKey());
                            }
                        }

                        adaptador.actualizarDatos(listaAlarmas, listaKeys);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Error al cargar alarmas", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}

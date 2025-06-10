package com.example.smarthouse.ui.views.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.smarthouse.R;

public class GestionUsuariosFragment extends Fragment {
    private RecyclerView recyclerUsuarios;
    private Button btnNuevoUsuario;

    public GestionUsuariosFragment() {
    }
    public static GestionUsuariosFragment newInstance(String param1, String param2) {
        GestionUsuariosFragment fragment = new GestionUsuariosFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gestion_usuarios, container, false);
        recyclerUsuarios = view.findViewById(R.id.recyclerUsuarios);
        btnNuevoUsuario = view.findViewById(R.id.btnNuevoUsuario);
        return view;
    }
}
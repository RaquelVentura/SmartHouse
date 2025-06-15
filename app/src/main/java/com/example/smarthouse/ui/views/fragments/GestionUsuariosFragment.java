package com.example.smarthouse.ui.views.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.smarthouse.R;
import com.example.smarthouse.data.services.UsuarioSerImplement;
import com.example.smarthouse.data.services.UsuarioServicio;
import com.example.smarthouse.ui.views.adapters.UsuarioAdaptador;
import com.example.smarthouse.ui.views.dialogs.GestionUsuariosDialog;

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
        asociarElementosXml(view);
        actualizarListaUsuarios();
        btnNuevoUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GestionUsuariosDialog gestionUsuariosDialog = GestionUsuariosDialog.nuevaInstancia();
                gestionUsuariosDialog.setListener(GestionUsuariosFragment.this::actualizarListaUsuarios);
                gestionUsuariosDialog.show(getChildFragmentManager(), "gestion_usuarios");
            }
        });
        return view;
    }

    private void asociarElementosXml(View view) {
        recyclerUsuarios = view.findViewById(R.id.recyclerUsuarios);
        btnNuevoUsuario = view.findViewById(R.id.btnNuevoUsuario);
    }

    public void actualizarListaUsuarios() {
        UsuarioServicio usuarioServicio = new UsuarioSerImplement();
        usuarioServicio.obtenerTodosLosUsuarios().addOnSuccessListener(lstUsuarios -> {
            if (!lstUsuarios.isEmpty()) {
                UsuarioAdaptador adaptador = new UsuarioAdaptador(
                        lstUsuarios,
                        this::actualizarListaUsuarios,
                        getContext(),
                        getChildFragmentManager()
                );
                recyclerUsuarios.setLayoutManager(new GridLayoutManager(
                        getContext(), 1, GridLayoutManager.VERTICAL, false
                ));

                recyclerUsuarios.setAdapter(adaptador);
            } else {
                Toast.makeText(requireActivity(), "Error 404: No hay usuarios para mostrar.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            System.out.println("Error al obtener usuarios: " + e.getMessage());
            Toast.makeText(requireActivity(), "Error: No se han podido cargar los usuarios :(", Toast.LENGTH_SHORT).show();
        });
    }
}
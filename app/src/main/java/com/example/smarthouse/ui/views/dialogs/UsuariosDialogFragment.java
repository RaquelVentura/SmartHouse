package com.example.smarthouse.ui.views.dialogs;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.smarthouse.R;

public class UsuariosDialogFragment extends DialogFragment {
    public UsuariosDialogFragment() {
        // Required empty public constructor
    }

    public static UsuariosDialogFragment newInstance(String param1, String param2) {
        UsuariosDialogFragment fragment = new UsuariosDialogFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_usuarios_dialog, container, false);
        return view;
    }
}
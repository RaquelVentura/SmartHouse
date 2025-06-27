package com.example.smarthouse.ui.views.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import androidx.fragment.app.DialogFragment;

import com.example.smarthouse.R;

public class TerminosYCondicionesFragment extends DialogFragment {

    public TerminosYCondicionesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_terminos_y_condiciones, container, false);

        Button btnCerrarDialog = view.findViewById(R.id.btnCerrarDialogTerminosYCOndicones);
        btnCerrarDialog.setOnClickListener(v -> dismiss());

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (getDialog() != null && getDialog().getWindow() != null) {
            Window window = getDialog().getWindow();
            int width = (int) (getResources().getDisplayMetrics().widthPixels * 1.0);
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            window.setLayout(width, height);
            window.setBackgroundDrawableResource(android.R.color.transparent);
        }
    }
}
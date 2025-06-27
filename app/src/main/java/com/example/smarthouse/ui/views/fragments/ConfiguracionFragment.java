package com.example.smarthouse.ui.views.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.smarthouse.R;
import com.example.smarthouse.ui.views.dialogs.CambioPinFragment;
import com.example.smarthouse.ui.views.dialogs.TerminosYCondicionesFragment;

public class ConfiguracionFragment extends DialogFragment {
    private CardView btnTemaClaro, btnTemaOscuro, btnTemaSistema;
    private TextView  btnCambiarPin, btnTerminosYCondiciones;
    private int temaActual;

    public ConfiguracionFragment() {

    }

    public static ConfiguracionFragment newInstance() {
        return new ConfiguracionFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_configuracion, container, false);

        // Inicializar vistas
        btnTemaClaro = view.findViewById(R.id.btnTemaClaro);
        btnTemaOscuro = view.findViewById(R.id.btnTemaOscuro);
        btnTemaSistema = view.findViewById(R.id.btnTemaSistema);
        btnCambiarPin = view.findViewById(R.id.btnCambiarPin);
        btnTerminosYCondiciones = view.findViewById(R.id.btnTerminosYCondiciones);

        btnCambiarPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CambioPinFragment cambioPinFragment = new CambioPinFragment();
                cambioPinFragment.show(getChildFragmentManager(), "CambioPinDialog");
            }
        });
        btnTerminosYCondiciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TerminosYCondicionesFragment terminosYCondicionesFragment = new TerminosYCondicionesFragment();
                terminosYCondicionesFragment.show(getChildFragmentManager(), "TerminosYCondicionesDialog");
            }
        });
        // Configurar botones
        setupThemeButtons();

        return view;
    }

    private void setupThemeButtons() {
        temaActual = getSavedTheme();
        actualizarEstadoBotones(temaActual);

        btnTemaClaro.setOnClickListener(v -> cambiarTema(AppCompatDelegate.MODE_NIGHT_NO));
        btnTemaOscuro.setOnClickListener(v -> cambiarTema(AppCompatDelegate.MODE_NIGHT_YES));
        btnTemaSistema.setOnClickListener(v -> cambiarTema(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM));
    }

    private void cambiarTema(int modo) {
        temaActual = modo;
        saveTheme(modo);
        AppCompatDelegate.setDefaultNightMode(modo);
        actualizarEstadoBotones(modo);
    }

    private void actualizarEstadoBotones(int modoActivo) {
        int colorSeleccionado = ContextCompat.getColor(requireContext(), R.color.azul_claro_vivo);
        int colorNormal = ContextCompat.getColor(requireContext(), R.color.blanco_hielo);
        int colorTextoSeleccionado = ContextCompat.getColor(requireContext(), R.color.blanco_hielo);
        int colorTextoNormal = ContextCompat.getColor(requireContext(), R.color.azul_claro_vivo);

        // Configurar tema claro
        actualizarEstadoBoton(btnTemaClaro,
                modoActivo == AppCompatDelegate.MODE_NIGHT_NO,
                colorSeleccionado, colorNormal,
                colorTextoSeleccionado, colorTextoNormal);

        // Configurar tema oscuro
        actualizarEstadoBoton(btnTemaOscuro,
                modoActivo == AppCompatDelegate.MODE_NIGHT_YES,
                colorSeleccionado, colorNormal,
                colorTextoSeleccionado, colorTextoNormal);

        // Configurar tema sistema
        actualizarEstadoBoton(btnTemaSistema,
                modoActivo == AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM,
                colorSeleccionado, colorNormal,
                colorTextoSeleccionado, colorTextoNormal);
    }

    private void actualizarEstadoBoton(CardView card, boolean seleccionado,
                                       int colorSeleccionado, int colorNormal,
                                       int colorTextoSeleccionado, int colorTextoNormal) {
        // Cambiar color de fondo
        card.setCardBackgroundColor(seleccionado ? colorSeleccionado : colorNormal);

        ViewGroup layout = (ViewGroup) card.getChildAt(0);
        for (int i = 0; i < layout.getChildCount(); i++) {
            View child = layout.getChildAt(i);
            if (child instanceof TextView) {
                ((TextView) child).setTextColor(seleccionado ? colorTextoSeleccionado : colorTextoNormal);
            }
        }
    }

    private int getSavedTheme() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
        return prefs.getInt("theme_mode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
    }

    private void saveTheme(int modo) {
        SharedPreferences prefs = requireActivity().getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
        prefs.edit().putInt("theme_mode", modo).apply();
    }
}
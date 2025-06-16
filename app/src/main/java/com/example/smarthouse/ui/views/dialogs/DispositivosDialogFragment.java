package com.example.smarthouse.ui.views.dialogs;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.smarthouse.R;
import com.example.smarthouse.data.helpers.CambiosDispositivosHelper;
import com.example.smarthouse.data.models.CambioDispositivo;
import com.example.smarthouse.data.models.UnidadDeSalida;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DispositivosDialogFragment extends DialogFragment {
    private String dispositivoId;
    private String dispositivoTipo;
    private String dispositivoNombre;
    private boolean estadoActual;
    private Button btnFecha, btnHora;
    private RadioGroup radioGroupEstado;
    private String fechaSeleccionada, horaSeleccionada;

    public static DispositivosDialogFragment newInstance(String id, String tipo, String nombre, boolean estado) {
        DispositivosDialogFragment fragment = new DispositivosDialogFragment();
        Bundle args = new Bundle();
        args.putString("dispositivoId", id);
        args.putString("dispositivoTipo", tipo);
        args.putString("dispositivoNombre", nombre);
        args.putBoolean("estadoActual", estado);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            dispositivoId = getArguments().getString("dispositivoId");
            dispositivoTipo = getArguments().getString("dispositivoTipo");
            dispositivoNombre = getArguments().getString("dispositivoNombre");
            estadoActual = getArguments().getBoolean("estadoActual");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dispositivos_dialog, container, false);

        TextView title = view.findViewById(R.id.title);
        title.setText(String.format("Programar %s", dispositivoNombre));

        RadioButton radioAccion1 = view.findViewById(R.id.radioAccion1);
        RadioButton radioAccion2 = view.findViewById(R.id.radioAccion2);
        radioGroupEstado = view.findViewById(R.id.radioGroupEstado);

        if ("LED".equals(dispositivoTipo)) {
            radioAccion1.setText(estadoActual ? "Apagar" : "Encender");
            radioAccion2.setText(estadoActual ? "Mantener encendido" : "Mantener apagado");
        } else {
            radioAccion1.setText(estadoActual ? "Cerrar" : "Abrir");
            radioAccion2.setText(estadoActual ? "Mantener abierto" : "Mantener cerrado");
        }

        btnFecha = view.findViewById(R.id.btnSeleccionarFecha);
        btnHora = view.findViewById(R.id.btnSeleccionarHora);
        Button btnGuardar = view.findViewById(R.id.btnGuardarProgramacion);
        Button btnCancelar = view.findViewById(R.id.btnCancelar);

        btnFecha.setOnClickListener(v -> mostrarDatePicker());
        btnHora.setOnClickListener(v -> mostrarTimePicker());
        btnGuardar.setOnClickListener(v -> programarCambioDispositivo());
        btnCancelar.setOnClickListener(v -> dismiss());

        return view;
    }

    private void mostrarDatePicker() {
        Calendar calendario = Calendar.getInstance();
        DatePickerDialog datePicker = new DatePickerDialog(requireContext(),
                (view, year, month, dayOfMonth) -> {
                    fechaSeleccionada = String.format(Locale.getDefault(), "%02d/%02d/%d",
                            dayOfMonth, month + 1, year);
                    btnFecha.setText(fechaSeleccionada);
                },
                calendario.get(Calendar.YEAR),
                calendario.get(Calendar.MONTH),
                calendario.get(Calendar.DAY_OF_MONTH));
        datePicker.show();
    }

    private void mostrarTimePicker() {
        Calendar calendario = Calendar.getInstance();
        TimePickerDialog timePicker = new TimePickerDialog(requireContext(),
                (view, hourOfDay, minute) -> {
                    horaSeleccionada = String.format(Locale.getDefault(), "%02d:%02d",
                            hourOfDay, minute);
                    btnHora.setText(horaSeleccionada);
                },
                calendario.get(Calendar.HOUR_OF_DAY),
                calendario.get(Calendar.MINUTE),
                true);
        timePicker.show();
    }
    private void programarCambioDispositivo() {
        if (fechaSeleccionada == null || horaSeleccionada == null) {
            Toast.makeText(getContext(), "Seleccione fecha y hora", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            Date fechaHora = sdf.parse(fechaSeleccionada + " " + horaSeleccionada);
            long timestamp = fechaHora != null ? fechaHora.getTime() : System.currentTimeMillis();

            boolean nuevoEstado = radioGroupEstado.getCheckedRadioButtonId() == R.id.radioAccion1 ?
                    !estadoActual : estadoActual;

            UnidadDeSalida unidad = new UnidadDeSalida(
                    dispositivoId,
                    dispositivoNombre,
                    estadoActual,
                    dispositivoTipo
            );

            CambiosDispositivosHelper.registrarCambio(
                    unidad,
                    nuevoEstado,
                    "programado",
                    timestamp
            );

            Toast.makeText(getContext(), "Cambio programado correctamente", Toast.LENGTH_SHORT).show();
            dismiss();
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error en formato de fecha/hora", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}
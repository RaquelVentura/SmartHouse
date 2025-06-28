package com.example.smarthouse.ui.views.dialogs;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
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
import com.example.smarthouse.data.models.UnidadDeSalida;
import com.example.smarthouse.ui.receivers.EjecutarCambioReceiver;
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
        radioGroupEstado = view.findViewById(R.id.radioGroupEstado);

        if ("LED".equals(dispositivoTipo)) {
            radioAccion1.setText(estadoActual ? "Apagar" : "Encender");
        } else {
            radioAccion1.setText(estadoActual ? "Cerrar" : "Abrir");
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
            if (fechaHora == null || fechaHora.before(new Date())) {
                Toast.makeText(getContext(), "No se puede programar en el pasado", Toast.LENGTH_SHORT).show();
                return;
            }

            long timestamp = fechaHora.getTime();
            boolean nuevoEstado = radioGroupEstado.getCheckedRadioButtonId() == R.id.radioAccion1 ? !estadoActual : estadoActual;

            String cambioId = FirebaseDatabase.getInstance().getReference().push().getKey();
            String usuarioId = FirebaseAuth.getInstance().getCurrentUser().getUid();

            Map<String, Object> datosCambio = new HashMap<>();
            datosCambio.put("id", cambioId);
            datosCambio.put("usuarioId", usuarioId);
            datosCambio.put("idUnidadSalida", dispositivoId);
            datosCambio.put("nombreDispositivo", dispositivoNombre);
            datosCambio.put("tipoDispositivo", dispositivoTipo);
            datosCambio.put("estado", null);
            datosCambio.put("fecha", fechaSeleccionada);
            datosCambio.put("hora", horaSeleccionada);
            datosCambio.put("timestamp", timestamp);
            datosCambio.put("tipoCambio", "programado");
            datosCambio.put("ejecutado", false);

            DatabaseReference refCambios = FirebaseDatabase.getInstance().getReference("cambiosDispositivos").child(cambioId);
            refCambios.setValue(datosCambio).addOnSuccessListener(unused -> {
                Toast.makeText(getContext(), "Cambio programado correctamente", Toast.LENGTH_SHORT).show();
                programarAlarma(timestamp, cambioId);
                dismiss();
            }).addOnFailureListener(e -> {
                Toast.makeText(getContext(), "Error al guardar cambio", Toast.LENGTH_SHORT).show();
            });

        } catch (Exception e) {
            Toast.makeText(getContext(), "Error en formato de fecha/hora", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void programarAlarma(long timestamp, String idCambio) {
        Intent intent = new Intent(requireContext(), EjecutarCambioReceiver.class);
        intent.putExtra("idCambio", idCambio);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                requireContext(),
                idCambio.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(getContext().ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    timestamp,
                    pendingIntent
            );
        }
    }
}
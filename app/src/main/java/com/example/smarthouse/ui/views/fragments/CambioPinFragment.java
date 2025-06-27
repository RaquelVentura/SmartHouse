package com.example.smarthouse.ui.views.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.example.smarthouse.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CambioPinFragment extends DialogFragment {

    private TextInputEditText edtPinActual, edtNuevoPin, edtConfirmarNuevoPin;
    private Button btnActualizarPin;

    public CambioPinFragment() {}

    public static CambioPinFragment newInstance(String param1, String param2) {
        return new CambioPinFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_cambio_pin, container, false);

        edtPinActual = view.findViewById(R.id.edtPinActual);
        edtNuevoPin = view.findViewById(R.id.edtNuevoPin);
        edtConfirmarNuevoPin = view.findViewById(R.id.edtConfirmarNuevoPin);
        btnActualizarPin = view.findViewById(R.id.button2);

        btnActualizarPin.setOnClickListener(v -> validarYActualizarPin());

        return view;
    }

    private void validarYActualizarPin() {
        DatabaseReference pinRef = FirebaseDatabase.getInstance().getReference("pinAcceso");

        String pinActual = edtPinActual.getText() != null ? edtPinActual.getText().toString().trim() : "";
        String nuevoPin = edtNuevoPin.getText() != null ? edtNuevoPin.getText().toString().trim() : "";
        String confirmarPin = edtConfirmarNuevoPin.getText() != null ? edtConfirmarNuevoPin.getText().toString().trim() : "";

        if (TextUtils.isEmpty(pinActual) || TextUtils.isEmpty(nuevoPin) || TextUtils.isEmpty(confirmarPin)) {
            Toast.makeText(getContext(), "Por favor complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!nuevoPin.equals(confirmarPin)) {
            Toast.makeText(getContext(), "El nuevo PIN y la confirmación no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }

        if (pinActual.equals(nuevoPin)) {
            Toast.makeText(getContext(), "El nuevo PIN no puede ser igual al actual", Toast.LENGTH_SHORT).show();
            return;
        }

        pinRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                Object value = task.getResult().getValue();

                if (value == null) {
                    Toast.makeText(getContext(), "Error: PIN no encontrado en base de datos", Toast.LENGTH_SHORT).show();
                    return;
                }

                String pinEnBase = String.valueOf(value);

                if (pinEnBase.equals(pinActual)) {
                    // Actualiza el PIN
                    pinRef.setValue(nuevoPin)
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(getContext(), "PIN actualizado correctamente", Toast.LENGTH_LONG).show();
                                edtPinActual.setText("");
                                edtNuevoPin.setText("");
                                edtConfirmarNuevoPin.setText("");
                                dismiss();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getContext(), "Error al actualizar el PIN", Toast.LENGTH_SHORT).show();
                            });
                } else {
                    Toast.makeText(getContext(), "El PIN actual ingresado no es correcto", Toast.LENGTH_SHORT).show();
                }

            } else {
                Toast.makeText(getContext(), "Error al acceder a la base de datos", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

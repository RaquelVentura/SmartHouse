package com.example.smarthouse.ui.views.fragments;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.smarthouse.R;
import com.example.smarthouse.data.models.DHT11;
import com.example.smarthouse.data.models.MQ2;
import com.example.smarthouse.data.models.UnidadDeSalida;
import com.example.smarthouse.databinding.FragmentDispositivosBinding;
import com.example.smarthouse.ui.adapters.adaptadorDHT11;
import com.example.smarthouse.ui.adapters.adaptadorLuces;
import com.example.smarthouse.ui.adapters.adaptadorSensorGas;
import com.example.smarthouse.ui.adapters.adaptadorServo;
import com.example.smarthouse.ui.adapters.adapterSensorLamina;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DispositivosFragment extends Fragment {
    private FragmentDispositivosBinding binding;
    private Button btnModoSeguro;
    private Switch switchLuces;
    private List<UnidadDeSalida> todasLasUnidades = new ArrayList<>();
    private List<DHT11> listDHT11 = new ArrayList<>();
    private List<MQ2> listaMQ2 = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDispositivosBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        switchLuces = binding.switchLuces;
        btnModoSeguro = binding.btnCerrarCasa;
        btnModoSeguro.setCompoundDrawablesWithIntrinsicBounds(
                ContextCompat.getDrawable(getContext(), R.drawable.ic_security),
                null, null, null);
        //verifica que los fragmentos no sean nulos
        if (binding != null && isAdded()) {
            binding.recyclerLeds.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            binding.recyclerServo.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            binding.recyclerDHT11.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            binding.recyclerSensorGas.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            binding.recyclerVentanas.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        }
        switchLuces.setOnClickListener(v -> {
            if (todasLasUnidades.isEmpty()) {
                Toast.makeText(getContext(), "Dispositivos aún no cargados", Toast.LENGTH_SHORT).show();
                return;
            }
                boolean nuevoEstado = switchLuces.isChecked();
                List<UnidadDeSalida> leds = new ArrayList<>();
                for (UnidadDeSalida unidad : todasLasUnidades) {
                    if ("LED".equalsIgnoreCase(unidad.getTipo())) {
                        leds.add(unidad);
                    }
                }


            if (leds.isEmpty()) {
                Toast.makeText(getContext(), "No hay luces para controlar", Toast.LENGTH_SHORT).show();
                return;
            }

            final int total = leds.size();
            final int[] actualizados = {0};
            for (UnidadDeSalida led : leds) {
                led.setEstado(nuevoEstado);
                FirebaseDatabase.getInstance()
                        .getReference("unidadesSalida")
                        .child(led.getId())
                        .child("estado")
                        .setValue(nuevoEstado)
                        .addOnCompleteListener(task -> {
                            actualizados[0]++;
                            if (actualizados[0] == total) {
                                Toast.makeText(getContext(), nuevoEstado ? "Luces encendidas" : "Luces apagadas", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        cargarDatosFirebase();

        DatabaseReference modoSeguroRef = FirebaseDatabase.getInstance().getReference("modoSeguro");
        modoSeguroRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!isAdded() || getContext() == null) return;

                Boolean estado = snapshot.getValue(Boolean.class);
                if (Boolean.TRUE.equals(estado)) {
                    btnModoSeguro.setText("DESACTIVAR MODO SEGURO");
                    btnModoSeguro.setBackgroundTintList(ColorStateList.valueOf(
                            ContextCompat.getColor(requireContext(), R.color.green_safe)));
                } else {
                    btnModoSeguro.setText("ACTIVAR MODO SEGURO");
                    btnModoSeguro.setBackgroundTintList(ColorStateList.valueOf(
                            ContextCompat.getColor(requireContext(), R.color.red_danger)));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (isAdded() && getContext() != null) {
                    Toast.makeText(getContext(), "Error al leer modo seguro", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return root;
    }

    private void cargarDatosFirebase() {
        //referencias de los nodos de la bd
        DatabaseReference refDHT11 = FirebaseDatabase.getInstance().getReference("DHT11");
        DatabaseReference refMQ2 = FirebaseDatabase.getInstance().getReference("mq2");
        DatabaseReference refUnidades = FirebaseDatabase.getInstance().getReference("unidadesSalida");

        refUnidades.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                todasLasUnidades.clear();
                for (DataSnapshot unidadSnapshot : snapshot.getChildren()) {
                    UnidadDeSalida unidad = unidadSnapshot.getValue(UnidadDeSalida.class);
                    if (unidad != null) {
                        unidad.setId(unidadSnapshot.getKey());
                        todasLasUnidades.add(unidad);
                    }
                }
                //verifica que los fragmentos no sean nulos y les asiga el adaptador correspondiente a cada recyclearView
                if (binding != null && isAdded()) {
                    binding.recyclerLeds.setAdapter(new adaptadorLuces(getContext(), todasLasUnidades));
                    binding.recyclerServo.setAdapter(new adaptadorServo(getContext(), todasLasUnidades));
                    binding.recyclerVentanas.setAdapter(new adapterSensorLamina(getContext(), todasLasUnidades));

                    verificarEstadoModoSeguro();

                    boolean todasEncendidas = true;
                    for (UnidadDeSalida unidad : todasLasUnidades) {
                        if ("LED".equalsIgnoreCase(unidad.getTipo()) && !unidad.getEstado()) {
                            todasEncendidas = false;
                            break;
                        }
                    }
                    switchLuces.setChecked(todasEncendidas);

                    btnModoSeguro.setOnClickListener(v -> modoSeguro());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error al cargar dispositivos", Toast.LENGTH_SHORT).show();
            }
        });

        refDHT11.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listDHT11.clear();
                for (DataSnapshot sensorSnapshot : snapshot.getChildren()) {
                    DHT11 dth11 = sensorSnapshot.getValue(DHT11.class);
                    if (dth11 != null) {
                        dth11.setId(sensorSnapshot.getKey());
                        listDHT11.add(dth11);
                    }
                }
                binding.recyclerDHT11.setAdapter(new adaptadorDHT11(getContext(), listDHT11));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error al cargar sensores", Toast.LENGTH_SHORT).show();
            }
        });

        refMQ2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaMQ2.clear();
                for (DataSnapshot gasSnapshot : snapshot.getChildren()) {
                    MQ2 sensorGas = gasSnapshot.getValue(MQ2.class);
                    if (sensorGas != null) {
                        sensorGas.setId(gasSnapshot.getKey());
                        listaMQ2.add(sensorGas);
                    }
                }
                if (binding != null && isAdded()) {
                    binding.recyclerSensorGas.setAdapter(new adaptadorSensorGas(listaMQ2, getContext()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error al cargar sensores de gas", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void verificarEstadoModoSeguro() {
        boolean todasCerradas = true;
        for (UnidadDeSalida unidad : todasLasUnidades) {
            if (("SERVO".equalsIgnoreCase(unidad.getTipo()) ||
                    "Lamina".equalsIgnoreCase(unidad.getTipo())) && unidad.getEstado()) {
                todasCerradas = false;
                break;
            }
        }
    }

    private void modoSeguro() {
        btnModoSeguro.setEnabled(false);
        btnModoSeguro.setText("PROCESANDO...");

        DatabaseReference modoSeguroRef = FirebaseDatabase.getInstance().getReference("modoSeguro");

        modoSeguroRef.get().addOnSuccessListener(snapshot -> {
            Boolean modoActual = snapshot.getValue(Boolean.class);

            if (Boolean.TRUE.equals(modoActual)) {
                modoSeguroRef.setValue(false)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(getContext(), "Modo seguro desactivado", Toast.LENGTH_SHORT).show();
                            btnModoSeguro.setEnabled(true);
                            btnModoSeguro.setText("ACTIVAR MODO SEGURO");
                            btnModoSeguro.setBackgroundTintList(ColorStateList.valueOf(
                                    ContextCompat.getColor(getContext(), R.color.red_danger)));
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(getContext(), "Error al desactivar modo seguro", Toast.LENGTH_SHORT).show();
                            btnModoSeguro.setEnabled(true);
                        });
            } else {
                activarModoSeguro();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(), "Error al verificar el estado de modo seguro", Toast.LENGTH_SHORT).show();
            btnModoSeguro.setEnabled(true);
        });
    }

    private void activarModoSeguro() {
        DatabaseReference modoSeguroRef = FirebaseDatabase.getInstance().getReference("modoSeguro");
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("unidadesSalida");

        final int[] totalDispositivos = {0};
        final int[] dispositivosActualizados = {0};

        for (UnidadDeSalida unidad : todasLasUnidades) {
            if ("SERVO".equalsIgnoreCase(unidad.getTipo()) || "Lamina".equalsIgnoreCase(unidad.getTipo())) {
                totalDispositivos[0]++;
            }
        }

        if (totalDispositivos[0] == 0) {
            Toast.makeText(getContext(), "No hay puertas/ventanas para cerrar", Toast.LENGTH_SHORT).show();
            btnModoSeguro.setEnabled(true);
            btnModoSeguro.setText("ACTIVAR MODO SEGURO");
            return;
        }

        for (UnidadDeSalida unidad : todasLasUnidades) {
            if ("SERVO".equalsIgnoreCase(unidad.getTipo()) || "Lamina".equalsIgnoreCase(unidad.getTipo())) {
                dbRef.child(unidad.getId()).child("estado").setValue(false)
                        .addOnSuccessListener(aVoid -> {
                            dispositivosActualizados[0]++;
                            if (dispositivosActualizados[0] == totalDispositivos[0]) {
                                modoSeguroRef.setValue(true)
                                        .addOnSuccessListener(aVoid1 -> {
                                            Toast.makeText(getContext(),
                                                    "Modo seguro activado por " +
                                                            (FirebaseAuth.getInstance().getCurrentUser() != null ?
                                                                    FirebaseAuth.getInstance().getCurrentUser().getEmail() : "Sistema"),
                                                    Toast.LENGTH_LONG).show();
                                            btnModoSeguro.setEnabled(true);
                                            if (isAdded() && getContext() != null) {
                                                btnModoSeguro.setText("DESACTIVAR MODO SEGURO");
                                                btnModoSeguro.setBackgroundTintList(ColorStateList.valueOf(
                                                        ContextCompat.getColor(getContext(), R.color.green_safe)));
                                            }

                                        });
                            }
                        })
                        .addOnFailureListener(e -> {
                            dispositivosActualizados[0]++;
                            Toast.makeText(getContext(),
                                    "Error al cerrar " + unidad.getUbicacion(),
                                    Toast.LENGTH_SHORT).show();
                            if (dispositivosActualizados[0] == totalDispositivos[0]) {
                                btnModoSeguro.setEnabled(true);
                                btnModoSeguro.setText("ACTIVAR MODO SEGURO");
                            }
                        });
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

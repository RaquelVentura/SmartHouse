package com.example.smarthouse.ui.views.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.smarthouse.data.models.Usuario;
import com.example.smarthouse.databinding.FragmentHomeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        String urlGif = "https://media3.giphy.com/media/v1.Y2lkPTc5MGI3NjExbXVrNHhrNDh6NmlyNTI5M3pwYWRyMm80bzkwc2V5eXh3emd3eHo2dCZlcD12MV9pbnRlcm5hbF9naWZfYnlfaWQmY3Q9Zw/W1fFapmqgqEf8RJ9TQ/giphy.gif";
        String gifSmartHome = "https://media4.giphy.com/media/v1.Y2lkPTc5MGI3NjExc2Jpb2MydGNhenZseTdvemhxbXJuMDJhanAxZHBkaDVtbWRxdjRhYSZlcD12MV9pbnRlcm5hbF9naWZfYnlfaWQmY3Q9Zw/gy6fVEyv1sJpvJSBI8/giphy.gif";

        Glide.with(this)
                .asGif()
                .load(urlGif)
                .into(binding.imgGif);

        Glide.with(this)
                .asGif()
                .load(gifSmartHome)
                .into(binding.imgSmartHome);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            String uid = firebaseUser.getUid();
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usuarios").child(uid);

            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Usuario usuario = snapshot.getValue(Usuario.class);
                    if (usuario != null && usuario.getNombreCompleto() != null) {
                        String saludo = "Hola, " + usuario.getNombreCompleto() + " Bienvenido de nuevo";
                        binding.textSaludo.setText(saludo);
                    } else {
                        binding.textSaludo.setText("Hola, bienvenida de nuevo");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    binding.textSaludo.setText("Hola, bienvenida de nuevo");
                }
            });
        } else {
            binding.textSaludo.setText("Hola, bienvenida de nuevo");
        }

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

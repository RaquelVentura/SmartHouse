package com.example.smarthouse.ui.views.activities;

import static com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL;

import android.content.Intent;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.credentials.Credential;
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.CustomCredential;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.GetCredentialException;

import com.example.smarthouse.R;
import com.example.smarthouse.data.models.Usuario;
import com.example.smarthouse.data.repositories.UsuarioRepoImplement;
import com.example.smarthouse.data.repositories.UsuarioRepositorio;
import com.example.smarthouse.data.services.UsuarioSerImplement;
import com.example.smarthouse.data.services.UsuarioServicio;
import com.example.smarthouse.ui.utils.UsuarioSesion;
import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {
    private EditText edtCorreoEmpleado,edtPassword;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private CredentialManager credentialManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        asociarElementosXml();

        credentialManager = CredentialManager.create(getBaseContext());

        crearAdministradorPorDefecto();
    }

    private void asociarElementosXml() {
        edtCorreoEmpleado = findViewById(R.id.edtCorreoEmpleado);
        edtPassword = findViewById(R.id.edtPassword);
    }

    private void crearAdministradorPorDefecto() {
        String email = "vc21033@ues.edu.sv", password = "12345678";
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(tarea -> {
            if (!tarea.isSuccessful()) {
                auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(resultado -> {
                    FirebaseUser firebaseUser = auth.getCurrentUser();
                    firebaseUser.sendEmailVerification()
                            .addOnCompleteListener(t -> {
                                if (t.isSuccessful()) {
                                    String uid = firebaseUser.getUid();
                                    Usuario admin = new Usuario(
                                            uid,
                                            "Administrador del Sistema",
                                            "administrador",
                                            "vc21033@ues.edu.sv"
                                    );
                                    UsuarioRepositorio repositorio = new UsuarioRepoImplement();
                                    repositorio.crearUsuario(admin).addOnSuccessListener(aVoid -> {
                                        Toast.makeText(getApplicationContext(), "Administrador creado correctamente, se envió correo de verificación.", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(getApplicationContext(), MenuActivity.class));
                                        finish();
                                    });
                                } else {
                                    Toast.makeText(this, "No se pudo enviar verificación. Verifique el correo.", Toast.LENGTH_SHORT).show();
                                }
                            });
                });
            }
        });
    }

    public void login(View view) {
        String correo = edtCorreoEmpleado.getText().toString();
        String contrasenna = edtPassword.getText().toString();

        if (correo.isEmpty() || contrasenna.isEmpty()) {
            Toast.makeText(this, "Debe rellenar todos los campos del formulario", Toast.LENGTH_SHORT).show();
            return;
        }

        UsuarioServicio service = new UsuarioSerImplement();
        service.loginConCorreo(correo, contrasenna).addOnSuccessListener(aVoid -> {
            redirigirPorRol(UsuarioSesion.obtenerUsuario().getRol());
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        });
    }

    public void loginConGoogle(View view) {
        iniciarGoogleAuth();
    }

    private void iniciarGoogleAuth() {
        GetGoogleIdOption getGoogleIdOption = new GetGoogleIdOption
                .Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId("963422096390-usph82511t7k5p788qapbopvdvo2apu8.apps.googleusercontent.com")
                .build();

        GetCredentialRequest request = new GetCredentialRequest
                .Builder()
                .addCredentialOption(getGoogleIdOption)
                .build();

        credentialManager.getCredentialAsync(
                this,
                request,
                new CancellationSignal(),
                Executors.newSingleThreadExecutor(),
                new CredentialManagerCallback<>() {
                    @Override
                    public void onResult(GetCredentialResponse getCredentialResponse) {
                        handleSignIn(getCredentialResponse.getCredential());
                    }

                    @Override
                    public void onError(@NonNull GetCredentialException e) {
                        Log.e("Error: ", e.getMessage());
                    }
                }
        );
    }

    private void handleSignIn(Credential credential) {
        if (credential instanceof CustomCredential && credential.getType().equals(TYPE_GOOGLE_ID_TOKEN_CREDENTIAL)) {
            CustomCredential customCredential = (CustomCredential) credential;
            Bundle credentialData = customCredential.getData();
            GoogleIdTokenCredential googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credentialData);

            UsuarioServicio service = new UsuarioSerImplement();
            service.loginConGoogle(googleIdTokenCredential.getIdToken())
                    .addOnSuccessListener(v -> {
                        redirigirPorRol(UsuarioSesion.obtenerUsuario().getRol());
                    })
                    .addOnFailureListener(e -> {
                        if (e.getMessage().toLowerCase().contains("no está registrado")) {
                            pedirTokenDeRegistro(googleIdTokenCredential.getIdToken());
                        } else {
                            Toast.makeText(this, "Error al iniciar sesión con Google: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }

    private void pedirTokenDeRegistro(String idToken) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ingrese su token de acceso");

        final EditText input = new EditText(this);
        input.setHint("Ej: ABC12345");
        builder.setView(input);

        builder.setPositiveButton("Validar", (dialog, which) -> {
            String tokenIngresado = input.getText().toString().trim();
            if (tokenIngresado.isEmpty()) {
                Toast.makeText(this, "Debe ingresar el token", Toast.LENGTH_SHORT).show();
                return;
            }

            UsuarioServicio service = new UsuarioSerImplement();
            service.validarCuentaGoogleConToken(idToken, tokenIngresado)
                    .addOnSuccessListener(v -> {
                        redirigirPorRol(UsuarioSesion.obtenerUsuario().getRol());
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void redirigirPorRol(String rol) {
        Intent intent;
        switch (rol) {
            case "Administrador":
                intent = new Intent(this, MenuActivity.class);
                break;
            case "Residente":
                intent = new Intent(this, MenuActivity.class);
                break;
            default:
                Toast.makeText(this, "Rol no reconocido: " + rol, Toast.LENGTH_SHORT).show();
                return;
        }
        startActivity(intent);
        finish();
    }
}
package com.example.smarthouse.ui.views.dialogs;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.smarthouse.R;
import com.example.smarthouse.data.models.Usuario;
import com.example.smarthouse.data.repositories.UsuarioRepoImplement;
import com.example.smarthouse.data.repositories.UsuarioRepositorio;
import com.example.smarthouse.data.services.UsuarioSerImplement;
import com.example.smarthouse.data.services.UsuarioServicio;
import com.example.smarthouse.ui.views.activities.MenuActivity;
import com.example.smarthouse.ui.views.functions.IUsuariosListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.security.SecureRandom;


public class GestionUsuariosDialog extends DialogFragment {
    private TextView tvTituloGestionUsuarios;
    private ImageView imgvCerrarDialogo;
    private EditText etNombreUsuario, etCorreoUsuario, etPassword;
    private AutoCompleteTextView actvRoles;
    private CheckBox cbxCrearConGoogle;
    private TextInputLayout tilPassword;
    private Button btnGuardar;
    private boolean crearCuentaConGoogle = false;
    private UsuarioServicio usuarioServicio;
    private String rolUsuario = "__NULL__";
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private IUsuariosListener listener;
    private String idUsuario = "-1";
    private Usuario usuarioEditar = null;

    public GestionUsuariosDialog() {}

    public static GestionUsuariosDialog nuevaInstancia() {
        GestionUsuariosDialog dialog = new GestionUsuariosDialog();
        return dialog;
    }

    public void setListener(IUsuariosListener listener) {
        this.listener = listener;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            // Se define el tamaño del dialogo como el 90% del alto y ancho de la pantalla
            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.9);
            int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.9);
            getDialog().getWindow().setLayout(width, height);
            getDialog().getWindow().setGravity(Gravity.CENTER); // Asegura que esté centrado
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        // Hace el fondo del DialogFragment transparente
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_gestion_usuarios, container, false);

        asociarElementosXml(view);

        usuarioServicio = new UsuarioSerImplement();

        // Agrega los roles
        agregarRoles();

        // Obtiene el id del usuario que se desea editar (en caso que se desee hacerlo)
        if (!idUsuario.equals("-1")) {
            usuarioServicio.obtenerUsuarioPorId(idUsuario).addOnSuccessListener(usuario -> {
                usuarioEditar = usuario;
                tvTituloGestionUsuarios.setText("ACTUALIZAR INFORMACIÓN DE USUARIO");
                etNombreUsuario.setText(usuario.getNombreCompleto());
                etCorreoUsuario.setText(usuario.getCorreo());
                cbxCrearConGoogle.setVisibility(View.GONE);
                tilPassword.setVisibility(View.GONE);
            }).addOnFailureListener(e -> {
                Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        }

        // Si desea crear la cuenta con google y no con correo y contraseña
        cbxCrearConGoogle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                crearCuentaConGoogle = isChecked;

                if (isChecked) {
                    tilPassword.setVisibility(View.GONE);
                } else {
                    tilPassword.setVisibility(View.VISIBLE);
                }
            }
        });

        actvRoles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String seleccionado = parent.getItemAtPosition(position).toString();
                rolUsuario = seleccionado;
            }
        });

        // Evento de guardar o actualizar el registro
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ---> INICIO VALIDACIONES
                String nombre = etNombreUsuario.getText().toString();
                String correo = etCorreoUsuario.getText().toString();
                if (nombre.isEmpty()) {
                    Toast.makeText(getContext(), "El nombre es obligatorio.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (correo.isEmpty()) {
                    Toast.makeText(getContext(), "El correo es obligatorio.", Toast.LENGTH_SHORT).show();
                    return;
                }
                // ---> FIN VALIDACIONES

                // Almacena toda la informacion base del usuario
                Usuario usuario = new Usuario(
                        nombre,
                        rolUsuario,
                        correo
                );

                 // Caso en que se este editando un usuario
                if (usuarioEditar != null) {
                    usuarioEditar.setNombreCompleto(usuario.getNombreCompleto());
                    usuarioEditar.setCorreo(usuario.getCorreo());
                    usuarioEditar.setRol(rolUsuario);
                    usuarioServicio.actualizarUsuario(usuarioEditar).addOnSuccessListener(vid -> {
                        listener.actualizarListaUsuarios();
                        Toast.makeText(getContext(), "Usuario actualizado exitosamente.", Toast.LENGTH_SHORT).show();
                        dismiss();
                    }).addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "No se ha podido actualizar la información del usuario.", Toast.LENGTH_SHORT).show();
                        dismiss();
                    });
                } else {
                    if (!crearCuentaConGoogle) {
                        String password = etPassword.getText().toString();
                        if (password.isEmpty()) {
                            Toast.makeText(getContext(), "Si no usará la cuenta de Google debe crear una contraseña de acceso.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        // Crear cuenta con correo y contrasña
                        auth.signInWithEmailAndPassword(correo, password).addOnCompleteListener(tarea -> {
                            if (!tarea.isSuccessful()) {
                                auth.createUserWithEmailAndPassword(correo, password).addOnSuccessListener(resultado -> {
                                    FirebaseUser firebaseUser = auth.getCurrentUser();
                                    firebaseUser.sendEmailVerification()
                                            .addOnCompleteListener(t -> {
                                                if (t.isSuccessful()) {
                                                    String uid = firebaseUser.getUid();
                                                    usuario.setId(uid);
                                                    UsuarioRepositorio repositorio = new UsuarioRepoImplement();
                                                    repositorio.crearUsuario(usuario).addOnSuccessListener(aVoid -> {
                                                        listener.actualizarListaUsuarios();
                                                        Toast.makeText(getContext(), "Usuario creado correctamente, se envió correo de verificación.", Toast.LENGTH_SHORT).show();
                                                        dismiss();
                                                    });
                                                } else {
                                                    Toast.makeText(getContext(), "No se pudo enviar verificación. Verifique el correo.", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                });
                            }
                        });
                    } else {
                        String token = generarTokenUnico();
                        usuarioServicio.crearUsuarioPendiente(usuario, token).addOnSuccessListener(vid -> {
                            enviarCorreoConToken(correo, token);
                            listener.actualizarListaUsuarios();
                            Toast.makeText(getContext(), "Usuario creado exitosamente", Toast.LENGTH_SHORT).show();
                            dismiss();
                        }).addOnFailureListener(e -> {
                            Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        });
                    }
                }
            }
        });

        // Cerrar el dialogo
        imgvCerrarDialogo.setOnClickListener(vista -> dismiss());

        return view;
    }

    private void asociarElementosXml(View view) {
        tvTituloGestionUsuarios = view.findViewById(R.id.tvTituloGestionUsuarios);
        tilPassword = view.findViewById(R.id.tilPassword);
        imgvCerrarDialogo = view.findViewById(R.id.imgvCerrarDialogo);
        etNombreUsuario = view.findViewById(R.id.etNombreUsuario);
        etCorreoUsuario = view.findViewById(R.id.etCorreoUsuario);
        etPassword = view.findViewById(R.id.etPassword);
        actvRoles = view.findViewById(R.id.actvRoles);
        btnGuardar = view.findViewById(R.id.btnGuardar);
        cbxCrearConGoogle = view.findViewById(R.id.cbxCrearConGoogle);
    }

    private void agregarRoles() {
        String[] roles = {"Administrador", "Residente"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), R.layout.lista_roles, roles);
        actvRoles.setAdapter(adapter);
    }

    private String generarTokenUnico() {
        SecureRandom random = new SecureRandom();
        String letras = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            sb.append(letras.charAt(random.nextInt(letras.length())));
        }
        return sb.toString();
    }

    public void enviarCorreoConToken(String correoDestino, String token) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822"); // Formato de correo

        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{correoDestino});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Token de acceso a Smart House");
        intent.putExtra(Intent.EXTRA_TEXT, "Hola, se te ha registrado como usuario de Smart House.\n\nTu token de acceso es:\n\n" + token + "\n\nUtiliza este código para registrar tu cuenta con Google desde la aplicación.");

        try {
            startActivity(Intent.createChooser(intent, "Enviar correo con... \n\n[SELECCIONE LA APP DE EMAIL O COPIE EL TEXTO AL PORTA PAPELES]"));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getContext(), "No hay apps de correo instaladas.", Toast.LENGTH_SHORT).show();
        }
    }
}

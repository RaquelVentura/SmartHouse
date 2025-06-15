package com.example.smarthouse.ui.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarthouse.R;
import com.example.smarthouse.data.models.Usuario;
import com.example.smarthouse.ui.views.dialogs.GestionUsuariosDialog;
import com.example.smarthouse.ui.views.fragments.GestionUsuariosFragment;
import com.example.smarthouse.ui.views.functions.IUsuariosListener;

import java.util.List;

public class UsuarioAdaptador extends RecyclerView.Adapter<UsuarioAdaptador.UsuarioViewHolder> {
    private List<Usuario> lstUsuarios;
    private IUsuariosListener listener;
    private Context context;
    private FragmentManager fragmentManager;

    public UsuarioAdaptador(List<Usuario> lstUsuarios, IUsuariosListener listener, Context context, FragmentManager fragmentManager) {
        this.lstUsuarios = lstUsuarios;
        this.listener = listener;
        this.context = context;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public UsuarioAdaptador.UsuarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.usuario_item_lista, parent, false);
        return new UsuarioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsuarioAdaptador.UsuarioViewHolder holder, int position) {
        Usuario usuario = lstUsuarios.get(position);
        holder.imgItemUsuario.setImageResource(R.drawable.profile);
        holder.tvNombreUsuario.setText(usuario.getNombreCompleto());
        holder.tvCorreoUsuario.setText(usuario.getCorreo());
        holder.tvRolUsuario.setText(usuario.getRol());

        // Actualizar
        holder.btnActualizarUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GestionUsuariosDialog gestionUsuariosDialog = GestionUsuariosDialog.nuevaInstancia();
                gestionUsuariosDialog.setListener(listener);
                gestionUsuariosDialog.setIdUsuario(usuario.getId());
                gestionUsuariosDialog.show(fragmentManager, "gestion_usuarios");
            }
        });

        // Eliminar
        holder.btnEliminarUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return lstUsuarios.size();
    }

    public class UsuarioViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgItemUsuario;
        private TextView tvNombreUsuario, tvCorreoUsuario, tvRolUsuario;
        private ImageButton btnActualizarUsuario, btnEliminarUsuario;
        public UsuarioViewHolder(@NonNull View itemView) {
            super(itemView);
            asociarElementosXml(itemView);
        }
        private void asociarElementosXml(View view) {
            imgItemUsuario = view.findViewById(R.id.imgItemUsuario);
            tvNombreUsuario = view.findViewById(R.id.tvNombreUsuario);
            tvCorreoUsuario = view.findViewById(R.id.tvCorreoUsuario);
            tvRolUsuario = view.findViewById(R.id.tvRolUsuario);
            btnActualizarUsuario = view.findViewById(R.id.btnActualizarUsuario);
            btnEliminarUsuario = view.findViewById(R.id.btnEliminarUsuario);
        }
    }
}

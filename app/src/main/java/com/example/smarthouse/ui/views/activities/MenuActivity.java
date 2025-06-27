package com.example.smarthouse.ui.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.widget.Toast;

import com.example.smarthouse.R;
import com.example.smarthouse.data.helpers.CambiosDispositivosHelper;
import com.example.smarthouse.data.models.Usuario;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.example.smarthouse.ui.utils.UsuarioSesion;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smarthouse.databinding.ActivityMenuBinding;

public class MenuActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMenuBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMenuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMenu.toolbar);

        final DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        Usuario usuario = UsuarioSesion.obtenerUsuario();
        if (usuario != null) {
            String rolUsuario = usuario.getRol();
            if (!"Administrador".equalsIgnoreCase(rolUsuario)) {
                Menu navMenu = navigationView.getMenu();
                navMenu.findItem(R.id.nav_gestion_usuarios).setVisible(false);
                navMenu.findItem(R.id.nav_reportes).setVisible(false);
                navMenu.findItem(R.id.nav_historial).setVisible(false);
            }
        }

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_dispositivos, R.id.nav_gestion_usuarios, R.id.nav_reportes, R.id.nav_historial,R.id.nav_configuracion)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_menu);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.btn_salir) {
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
                return true;
            }
            boolean handled = NavigationUI.onNavDestinationSelected(item, navController);
            if (handled) {
                drawer.closeDrawers();
            }
            return handled;
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_menu);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
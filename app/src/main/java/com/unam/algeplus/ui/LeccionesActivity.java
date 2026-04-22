package com.unam.algeplus.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.unam.algeplus.R;
import com.unam.algeplus.adapter.LeccionesAdapter;
import com.unam.algeplus.model.Leccion;
import com.unam.algeplus.viewmodel.LeccionesViewModel;

/**
 * Pantalla "Lista de Lecciones".
 *
 * Patrón Infinite List: RecyclerView con todas las lecciones disponibles.
 * Patrón List Inlay: cada ítem muestra una breve descripción de la lección.
 * Patrón Escape Hatch: toolbar con flecha de regreso al menú principal.
 */
public class LeccionesActivity extends AppCompatActivity {

    public static final String EXTRA_LECCION_ID = "extra_leccion_id";
    public static final String EXTRA_USERNAME    = "extra_username";
    public static final String EXTRA_MODO        = "extra_modo";

    private String username;
    private String modo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecciones);

        username = getIntent().getStringExtra(MainActivity.EXTRA_USERNAME);
        modo     = getIntent().getStringExtra(MainActivity.EXTRA_MODO);
        if (username == null) username = "Usuario";
        if (modo == null) modo = MainActivity.MODO_REPASO;

        // ── Toolbar (Escape Hatch) ──────────────────────────────────────────
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.titulo_lecciones);
        }

        // ── Encabezado con usuario ──────────────────────────────────────────
        TextView tvGreeting = findViewById(R.id.tvGreeting);
        tvGreeting.setText(getString(R.string.saludo_usuario, username));

        // ── RecyclerView (Infinite List + List Inlay) ───────────────────────
        RecyclerView rvLecciones = findViewById(R.id.rvLecciones);
        rvLecciones.setLayoutManager(new LinearLayoutManager(this));

        LeccionesViewModel viewModel = new ViewModelProvider(this).get(LeccionesViewModel.class);
        LeccionesAdapter adapter = new LeccionesAdapter(leccion -> abrirEjercicio(leccion));
        rvLecciones.setAdapter(adapter);

        viewModel.getLecciones().observe(this, adapter::submitList);
    }

    private void abrirEjercicio(Leccion leccion) {
        Intent intent = new Intent(this, EjercicioActivity.class);
        intent.putExtra(EXTRA_LECCION_ID, leccion.getId());
        intent.putExtra(EXTRA_USERNAME, username);
        intent.putExtra(EXTRA_MODO, modo);
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}

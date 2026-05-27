package com.unam.algeplus.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.unam.algeplus.R;
import com.unam.algeplus.adapter.LeccionesAdapter;
import com.unam.algeplus.database.ProgresoLeccionRepository;
import com.unam.algeplus.model.Leccion;
import com.unam.algeplus.viewmodel.LeccionesViewModel;

/**
 * Clase LeccionesActivity.
 * Actividad que despliega la lista de lecciones disponibles para el usuario.
 * Se encarga de mostrar el puntaje global e instanciar el RecyclerView correspondiente.
 */
public class LeccionesActivity extends AppCompatActivity {

    public static final String EXTRA_LECCION_ID = "extra_leccion_id";
    public static final String EXTRA_USERNAME = "extra_username";
    public static final String EXTRA_MODO = "extra_modo";

    private String username;
    private String modo;

    /**
     * Método onCreate.
     * Inicializa la interfaz de usuario, inyecta el ViewModel de las lecciones y arranca el
     * repositorio para observar la persistencia de puntajes locales.
     *
     * @param savedInstanceState Estado de la instancia previamente guardada.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecciones);

        username = getIntent().getStringExtra(MainActivity.EXTRA_USERNAME);
        modo = getIntent().getStringExtra(MainActivity.EXTRA_MODO);
        if (username == null || username.trim().isEmpty()) username = "Usuario";
        if (modo == null) modo = MainActivity.MODO_REPASO;

        ImageButton btnBack = findViewById(R.id.btnBack);
        TextView tvUsername = findViewById(R.id.tvUsername);
        TextView tvScore = findViewById(R.id.tvScore);
        TextView tvGreeting = findViewById(R.id.tvGreeting);

        btnBack.setOnClickListener(v -> finish());
        tvUsername.setText(username);
        tvGreeting.setText(getString(R.string.saludo_usuario, username));

        RecyclerView rvLecciones = findViewById(R.id.rvLecciones);
        rvLecciones.setLayoutManager(new LinearLayoutManager(this));

        LeccionesViewModel viewModel = new ViewModelProvider(this).get(LeccionesViewModel.class);
        LeccionesAdapter adapter = new LeccionesAdapter(this::abrirEjercicio);
        rvLecciones.setAdapter(adapter);
        viewModel.getLecciones().observe(this, adapter::submitList);

        ProgresoLeccionRepository progresoRepository = new ProgresoLeccionRepository(getApplication());
        progresoRepository.observarPuntajeTotal(username).observe(this, total -> {
            int puntos = total == null ? 0 : total;
            tvScore.setText(getString(R.string.puntos_formato, puntos));
        });
        progresoRepository.observarPorUsuario(username).observe(this, adapter::setProgreso);
    }

    /**
     * Método abrirEjercicio.
     * Lanza la pantalla de ejercicios transfiriendo los parámetros necesarios de la lección seleccionada.
     *
     * @param leccion El objeto Leccion al que el usuario desea acceder.
     */
    private void abrirEjercicio(Leccion leccion) {
        Intent intent = new Intent(this, EjercicioActivity.class);
        intent.putExtra(EXTRA_LECCION_ID, leccion.getId());
        intent.putExtra(EXTRA_USERNAME, username);
        intent.putExtra(EXTRA_MODO, modo);
        startActivity(intent);
    }
}

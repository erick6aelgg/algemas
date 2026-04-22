package com.unam.algeplus.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.unam.algeplus.R;

/**
 * Pantalla "Resultados".
 * Muestra el resumen de la lección completada.
 */
public class ResultadosActivity extends AppCompatActivity {

    public static final String EXTRA_SCORE          = "extra_score";
    public static final String EXTRA_LECCION_NOMBRE = "extra_leccion_nombre";
    public static final String EXTRA_LECCION_ID     = "extra_leccion_id";
    public static final String EXTRA_USERNAME       = "extra_username";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultados);

        int    score         = getIntent().getIntExtra(EXTRA_SCORE, 0);
        String leccionNombre = getIntent().getStringExtra(EXTRA_LECCION_NOMBRE);
        int    leccionId     = getIntent().getIntExtra(EXTRA_LECCION_ID, 1);
        String username      = getIntent().getStringExtra(EXTRA_USERNAME);
        if (leccionNombre == null) leccionNombre = "Lección";
        if (username == null) username = "Usuario";

        // ── Modo badge ──────────────────────────────────────────────────────
        TextView tvModo = findViewById(R.id.tvModo);
        tvModo.setText(getString(R.string.modo_repaso));

        // ── Nombre de lección ────────────────────────────────────────────────
        TextView tvLeccion = findViewById(R.id.tvLeccionNombre);
        tvLeccion.setText(leccionNombre);

        // ── Puntaje ──────────────────────────────────────────────────────────
        TextView tvScore = findViewById(R.id.tvScore);
        tvScore.setText(getString(R.string.puntos_formato, score));

        // ── Mensaje de calificación ──────────────────────────────────────────
        TextView tvMensaje = findViewById(R.id.tvMensaje);
        tvMensaje.setText(mensajeSegunPuntaje(score));

        // ── Botones ──────────────────────────────────────────────────────────
        Button btnVolverLecciones = findViewById(R.id.btnVolverLecciones);
        Button btnJugarNuevo      = findViewById(R.id.btnJugarNuevo);

        final String usernameFinal = username;
        btnVolverLecciones.setOnClickListener(v -> {
            Intent intent = new Intent(this, LeccionesActivity.class);
            intent.putExtra(MainActivity.EXTRA_USERNAME, usernameFinal);
            intent.putExtra(MainActivity.EXTRA_MODO, MainActivity.MODO_REPASO);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });

        final int leccionIdFinal = leccionId;
        btnJugarNuevo.setOnClickListener(v -> {
            Intent intent = new Intent(this, EjercicioActivity.class);
            intent.putExtra(EjercicioActivity.EXTRA_LECCION_ID, leccionIdFinal);
            intent.putExtra(EjercicioActivity.EXTRA_USERNAME, usernameFinal);
            startActivity(intent);
            finish();
        });
    }

    private String mensajeSegunPuntaje(int score) {
        if (score >= 30) return getString(R.string.resultado_excelente);
        if (score >= 20) return getString(R.string.resultado_bien);
        if (score >= 10) return getString(R.string.resultado_regular);
        return getString(R.string.resultado_practica);
    }
}

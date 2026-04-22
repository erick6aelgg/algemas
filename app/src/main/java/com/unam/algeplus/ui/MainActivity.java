package com.unam.algeplus.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.unam.algeplus.R;
import com.unam.algeplus.viewmodel.MenuViewModel;

/**
 * Pantalla "Menú Principal".
 *
 * Patrón Clear Entry Points: tres puntos de entrada claros —
 *   (1) campo de nombre, (2) botón Repaso, (3) botón Competencia (deshabilitado).
 * Patrón Input Prompt: campo de texto con hint "Nombre de usuario".
 */
public class MainActivity extends AppCompatActivity {

    private MenuViewModel viewModel;
    private EditText etUsername;

    // Extras de intent para pasar el nombre entre actividades
    public static final String EXTRA_USERNAME = "extra_username";
    public static final String EXTRA_MODO     = "extra_modo";
    public static final String MODO_REPASO    = "repaso";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewModel = new ViewModelProvider(this).get(MenuViewModel.class);

        etUsername = findViewById(R.id.etUsername);
        Button btnRepaso      = findViewById(R.id.btnRepaso);
        Button btnCompetencia = findViewById(R.id.btnCompetencia);
        TextView tvVersion    = findViewById(R.id.tvVersion);

        tvVersion.setText(getString(R.string.version));

        // ── Input Prompt: actualiza el ViewModel con cada cambio ────────────
        etUsername.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int a, int b, int c) {}
            @Override public void onTextChanged(CharSequence s, int a, int b, int c) {
                viewModel.setUsername(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // ── Modo Repaso ─────────────────────────────────────────────────────
        btnRepaso.setOnClickListener(v -> iniciarModo(MODO_REPASO));

        // ── Modo Competencia (deshabilitado en esta versión) ─────────────────
        btnCompetencia.setEnabled(false);
        btnCompetencia.setAlpha(0.45f);

        // ── Loading indicator: el botón de competencia muestra estado ───────
        // (En versiones futuras: mostraría un ProgressBar mientras conecta)
    }

    private void iniciarModo(String modo) {
        String username = etUsername.getText().toString().trim();
        if (username.isEmpty()) username = "Usuario";

        Intent intent = new Intent(this, LeccionesActivity.class);
        intent.putExtra(EXTRA_USERNAME, username);
        intent.putExtra(EXTRA_MODO, modo);
        startActivity(intent);
    }
}

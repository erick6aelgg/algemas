package com.unam.algeplus.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.unam.algeplus.R;
import com.unam.algeplus.viewmodel.MenuViewModel;

/**
 * Clase MainActivity
 * Representa la pantalla principal.
 *
 * Implementa los siguientes patrones de diseño.
 *
 * Patrón Clear Entry Points: tres puntos de entrada claros —
 *   (1) campo de nombre
 *   (2) botón Repaso
 *   (3) botón Competencia (deshabilitado).
 *
 * Patrón Input Prompt: campo de texto con hint "Nombre de usuario".
 *
 * @see LeccionesActivity
 * @see MenuViewModel
 *
 * @author Movilísticos - ICAT, UNAM
 * @version 1.0.1
 */
public class MainActivity extends AppCompatActivity {

    private MenuViewModel viewModel;
    private EditText etUsername;

    // Extras de intent para pasar el nombre entre actividades
    public static final String EXTRA_USERNAME = "extra_username";
    public static final String EXTRA_MODO     = "extra_modo";
    public static final String MODO_REPASO    = "repaso";

    /**
     * Método onCreate.
     * Inicializa la actividad principal, configura la interfaz de usuario,
     * los listeners de texto y el menú base de selección.
     *
     * @param savedInstanceState Estado de la instancia previamente guardada.
     */
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

    /**
     * Método iniciarModo.
     * Navega a la actividad de lecciones e inyecta el modo de juego seleccionado y el nombre de usuario.
     * @param modo modo de juego ("Practicar" o "Competencia")
     */
    private void iniciarModo(String modo) {
        String username = etUsername.getText().toString().trim();
        if (username.isEmpty()) username = "Usuario";

        Intent intent = new Intent(this, LeccionesActivity.class);
        intent.putExtra(EXTRA_USERNAME, username);
        intent.putExtra(EXTRA_MODO, modo);
        startActivity(intent);
    }

    /**
     * Método onWindowFocusChanged
     * Oculta las barras de sistema cuando la aplicación tiene el foco (Modo Inmersivo).
     *
     * @param hasFocus Indica si la ventana actual ha ganado o perdido foco.
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            WindowInsetsControllerCompat controller =
                    new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
            controller.hide(WindowInsetsCompat.Type.systemBars());
            controller.setSystemBarsBehavior(
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            );
        }
    }
}

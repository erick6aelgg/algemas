package com.unam.algeplus.ui;

import android.content.ClipData;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;

import com.unam.algeplus.R;
import com.unam.algeplus.data.LeccionesData;
import com.unam.algeplus.model.Ejercicio;
import com.unam.algeplus.model.Leccion;
import com.unam.algeplus.model.Paso;
import com.unam.algeplus.model.PasoToken;
import com.unam.algeplus.view.BalanzaView;
import com.unam.algeplus.viewmodel.EjercicioViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Pantalla "Muestra Ejercicio".
 *
 * Implementa:
 *   – Patrón Sequence Map: barra de círculos de progreso.
 *   – Patrón Prominent Done Button: botón "Verificar ✓" destacado.
 *   – Patrón Escape Hatch: flecha ← en la barra superior.
 *   – Patrón Modal Panel: overlays de felicitación y pista.
 *   – Drag & Drop: tokens de operadores arrastrables a drop zones.
 */
public class EjercicioActivity extends AppCompatActivity {

    // ── Extras de Intent ────────────────────────────────────────────────────
    public static final String EXTRA_LECCION_ID = "extra_leccion_id";
    public static final String EXTRA_USERNAME   = "extra_username";

    // ── Views ────────────────────────────────────────────────────────────────
    private TextView tvUsername, tvScore;
    private LinearLayout exerciseContainer, progressDots, tokenBoard;
    private ScrollView scrollExercise;
    private Button btnVerificar, btnPista;
    private View dimOverlay;
    private CardView cardTip, cardFelicitacion;
    private TextView tvTipMsg, tvFelicitacionMsg, tvPuntosGanados;
    private Button btnCerrarTip, btnAvanzar;

    // ── Estado ────────────────────────────────────────────────────────────────
    private EjercicioViewModel viewModel;
    private String username;

    // Mapas para rastrear blancos: (View → respuesta esperada) y (View → valor ingresado)
    private final Map<View, String> dropZoneExpected = new HashMap<>();
    private final Map<View, String> dropZoneFilled   = new HashMap<>();
    private final List<EditText> editTextsNumericos   = new ArrayList<>();

    private boolean primeraVez = true; // para mostrar el mensaje de +5 puntos al entrar

    // ── Colores de diseño ────────────────────────────────────────────────────
    private static final String COLOR_PRIMARY     = "#5C4DB1";
    private static final String COLOR_SURFACE      = "#E8E1FF";
    private static final String COLOR_TEXT         = "#1A1A2E";
    private static final String COLOR_HINT         = "#9E9CB5";
    private static final String COLOR_SUCCESS      = "#4CAF50";
    private static final String COLOR_DROP_IDLE    = "#E0DBFF";
    private static final String COLOR_DROP_ACTIVE  = "#BBAAFF";
    private static final String COLOR_DROP_FILLED  = "#C5F0C5";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ejercicio);

        username = getIntent().getStringExtra(EXTRA_USERNAME);
        int leccionId = getIntent().getIntExtra(EXTRA_LECCION_ID, 1);
        if (username == null) username = "Usuario";

        bindViews();
        setupViewModel(leccionId);
        setupTokenBoard();
        setupOverlayListeners();

        // Escape Hatch
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // Prominent Done Button
        btnVerificar.setOnClickListener(v -> verificarRespuesta());

        // Pista
        btnPista.setOnClickListener(v -> mostrarPista());

        // Observar cambios de puntaje
        viewModel.getScore().observe(this, pts ->
                tvScore.setText(getString(R.string.puntos_formato, pts)));

        // Observar fin de lección
        viewModel.getLeccionTerminada().observe(this, terminada -> {
            if (Boolean.TRUE.equals(terminada)) irAResultados();
        });
    }

    // ── Binding de vistas ────────────────────────────────────────────────────

    private void bindViews() {
        tvUsername        = findViewById(R.id.tvUsername);
        tvScore           = findViewById(R.id.tvScore);
        exerciseContainer = findViewById(R.id.exerciseContainer);
        progressDots      = findViewById(R.id.progressDots);
        scrollExercise    = findViewById(R.id.scrollExercise);
        tokenBoard        = findViewById(R.id.tokenBoard);
        btnVerificar      = findViewById(R.id.btnVerificar);
        btnPista          = findViewById(R.id.btnPista);
        dimOverlay        = findViewById(R.id.dimOverlay);
        cardTip           = findViewById(R.id.cardTip);
        cardFelicitacion  = findViewById(R.id.cardFelicitacion);
        tvTipMsg          = findViewById(R.id.tvTipMsg);
        tvFelicitacionMsg = findViewById(R.id.tvFelicitacionMsg);
        tvPuntosGanados   = findViewById(R.id.tvPuntosGanados);
        btnCerrarTip      = findViewById(R.id.btnCerrarTip);
        btnAvanzar        = findViewById(R.id.btnAvanzar);

        tvUsername.setText(username);
    }

    // ── ViewModel y primera carga ────────────────────────────────────────────

    private void setupViewModel(int leccionId) {
        viewModel = new ViewModelProvider(this).get(EjercicioViewModel.class);

        Leccion leccion = LeccionesData.getLecciones().stream()
                .filter(l -> l.getId() == leccionId)
                .findFirst()
                .orElse(LeccionesData.getLecciones().get(0));

        viewModel.iniciarLeccion(leccion);
        mostrarEjercicioActual(primeraVez);
        primeraVez = false;
    }

    // ── Renderizado del ejercicio ────────────────────────────────────────────

    private void mostrarEjercicioActual(boolean esInicio) {
        exerciseContainer.removeAllViews();
        dropZoneExpected.clear();
        dropZoneFilled.clear();
        editTextsNumericos.clear();

        Ejercicio ej = viewModel.getEjercicioActual();
        if (ej == null) return;

        // Si es tipo BALANZA, mostrar la vista de balanza antes de los pasos
        if (ej.getTipo() == Ejercicio.Tipo.BALANZA) {
            BalanzaView balanza = new BalanzaView(this);
            balanza.setEquation(ej.getLadoIzquierdo(), ej.getLadoDerecho());
            LinearLayout.LayoutParams balanzaParams =
                    new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(200));
            balanzaParams.setMargins(0, 0, 0, dp(12));
            exerciseContainer.addView(balanza, balanzaParams);
        }

        // Renderizar cada paso
        for (int i = 0; i < ej.getPasos().size(); i++) {
            Paso paso = ej.getPasos().get(i);
            LinearLayout fila = crearFilaPaso(paso, paso.esEncabezado());
            if (i == 0) {
                // Encabezado: fondo especial para destacar la ecuación
                fila.setBackgroundColor(Color.parseColor(COLOR_SURFACE));
                LinearLayout.LayoutParams params =
                        new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 0, 0, dp(8));
                fila.setLayoutParams(params);
            }
            exerciseContainer.addView(fila);
        }

        actualizarProgressDots();

        // Mensaje de bienvenida la primera vez
        if (esInicio) {
            Toast.makeText(this,
                    getString(R.string.mensaje_inicio_leccion),
                    Toast.LENGTH_LONG).show();
        }
    }

    private LinearLayout crearFilaPaso(Paso paso, boolean esEncabezado) {
        LinearLayout fila = new LinearLayout(this);
        fila.setOrientation(LinearLayout.HORIZONTAL);
        fila.setGravity(Gravity.CENTER_VERTICAL);
        fila.setPadding(dp(12), dp(10), dp(12), dp(10));

        float textSizeSp = esEncabezado ? 26f : 22f;

        for (PasoToken token : paso.getTokens()) {
            View v = crearVistaToken(token, esEncabezado, textSizeSp);
            if (v != null) {
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                lp.setMargins(0, 0, dp(2), 0);
                fila.addView(v, lp);
            }
        }
        return fila;
    }

    private View crearVistaToken(PasoToken token, boolean esEncabezado, float textSp) {
        switch (token.getTipo()) {
            case TEXTO:
                TextView tv = new TextView(this);
                tv.setText(token.getTexto());
                tv.setTextSize(textSp);
                tv.setTextColor(Color.parseColor(COLOR_TEXT));
                if (esEncabezado) tv.setTextColor(Color.parseColor(COLOR_PRIMARY));
                return tv;

            case BLANCO_OP:
                return crearDropZone(token.getRespuestaEsperada(), (int) textSp);

            case BLANCO_NUM:
                return crearEditTextNum(token.getRespuestaEsperada(), (int) textSp);

            default:
                return null;
        }
    }

    // ── Drop Zone para operadores (Drag & Drop) ──────────────────────────────

    private LinearLayout crearDropZone(String expected, int textSp) {
        LinearLayout dropZone = new LinearLayout(this);
        dropZone.setOrientation(LinearLayout.HORIZONTAL);
        dropZone.setGravity(Gravity.CENTER);
        dropZone.setMinimumWidth(dp(52));
        dropZone.setMinimumHeight(dp(44));
        dropZone.setPadding(dp(8), dp(4), dp(8), dp(4));
        aplicarFondoDropZone(dropZone, false);

        dropZoneExpected.put(dropZone, expected);

        dropZone.setOnDragListener((v, event) -> {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_ENTERED:
                    aplicarFondoDropZone((LinearLayout) v, true);
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    aplicarFondoDropZone((LinearLayout) v, dropZoneFilled.containsKey(v));
                    break;
                case DragEvent.ACTION_DROP:
                    String operator = event.getClipData().getItemAt(0).getText().toString();
                    dropZoneFilled.put(v, operator);
                    actualizarDropZoneUI((LinearLayout) v, operator, textSp);
                    return true;
                case DragEvent.ACTION_DRAG_ENDED:
                    break;
            }
            return true;
        });

        // Tap para limpiar si ya tiene valor
        dropZone.setOnClickListener(v -> {
            if (dropZoneFilled.containsKey(v)) {
                dropZoneFilled.remove(v);
                ((LinearLayout) v).removeAllViews();
                aplicarFondoDropZone((LinearLayout) v, false);
            }
        });

        return dropZone;
    }

    private void aplicarFondoDropZone(LinearLayout v, boolean activo) {
        String color = activo ? COLOR_DROP_ACTIVE : COLOR_DROP_IDLE;
        v.setBackgroundColor(Color.parseColor(color));
        // Borde simulado con padding y color de contorno
        v.setPadding(dp(2), dp(2), dp(2), dp(2));
    }

    private void actualizarDropZoneUI(LinearLayout dropZone, String operator, int textSp) {
        dropZone.removeAllViews();
        TextView tv = new TextView(this);
        tv.setText(operator);
        tv.setTextSize(textSp);
        tv.setTextColor(Color.parseColor(COLOR_PRIMARY));
        dropZone.addView(tv);
        dropZone.setBackgroundColor(Color.parseColor(COLOR_DROP_FILLED));
    }

    // ── EditText para números ────────────────────────────────────────────────

    private EditText crearEditTextNum(String expected, int textSp) {
        EditText et = new EditText(this);
        et.setHint("?");
        et.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
        et.setTextSize(textSp);
        et.setTextColor(Color.parseColor(COLOR_TEXT));
        et.setHintTextColor(Color.parseColor(COLOR_HINT));
        et.setWidth(dp(72));
        et.setGravity(Gravity.CENTER);
        et.setTag(expected);  // respuesta esperada guardada como Tag
        et.setBackground(getDrawable(R.drawable.bg_edit_num));
        editTextsNumericos.add(et);
        return et;
    }

    // ── Barra de progreso (Sequence Map) ────────────────────────────────────

    private void actualizarProgressDots() {
        progressDots.removeAllViews();
        int total   = viewModel.getTotalEjercicios();
        int actual  = viewModel.getIndiceActual();

        for (int i = 0; i < total; i++) {
            TextView dot = new TextView(this);
            dot.setTextSize(10f);
            dot.setGravity(Gravity.CENTER);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(dp(32), dp(32));
            lp.setMargins(dp(4), 0, dp(4), 0);
            dot.setLayoutParams(lp);

            if (i < actual) {
                // Ejercicio completado: palomita
                dot.setText("✓");
                dot.setTextColor(Color.WHITE);
                dot.setBackgroundColor(Color.parseColor(COLOR_SUCCESS));
            } else if (i == actual) {
                // Ejercicio actual: número resaltado
                dot.setText(String.valueOf(i + 1));
                dot.setTextColor(Color.WHITE);
                dot.setBackgroundColor(Color.parseColor(COLOR_PRIMARY));
            } else {
                // Ejercicio pendiente: número tenue
                dot.setText(String.valueOf(i + 1));
                dot.setTextColor(Color.parseColor(COLOR_HINT));
                dot.setBackgroundColor(Color.parseColor(COLOR_DROP_IDLE));
            }

            progressDots.addView(dot);
        }
    }

    // ── Token Board (drag source) ────────────────────────────────────────────

    private void setupTokenBoard() {
        String[] operators = {"+", "−", "×", "÷"};

        for (String op : operators) {
            TextView token = new TextView(this);
            token.setText(op);
            token.setTextSize(24f);
            token.setTextColor(Color.WHITE);
            token.setGravity(Gravity.CENTER);
            token.setBackgroundColor(Color.parseColor(COLOR_PRIMARY));

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(dp(56), dp(56));
            lp.setMargins(dp(6), 0, dp(6), 0);
            token.setLayoutParams(lp);

            token.setOnLongClickListener(v -> {
                ClipData data = ClipData.newPlainText("operator", op);
                View.DragShadowBuilder shadow = new View.DragShadowBuilder(v);
                v.startDragAndDrop(data, shadow, null, 0);
                return true;
            });

            // También aceptar tap corto (para accesibilidad)
            token.setOnClickListener(v -> {
                // Rellenar el primer drop zone vacío disponible
                for (Map.Entry<View, String> entry : dropZoneExpected.entrySet()) {
                    if (!dropZoneFilled.containsKey(entry.getKey())) {
                        dropZoneFilled.put(entry.getKey(), op);
                        actualizarDropZoneUI((LinearLayout) entry.getKey(), op, 22);
                        break;
                    }
                }
            });

            tokenBoard.addView(token);
        }
    }

    // ── Verificación de respuesta ────────────────────────────────────────────

    private void verificarRespuesta() {
        boolean correcto = true;

        // Verificar drop zones (operadores)
        for (Map.Entry<View, String> entry : dropZoneExpected.entrySet()) {
            String ingresado = dropZoneFilled.getOrDefault(entry.getKey(), "");
            if (!ingresado.equals(entry.getValue())) {
                correcto = false;
                break;
            }
        }

        // Verificar EditTexts (números)
        if (correcto) {
            for (EditText et : editTextsNumericos) {
                String esperado  = (String) et.getTag();
                String ingresado = et.getText().toString().trim();
                if (!ingresado.equals(esperado)) {
                    correcto = false;
                    break;
                }
            }
        }

        if (correcto) {
            viewModel.registrarAcierto();
            // Animar balanza si aplica
            animarBalanzaSiExiste();
            mostrarFelicitacion();
        } else {
            // Vibración leve de feedback
            btnVerificar.animate().translationX(10f).setDuration(80)
                    .withEndAction(() -> btnVerificar.animate().translationX(-10f).setDuration(80)
                            .withEndAction(() -> btnVerificar.animate().translationX(0f).setDuration(80).start())
                            .start())
                    .start();
            Toast.makeText(this, getString(R.string.respuesta_incorrecta), Toast.LENGTH_SHORT).show();
        }
    }

    // ── Modal Panel: Pista ───────────────────────────────────────────────────

    private void mostrarPista() {
        viewModel.usarPista();
        Ejercicio ej = viewModel.getEjercicioActual();
        if (ej == null) return;
        tvTipMsg.setText(ej.getTip());
        dimOverlay.setVisibility(View.VISIBLE);
        cardTip.setVisibility(View.VISIBLE);
        cardTip.animate().alpha(1f).scaleX(1f).scaleY(1f).setDuration(250).start();
    }

    private void cerrarPista() {
        cardTip.animate().alpha(0f).scaleX(0.9f).scaleY(0.9f).setDuration(200)
                .withEndAction(() -> {
                    cardTip.setVisibility(View.GONE);
                    dimOverlay.setVisibility(View.GONE);
                }).start();
    }

    // ── Modal Panel: Felicitación ────────────────────────────────────────────

    private void mostrarFelicitacion() {
        int indice = viewModel.getIndiceActual();
        int total  = viewModel.getTotalEjercicios();
        tvFelicitacionMsg.setText(indice + 1 < total
                ? getString(R.string.felicitacion_ejercicio)
                : getString(R.string.felicitacion_ultima));
        tvPuntosGanados.setText(getString(R.string.puntos_ganados, 5));

        dimOverlay.setVisibility(View.VISIBLE);
        cardFelicitacion.setAlpha(0f);
        cardFelicitacion.setScaleX(0.85f);
        cardFelicitacion.setScaleY(0.85f);
        cardFelicitacion.setVisibility(View.VISIBLE);
        cardFelicitacion.animate().alpha(1f).scaleX(1f).scaleY(1f)
                .setInterpolator(new OvershootInterpolator())
                .setDuration(300).start();
    }

    private void avanzarEjercicio() {
        cardFelicitacion.setVisibility(View.GONE);
        dimOverlay.setVisibility(View.GONE);
        viewModel.avanzarEjercicio();

        if (!Boolean.TRUE.equals(viewModel.getLeccionTerminada().getValue())) {
            mostrarEjercicioActual(false);
        }
    }

    // ── Animación de balanza ─────────────────────────────────────────────────

    private void animarBalanzaSiExiste() {
        // Busca el BalanzaView en el contenedor y lo anima a equilibrado
        for (int i = 0; i < exerciseContainer.getChildCount(); i++) {
            View child = exerciseContainer.getChildAt(i);
            if (child instanceof BalanzaView) {
                ((BalanzaView) child).animateToBalanced(null);
                break;
            }
        }
    }

    // ── Listeners de overlays ────────────────────────────────────────────────

    private void setupOverlayListeners() {
        btnCerrarTip.setOnClickListener(v -> cerrarPista());
        btnAvanzar.setOnClickListener(v -> avanzarEjercicio());
        dimOverlay.setOnClickListener(v -> { /* bloquear clicks bajo el overlay */ });
    }

    // ── Navegación a resultados ──────────────────────────────────────────────

    private void irAResultados() {
        Intent intent = new Intent(this, ResultadosActivity.class);
        intent.putExtra(ResultadosActivity.EXTRA_SCORE,
                viewModel.getPuntajeFinal());
        intent.putExtra(ResultadosActivity.EXTRA_LECCION_NOMBRE,
                viewModel.getLeccionActual().getNombre());
        intent.putExtra(ResultadosActivity.EXTRA_LECCION_ID,
                viewModel.getLeccionActual().getId());
        intent.putExtra(ResultadosActivity.EXTRA_USERNAME, username);
        // No volver al ejercicio desde resultados con Back
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    // ── Utilidad ─────────────────────────────────────────────────────────────

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density);
    }
}

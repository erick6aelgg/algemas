package com.unam.algeplus.ui;

import android.content.ClipData;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Vibrator;
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
import androidx.core.content.ContextCompat;
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
 * Clase EjercicioActivity
 * Representa a la pantalla "Muestra Ejercicio".
 *
 * Implementa los siguientes patrones:
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
    private TextView tvUsername, tvScore, tvInstrucciones;
    private LinearLayout exerciseContainer, progressDots, tokenBoard;
    private ScrollView scrollExercise;
    private Button btnVerificar, btnPista;
    private View dimOverlay;
    private CardView cardTip, cardFelicitacion, cardConfirmarSalida;
    private TextView tvTipMsg, tvFelicitacionMsg, tvPuntosGanados;
    private Button btnCerrarTip, btnAvanzar, btnContinuarLeccion, btnSalirLeccion;

    // ── Estado ────────────────────────────────────────────────────────────────
    private EjercicioViewModel viewModel;
    private String username;

    // Mapas para rastrear blancos: (View → respuesta esperada) y (View → valor ingresado)
    private final Map<View, String> dropZoneExpected = new HashMap<>();
    private final Map<View, String> dropZoneFilled   = new HashMap<>();
    private final List<EditText> editTextsNumericos   = new ArrayList<>();

    private boolean primeraVez = true; // para mostrar el mensaje de +5 puntos al entrar

    /**
     * Método onCreate.
     * Inicializa la actividad, recupera los extras del Intent y configura las vistas y observadores.
     *
     * @param savedInstanceState Estado previamente guardado de la actividad, si existe.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ejercicio);

        username = getIntent().getStringExtra(EXTRA_USERNAME);
        int leccionId = getIntent().getIntExtra(EXTRA_LECCION_ID, 1);
        if (username == null) username = "Usuario";

        bindViews();
        exerciseContainer.setGravity(Gravity.CENTER_HORIZONTAL);
        setupViewModel(leccionId);
        setupTokenBoard();
        setupOverlayListeners();

        // Escape Hatch
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> intentarSalir());

        // Prominent Done Button
        btnVerificar.setOnClickListener(v -> verificarRespuesta());

        // Pista
        Drawable bulb = ContextCompat.getDrawable(this, R.drawable.ic_lightbulb);
        int size = dp(20); // tamaño deseado en dp (por ejemplo 20dp)
        bulb.setBounds(0, 0, size, size);
        btnPista.setCompoundDrawables(bulb, null, null, null);
        btnPista.setCompoundDrawablePadding(dp(8));
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

    /**
     * Método bindViews.
     * Vincula las variables de la clase con lasvistas correspondientes del layoutXML.
     */
    private void bindViews() {
        tvUsername        = findViewById(R.id.tvUsername);
        tvScore           = findViewById(R.id.tvScore);
        tvInstrucciones   = findViewById(R.id.tvInstrucciones);
        exerciseContainer = findViewById(R.id.exerciseContainer);
        progressDots      = findViewById(R.id.progressDots);
        scrollExercise    = findViewById(R.id.scrollExercise);
        tokenBoard        = findViewById(R.id.tokenBoard);
        btnVerificar      = findViewById(R.id.btnVerificar);
        btnPista          = findViewById(R.id.btnPista);
        dimOverlay        = findViewById(R.id.dimOverlay);
        cardTip           = findViewById(R.id.cardTip);
        cardFelicitacion  = findViewById(R.id.cardFelicitacion);
        cardConfirmarSalida = findViewById(R.id.cardConfirmarSalida);
        tvTipMsg          = findViewById(R.id.tvTipMsg);
        tvFelicitacionMsg = findViewById(R.id.tvFelicitacionMsg);
        tvPuntosGanados   = findViewById(R.id.tvPuntosGanados);
        btnCerrarTip      = findViewById(R.id.btnCerrarTip);
        btnAvanzar        = findViewById(R.id.btnAvanzar);
        btnContinuarLeccion = findViewById(R.id.btnContinuarLeccion);
        btnSalirLeccion = findViewById(R.id.btnSalirLeccion);

        tvUsername.setText(username);
    }

    // ── ViewModel y primera carga ────────────────────────────────────────────

    /**
     * Método setupViewModel.
     * Configura el ViewModel de la actividad y carga la lección solicitada.
     *
     * @param leccionId Identificador de la lección a inicializar.
     */
    private void setupViewModel(int leccionId) {
        viewModel = new ViewModelProvider(this).get(EjercicioViewModel.class);

        Leccion leccion = LeccionesData.getLecciones().get(0);
        for (Leccion item : LeccionesData.getLecciones()) {
            if (item.getId() == leccionId) {
                leccion = item;
                break;
            }
        }

        btnVerificar.setEnabled(false);
        btnPista.setEnabled(false);

        viewModel.getLeccionInicializada().observe(this, inicializada -> {
            if (Boolean.TRUE.equals(inicializada)) {
                btnVerificar.setEnabled(true);
                btnPista.setEnabled(true);
                mostrarEjercicioActual(primeraVez);
                primeraVez = false;
            }
        });

        viewModel.iniciarLeccion(username, leccion);
    }

    // ── Renderizado del ejercicio ────────────────────────────────────────────

    /**
     * Método mostrarEjercicioActual.
     * Limpia el contenedor de ejercicios y renderiza el ejercicio actual.
     *
     * @param esInicio Indica si es el primer ejercicio de la lección para mostrar mensajes iniciales.
     */
    private void mostrarEjercicioActual(boolean esInicio) {
        exerciseContainer.removeAllViews();
        dropZoneExpected.clear();
        dropZoneFilled.clear();
        editTextsNumericos.clear();

        Ejercicio ej = viewModel.getEjercicioActual();
        if (ej == null) return;

        tvInstrucciones.setText(getString(R.string.instrucciones));
        // Si es tipo BALANZA, mostrar la vista de balanza antes de los pasos
        if (ej.getTipo() == Ejercicio.Tipo.BALANZA) {
            BalanzaView balanza = new BalanzaView(this);
            balanza.setEquation(ej.getLadoIzquierdo(), ej.getLadoDerecho());

            int balanza_size = getResources().getDimensionPixelSize(R.dimen.balanza_size);
            LinearLayout.LayoutParams balanzaParams =
                    new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, balanza_size);

            int margen = getResources().getDimensionPixelSize(R.dimen.balanza_margin);
            balanzaParams.setMargins(margen, margen, margen, margen);
            exerciseContainer.addView(balanza, balanzaParams);
        }

        // Renderizar cada paso
        for (int i = 0; i < ej.getPasos().size(); i++) {
            Paso paso = ej.getPasos().get(i);
            LinearLayout fila = crearFilaPaso(paso, paso.esEncabezado());
            if (i == 0) {
                // Encabezado: fondo especial para destacar la ecuación
                fila.setBackgroundColor(getResources().getColor(R.color.colorSurface));
                LinearLayout.LayoutParams params =
                        new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 0, 0, dp(8));
                fila.setLayoutParams(params);
            }

            LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            rowParams.gravity = Gravity.CENTER_HORIZONTAL;  // ← centra la fila horizontalmente
            fila.setLayoutParams(rowParams);

            exerciseContainer.addView(fila);
        }

        actualizarProgressDots();

        // Mensaje de bienvenida la primera vez
        if (esInicio && Boolean.TRUE.equals(viewModel.getMostrarBonoInicio().getValue())) {
            Toast.makeText(this,
                    getString(R.string.mensaje_inicio_leccion),
                    Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Método crearFilaPaso.
     * Crea un contenedor horizontal (fila) que agrupa los tokens de un paso específico.
     *
     * @param paso El paso que contiene la lista de tokens a renderizar.
     * @param esEncabezado Indica si la fila corresponde al encabezado del ejercicio.
     * @return LinearLayout configurado con las vistas de los tokens.
     */
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

    /**
     * Método crearVistaToken.
     * Crea la vista individual correspondiente a un token (texto, zona de arrastre o campo numérico).
     *
     * @param token El token a renderizar.
     * @param esEncabezado Indica si la vista pertenece a un encabezado.
     * @param textSp Tamaño del texto en unidades SP.
     * @return La vista configurada, o null si el tipo de token no es reconocido.
     */
    private View crearVistaToken(PasoToken token, boolean esEncabezado, float textSp) {
        switch (token.getTipo()) {
            case TEXTO:
                TextView tv = new TextView(this);
                tv.setText(token.getTexto());
                tv.setTextSize(textSp);
                tv.setTextColor((getResources().getColor(R.color.colorTextSecondary)));
                if (esEncabezado) tv.setTextColor((getResources().getColor(R.color.colorPrimary)));
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

    /**
     * Método crearDropZone.
     * Genera un área interactiva para soltar un operador matemático mediante drag and drop.
     *
     * @param expected El operador esperado para esta zona.
     * @param textSp Tamaño de texto en SP para mostrar el operador soltado.
     * @return Un LinearLayout configurado como drop zone.
     */
    private LinearLayout crearDropZone(String expected, int textSp) {
        LinearLayout dropZone = new LinearLayout(this);
        dropZone.setOrientation(LinearLayout.HORIZONTAL);
        dropZone.setGravity(Gravity.CENTER);

        // Obtener dimensiones desde recursos
        int size = getResources().getDimensionPixelSize(R.dimen.drop_zone_size);
        int padding = getResources().getDimensionPixelSize(R.dimen.drop_zone_padding);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
        dropZone.setLayoutParams(params);

        aplicarFondoDropZone(dropZone, false);

        dropZoneExpected.put(dropZone, expected);

        dropZone.post(() -> {
            ViewGroup.LayoutParams lp = dropZone.getLayoutParams();
            if (lp.width != size || lp.height != size) {
                lp.width = size;
                lp.height = size;
                dropZone.setLayoutParams(lp);
                dropZone.requestLayout();
            }
        });

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

    /**
     * Método aplicarFondoDropZone.
     * Aplica un color de fondo dinámico a la zona de arrastre dependiendo de su estado.
     *
     * @param v La zona de arrastre (Drop Zone).
     * @param activo True si se debe mostrar como activa, false en caso contrario.
     */
    private void aplicarFondoDropZone(LinearLayout v, boolean activo) {
        int colorRes = activo ? R.color.colorDropZoneActive : R.color.colorDropZone;
        int color = ContextCompat.getColor(this, colorRes);
        v.setBackground(getCircleBackground(color));
    }

    /**
     * Metodo actualizarDropZoneUI
     * Actualiza la interfaz visual de una zona de arrastre cuando recibe un operador.
     *
     * @param dropZone La vista receptora.
     * @param operator El texto del operador insertado.
     * @param textSp El tamaño del texto en SP.
     */
    private void actualizarDropZoneUI(LinearLayout dropZone, String operator, int textSp) {
        dropZone.removeAllViews();
        TextView tv = new TextView(this);
        tv.setText(operator);
        tv.setTextSize(textSp);
        tv.setTextColor((getResources().getColor(R.color.colorText)));
        dropZone.addView(tv);
        dropZone.setBackground(getCircleBackground(getResources().getColor(R.color.colorDropZoneFilled)));
    }

    // ── EditText para números ────────────────────────────────────────────────

    /**
     * Método crearEditTextNum.
     * Crea un campo de texto para que el usuario ingrese la respuesta numérica esperada.
     *
     * @param expected La respuesta correcta esperada.
     * @param textSp Tamaño de la fuente en unidades SP.
     * @return El EditText configurado para la entrada de números.
     */
    private EditText crearEditTextNum(String expected, int textSp) {
        EditText et = new EditText(this);
        et.setHint("?");
        et.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
        et.setTextSize(textSp);
        et.setTextColor((getResources().getColor(R.color.colorText)));
        et.setHintTextColor((getResources().getColor(R.color.colorHint)));
        et.setWidth(dp(72));
        et.setGravity(Gravity.CENTER);
        et.setTag(expected);  // respuesta esperada guardada como Tag
        et.setBackground(getDrawable(R.drawable.bg_edit_num));
        editTextsNumericos.add(et);
        return et;
    }

    // ── Barra de progreso (Sequence Map) ────────────────────────────────────

    /**
     * Método actualizarProgressDots.
     * Actualiza la barra superior de indicadores de progreso (Patrón Sequence Map).
     */
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
                dot.setBackgroundColor((getResources().getColor(R.color.colorSuccess)));
            } else if (i == actual) {
                // Ejercicio actual: número resaltado
                dot.setText(String.valueOf(i + 1));
                dot.setTextColor(Color.WHITE);
                dot.setBackgroundColor((getResources().getColor(R.color.colorPrimary)));
            } else {
                // Ejercicio pendiente: número tenue
                dot.setText(String.valueOf(i + 1));
                dot.setTextColor((getResources().getColor(R.color.colorHint)));
                dot.setBackgroundColor((getResources().getColor(R.color.colorDropZone)));
            }

            progressDots.addView(dot);
        }
    }

    // ── Token Board (drag source) ────────────────────────────────────────────

    /**
     * Método setupTokenBoard.
     * Configura el panel inferior de operadores matemáticos arrastrables.
     */
    private void setupTokenBoard() {
        String[] operators = {"+", "−", "×", "÷"};

        int tokenSize = getResources().getDimensionPixelSize(R.dimen.drop_zone_size);
        int tokenColor = getResources().getColor(R.color.colorPrimary);

        for (String op : operators) {
            TextView token = new TextView(this);
            token.setText(op);
            token.setTextSize(20f); // Un poco más grande para legibilidad
            token.setTextColor(Color.WHITE);
            token.setGravity(Gravity.CENTER);

            // Aplicar fondo circular
            token.setBackground(getCircleBackground(tokenColor));

            // LayoutParams estrictamente cuadrados
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(tokenSize, tokenSize);
            lp.setMargins(dp(8), 0, dp(8), 0);
            token.setLayoutParams(lp);

            token.setOnLongClickListener(v -> {
                ClipData data = ClipData.newPlainText("operator", op);
                View.DragShadowBuilder shadow = new View.DragShadowBuilder(v);
                v.startDragAndDrop(data, shadow, null, 0);
                return true;
            });

            token.setOnClickListener(v -> {
                for (Map.Entry<View, String> entry : dropZoneExpected.entrySet()) {
                    if (!dropZoneFilled.containsKey(entry.getKey())) {
                        String opText = ((TextView)v).getText().toString();
                        dropZoneFilled.put(entry.getKey(), opText);
                        actualizarDropZoneUI((LinearLayout) entry.getKey(), opText, 22);
                        break;
                    }
                }
            });

            tokenBoard.addView(token);
        }
    }
    /**
     * Método getCircleBackground.
     * Crea un recurso de fondo circular con un color específico.
     *
     * @param color El color a aplicar al fondo.
     * @return El Drawable del círculo generado.
     */
    private GradientDrawable getCircleBackground(int color) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.OVAL);
        drawable.setColor(color);
        return drawable;
    }

    // ── Verificación de respuesta ────────────────────────────────────────────

    /**
     * Método verificarRespuesta.
     * Crea un recurso de fondo circular con un color específico.
     *
     * @param color El color a aplicar al fondo.
     * @return El Drawable del círculo generado.
     */
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
            vibrar(250);
            viewModel.registrarAcierto();
            animarBalanzaSiExiste();
            mostrarFelicitacion();
        } else {
            // Vibración leve de feedback
            vibrar(75);
            btnVerificar.animate().translationX(10f).setDuration(80)
                    .withEndAction(() -> btnVerificar.animate().translationX(-10f).setDuration(80)
                            .withEndAction(() -> btnVerificar.animate().translationX(0f).setDuration(80).start())
                            .start())
                    .start();
            Toast.makeText(this, getString(R.string.respuesta_incorrecta), Toast.LENGTH_SHORT).show();
        }
    }

    // ── Modal Panel: Pista ───────────────────────────────────────────────────

    /**
     * Método mostrarPista
     * Despliega un panel modal que revela una sugerencia al usuario y reduce puntos en el ViewModel.
     */
    private void mostrarPista() {
        vibrar(75);
        viewModel.usarPista();
        Ejercicio ej = viewModel.getEjercicioActual();
        if (ej == null) return;
        tvTipMsg.setText(ej.getTip());
        dimOverlay.setVisibility(View.VISIBLE);
        cardTip.setVisibility(View.VISIBLE);
        cardTip.animate().alpha(1f).scaleX(1f).scaleY(1f).setDuration(250).start();
    }

    /**
     * Método cerrarPista.
     * Oculta el panel modal de la pista y restaura la visibilidad del ejercicio.
     */
    private void cerrarPista() {
        cardTip.animate().alpha(0f).scaleX(0.9f).scaleY(0.9f).setDuration(200)
                .withEndAction(() -> {
                    cardTip.setVisibility(View.GONE);
                    dimOverlay.setVisibility(View.GONE);
                }).start();
    }

    // ── Modal Panel: Felicitación ────────────────────────────────────────────

    /**
     * Método mostrarFelicitación.
     * Muestra el panel modal de éxito, oculta el teclado virtual y anuncia los puntos ganados.
     */
    private void mostrarFelicitacion() {

        View viewFocus = this.getCurrentFocus();
        if (viewFocus != null) {
            android.view.inputmethod.InputMethodManager imm =
                    (android.view.inputmethod.InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            if (imm != null) {
                // Usamos 0 en lugar de flags restrictivos para forzar el cierre inmediato
                imm.hideSoftInputFromWindow(viewFocus.getWindowToken(), 0);
            }
            viewFocus.clearFocus();
        }

        int indice = viewModel.getIndiceActual();
        int total  = viewModel.getTotalEjercicios();
        tvFelicitacionMsg.setText(indice + 1 < total
                ? getString(R.string.felicitacion_ejercicio)
                : getString(R.string.felicitacion_ultima));
        tvPuntosGanados.setText(getString(R.string.puntos_ganados,
                viewModel.getPuntosPorAciertoActual()));

        dimOverlay.setVisibility(View.VISIBLE);
        cardFelicitacion.setAlpha(0f);
        cardFelicitacion.setScaleX(0.85f);
        cardFelicitacion.setScaleY(0.85f);
        cardFelicitacion.setVisibility(View.VISIBLE);
        cardFelicitacion.animate().alpha(1f).scaleX(1f).scaleY(1f)
                .setInterpolator(new OvershootInterpolator())
                .setDuration(300).start();
    }

    /**
     * Método avanzarEjercicio.
     * Cierra el panel de felicitación y avanza la lógica hacia el siguiente ejercicio de la lección.
     */
    private void avanzarEjercicio() {
        cardFelicitacion.setVisibility(View.GONE);
        dimOverlay.setVisibility(View.GONE);
        viewModel.avanzarEjercicio();

        if (!Boolean.TRUE.equals(viewModel.getLeccionTerminada().getValue())) {
            mostrarEjercicioActual(false);
        }
    }

    // ── Animación de balanza ─────────────────────────────────────────────────

    /**
     * Método animarBalanzaSiExiste
     * Busca la vista de la balanza en el contenedor y ejecuta su animación de equilibrio si esta existe.
     */
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

    /**
     * Método setupOverlayListeneres.
     * Configura los oyentes de eventos (listeners) para los botones dentro de los diferentes overlays modales.
     */
    private void setupOverlayListeners() {
        btnCerrarTip.setOnClickListener(v -> cerrarPista());
        btnAvanzar.setOnClickListener(v -> avanzarEjercicio());
        btnContinuarLeccion.setOnClickListener(v -> cerrarConfirmacionSalida());
        btnSalirLeccion.setOnClickListener(v -> salirDescartandoProgreso());
        dimOverlay.setOnClickListener(v -> { /* bloquear clicks bajo el overlay */ });
    }

    /**
     * Método intentarSalir.
     * Evalúa el progreso actual para determinar si debe mostrarse un cuadro de diálogo de confirmación antes de salir.
     */
    private void intentarSalir() {
        if (viewModel != null && viewModel.requiereConfirmarSalida()) {
            mostrarConfirmacionSalida();
        } else {
            finish();
        }
    }

    /**
     * Método mostrarConfirmaciónSalida
     * Muestra una alerta para prevenir la pérdida de progreso al salir prematuramente de un ejercicio.
     */
    private void mostrarConfirmacionSalida() {
        dimOverlay.setVisibility(View.VISIBLE);
        cardConfirmarSalida.setAlpha(0f);
        cardConfirmarSalida.setScaleX(0.85f);
        cardConfirmarSalida.setScaleY(0.85f);
        cardConfirmarSalida.setVisibility(View.VISIBLE);
        cardConfirmarSalida.animate().alpha(1f).scaleX(1f).scaleY(1f)
                .setInterpolator(new OvershootInterpolator())
                .setDuration(260).start();
    }

    /**
     * Método cerrarConfirmacionSalida.
     * Oculta la alerta de confirmación de salida.
     */
    private void cerrarConfirmacionSalida() {
        cardConfirmarSalida.animate().alpha(0f).scaleX(0.9f).scaleY(0.9f).setDuration(180)
                .withEndAction(() -> {
                    cardConfirmarSalida.setVisibility(View.GONE);
                    dimOverlay.setVisibility(View.GONE);
                }).start();
    }

    /**
     * Método salirDescartandoProgreso.
     * Finaliza la actividad actual y borra el progreso incompleto.
     */
    private void salirDescartandoProgreso() {
        viewModel.descartarProgresoIncompleto();
        finish();
    }

    /**
     * Método onBackPressed.
     * Sobrescribe el comportamiento del botón físico de retroceso para solicitar confirmación si hay progreso en curso.
     */
    @Override
    public void onBackPressed() {
        intentarSalir();
    }

    // ── Navegación a resultados ──────────────────────────────────────────────

    /**
     * Método irAResultados
     * Calcula la calificación máxima posible y navega hacia la pantalla de resultados enviando los puntajes mediante el Intent.
     */
    private void irAResultados() {
        vibrar(new long[]{0, 400, 100, 400, 100, 400} );

        int ejercicios = viewModel.getTotalEjercicios();
        int maxScore = 5 +(5*ejercicios);

        Intent intent = new Intent(this, ResultadosActivity.class);
        intent.putExtra(ResultadosActivity.EXTRA_SCORE,
                viewModel.getPuntajeFinal());
        intent.putExtra(ResultadosActivity.EXTRA_MAX_SCORE, maxScore);
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

    /**
     * Convierte un valor dado en píxeles de densidad independiente (dp) a píxeles exactos (px).
     *
     * @param value El valor numérico en dp.
     * @return El tamaño equivalente en píxeles.
     */
    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density);
    }

    // Vibración

    /**
     * Activa la vibración utilizando durante un determinado número de milisegundos.
     *
     * @param milliseconds tiempo en milisegundos que definen la la duración de la vibración.
     */
    private void vibrar(int milliseconds) {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        if (vibrator != null && vibrator.hasVibrator()) {
            vibrator.vibrate(milliseconds);
        }
    }

    /**
     * Activa la vibración utilizando un patrón específico de tiempos de encendido y apagado.
     *
     * @param pattern Arreglo de tiempos tipo long en milisegundos que definen la cadencia.
     */
    private void vibrar(long[] pattern) {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        if (vibrator != null && vibrator.hasVibrator()) {
            vibrator.vibrate(pattern, -1);
        }
    }

}

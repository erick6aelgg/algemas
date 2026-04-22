package com.unam.algeplus.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.unam.algeplus.AlgePlusApp;
import com.unam.algeplus.database.EcuacionRepository;
import com.unam.algeplus.model.Ecuacion;
import com.unam.algeplus.model.Ejercicio;
import com.unam.algeplus.model.Leccion;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * ViewModel para EjercicioActivity.
 * Mantiene el estado de la sesión de ejercicios:
 * índice actual, puntaje y lógica de validación.
 */
public class EjercicioViewModel extends AndroidViewModel {

    private static final int PUNTOS_INICIO_LECCION = 5;
    private static final int PUNTOS_POR_ACIERTO    = 5;
    private static final int PUNTOS_POR_PISTA      = -1;

    private Leccion leccionActual;
    private List<Ejercicio> ejercicios;
    private int indiceActual = 0;

    private final MutableLiveData<Integer> score = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> indice = new MutableLiveData<>(0);
    private final MutableLiveData<Boolean> leccionTerminada = new MutableLiveData<>(false);

    private final EcuacionRepository repository;

    public EjercicioViewModel(@NonNull Application application) {
        super(application);
        repository = new EcuacionRepository(application);
    }

    // ── Inicialización ──────────────────────────────────────────────────────

    public void iniciarLeccion(Leccion leccion) {
        this.leccionActual = leccion;
        this.ejercicios = leccion.getEjercicios();
        this.indiceActual = 0;
        AlgePlusApp.getInstance().resetScore();
        // Se otorgan puntos por iniciar la lección
        AlgePlusApp.getInstance().addScore(PUNTOS_INICIO_LECCION);
        score.setValue(AlgePlusApp.getInstance().getScore());
        indice.setValue(0);
        leccionTerminada.setValue(false);
    }

    // ── Ejercicio actual ────────────────────────────────────────────────────

    public Ejercicio getEjercicioActual() {
        if (ejercicios == null || indiceActual >= ejercicios.size()) return null;
        return ejercicios.get(indiceActual);
    }

    public int getIndiceActual() { return indiceActual; }
    public int getTotalEjercicios() { return ejercicios != null ? ejercicios.size() : 0; }
    public Leccion getLeccionActual() { return leccionActual; }

    public MutableLiveData<Integer> getScore() { return score; }
    public MutableLiveData<Integer> getIndice() { return indice; }
    public MutableLiveData<Boolean> getLeccionTerminada() { return leccionTerminada; }

    // ── Lógica de puntuación ────────────────────────────────────────────────

    public void registrarAcierto() {
        AlgePlusApp.getInstance().addScore(PUNTOS_POR_ACIERTO);
        score.setValue(AlgePlusApp.getInstance().getScore());
        persistirEjercicio();
    }

    public void usarPista() {
        // La pista descuenta un punto (mínimo 0)
        int actual = AlgePlusApp.getInstance().getScore();
        if (actual > 0) {
            AlgePlusApp.getInstance().addScore(PUNTOS_POR_PISTA);
            score.setValue(AlgePlusApp.getInstance().getScore());
        }
    }

    // ── Navegación entre ejercicios ─────────────────────────────────────────

    public void avanzarEjercicio() {
        indiceActual++;
        if (indiceActual >= ejercicios.size()) {
            leccionTerminada.setValue(true);
        } else {
            indice.setValue(indiceActual);
        }
    }

    public int getPuntajeFinal() {
        return AlgePlusApp.getInstance().getScore();
    }

    // ── Persistencia ────────────────────────────────────────────────────────

    private void persistirEjercicio() {
        Ejercicio ej = getEjercicioActual();
        if (ej == null) return;
        String fecha = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                .format(new Date());
        // Extraer el valor de x de la última línea del procedimiento (sin streams para API 21+)
        String resultado = "?";
        List<com.unam.algeplus.model.PasoToken> ultimosTokens =
                ej.getPasos().get(ej.getPasos().size() - 1).getTokens();
        for (com.unam.algeplus.model.PasoToken tk : ultimosTokens) {
            if (tk.getTipo() == com.unam.algeplus.model.PasoToken.Tipo.BLANCO_NUM) {
                resultado = tk.getRespuestaEsperada();
                break;
            }
        }
        repository.insertar(new Ecuacion(ej.getEcuacion(), resultado, fecha));
    }
}

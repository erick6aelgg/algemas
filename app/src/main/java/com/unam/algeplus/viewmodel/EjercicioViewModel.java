package com.unam.algeplus.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.unam.algeplus.AlgePlusApp;
import com.unam.algeplus.database.EcuacionRepository;
import com.unam.algeplus.database.ProgresoLeccionRepository;
import com.unam.algeplus.model.Ecuacion;
import com.unam.algeplus.model.Ejercicio;
import com.unam.algeplus.model.Leccion;
import com.unam.algeplus.model.PasoToken;
import com.unam.algeplus.model.ProgresoLeccion;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EjercicioViewModel extends AndroidViewModel {

    private static final int PUNTOS_INICIO_LECCION = 5;
    private static final int PUNTOS_POR_ACIERTO = 5;
    private static final int PUNTOS_POR_PISTA = -1;
    private static final int PUNTOS_POR_REPETIR = 1;

    private String username = "Usuario";
    private Leccion leccionActual;
    private List<Ejercicio> ejercicios;
    private int indiceActual = 0;
    private boolean leccionCompletadaPreviamente = false;
    private boolean puntajeReducidoPorReintento = false;
    private int vecesCompletada = 0;
    private boolean completadaAlEntrar = false;
    private int puntajeAlEntrar = 0;
    private int vecesCompletadaAlEntrar = 0;

    private final MutableLiveData<Integer> score = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> indice = new MutableLiveData<>(0);
    private final MutableLiveData<Boolean> leccionTerminada = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> leccionInicializada = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> mostrarBonoInicio = new MutableLiveData<>(false);

    private final EcuacionRepository ecuacionRepository;
    private final ProgresoLeccionRepository progresoRepository;

    public EjercicioViewModel(@NonNull Application application) {
        super(application);
        ecuacionRepository = new EcuacionRepository(application);
        progresoRepository = new ProgresoLeccionRepository(application);
    }

    public void iniciarLeccion(String username, Leccion leccion) {
        this.username = normalizarUsername(username);
        this.leccionActual = leccion;
        this.ejercicios = leccion.getEjercicios();
        this.indiceActual = 0;
        this.leccionCompletadaPreviamente = false;
        this.puntajeReducidoPorReintento = false;
        this.vecesCompletada = 0;
        this.completadaAlEntrar = false;
        this.puntajeAlEntrar = 0;
        this.vecesCompletadaAlEntrar = 0;

        indice.setValue(0);
        leccionTerminada.setValue(false);
        leccionInicializada.setValue(false);
        mostrarBonoInicio.setValue(false);

        progresoRepository.obtener(this.username, leccion.getId(), progreso -> {
            if (progreso == null) {
                leccionCompletadaPreviamente = false;
                puntajeReducidoPorReintento = false;
                vecesCompletada = 0;
                completadaAlEntrar = false;
                puntajeAlEntrar = 0;
                vecesCompletadaAlEntrar = 0;
                AlgePlusApp.getInstance().setScore(PUNTOS_INICIO_LECCION);
                guardarProgreso(false);
                mostrarBonoInicio.postValue(true);
            } else {
                leccionCompletadaPreviamente = progreso.isCompletada();
                puntajeReducidoPorReintento = true;
                vecesCompletada = progreso.getVecesCompletada();
                completadaAlEntrar = progreso.isCompletada();
                puntajeAlEntrar = progreso.getPuntaje();
                vecesCompletadaAlEntrar = progreso.getVecesCompletada();
                AlgePlusApp.getInstance().setScore(progreso.getPuntaje());
                mostrarBonoInicio.postValue(false);
            }
            score.postValue(AlgePlusApp.getInstance().getScore());
            leccionInicializada.postValue(true);
        });
    }

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
    public MutableLiveData<Boolean> getLeccionInicializada() { return leccionInicializada; }
    public MutableLiveData<Boolean> getMostrarBonoInicio() { return mostrarBonoInicio; }

    public void registrarAcierto() {
        AlgePlusApp.getInstance().addScore(getPuntosPorAciertoActual());
        score.setValue(AlgePlusApp.getInstance().getScore());
        persistirEjercicio();
        guardarProgreso(false);
    }

    public void usarPista() {
        int actual = AlgePlusApp.getInstance().getScore();
        if (actual > 0) {
            AlgePlusApp.getInstance().addScore(PUNTOS_POR_PISTA);
            score.setValue(AlgePlusApp.getInstance().getScore());
            guardarProgreso(false);
        }
    }

    public int getPuntosPorAciertoActual() {
        return puntajeReducidoPorReintento ? PUNTOS_POR_REPETIR : PUNTOS_POR_ACIERTO;
    }

    public boolean requiereConfirmarSalida() {
        return !Boolean.TRUE.equals(leccionTerminada.getValue())
                && Boolean.TRUE.equals(leccionInicializada.getValue());
    }

    public void descartarProgresoIncompleto() {
        if (leccionActual == null) return;

        if (completadaAlEntrar) {
            ProgresoLeccion progresoRestaurado = new ProgresoLeccion(
                    username,
                    leccionActual.getId(),
                    puntajeAlEntrar,
                    true,
                    vecesCompletadaAlEntrar,
                    fechaActual()
            );
            progresoRepository.guardar(progresoRestaurado);
            AlgePlusApp.getInstance().setScore(puntajeAlEntrar);
        } else {
            progresoRepository.eliminar(username, leccionActual.getId());
            AlgePlusApp.getInstance().setScore(0);
        }
    }

    public void avanzarEjercicio() {
        indiceActual++;
        if (indiceActual >= ejercicios.size()) {
            marcarLeccionCompletada();
            leccionTerminada.setValue(true);
        } else {
            indice.setValue(indiceActual);
        }
    }

    public int getPuntajeFinal() {
        return AlgePlusApp.getInstance().getScore();
    }

    private void persistirEjercicio() {
        Ejercicio ej = getEjercicioActual();
        if (ej == null) return;

        String resultado = "?";
        List<PasoToken> ultimosTokens =
                ej.getPasos().get(ej.getPasos().size() - 1).getTokens();
        for (PasoToken tk : ultimosTokens) {
            if (tk.getTipo() == PasoToken.Tipo.BLANCO_NUM) {
                resultado = tk.getRespuestaEsperada();
                break;
            }
        }
        ecuacionRepository.insertar(new Ecuacion(ej.getEcuacion(), resultado, fechaActual()));
    }

    private void marcarLeccionCompletada() {
        if (!leccionCompletadaPreviamente) {
            vecesCompletada = 1;
        } else {
            vecesCompletada++;
        }
        leccionCompletadaPreviamente = true;
        puntajeReducidoPorReintento = true;
        guardarProgreso(true);
    }

    private void guardarProgreso(boolean completada) {
        if (leccionActual == null) return;
        ProgresoLeccion progreso = new ProgresoLeccion(
                username,
                leccionActual.getId(),
                AlgePlusApp.getInstance().getScore(),
                completada || leccionCompletadaPreviamente,
                vecesCompletada,
                fechaActual()
        );
        progresoRepository.guardar(progreso);
    }

    private String fechaActual() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                .format(new Date());
    }

    private String normalizarUsername(String username) {
        if (username == null) return "Usuario";
        String limpio = username.trim();
        return limpio.isEmpty() ? "Usuario" : limpio;
    }
}

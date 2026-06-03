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


/**
 * Clase EjercicioViewModel
 * ViewModel que gestiona el estado y la lógica de negocio de la pantalla de ejercicios.
 *
 * @author Movilísticos - ICAT, UNAM
 * @version 1.0.1
 */

public class EjercicioViewModel extends AndroidViewModel {

    /** Puntos otorgados al iniciar una lección por primera vez. */
    private static final int PUNTOS_INICIO_LECCION = 5;

    /** Puntos por cada ejercicio resuelto correctamente en la primera vez. */
    private static final int PUNTOS_POR_ACIERTO = 5;

    /** Descuento aplicado al puntaje cuando el alumno solicita una pista. */
    private static final int PUNTOS_POR_PISTA = -1;

    /** Puntos por ejercicio resuelto en un reintento (lección ya completada antes). */
    private static final int PUNTOS_POR_REPETIR = 1;

    // ─────────────────────────────────────────────────────────────────────────
    //  Estado de sesión
    // ─────────────────────────────────────────────────────────────────────────

    /** Nombre del usuario activo en la sesión. */
    private String username = "Usuario";

    /** Lección que se está ejecutando actualmente. */
    private Leccion leccionActual;

    /** Lista de ejercicios de la lección actual. */
    private List<Ejercicio> ejercicios;

    /** Índice del ejercicio en curso dentro de ejercicios. */
    private int indiceActual = 0;

    /** true} si el usuario ya había completado esta lección antes de entrar. */
    private boolean leccionCompletadaPreviamente = false;

    /**
     * true cuando el alumno reinicia una lección ya completada.
     * Activa la puntuación reducida.
     */
    private boolean puntajeReducidoPorReintento = false;

    /** Número de veces que el usuario ha completado la lección. */
    private int vecesCompletada = 0;

    /** Estado de la lección al entrar. */
    private boolean completadaAlEntrar = false;

    /** Puntaje al entrar a la lección. */
    private int puntajeAlEntrar = 0;

    /** Veces completada al entrar. */
    private int vecesCompletadaAlEntrar = 0;

    // ─────────────────────────────────────────────────────────────────────────
    //  LiveData observables por la Activity
    // ─────────────────────────────────────────────────────────────────────────

    /** Puntaje actual del usuario, actualizado en cada acierto, pista y al iniciar. */
    private final MutableLiveData<Integer> score = new MutableLiveData<>(0);

    /** Índice del ejercicio actual. */
    private final MutableLiveData<Integer> indice = new MutableLiveData<>(0);

    /** true cuando se termina el último ejercicio de la lección. */
    private final MutableLiveData<Boolean> leccionTerminada = new MutableLiveData<>(false);

    /**
     * true cuando el progreso previo se ha cargado y la lección está lista para comenzar.
     * Activity espera este evento antes de habilitar los botones interactivos.
     */
    private final MutableLiveData<Boolean> leccionInicializada = new MutableLiveData<>(false);

    /** true si es la primera vez que el alumno entra a esta lección */
    private final MutableLiveData<Boolean> mostrarBonoInicio = new MutableLiveData<>(false);

    // ─────────────────────────────────────────────────────────────────────────
    //  Repositorios
    // ─────────────────────────────────────────────────────────────────────────

    /** Repositorio para persistir los intentos de resolución de ecuaciones. */
    private final EcuacionRepository ecuacionRepository;
    /** Repositorio para leer y actualizar el progreso del usuario en la lección. */
    private final ProgresoLeccionRepository progresoRepository;

    // ─────────────────────────────────────────────────────────────────────────
    //  Constructor
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Crea el ViewModel e inicializa los repositorios de persistencia.
     *
     * @param application contexto de aplicación.
     */
    public EjercicioViewModel(@NonNull Application application) {
        super(application);
        ecuacionRepository = new EcuacionRepository(application);
        progresoRepository = new ProgresoLeccionRepository(application);
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Inicialización de sesión
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Método iniciarLeccion.
     * Inicia la sesión de la lección para el usuario dado.
     *
     * Consulta el progreso previo de forma asíncrona. Según el resultado:
     * Sin progreso previo: otorga el bono de inicio y guarda un registro inicial.
     * Con progreso previo: restaura el puntaje anterior y activa el modo de puntuación reducida.
     *
     * @param username nombre del usuario activo.
     * @param leccion  lección a iniciar; no debe ser null.
     */
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


    // ─────────────────────────────────────────────────────────────────────────
    //  Acceso al ejercicio actual
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Método getEjercicioActual.
     * Devuelve el ejercicio que el alumno está resolviendo en este momento.
     *
     * @return ejercicio actual, o null si la lista está vacía.
     */
    public Ejercicio getEjercicioActual() {
        if (ejercicios == null || indiceActual >= ejercicios.size()) return null;
        return ejercicios.get(indiceActual);
    }

    /**
     * Método getIndiceActual.
     * Devuelve el índice del ejercicio en curso.
     *
     * @return índiceActual.
     */
    public int getIndiceActual() { return indiceActual; }

    /**
     * Método getTotalEjercicios.
     * Devuelve el número total de ejercicios en la lección.
     *
     * @return total de ejercicios, o 0 si la lista no está inicializada.
     */
    public int getTotalEjercicios() { return ejercicios != null ? ejercicios.size() : 0; }

    /**
     * Método getLeccionActual.
     * Devuelve la lección que se está ejecutando.
     *
     * @return lección actual; puede ser null.
     */
    public Leccion getLeccionActual() { return leccionActual; }


    // ─────────────────────────────────────────────────────────────────────────
    //  LiveData getters
    // ─────────────────────────────────────────────────────────────────────────


    /** @return score puntaje actual del usuario. */
    public MutableLiveData<Integer> getScore() { return score; }

    /** @return indice regresa el índice del ejercicio en curso. */
    public MutableLiveData<Integer> getIndice() { return indice; }

    /** @return leccionTerminada true al completar la lección. */
    public MutableLiveData<Boolean> getLeccionTerminada() { return leccionTerminada; }

    /** @return leccionInicializada true cuando la lección está lista para empezar. */
    public MutableLiveData<Boolean> getLeccionInicializada() { return leccionInicializada; }

    /** @return mostrarBonoInicio true si se debe mostrar el mensaje de bono de inicio. */
    public MutableLiveData<Boolean> getMostrarBonoInicio() { return mostrarBonoInicio; }

    // ─────────────────────────────────────────────────────────────────────────
    //  Acciones del usuario
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Método registrarAcierto.
     * Suma los puntos correspondientes, persiste el ejercicio
     * en el historial y guarda el progreso actualizado.
     *
     * Los puntos sumados dependen de si es primera vez o reintento
     */
    public void registrarAcierto() {
        AlgePlusApp.getInstance().addScore(getPuntosPorAciertoActual());
        score.setValue(AlgePlusApp.getInstance().getScore());
        persistirEjercicio();
        guardarProgreso(false);
    }

    /**
     * Método usarPista.
     * Aplica el descuento de pista al puntaje.
     * El puntaje no bajará de 0.
     */
    public void usarPista() {
        int actual = AlgePlusApp.getInstance().getScore();
        if (actual > 0) {
            AlgePlusApp.getInstance().addScore(PUNTOS_POR_PISTA);
            score.setValue(AlgePlusApp.getInstance().getScore());
            guardarProgreso(false);
        }
    }

    /**
     * Método getPuntosPorAciertosActual.
     * Devuelve la cantidad de puntos que se otorgarán por el siguiente acierto.
     *
     * @return PUNTOS_POR_ACIERTO si es primera vez; PUNTOS_POR_REPETIR si es reintento.
     */
    public int getPuntosPorAciertoActual() {
        return puntajeReducidoPorReintento ? PUNTOS_POR_REPETIR : PUNTOS_POR_ACIERTO;
    }

    /**
     * Método requiereConfirmarSalida.
     * Indica si debe mostrarse un diálogo de confirmación antes de abandonar la lección.
     *
     * true cuando la lección está inicializada pero aún no terminada,
     * lo que significaría perder el progreso parcial del intento.
     *
     * @return true si se requiere confirmación de salida.
     */
    public boolean requiereConfirmarSalida() {
        return !Boolean.TRUE.equals(leccionTerminada.getValue())
                && Boolean.TRUE.equals(leccionInicializada.getValue());
    }


    /**
     * Método descartarProgresoIncompleto.
     * Descarta el progreso del intento en curso al abandonar la lección sin completarla.
     *
     * Si la lección ya estaba completada antes de entrar, restaura el progreso anterior.
     * Si es el primer intento, elimina el registro creado al inicio.
     */
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

    /**
     * Método avanzarEjercicio.
     * Avanza al siguiente ejercicio o marca la lección como terminada.
     */
    public void avanzarEjercicio() {
        indiceActual++;
        if (indiceActual >= ejercicios.size()) {
            marcarLeccionCompletada();
            leccionTerminada.setValue(true);
        } else {
            indice.setValue(indiceActual);
        }
    }

    /**
     * Método getPuntajeFinal.
     * Devuelve el puntaje final obtenido al terminar la lección.
     *
     * @return puntaje acumulado.
     */
    public int getPuntajeFinal() {
        return AlgePlusApp.getInstance().getScore();
    }

    /**
     * Método persistirEjercicio.
     * Extrae el resultado esperado del último paso del ejercicio actual y lo
     * persiste como un registro de Ecuacion en el historial.
     */
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

    /**
     * Método marcarLeccionCompletada.
     * Actualiza el estado interno al completar todos los ejercicios e incrementa
     * el contador de veces completada.
     */
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

    /**
     * Método guardarProgreso.
     * Persiste el estado de progreso actual (puntaje, completada, veces completada) en Room.
     *
     * @param completada true si la lección acaba de terminarse.
     */
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

    /**
     * Método fechaActual.
     * Devuelve la fecha y hora actual formateada como "yyyy-MM-dd HH:mm".
     *
     * @return cadena con la fecha actual.
     */
    private String fechaActual() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                .format(new Date());
    }

    /**
     * Método normalizarUsername.
     * Normaliza el nombre de usuario: elimina espacios, y devuelve "Usuario"
     * si el resultado es vacío o nulo.
     *
     * @param username cadena a normalizar; puede ser null.
     * @return nombre de usuario normalizado; nunca null ni vacío.
     */
    private String normalizarUsername(String username) {
        if (username == null) return "Usuario";
        String limpio = username.trim();
        return limpio.isEmpty() ? "Usuario" : limpio;
    }
}

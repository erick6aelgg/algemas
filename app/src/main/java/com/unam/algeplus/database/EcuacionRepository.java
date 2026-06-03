package com.unam.algeplus.database;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.unam.algeplus.model.Ecuacion;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Clase EcuacionRepository
 *  Repositorio que actúa como capa de abstracción entre los ViewModels y la
 *  persistencia Room para la entidad Ecuacion.
 *
 * Las escrituras se ejecutan fuera del hilo principal.
 *
 * @author Movilísticos - ICAT, UNAM
 *  * @version 1.0.1
 */
public class EcuacionRepository {

    /** DAO obtenido de la base de datos singleton; inmutable tras la construcción. */
    private final EcuacionDao dao;

    /**
     * Ejecutor de un solo hilo para serializar las escrituras en segundo plano.
     * Evita condiciones de carrera y garantiza el orden de las operaciones.
     */
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    // ─────────────────────────────────────────────────────────────────────────
    //  Constructor
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Método EcuacionRepository.
     * Crea una nueva instancia del repositorio.
     *
     * Obtiene el DAO desde la instancia singleton de AppDatabase.
     *
     * @param application contexto de aplicación utilizado para inicializar Room;
     * no debe ser null.
     */
    public EcuacionRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        dao = db.ecuacionDao();
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Operaciones de escritura
    // ─────────────────────────────────────────────────────────────────────────


    /**
     * Método insertar.
     * Persiste un nuevo intento de resolución de ecuación en la base de datos.
     *
     * La operación se ejecuta de forma asíncrona en el hilo del executor;
     * este método retorna inmediatamente sin bloquear el hilo que lo invoca.
     *
     * @param ecuacion objeto a insertar; su campo id se ignora.
     */
    public void insertar(Ecuacion ecuacion) {
        executor.execute(() -> dao.insertar(ecuacion));
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Operaciones de lectura (reactivas)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Método obtenerHistorial.
     * Devuelve el historial completo de intentos de resolución, del más reciente
     * al más antiguo.
     *
     * El LiveData resultante emite automáticamente una nueva lista
     * cada vez que se insertan o modifican registros en la tabla.
     *
     * @return LiveData con la lista completa de Ecuacion;
     *         nunca  null.
     */
    public LiveData<List<Ecuacion>> obtenerHistorial() {
        return dao.obtenerHistorial();
    }


    /**
     * Método obtenerHistorialReciente.
     * Devuelve los limite registros más recientes del historial.
     *
     * @param limite número máximo de registros a recuperar (debe ser  > 0).
     * @return LiveData con los últimos limite intentos.
     */
    public LiveData<List<Ecuacion>> obtenerHistorialReciente(int limite) {
        return dao.obtenerHistorialReciente(limite);
    }
}

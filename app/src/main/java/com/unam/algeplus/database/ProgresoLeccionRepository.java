package com.unam.algeplus.database;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.unam.algeplus.model.ProgresoLeccion;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Clase ProgresoLeccionRepository
 * Repositorio que abstrae el acceso a los datos de progreso de lecciones.
 *
 * Actúa como intermediario entre los ViewModels y el DAO ProgresoLeccionDao.
 *
 * @author Movilísticos - ICAT, UNAM
 * @version 1.0.1
 */
public class ProgresoLeccionRepository {

    /**
     * Interfaz de callback para recibir el resultado de una consulta síncrona
     * ejecutada en un hilo de fondo.
     */
    public interface ProgresoCallback {

        /**
         * Método onResult.
         * Se invoca cuando la consulta de progreso finaliza.
         *
         * @param progreso registro encontrado, o null si no existe.
         */
        void onResult(ProgresoLeccion progreso);
    }

    /** DAO de acceso a la tabla ProgresoLeccion. */
    private final ProgresoLeccionDao dao;

    /**
     * Ejecutor de un solo hilo para serializar operaciones bloqueantes fuera del
     * hilo principal y garantizar el orden de ejecución.
     */
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    // ─────────────────────────────────────────────────────────────────────────
    //  Constructor
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Método ProgresoLeccionRepository.
     * Crea el repositorio y obtiene el DAO desde la instancia singleton de la base de datos.
     *
     * @param application contexto de aplicación; no debe ser null.
     */
    public ProgresoLeccionRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        dao = db.progresoLeccionDao();
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Escrituras
    // ─────────────────────────────────────────────────────────────────────────


    /**
     * Método guardar.
     * Inserta o actualiza el registro de progreso de un usuario en una lección.
     *
     * Operación asíncrona; retorna inmediatamente. Si ya existe un registro con
     * la misma llave primaria compuesta, lo reemplaza completamente.
     *
     * @param progreso objeto ProgresoLeccion a guardar; no debe ser null.
     */
    public void guardar(ProgresoLeccion progreso) {
        executor.execute(() -> dao.guardar(progreso));
    }

    /**
     * Método eliminar.
     * Elimina el registro de progreso de un usuario en la lección indicada.
     *
     * Operación asíncrona; retorna inmediatamente. No hace nada si el registro
     * no existe.
     *
     * @param username  nombre del usuario.
     * @param leccionId identificador de la lección.
     */
    public void eliminar(String username, int leccionId) {
        executor.execute(() -> dao.eliminar(username, leccionId));
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Lecturas
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Método obtener.
     * Recupera de forma asíncrona el progreso de un usuario en una lección
     * y lo entrega a través del callback proporcionado.
     *
     * El callback se invoca en el hilo del executor, no en el hilo
     * principal.
     *
     * @param username  nombre del usuario.
     * @param leccionId identificador de la lección.
     * @param callback  receptor del resultado; se llama con null si no hay registro.
     */
    public void obtener(String username, int leccionId, ProgresoCallback callback) {
        executor.execute(() -> callback.onResult(dao.obtener(username, leccionId)));
    }

    /**
     * Método observarPorUsuario.
     * Devuelve un LiveData con todos los registros de progreso del usuario,
     * ordenados por leccionId ascendente.
     *
     * Emite automáticamente una nueva lista ante cualquier cambio en la tabla.
     *
     * @param username nombre del usuario a observar.
     * @return LiveData con la lista de progresos; nunca null.
     */
    public LiveData<List<ProgresoLeccion>> observarPorUsuario(String username) {
        return dao.observarPorUsuario(username);
    }

    /**
     * Método observarPuntajeTotal.
     * Devuelve un LiveData con el puntaje total acumulado del usuario
     * en todas sus lecciones.
     *
     * Emite 0 si el usuario no tiene ningún registro.
     *
     * @param username nombre del usuario.
     * @return LiveData que emite el puntaje total como Integer.
     */
    public LiveData<Integer> observarPuntajeTotal(String username) {
        return dao.observarPuntajeTotal(username);
    }
}

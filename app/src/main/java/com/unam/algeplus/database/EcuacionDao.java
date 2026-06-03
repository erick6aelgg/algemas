package com.unam.algeplus.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.unam.algeplus.model.Ecuacion;

import java.util.List;

/**
 * Interfaz EcuacionDao
 * DAO (Data Access Object) para la entidad Ecuacion.
 *
 * Define las operaciones de persistencia disponibles sobre la tabla Ecuacion.
 * Room genera automáticamente la implementación concreta de esta interfaz en tiempo
 * de compilación.
 *
 * @author Movilísticos - ICAT, UNAM
 *  * @version 1.0.1
 */
@Dao
public interface EcuacionDao {

    /**
     * Método insertar.
     * Inserta un nuevo registro de intento de resolución en la tabla Ecuacion.
     *
     * Descarta la inserción si ya existe un registro con el mismo id.
     *
     * @param ecuacion objeto Ecuacion a persistir; su campo id
     * será ignorado y asignado automáticamente por Room.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertar(Ecuacion ecuacion);

    /**
     * Método obtenerHistorial.
     * Devuelve el historial completo de intentos de resolución, ordenado del más
     * reciente al más antiguo.
     *
     * Se emite una nueva lista cada vez que la tabla cambia,
     * sin necesidad de volver a llamar a este método.
     *
     * @return LiveData que emite la lista completa de Ecuacion;
     * nunca null, aunque puede emitir una lista vacía.
     */
    @Query("SELECT * FROM Ecuacion ORDER BY id DESC")
    LiveData<List<Ecuacion>> obtenerHistorial();

    /**
     * Método obtenerHistorialReciente.
     * Devuelve los limite registros más recientes del historial.
     *
     * Útil para mostrar en la UI solo los últimos intentos sin cargar
     * toda la tabla en memoria.
     *
     * @param limite número máximo de registros a devolver (debe ser > 0).
     * @return LiveData con los últimos limite registros, ordenados
     * del más reciente al más antiguo.
     */
    @Query("SELECT * FROM Ecuacion ORDER BY id DESC LIMIT :limite")
    LiveData<List<Ecuacion>> obtenerHistorialReciente(int limite);
}

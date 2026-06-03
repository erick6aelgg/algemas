package com.unam.algeplus.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.unam.algeplus.model.ProgresoLeccion;

import java.util.List;

/**
 * Interfaz ProgresoLeccionDao
 * DAO (Data Access Object) para la entidad ProgresoLeccion.
 *
 * Expone las operaciones CRUD sobre la tabla ProgresoLeccion, que almacena
 * el progreso de cada usuario en cada lección. Room genera la implementación concreta
 * de esta interfaz en tiempo de compilación.
 *
 * @author Movilísticos - ICAT, UNAM
 * @version 1.0.1
 */

@Dao
public interface ProgresoLeccionDao {


    /**
     * Método guardar.
     * Inserta o reemplaza el registro de progreso de un usuario en una lección.
     *
     * Actualiza el registro existente cuando la llave primaria compuesta (username, leccionId)
     * ya existe en la tabla.
     *
     * @param progreso objeto ProgresoLeccion a insertar o actualizar.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void guardar(ProgresoLeccion progreso);

    /**
     * Método obtener.
     * Recupera de forma síncrona el registro de progreso de un usuario en una lección.
     *
     * Devuelve null si el usuario nunca ha iniciado esa lección.
     *
     *
     * @param username  nombre del usuario cuyo progreso se consulta.
     * @param leccionId identificador de la lección.
     *
     * @return ProgresoLeccion encontrado, o null si no existe registro.
     */
    @Query("SELECT * FROM ProgresoLeccion WHERE username = :username AND leccionId = :leccionId LIMIT 1")
    ProgresoLeccion obtener(String username, int leccionId);

    /**
     * Método observarPorUsuario.
     * Observa de forma reactiva todos los registros de progreso de un usuario,
     * ordenados por leccionId ascendente.
     *
     * Emite una nueva lista cada vez que cualquier registro del usuario cambia.
     *
     * @param username nombre del usuario a observar.
     * @return LiveData con la lista de progresos del usuario; nunca null.
     */
    @Query("SELECT * FROM ProgresoLeccion WHERE username = :username ORDER BY leccionId ASC")
    LiveData<List<ProgresoLeccion>> observarPorUsuario(String username);

    /**
     * Método observarPuntajeTotal.
     * Observa de forma reactiva el puntaje total acumulado de un usuario
     * sumando todos sus registros de progreso.
     *
     * Devuelve 0 cuando el usuario no tiene
     * ningún registro (en lugar de NULL).
     *
     * @param username nombre del usuario.
     * @return LiveData que emite el puntaje total; nunca emite null
     * (usa 0 como valor por defecto).
     */
    @Query("SELECT COALESCE(SUM(puntaje), 0) FROM ProgresoLeccion WHERE username = :username")
    LiveData<Integer> observarPuntajeTotal(String username);


    /**
     * Método eliminar.
     * Elimina el registro de progreso de un usuario en una lección específica.
     *
     * Se usa cuando se abandona una lección a mitad sin haberla completado
     * antes, para no dejar un progreso parcial huérfano.
     *
     * @param username  nombre del usuario.
     * @param leccionId identificador de la lección cuyo progreso se elimina.
     */
    @Query("DELETE FROM ProgresoLeccion WHERE username = :username AND leccionId = :leccionId")
    void eliminar(String username, int leccionId);
}

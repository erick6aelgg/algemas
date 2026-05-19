package com.unam.algeplus.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.unam.algeplus.model.ProgresoLeccion;

import java.util.List;

@Dao
public interface ProgresoLeccionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void guardar(ProgresoLeccion progreso);

    @Query("SELECT * FROM ProgresoLeccion WHERE username = :username AND leccionId = :leccionId LIMIT 1")
    ProgresoLeccion obtener(String username, int leccionId);

    @Query("SELECT * FROM ProgresoLeccion WHERE username = :username ORDER BY leccionId ASC")
    LiveData<List<ProgresoLeccion>> observarPorUsuario(String username);

    @Query("SELECT COALESCE(SUM(puntaje), 0) FROM ProgresoLeccion WHERE username = :username")
    LiveData<Integer> observarPuntajeTotal(String username);

    @Query("DELETE FROM ProgresoLeccion WHERE username = :username AND leccionId = :leccionId")
    void eliminar(String username, int leccionId);
}

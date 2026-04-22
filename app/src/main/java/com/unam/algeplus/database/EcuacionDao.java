package com.unam.algeplus.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.unam.algeplus.model.Ecuacion;

import java.util.List;

/**
 * DAO (Data Access Object) para la entidad Ecuacion.
 * Expone operaciones de inserción y consulta de historial.
 */
@Dao
public interface EcuacionDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertar(Ecuacion ecuacion);

    @Query("SELECT * FROM Ecuacion ORDER BY id DESC")
    LiveData<List<Ecuacion>> obtenerHistorial();

    @Query("SELECT * FROM Ecuacion ORDER BY id DESC LIMIT :limite")
    LiveData<List<Ecuacion>> obtenerHistorialReciente(int limite);
}

package com.unam.algeplus.database;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.unam.algeplus.model.Ecuacion;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Repositorio que media entre los ViewModels y la capa de persistencia Room.
 * Las escrituras se ejecutan fuera del hilo principal.
 */
public class EcuacionRepository {

    private final EcuacionDao dao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public EcuacionRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        dao = db.ecuacionDao();
    }

    public void insertar(Ecuacion ecuacion) {
        executor.execute(() -> dao.insertar(ecuacion));
    }

    public LiveData<List<Ecuacion>> obtenerHistorial() {
        return dao.obtenerHistorial();
    }

    public LiveData<List<Ecuacion>> obtenerHistorialReciente(int limite) {
        return dao.obtenerHistorialReciente(limite);
    }
}

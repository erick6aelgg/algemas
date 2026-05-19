package com.unam.algeplus.database;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.unam.algeplus.model.ProgresoLeccion;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProgresoLeccionRepository {

    public interface ProgresoCallback {
        void onResult(ProgresoLeccion progreso);
    }

    private final ProgresoLeccionDao dao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public ProgresoLeccionRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        dao = db.progresoLeccionDao();
    }

    public void guardar(ProgresoLeccion progreso) {
        executor.execute(() -> dao.guardar(progreso));
    }

    public void eliminar(String username, int leccionId) {
        executor.execute(() -> dao.eliminar(username, leccionId));
    }

    public void obtener(String username, int leccionId, ProgresoCallback callback) {
        executor.execute(() -> callback.onResult(dao.obtener(username, leccionId)));
    }

    public LiveData<List<ProgresoLeccion>> observarPorUsuario(String username) {
        return dao.observarPorUsuario(username);
    }

    public LiveData<Integer> observarPuntajeTotal(String username) {
        return dao.observarPuntajeTotal(username);
    }
}

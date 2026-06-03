package com.unam.algeplus.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.unam.algeplus.data.LeccionesData;
import com.unam.algeplus.model.Leccion;

import java.util.List;

/**
 * Clase LeccionesViewModel
 * ViewModel que provee la lista estática de lecciones.
 *
 * @author Movilísticos - ICAT, UNAM
 * @version 1.0.1
 */
public class LeccionesViewModel extends ViewModel {

    /**
     * Lista observable de lecciones disponibles en la aplicación.
     * Se inicializa una sola vez con los datos estáticos de LeccionesData.
     */
    private final MutableLiveData<List<Leccion>> lecciones = new MutableLiveData<>();

    /** Carga el catálogo de lecciones al crear el ViewModel.*/
    public LeccionesViewModel() {
        lecciones.setValue(LeccionesData.getLecciones());
    }

    /**
     * Método getLecciones.
     * Devuelve la lista completa de lecciones.
     *
     * La lista nunca cambia durante la sesión.
     *
     * @return lecciones con la lista de Leccion; nunca null.
     */
    public LiveData<List<Leccion>> getLecciones() { return lecciones; }
}

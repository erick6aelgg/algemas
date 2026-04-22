package com.unam.algeplus.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.unam.algeplus.data.LeccionesData;
import com.unam.algeplus.model.Leccion;

import java.util.List;

public class LeccionesViewModel extends ViewModel {

    private final MutableLiveData<List<Leccion>> lecciones = new MutableLiveData<>();

    public LeccionesViewModel() {
        lecciones.setValue(LeccionesData.getLecciones());
    }

    public LiveData<List<Leccion>> getLecciones() { return lecciones; }
}

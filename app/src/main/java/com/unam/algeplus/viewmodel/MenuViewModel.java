package com.unam.algeplus.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.unam.algeplus.AlgePlusApp;

/**
 * Clase MenuViewModel
 * ViewModel de la pantalla principal.
 *
 * @author Movilísticos - ICAT, UNAM
 * @version 1.0.1
 */
public class MenuViewModel extends ViewModel {

    /**
     * Nombre de usuario activo.
     * El valor por defecto es "Usuario" para garantizar que nunca sea nulo.
     */
    private final MutableLiveData<String> username = new MutableLiveData<>("Usuario");


    /**
     * Método getUsername.
     * Devuelve el MutableLiveData que contiene el nombre de usuario.
     *
     * La Activity puede observarlo para reaccionar a cambios.
     *
     * @return username con el nombre de usuario; nunca null.
     */
    public MutableLiveData<String> getUsername() { return username; }


    /**
     * Método setUsername.
     * Actualiza el nombre de usuario almacenado.
     *
     * Si el valor recibido es null o vacío,
     * se conserva el valor anterior (no se sobreescribe con vacío).
     *
     * @param name nuevo nombre introducido por el usuario.
     */
    public void setUsername(String name) {
        String trimmed = (name == null || name.trim().isEmpty()) ? "Usuario" : name.trim();
        username.setValue(trimmed);
        AlgePlusApp.getInstance().setUsername(trimmed);
    }
}

package com.unam.algeplus.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.unam.algeplus.AlgePlusApp;

public class MenuViewModel extends ViewModel {

    private final MutableLiveData<String> username = new MutableLiveData<>("Usuario");

    public MutableLiveData<String> getUsername() { return username; }

    public void setUsername(String name) {
        String trimmed = (name == null || name.trim().isEmpty()) ? "Usuario" : name.trim();
        username.setValue(trimmed);
        AlgePlusApp.getInstance().setUsername(trimmed);
    }
}

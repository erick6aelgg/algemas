package com.unam.algeplus;

import android.app.Application;

/** Singleton de aplicación: almacena estado global de sesión (usuario, puntaje). */
public class AlgePlusApp extends Application {

    private static AlgePlusApp instance;

    private String username = "Usuario";
    private int score = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static AlgePlusApp getInstance() {
        return instance;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = Math.max(0, score); }
    public void addScore(int delta) { setScore(this.score + delta); }
    public void resetScore() { this.score = 0; }
}

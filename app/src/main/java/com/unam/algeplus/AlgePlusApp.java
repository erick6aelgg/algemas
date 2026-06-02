package com.unam.algeplus;

import android.app.Application;

/**
 * Extiende Application para actuar como singleton global durante el ciclo de vida del proceso.
 * Almacena el estado de sesión del usuario (nombre y puntaje acumulado) de
 * forma accesible desde cualquier componente.
 *
 * @author Movilísticos - ICAT, UNAM
 * @version 1.0.1
 */
public class AlgePlusApp extends Application {

    private static AlgePlusApp instance;

    private String username = "Usuario";
    private int score = 0;

    /**
     * Llamado por el sistema al crear el proceso de la aplicación.
     * Registra esta instancia como singleton global.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    /**
     *  Devuelve la instancia singleton de AlgePlusApp.
     */
    public static AlgePlusApp getInstance() {
        return instance;
    }

    /**
     * Devuelve el nombre del usuario activo en la sesión.
     *
     * @return nombre de usuario; nunca por defecto "Usuario".
     */
    public String getUsername() { return username; }

    /**
     * Establece el nombre del usuario para la sesión actual.
     *
     * @param username nombre a asignar; no debe ser null.
     */
    public void setUsername(String username) { this.username = username; }

    /**
     * Devuelve el puntaje acumulado del usuario en la sesión actual.
     *
     * @return puntaje actual (siempre >= 0).
     */
    public int getScore() { return score; }

    /**
     * Reemplaza el puntaje del usuario por el valor indicado.
     * Si el valor es negativo se normaliza a 0.
     *
     * @param score nuevo puntaje; los valores negativos se convierten en 0.
     */
    public void setScore(int score) { this.score = Math.max(0, score); }

    /**
     * Incrementa (o decrementa) el puntaje en delta puntos.
     * El resultado se normaliza a 0 si quedaría negativo.
     *
     * @param delta cantidad a sumar al puntaje; puede ser negativo para restar.
     */
    public void addScore(int delta) { setScore(this.score + delta); }

    /**
     * Reinicia el puntaje a 0.
     * Útil al comenzar una nueva sesión o al descartar progreso.
     */
    public void resetScore() { this.score = 0; }
}

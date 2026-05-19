package com.unam.algeplus.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;

/**
 * Puntaje persistente de una leccion para un usuario.
 * La llave compuesta evita mezclar el progreso de usuarios distintos.
 */
@Entity(
        tableName = "ProgresoLeccion",
        primaryKeys = {"username", "leccionId"}
)
public class ProgresoLeccion {

    @NonNull
    private String username;

    private int leccionId;
    private int puntaje;
    private boolean completada;
    private int vecesCompletada;

    @NonNull
    private String fechaActualizacion;

    public ProgresoLeccion(@NonNull String username, int leccionId, int puntaje,
                           boolean completada, int vecesCompletada,
                           @NonNull String fechaActualizacion) {
        this.username = username;
        this.leccionId = leccionId;
        this.puntaje = puntaje;
        this.completada = completada;
        this.vecesCompletada = vecesCompletada;
        this.fechaActualizacion = fechaActualizacion;
    }

    @NonNull
    public String getUsername() { return username; }
    public void setUsername(@NonNull String username) { this.username = username; }

    public int getLeccionId() { return leccionId; }
    public void setLeccionId(int leccionId) { this.leccionId = leccionId; }

    public int getPuntaje() { return puntaje; }
    public void setPuntaje(int puntaje) { this.puntaje = Math.max(0, puntaje); }

    public boolean isCompletada() { return completada; }
    public void setCompletada(boolean completada) { this.completada = completada; }

    public int getVecesCompletada() { return vecesCompletada; }
    public void setVecesCompletada(int vecesCompletada) {
        this.vecesCompletada = Math.max(0, vecesCompletada);
    }

    @NonNull
    public String getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(@NonNull String fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }
}

package com.unam.algeplus.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;

/**
 * Clase ProgresoLeccion
 * Puntaje persistente de una leccion para un usuario.
 * La llave compuesta evita mezclar el progreso de usuarios distintos.
 *
 * @author Movilísticos - ICAT, UNAM
 * @version 1.0.1
 */
@Entity(
        tableName = "ProgresoLeccion",
        primaryKeys = {"username", "leccionId"}
)
public class ProgresoLeccion {

    /**
     * Nombre del usuario propietario del registro.
     * Forma parte de la llave primaria compuesta.
     */
    @NonNull
    private String username;

    /**
     * Identificador de la lección a la que pertenece el progreso.
     * Forma parte de la llave primaria compuesta.
     */
    private int leccionId;

    /**
     * Puntaje acumulado del usuario en esta lección.
     * Nunca puede ser negativo.
     */
    private int puntaje;

    /** Indica si el usuario ha completado todos los ejercicios de la lección al menos una vez. */
    private boolean completada;

    /** Número de veces que el usuario ha completado la lección desde el principio. */
    private int vecesCompletada;


    /** Fecha y hora de la última actualización del registro */
    @NonNull
    private String fechaActualizacion;

    // ─────────────────────────────────────────────────────────────────────────
    //  Constructor
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Crea un registro de progreso con todos sus campos.
     *
     * @param username           nombre del usuario; no debe ser null.
     * @param leccionId          id de la lección.
     * @param puntaje            puntos acumulados ( >= 0).
     * @param completada         true si la lección ya fue terminada al menos una vez.
     * @param vecesCompletada    número de veces que se ha completado ( >= 0).
     * @param fechaActualizacion fecha de la última actualización en formato.
     */
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

    // ─────────────────────────────────────────────────────────────────────────
    //  Getters y setters
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Método getUsername.
     * Devuelve el nombre del usuario propietario del progreso.
     *
     * @return nombre de usuario; nunca null.
     */
    @NonNull
    public String getUsername() { return username; }

    /**
     * Método setUsername.
     * Establece el nombre del usuario (uso interno de Room).
     *
     * @param username nombre de usuario; no debe ser null.
     */
    public void setUsername(@NonNull String username) { this.username = username; }

    /**
     * Método getLeccionId.
     * Devuelve el identificador de la lección asociada.
     *
     * @return id de la lección.
     */
    public int getLeccionId() { return leccionId; }

    /**
     * Método setLeccionId.
     * Establece el id de la lección (uso interno de Room).
     *
     * @param leccionId id de la lección.
     */
    public void setLeccionId(int leccionId) { this.leccionId = leccionId; }

    /**
     * Método getPuntaje.
     * Devuelve el puntaje acumulado del usuario en esta lección.
     *
     * @return puntaje ( >= 0).
     */
    public int getPuntaje() { return puntaje; }

    /**
     * Método setPuntaje.
     * Establece el puntaje. Los valores negativos se normalizan a 0.
     *
     * @param puntaje nuevo puntaje.
     */
    public void setPuntaje(int puntaje) { this.puntaje = Math.max(0, puntaje); }

    /**
     * Método isCompletada.
     * Indica si la lección ha sido completada al menos una vez.
     *
     * @return true si está completada; false en caso contrario.
     */
    public boolean isCompletada() { return completada; }

    /**
     * Método setCompletada.
     * Actualiza el estado de completado de la lección.
     *
     * @param completada true para marcar como completada.
     */
    public void setCompletada(boolean completada) { this.completada = completada; }

    /**
     * Método getVecesCompletada.
     * Devuelve el número de veces que el usuario ha completado la lección.
     *
     * @return número de completaciones ( >= 0).
     */
    public int getVecesCompletada() { return vecesCompletada; }

    /**
     * Método setVecesCompletada.
     * Establece el número de veces completada. Los valores negativos se normalizan a 0.
     *
     * @param vecesCompletada nuevo conteo.
     */
    public void setVecesCompletada(int vecesCompletada) {
        this.vecesCompletada = Math.max(0, vecesCompletada);
    }

    /**
     * Método getFechaActualizacion.
     * Devuelve la fecha de la última actualización del registro.
     *
     * @return fecha en formato "yyyy-MM-dd HH:mm"; nunca null.
     */
    @NonNull
    public String getFechaActualizacion() { return fechaActualizacion; }

    /**
     * Método setFechaActualizacion.
     * Actualiza la fecha de última modificación del registro.
     *
     * @param fechaActualizacion nueva fecha en formato "yyyy-MM-dd HH:mm"; no debe ser null.
     */
    public void setFechaActualizacion(@NonNull String fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }
}

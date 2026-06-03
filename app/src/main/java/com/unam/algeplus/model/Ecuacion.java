package com.unam.algeplus.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

/**
 * Clase Ecuacion
 * Entidad Room que representa un intento de resolución de ecuación.
 * Cada vez que el usuario completa correctamente un ejercicio, se persiste un registro
 * de esta clase en la tabla "Ecuacion" de la base de datos local.
 *
 * @author Movilísticos - ICAT, UNAM
 * @version 1.0.1
 */
@Entity(tableName = "Ecuacion")
public class Ecuacion {

    /**
     * Identificador único autogenerado por Room.
     * Se asigna en la primera inserción y no debe modificarse manualmente.
     */
    @PrimaryKey(autoGenerate = true)
    private int id;

    /** Expresión textual de la ecuación resuelta */
    @NonNull
    private String ecuacion;

    /** Valor numérico de x obtenido al resolver la ecuación. */
    @NonNull
    private String resultado;

    /** Fecha y hora del intento en formato "yyyy-MM-dd HH:mm" */
    @NonNull
    private String fecha;

    // ─────────────────────────────────────────────────────────────────────────
    //  Constructor
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Método Ecuacion.
     * Crea un nuevo registro de intento de resolución.
     *
     * @param ecuacion  expresión de la ecuación; no debe ser null.
     * @param resultado valor de x como cadena; no debe ser null.
     * @param fecha fecha del intento en formato "yyyy-MM-dd HH:mm"; no debe ser null.
     */
    public Ecuacion(@NonNull String ecuacion, @NonNull String resultado, @NonNull String fecha) {
        this.ecuacion = ecuacion;
        this.resultado = resultado;
        this.fecha = fecha;
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Getters y setters
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Método getId.
     * Devuelve el identificador único del registro.
     *
     * @return id autogenerado por Room (0 si aún no se ha insertado).
     */
    public int getId() { return id; }

    /**
     * Método setId.
     * Establece el identificador único. Usado internamente por Room tras la inserción.
     *
     * @param id identificador asignado por la base de datos.
     */
    public void setId(int id) { this.id = id; }

    /**
     * Método getEcuacion.
     * Devuelve la expresión textual de la ecuación.
     *
     * @return cadena con la ecuación, p. ej.  "x + 5 = 12".
     */
    @NonNull public String getEcuacion() { return ecuacion; }

    /**
     * Método setEcuacion.
     * Actualiza la expresión de la ecuación.
     *
     * @param ecuacion nueva expresión.
     */
    public void setEcuacion(@NonNull String ecuacion) { this.ecuacion = ecuacion; }

    /**
     * Método getResultado.
     * Devuelve el resultado (valor de x) de la ecuación.
     *
     * @return resultado como cadena.
     */
    @NonNull public String getResultado() { return resultado; }

    /**
     * Método setResultado.
     * Actualiza el resultado de la ecuación.
     *
     * @param resultado nuevo resultado.
     */
    public void setResultado(@NonNull String resultado) { this.resultado = resultado; }

    /**
     * Método getFecha.
     * Devuelve la fecha y hora del intento.
     *
     * @return cadena con la fecha en formato "yyyy-MM-dd HH:mm".
     */
    @NonNull public String getFecha() { return fecha; }

    /**
     * Método setFecha.
     * Actualiza la fecha del intento.
     *
     * @param fecha nueva fecha en formato "yyyy-MM-dd HH:mm".
     */
    public void setFecha(@NonNull String fecha) { this.fecha = fecha; }
}

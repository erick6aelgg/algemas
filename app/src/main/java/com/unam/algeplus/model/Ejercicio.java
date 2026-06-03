package com.unam.algeplus.model;

import java.util.List;

/**
 * Clase Ejercicio
 * Representa un ejercicio de ecuación de primer grado.
 * Puede ser de tipo PROCEDIMIENTO (paso a paso) o BALANZA (método de la balanza).
 *
 * @author Movilísticos - ICAT, UNAM
 * @version 1.0.1
 */
public class Ejercicio {

    /**
     * Modalidad de presentación del ejercicio.
     *
     * PROCEDIMIENTO — solo pasos algebraicos textuales.
     * BALANZA — pasos más vista gráfica de balanza con los dos lados de la ecuación.
     *
     */
    public enum Tipo { PROCEDIMIENTO, BALANZA }

    /** Identificador único del ejercicio dentro del catálogo de datos. */
    private final int id;

    /** Expresión textual completa de la ecuación que el alumno debe resolver. */
    private final String ecuacion;

    /** Modalidad de presentación; determina si se muestra la Balanza. */
    private final Tipo tipo;

    /** Lista ordenada de pasos de resolución. */
    private final List<Paso> pasos;

    /** Pista teórica (no la respuesta) */
    private final String tip;

    /**
     * Nivel de dificultad del ejercicio.
     *  1 — Fácil.
     *  2 — Intermedio.
     *  3 — Difícil (reservado para versiones futuras).
     */
    private final int nivelDificultad;

    /** Texto del lado izquierdo de la ecuación para mostrar en la balanza.*/
    private final String ladoIzquierdo;

    /** Texto del lado derecho de la ecuación para mostrar en la balanza. */
    private final String ladoDerecho;

    // ─────────────────────────────────────────────────────────────────────────
    //  Constructores
    // ─────────────────────────────────────────────────────────────────────────


    /**
     * Método Ejercicio.
     * Crea un ejercicio de tipo PROCEDIMIENTO (sin balanza).
     *
     * @param id              identificador único del ejercicio.
     * @param ecuacion        expresión textual de la ecuación.
     * @param tipo            modalidad de presentación (PROCEDIMIENTO o BALANZA).
     * @param pasos           lista ordenada de Paso; el índice 0 es el encabezado.
     * @param tip             pista teorica.
     * @param nivelDificultad nivel 1 = fácil, 2 = intermedio, 3 = difícil.
     */
    public Ejercicio(int id, String ecuacion, Tipo tipo,
                     List<Paso> pasos, String tip, int nivelDificultad) {
        this(id, ecuacion, tipo, pasos, tip, nivelDificultad, null, null);
    }

    /**
     * Crea un ejercicio con soporte para la vista de balanza.
     *
     * @param id              identificador único del ejercicio.
     * @param ecuacion        expresión textual de la ecuación.
     * @param tipo            modalidad de presentación (PROCEDIMIENTO o BALANZA).
     * @param pasos           lista ordenada de Paso; el índice 0 es el encabezado.
     * @param tip             pista teorica.
     * @param nivelDificultad nivel 1 = fácil, 2 = intermedio, 3 = difícil.
     * @param ladoIzquierdo   texto del platillo izquierdo de la balanza, p. ej. "x + 5"
     *                        puede ser null si tipo es PROCEDIMIENTO.
     * @param ladoDerecho     texto del platillo derecho de la balanza, p. ej. "12"
     *                        puede ser null si tipo es PROCEDIMIENTO.
     */
    public Ejercicio(int id, String ecuacion, Tipo tipo,
                     List<Paso> pasos, String tip, int nivelDificultad,
                     String ladoIzquierdo, String ladoDerecho) {
        this.id = id;
        this.ecuacion = ecuacion;
        this.tipo = tipo;
        this.pasos = pasos;
        this.tip = tip;
        this.nivelDificultad = nivelDificultad;
        this.ladoIzquierdo = ladoIzquierdo;
        this.ladoDerecho = ladoDerecho;
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Getters
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Método getId.
     * Devuelve el identificador único del ejercicio.
     *
     * @return id del ejercicio.
     */
    public int getId() { return id; }

    /**
     * Método getEcuacion.
     * Devuelve la expresión textual completa de la ecuación.
     *
     * @return cadena con la ecuación, p. ej. "x + 5 = 12".
     */
    public String getEcuacion() { return ecuacion; }

    /**
     * Método getTipo.
     * Devuelve la modalidad de presentación del ejercicio.
     *
     * @return Tipo (PROCEDIMIENTO o BALANZA).
     */
    public Tipo getTipo() { return tipo; }

    /**
     * Método getPasos.
     * Devuelve la lista ordenada de pasos de resolución.
     * El índice 0 corresponde siempre al encabezado (solo lectura).
     *
     * @return lista de Paso; nunca null.
     */
    public List<Paso> getPasos() { return pasos; }

    /**
     * Método getTip.
     * Devuelve la pista pedagógica asociada al ejercicio.
     *
     * @return texto de la pista; nunca null.
     */
    public String getTip() { return tip; }

    /**
     * Método getNivelDificultad.
     * Devuelve el nivel de dificultad del ejercicio.
     *
     * @return 1 (fácil), 2 (intermedio) o 3 (difícil).
     */
    public int getNivelDificultad() { return nivelDificultad; }

    /**
     * Método getLadoIzquierdo.
     * Devuelve el texto del platillo izquierdo de la balanza.
     * Si no se definió, devuelve una cadena vacía "".
     *
     * @return texto del lado izquierdo; nunca null.
     */
    public String getLadoIzquierdo() { return ladoIzquierdo != null ? ladoIzquierdo : ""; }

    /**
     * Método getLadoDerecho.
     * Devuelve el texto del platillo derecho de la balanza.
     * Si no se definió, devuelve una cadena vacía "".
     *
     * @return texto del lado derecho; nunca null.
     */
    public String getLadoDerecho() { return ladoDerecho != null ? ladoDerecho : ""; }
}

package com.unam.algeplus.model;

import java.util.List;

/**
 * Representa un ejercicio de ecuación de primer grado.
 * Puede ser de tipo PROCEDIMIENTO (paso a paso) o BALANZA (método de la balanza).
 */
public class Ejercicio {

    public enum Tipo { PROCEDIMIENTO, BALANZA }

    private final int id;
    private final String ecuacion;       // p.ej. "x + 5 = 12"
    private final Tipo tipo;
    private final List<Paso> pasos;      // pasos[0] = encabezado (solo lectura)
    private final String tip;            // pista teórica (no la respuesta)
    private final int nivelDificultad;   // 1 = fácil, 2 = medio, 3 = difícil

    // Para el tipo BALANZA: textos de cada platillo
    private final String ladoIzquierdo; // p.ej. "x + 5"
    private final String ladoDerecho;   // p.ej. "12"

    public Ejercicio(int id, String ecuacion, Tipo tipo,
                     List<Paso> pasos, String tip, int nivelDificultad) {
        this(id, ecuacion, tipo, pasos, tip, nivelDificultad, null, null);
    }

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

    public int getId() { return id; }
    public String getEcuacion() { return ecuacion; }
    public Tipo getTipo() { return tipo; }
    public List<Paso> getPasos() { return pasos; }
    public String getTip() { return tip; }
    public int getNivelDificultad() { return nivelDificultad; }
    public String getLadoIzquierdo() { return ladoIzquierdo != null ? ladoIzquierdo : ""; }
    public String getLadoDerecho() { return ladoDerecho != null ? ladoDerecho : ""; }
}

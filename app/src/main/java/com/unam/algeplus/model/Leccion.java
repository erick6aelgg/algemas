package com.unam.algeplus.model;

import java.util.List;

/**
 * Agrupa 5 ejercicios del mismo tema y nivel de dificultad.
 */
public class Leccion {

    private final int id;
    private final String nombre;
    private final String descripcion;
    private final int nivelDificultad;   // 1 = fácil, 2 = medio, 3 = difícil
    private final List<Ejercicio> ejercicios;

    public Leccion(int id, String nombre, String descripcion,
                   int nivelDificultad, List<Ejercicio> ejercicios) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.nivelDificultad = nivelDificultad;
        this.ejercicios = ejercicios;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public int getNivelDificultad() { return nivelDificultad; }
    public List<Ejercicio> getEjercicios() { return ejercicios; }
    public int getTotalEjercicios() { return ejercicios.size(); }

    /**
     * Devuelve la etiqueta de dificultad legible.
     */
    public String getEtiquetaDificultad() {
        switch (nivelDificultad) {
            case 1: return "Fácil";
            case 2: return "Intermedio";
            case 3: return "Difícil";
            default: return "Fácil";
        }
    }
}

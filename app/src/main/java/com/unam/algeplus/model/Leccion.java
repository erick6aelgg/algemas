package com.unam.algeplus.model;

import java.util.List;

/**
 * Clase Leccion
 * Agrupa 5 ejercicios del mismo tema y nivel de dificultad.
 *
 * @author Movilísticos - ICAT, UNAM
 * @version 1.0.1
 */
public class Leccion {

    /** Identificador único de la lección. */
    private final int id;

    /** Nombre corto de la lección mostrado en la lista, p. ej. "Suma y Resta Simples". */
    private final String nombre;

    /** Descripción breve de los conceptos que se practican en la lección. */
    private final String descripcion;

    /**
     * Nivel de dificultad de todos los ejercicios de la lección.
     * 1 = Fácil, 2 = Intermedio, 3 = Difícil.
     */
    private final int nivelDificultad;   // 1 = fácil, 2 = medio, 3 = difícil

    /** Lista ordenada de ejercicios que conforman la lección. */
    private final List<Ejercicio> ejercicios;

    /**
     * Puntos obtenidos por el usuario en el último intento de esta lección.
     * 0 si nunca se ha jugado o si el progreso fue descartado.
     */
    private final int ptsobtenidos;


    // ─────────────────────────────────────────────────────────────────────────
    //  Constructores
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Crea una lección con puntos obtenidos en 0 (lección nueva)
     *
     * @param id              identificador único de la lección.
     * @param nombre          nombre corto mostrado en la UI.
     * @param descripcion     descripción de los conceptos practicados.
     * @param nivelDificultad nivel 1 = fácil, 2 = intermedio, 3 = difícil.
     * @param ejercicios      lista de ejercicios.
     */
    public Leccion(int id, String nombre, String descripcion,
                   int nivelDificultad, List<Ejercicio> ejercicios) {
        this(id, nombre, descripcion, nivelDificultad, ejercicios, 0);
    }

    /**
     *
     * Crea una lección con un puntaje previo registrado.
     *
     * @param id              identificador único de la lección.
     * @param nombre          nombre corto mostrado en la UI.
     * @param descripcion     descripción de los conceptos practicados.
     * @param nivelDificultad nivel 1 = fácil, 2 = intermedio, 3 = difícil.
     * @param ejercicios      lista de ejercicios; normalmente 5 elementos.
     * @param ptsobtenidos    puntos obtenidos en el último intento ( >= 0).
     */
    public Leccion(int id, String nombre, String descripcion,
                   int nivelDificultad, List<Ejercicio> ejercicios, int ptsobtenidos) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.nivelDificultad = nivelDificultad;
        this.ejercicios = ejercicios;
        this.ptsobtenidos = ptsobtenidos;
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Getters
    // ─────────────────────────────────────────────────────────────────────────



    /**
     * Método getId.
     * Devuelve el identificador único de la lección.
     *
     * @return id de la lección.
     */
    public int getId() { return id; }

    /**
     * Método getNombre.
     * Devuelve el nombre corto de la lección.
     *
     * @return nombre; nunca  null.
     */
    public String getNombre() { return nombre; }

    /**
     * Método getDescripcion.
     * Devuelve la descripción de los conceptos practicados en la lección.
     *
     * @return descripción; nunca  null.
     */
    public String getDescripcion() { return descripcion; }

    /**
     * Método getNivelDificultad.
     * Devuelve el nivel de dificultad de la lección.
     *
     * @return 1 (fácil), 2 (intermedio) o 3 (difícil).
     */
    public int getNivelDificultad() { return nivelDificultad; }

    /**
     * Método getEjercicios.
     * Devuelve la lista completa de ejercicios de la lección.
     *
     * @return lista de  Ejercicio; nunca null.
     */
    public List<Ejercicio> getEjercicios() { return ejercicios; }

    /**
     * Método getTotalEjercicios.
     * Devuelve el número total de ejercicios en la lección.
     *
     * @return tamaño de la lista de ejercicios.
     */
    public int getTotalEjercicios() { return ejercicios.size(); }

    /**
     * Método getEtiquetaDificultad.
     * Devuelve la etiqueta de dificultad legible.
     *
     * @return "Fácil", "Intermedio" o "Difícil";
     * devuelve "Fácil" para valores no reconocidos.
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

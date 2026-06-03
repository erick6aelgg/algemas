package com.unam.algeplus.model;

import java.util.List;

/**
 * Clase Paso
 * Un paso dentro del procedimiento de resolución de una ecuación.
 * Contiene una lista de PasoToken que se renderiza como una fila horizontal.
 * El primer paso (índice 0) es el encabezado de solo lectura.
 *
 * @author Movilísticos - ICAT, UNAM
 * @version 1.0.1
 */
public class Paso {

    /**
     * Lista ordenada de tokens que conforman la fila del paso.
     * El orden en la lista determina el orden visual de izquierda a derecha.
     */
    private final List<PasoToken> tokens;

    /**
     * Indica si este paso es el encabezado de la ecuación original.
     * Cuando es true, la fila se muestra con estilo destacado y sin blancos interactivos.
     */
    private final boolean esEncabezado;

    // ─────────────────────────────────────────────────────────────────────────
    //  Constructor
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Crea un paso con su lista de tokens y su rol dentro del ejercicio.
     *
     * @param tokens       lista de PasoToken que conforman la fila; no debe ser null.
     * @param esEncabezado true si es el paso de encabezado (solo lectura);
     *                     false si contiene blancos interactivos.
     */
    public Paso(List<PasoToken> tokens, boolean esEncabezado) {
        this.tokens = tokens;
        this.esEncabezado = esEncabezado;
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Getters
    // ─────────────────────────────────────────────────────────────────────────

    /**Método getTokens.
     * Devuelve la lista de tokens que componen la fila del paso.
     *
     * @return lista de PasoToken; nunca null.
     */
    public List<PasoToken> getTokens() { return tokens; }

    /**
     * Método getEncabezado.
     * Indica si este paso es el encabezado de solo lectura.
     *
     * @return true si es encabezado; false si es interactivo.
     */
    public boolean esEncabezado() { return esEncabezado; }

    /**
     * Método tieneBlancos.
     * Devuelve true si el paso tiene al menos un blanco que rellenar.
     *
     * @return true si hay al menos un PasoToken en blanco
     * o false en caso contrario.
     * */
    public boolean tieneBlancos() {
        for (PasoToken t : tokens) {
            if (t.getTipo() != PasoToken.Tipo.TEXTO) return true;
        }
        return false;
    }
}

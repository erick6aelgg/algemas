package com.unam.algeplus.model;

import java.util.List;

/**
 * Un paso dentro del procedimiento de resolución de una ecuación.
 * Contiene una lista de PasoToken que se renderiza como una fila horizontal.
 * El primer paso (índice 0) es el encabezado de solo lectura.
 */
public class Paso {

    private final List<PasoToken> tokens;
    private final boolean esEncabezado; // si es true no tiene blancos interactivos

    public Paso(List<PasoToken> tokens, boolean esEncabezado) {
        this.tokens = tokens;
        this.esEncabezado = esEncabezado;
    }

    public List<PasoToken> getTokens() { return tokens; }
    public boolean esEncabezado() { return esEncabezado; }

    /** Devuelve true si el paso tiene al menos un blanco que rellenar. */
    public boolean tieneBlancos() {
        for (PasoToken t : tokens) {
            if (t.getTipo() != PasoToken.Tipo.TEXTO) return true;
        }
        return false;
    }
}

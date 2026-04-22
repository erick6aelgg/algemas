package com.unam.algeplus.model;

/**
 * Unidad mínima de un paso de resolución.
 * Puede ser texto estático, un espacio para operador (drag & drop)
 * o un espacio para número (teclado).
 */
public class PasoToken {

    public enum Tipo {
        TEXTO,       // texto fijo, no editable
        BLANCO_OP,   // hueco para arrastrar un operador (+, −, ×, ÷)
        BLANCO_NUM   // hueco para escribir un número
    }

    private final Tipo tipo;
    private final String texto;               // contenido para TEXTO
    private final String respuestaEsperada;   // para BLANCO_OP y BLANCO_NUM

    private PasoToken(Tipo tipo, String texto, String respuestaEsperada) {
        this.tipo = tipo;
        this.texto = texto;
        this.respuestaEsperada = respuestaEsperada;
    }

    // ── Fábricas estáticas ──────────────────────────────────────────────────

    public static PasoToken texto(String texto) {
        return new PasoToken(Tipo.TEXTO, texto, null);
    }

    /** @param expected operador esperado: "+", "−", "×", "÷" */
    public static PasoToken blancoOp(String expected) {
        return new PasoToken(Tipo.BLANCO_OP, null, expected);
    }

    /** @param expected número esperado como String, p.ej. "7" o "-5" */
    public static PasoToken blancoNum(String expected) {
        return new PasoToken(Tipo.BLANCO_NUM, null, expected);
    }

    // ── Getters ─────────────────────────────────────────────────────────────

    public Tipo getTipo() { return tipo; }
    public String getTexto() { return texto; }
    public String getRespuestaEsperada() { return respuestaEsperada; }
}

package com.unam.algeplus.model;

/**
 * Clase PasoToken
 * Unidad mínima de un paso de resolución.
 * Puede ser texto estático, un espacio para operador (drag & drop)
 * o un espacio para número (teclado).
 *
 * @author Movilísticos - ICAT, UNAM
 * @version 1.0.1
 */
public class PasoToken {

    /**
     * Clasificación funcional de un token dentro de la fila de un paso.
     */
    public enum Tipo {
        TEXTO,       // texto fijo, no editable
        BLANCO_OP,   // hueco para arrastrar un operador (+, −, ×, ÷)
        BLANCO_NUM   // hueco para escribir un número
    }


    private final Tipo tipo;                  // Clasificación funcional de este token.
    private final String texto;               // contenido para TEXTO
    private final String respuestaEsperada;   // para BLANCO_OP y BLANCO_NUM


    // ─────────────────────────────────────────────────────────────────────────
    //  Constructor privado (uso exclusivo de las fábricas)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Constructor privado. Usar las fábricas estáticas texto(String),
     * blancoOp(String) y blancoNum(String).
     *
     * @param tipo              clasificación del token.
     * @param texto             contenido para tipo TEXTO; null en otros casos.
     * @param respuestaEsperada respuesta correcta para huecos; null en tipo TEXTO.
     */
    private PasoToken(Tipo tipo, String texto, String respuestaEsperada) {
        this.tipo = tipo;
        this.texto = texto;
        this.respuestaEsperada = respuestaEsperada;
    }


    // ─────────────────────────────────────────────────────────────────────────
    //  Fábricas estáticas
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Método texto.
     * Crea un token de texto estático no editable.
     *
     * @param texto contenido a mostrar, p. ej. "x = 12 ".
     * @return nuevo PasoToken de tipo EXTO.
     */
    public static PasoToken texto(String texto) {
        return new PasoToken(Tipo.TEXTO, texto, null);
    }


    /**
     * Método blancoOp.
     * Crea un hueco para arrastrar un operador algebraico (drag &amp; drop).
     *
     * @param expected operador correcto esperado: "+", "−", "×" o "÷".
     * @return nuevo PasoToken de tipo BLANCO_OP.
     */
    public static PasoToken blancoOp(String expected) {
        return new PasoToken(Tipo.BLANCO_OP, null, expected);
    }


    /**
     * Método blancoNum.
     * Crea un hueco para ingresar un número entero con signo mediante teclado.
     *
     * Al verificar la cadena ingresada por el alumno se compara
     * con expected (incluyendo el signo negativo si aplica).
     *
     * @param expected número correcto esperado como cadena.
     * @return nuevo PasoToken de tipo BLANCO_NUM.
     */
    public static PasoToken blancoNum(String expected) {
        return new PasoToken(Tipo.BLANCO_NUM, null, expected);
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Getters
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Método getTipo.
     * Devuelve la clasificación funcional del token.
     *
     * @return Tipo TEXTO, BLANCO_OP o BLANCO_NUM.
     */
    public Tipo getTipo() { return tipo; }

    /**
     * Método getTexto.
     * Devuelve el contenido textual del token.
     * Solo tiene valor para tokens de tipo TEXTO.
     *
     * @return texto estático, o null si es un hueco.
     */
    public String getTexto() { return texto; }

    /**
     * Método getRespuestaEsperada.
     * Devuelve la respuesta correcta esperada para huecos interactivos.
     * Solo tiene valor para tokens de tipo BLANCO_OP y BLANCO_NUM.
     *
     * @return cadena con la respuesta esperada, o null si el token es de tipo TEXTO.
     */
    public String getRespuestaEsperada() { return respuestaEsperada; }
}

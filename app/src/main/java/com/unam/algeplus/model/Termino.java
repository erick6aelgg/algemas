package com.unam.algeplus.model;

/**
 * Clase Termino
 * Término algebraico de una ecuación.
 * Modela cada sumando o factor, indicando su tipo,
 * signo y el lado de la ecuación al que pertenece.
 *
 * @author Movilísticos - ICAT, UNAM
 *  * @version 1.0.1
 */
public class Termino {


    public enum TipoTermino { VARIABLE, CONSTANTE }  // Término que contiene incógnita
    public enum LadoEcuacion { IZQUIERDO, DERECHO }  // Término que sin la incógnita

    private float valor;         // siempre positivo
    private TipoTermino tipo;    // VARIABLE o CONSTANTE
    private int signo;           // +1 o -1
    private LadoEcuacion lado;   // Lado de la ecuación al que pertenece este término


    // ─────────────────────────────────────────────────────────────────────────
    //  Constructor
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Crea un término algebraico con todos sus atributos.
     *
     * @param valor valor absoluto del coeficiente.
     * @param tipo  VARIABLE o CONSTANTE.
     * @param signo +1 para positivo, -1 para negativo.
     * @param lado  IZQUIERDO o DERECHO.
     */
    public Termino(float valor, TipoTermino tipo, int signo, LadoEcuacion lado) {
        this.valor = valor;
        this.tipo = tipo;
        this.signo = signo;
        this.lado = lado;
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Getters y setters
    // ─────────────────────────────────────────────────────────────────────────


    /**
     * Método getValor.
     * Devuelve el valor absoluto del coeficiente.
     *
     * @return coeficiente sin signo, como double.
     */
    public double getValor() { return valor; }

    /**
     * Método setValor.
     * Actualiza el valor absoluto del coeficiente.
     *
     * @param valor nuevo coeficiente (debe ser positivo).
     */
    public void setValor(float valor) { this.valor = valor; }

    /**
     * Método getTipo.
     * Devuelve el tipo semántico del término.
     *
     * @return tipo VARIABLE o CONSTANTE.
     */
    public TipoTermino getTipo() { return tipo; }

    /**
     * Método getSigno.
     * Devuelve el signo algebraico del término.
     *
     * @return +1 si es positivo,  -1 si es negativo.
     */
    public int getSigno() { return signo; }


    /**
     * Método getValorSignado.
     * Devuelve el valor del término incluyendo su signo algebraico.
     *
     * @return valor con signo (puede ser negativo).
     */
    public double getValorSignado() { return signo * valor; }


    /**
     * Método toString.
     * Representa el término como cadena algebraica legible.
     *
     * @return representación textual del término.
     */
    @Override
    public String toString() {
        String signoStr = signo < 0 ? "−" : "+";
        if (tipo == TipoTermino.VARIABLE) {
            return signoStr + (valor == 1 ? "" : String.valueOf((int) valor)) + "x";
        }
        return signoStr + (int) valor;
    }
}

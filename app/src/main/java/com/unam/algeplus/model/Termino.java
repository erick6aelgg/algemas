package com.unam.algeplus.model;

/**
 * Término algebraico de una ecuación.
 * Modela cada sumando o factor, indicando su tipo,
 * signo y el lado de la ecuación al que pertenece.
 */
public class Termino {

    public enum TipoTermino { VARIABLE, CONSTANTE }
    public enum LadoEcuacion { IZQUIERDO, DERECHO }

    private double valor;
    private TipoTermino tipo;
    private int signo;          // +1 o -1
    private LadoEcuacion lado;

    public Termino(double valor, TipoTermino tipo, int signo, LadoEcuacion lado) {
        this.valor = valor;
        this.tipo = tipo;
        this.signo = signo;
        this.lado = lado;
    }

    public double getValor() { return valor; }
    public void setValor(double valor) { this.valor = valor; }

    public TipoTermino getTipo() { return tipo; }
    public int getSigno() { return signo; }
    public LadoEcuacion getLado() { return lado; }

    public double getValorSignado() { return signo * valor; }

    @Override
    public String toString() {
        String signoStr = signo < 0 ? "−" : "+";
        if (tipo == TipoTermino.VARIABLE) {
            return signoStr + (valor == 1 ? "" : String.valueOf((int) valor)) + "x";
        }
        return signoStr + (int) valor;
    }
}

package com.unam.algeplus.data;

import com.unam.algeplus.model.Ejercicio;
import com.unam.algeplus.model.Leccion;
import com.unam.algeplus.model.Paso;
import com.unam.algeplus.model.PasoToken;

import java.util.Arrays;
import java.util.List;

/**
 * Fuente de datos estática con las 4 lecciones y sus 20 ejercicios.
 *
 * Niveles de dificultad:
 *   1 = Fácil  (operaciones de un paso: suma/resta o mult/div)
 *   2 = Intermedio (dos pasos: suma/resta + mult/div)
 *
 * Tipos de ejercicio:
 *   PROCEDIMIENTO – muestra el despeje paso a paso con blancos
 *   BALANZA       – incluye además la vista de balanza al inicio
 *
 * Operadores que se arrastran:  "+", "−", "×", "÷"
 * Números que se teclean: cualquier entero con signo.
 */
public class LeccionesData {

    // ═══════════════════════════════════════════════════════════════════════
    //  PUNTO DE ENTRADA
    // ═══════════════════════════════════════════════════════════════════════

    public static List<Leccion> getLecciones() {
        return Arrays.asList(
                leccion0(),
                leccion1(),
                leccion2(),
                leccion3(),
                leccion4()
        );
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  LECCIÓN 1 – Suma y Resta Simples  (nivel 1)
    // ═══════════════════════════════════════════════════════════════════════

    private static Leccion leccion0() {
        return new Leccion(1,
                "Prueba de ecuaciones",
                "Despeja x usando sumas y restas de un solo paso.",
                1,
                Arrays.asList(
                        ej1_1(), ej1_2()
                ));
    }

    private static Leccion leccion1() {
        return new Leccion(2,
                "Suma y Resta Simples",
                "Despeja x usando sumas y restas de un solo paso.",
                1,
                Arrays.asList(
                        ej1_1(), ej1_2(), ej1_3(), ej1_4(), ej1_5()
                ));
    }

    /** x + 5 = 12  →  x = 7  (BALANZA) */
    private static Ejercicio ej1_1() {
        return new Ejercicio(
                101, "x + 5 = 12", Ejercicio.Tipo.BALANZA,
                Arrays.asList(
                        paso(true,  t("x + 5 = 12")),
                        paso(false, t("x = 12 "), op("−"), t(" 5")),
                        paso(false, t("x = "), num("7"))
                ),
                "Lo que está sumando a un lado pasa restando al otro lado de la igualdad.",
                1, "x + 5", "12"
        );
    }

    /** x − 8 = 10  →  x = 18  (PROCEDIMIENTO) */
    private static Ejercicio ej1_2() {
        return new Ejercicio(
                102, "x − 8 = 10", Ejercicio.Tipo.PROCEDIMIENTO,
                Arrays.asList(
                        paso(true,  t("x − 8 = 10")),
                        paso(false, t("x = 10 "), op("+"), t(" 8")),
                        paso(false, t("x = "), num("18"))
                ),
                "Lo que está restando pasa sumando al otro lado de la ecuación.",
                1
        );
    }

    /** x + 15 = 30  →  x = 15  (PROCEDIMIENTO) */
    private static Ejercicio ej1_3() {
        return new Ejercicio(
                103, "x + 15 = 30", Ejercicio.Tipo.PROCEDIMIENTO,
                Arrays.asList(
                        paso(true,  t("x + 15 = 30")),
                        paso(false, t("x = 30 "), op("−"), t(" 15")),
                        paso(false, t("x = "), num("15"))
                ),
                "Recuerda: la igualdad se mantiene si aplicas la misma operación en ambos lados.",
                1
        );
    }

    /** x − 7 = 3  →  x = 10  (BALANZA) */
    private static Ejercicio ej1_4() {
        return new Ejercicio(
                104, "x − 7 = 3", Ejercicio.Tipo.BALANZA,
                Arrays.asList(
                        paso(true,  t("x − 7 = 3")),
                        paso(false, t("x = 3 "), op("+"), t(" 7")),
                        paso(false, t("x = "), num("10"))
                ),
                "Lo que resta a x pasa sumando al lado opuesto del signo igual.",
                1, "x − 7", "3"
        );
    }

    /** x + 10 = 5  →  x = −5  (PROCEDIMIENTO, resultado negativo) */
    private static Ejercicio ej1_5() {
        return new Ejercicio(
                105, "x + 10 = 5", Ejercicio.Tipo.PROCEDIMIENTO,
                Arrays.asList(
                        paso(true,  t("x + 10 = 5")),
                        paso(false, t("x = 5 "), op("−"), t(" 10")),
                        paso(false, t("x = "), num("-5"))
                ),
                "El resultado puede ser negativo: 5 menos 10 es menor que cero.",
                1
        );
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  LECCIÓN 2 – Multiplicación y División Simples  (nivel 1)
    // ═══════════════════════════════════════════════════════════════════════

    private static Leccion leccion2() {
        return new Leccion(3,
                "Multiplicación y División",
                "Despeja x usando multiplicación o división.",
                1,
                Arrays.asList(
                        ej2_1(), ej2_2(), ej2_3(), ej2_4(), ej2_5()
                ));
    }

    /** 3x = 15  →  x = 5  (BALANZA) */
    private static Ejercicio ej2_1() {
        return new Ejercicio(
                201, "3x = 15", Ejercicio.Tipo.BALANZA,
                Arrays.asList(
                        paso(true,  t("3x = 15")),
                        paso(false, t("x = 15 "), op("÷"), t(" 3")),
                        paso(false, t("x = "), num("5"))
                ),
                "Lo que está multiplicando a x pasa dividiendo al otro lado.",
                1, "3x", "15"
        );
    }

    /** x ÷ 4 = 2  →  x = 8  (PROCEDIMIENTO) */
    private static Ejercicio ej2_2() {
        return new Ejercicio(
                202, "x ÷ 4 = 2", Ejercicio.Tipo.PROCEDIMIENTO,
                Arrays.asList(
                        paso(true,  t("x ÷ 4 = 2")),
                        paso(false, t("x = 2 "), op("×"), t(" 4")),
                        paso(false, t("x = "), num("8"))
                ),
                "Lo que está dividiendo a x pasa multiplicando al otro lado.",
                1
        );
    }

    /** 2x = 20  →  x = 10  (PROCEDIMIENTO) */
    private static Ejercicio ej2_3() {
        return new Ejercicio(
                203, "2x = 20", Ejercicio.Tipo.PROCEDIMIENTO,
                Arrays.asList(
                        paso(true,  t("2x = 20")),
                        paso(false, t("x = 20 "), op("÷"), t(" 2")),
                        paso(false, t("x = "), num("10"))
                ),
                "Para despejar x, divide ambos lados entre el coeficiente que la acompaña.",
                1
        );
    }

    /** 5x = 50  →  x = 10  (BALANZA) */
    private static Ejercicio ej2_4() {
        return new Ejercicio(
                204, "5x = 50", Ejercicio.Tipo.BALANZA,
                Arrays.asList(
                        paso(true,  t("5x = 50")),
                        paso(false, t("x = 50 "), op("÷"), t(" 5")),
                        paso(false, t("x = "), num("10"))
                ),
                "El coeficiente de x pasa dividiendo al otro lado de la igualdad.",
                1, "5x", "50"
        );
    }

    /** x ÷ 2 = 12  →  x = 24  (PROCEDIMIENTO) */
    private static Ejercicio ej2_5() {
        return new Ejercicio(
                205, "x ÷ 2 = 12", Ejercicio.Tipo.PROCEDIMIENTO,
                Arrays.asList(
                        paso(true,  t("x ÷ 2 = 12")),
                        paso(false, t("x = 12 "), op("×"), t(" 2")),
                        paso(false, t("x = "), num("24"))
                ),
                "Dividir es la operación inversa de multiplicar; úsala para despejar x.",
                1
        );
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  LECCIÓN 3 – Ecuaciones de Dos Pasos I  (nivel 2)
    // ═══════════════════════════════════════════════════════════════════════

    private static Leccion leccion3() {
        return new Leccion(4,
                "Dos Pasos I",
                "Primero despeja sumas/restas; luego multiplicaciones/divisiones.",
                2,
                Arrays.asList(
                        ej3_1(), ej3_2(), ej3_3(), ej3_4(), ej3_5()
                ));
    }

    /** 2x + 4 = 10  →  x = 3 */
    private static Ejercicio ej3_1() {
        return new Ejercicio(
                301, "2x + 4 = 10", Ejercicio.Tipo.PROCEDIMIENTO,
                Arrays.asList(
                        paso(true,  t("2x + 4 = 10")),
                        paso(false, t("2x = 10 "), op("−"), t(" 4")),
                        paso(false, t("2x = "), num("6")),
                        paso(false, t("x = 6 "), op("÷"), t(" 2")),
                        paso(false, t("x = "), num("3"))
                ),
                "Primero elimina la suma o resta; después elimina la multiplicación.",
                2
        );
    }

    /** 3x − 5 = 10  →  x = 5 */
    private static Ejercicio ej3_2() {
        return new Ejercicio(
                302, "3x − 5 = 10", Ejercicio.Tipo.PROCEDIMIENTO,
                Arrays.asList(
                        paso(true,  t("3x − 5 = 10")),
                        paso(false, t("3x = 10 "), op("+"), t(" 5")),
                        paso(false, t("3x = "), num("15")),
                        paso(false, t("x = 15 "), op("÷"), t(" 3")),
                        paso(false, t("x = "), num("5"))
                ),
                "Lo que resta pasa sumando; luego el coeficiente pasa dividiendo.",
                2
        );
    }

    /** 5x + 2 = 17  →  x = 3 */
    private static Ejercicio ej3_3() {
        return new Ejercicio(
                303, "5x + 2 = 17", Ejercicio.Tipo.PROCEDIMIENTO,
                Arrays.asList(
                        paso(true,  t("5x + 2 = 17")),
                        paso(false, t("5x = 17 "), op("−"), t(" 2")),
                        paso(false, t("5x = "), num("15")),
                        paso(false, t("x = 15 "), op("÷"), t(" 5")),
                        paso(false, t("x = "), num("3"))
                ),
                "En dos pasos: primero despeja la suma, luego la multiplicación.",
                2
        );
    }

    /** 4x − 8 = 12  →  x = 5 */
    private static Ejercicio ej3_4() {
        return new Ejercicio(
                304, "4x − 8 = 12", Ejercicio.Tipo.PROCEDIMIENTO,
                Arrays.asList(
                        paso(true,  t("4x − 8 = 12")),
                        paso(false, t("4x = 12 "), op("+"), t(" 8")),
                        paso(false, t("4x = "), num("20")),
                        paso(false, t("x = 20 "), op("÷"), t(" 4")),
                        paso(false, t("x = "), num("5"))
                ),
                "La resta pasa sumando y el coeficiente pasa dividiendo al otro lado.",
                2
        );
    }

    /** x ÷ 3 + 1 = 4  →  x = 9 */
    private static Ejercicio ej3_5() {
        return new Ejercicio(
                305, "x ÷ 3 + 1 = 4", Ejercicio.Tipo.PROCEDIMIENTO,
                Arrays.asList(
                        paso(true,  t("x ÷ 3 + 1 = 4")),
                        paso(false, t("x ÷ 3 = 4 "), op("−"), t(" 1")),
                        paso(false, t("x ÷ 3 = "), num("3")),
                        paso(false, t("x = 3 "), op("×"), t(" 3")),
                        paso(false, t("x = "), num("9"))
                ),
                "Recuerda que la división pasa multiplicando al otro lado de la igualdad.",
                2
        );
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  LECCIÓN 4 – Ecuaciones de Dos Pasos II  (nivel 2)
    // ═══════════════════════════════════════════════════════════════════════

    private static Leccion leccion4() {
        return new Leccion(5,
                "Dos Pasos II",
                "Practica más ecuaciones de dos pasos con distintos coeficientes.",
                2,
                Arrays.asList(
                        ej4_1(), ej4_2(), ej4_3(), ej4_4(), ej4_5()
                ));
    }

    /** 2x − 10 = 0  →  x = 5 */
    private static Ejercicio ej4_1() {
        return new Ejercicio(
                401, "2x − 10 = 0", Ejercicio.Tipo.PROCEDIMIENTO,
                Arrays.asList(
                        paso(true,  t("2x − 10 = 0")),
                        paso(false, t("2x = 0 "), op("+"), t(" 10")),
                        paso(false, t("2x = "), num("10")),
                        paso(false, t("x = 10 "), op("÷"), t(" 2")),
                        paso(false, t("x = "), num("5"))
                ),
                "El cero no cambia nada en la suma: 0 + 10 = 10.",
                2
        );
    }

    /** 6x + 6 = 36  →  x = 5 */
    private static Ejercicio ej4_2() {
        return new Ejercicio(
                402, "6x + 6 = 36", Ejercicio.Tipo.PROCEDIMIENTO,
                Arrays.asList(
                        paso(true,  t("6x + 6 = 36")),
                        paso(false, t("6x = 36 "), op("−"), t(" 6")),
                        paso(false, t("6x = "), num("30")),
                        paso(false, t("x = 30 "), op("÷"), t(" 6")),
                        paso(false, t("x = "), num("5"))
                ),
                "Aplica las operaciones inversas en el orden correcto.",
                2
        );
    }

    /** 7x − 3 = 11  →  x = 2 */
    private static Ejercicio ej4_3() {
        return new Ejercicio(
                403, "7x − 3 = 11", Ejercicio.Tipo.PROCEDIMIENTO,
                Arrays.asList(
                        paso(true,  t("7x − 3 = 11")),
                        paso(false, t("7x = 11 "), op("+"), t(" 3")),
                        paso(false, t("7x = "), num("14")),
                        paso(false, t("x = 14 "), op("÷"), t(" 7")),
                        paso(false, t("x = "), num("2"))
                ),
                "Cada paso despeja un elemento: primero la suma/resta, luego el coeficiente.",
                2
        );
    }

    /** x ÷ 5 − 2 = 1  →  x = 15 */
    private static Ejercicio ej4_4() {
        return new Ejercicio(
                404, "x ÷ 5 − 2 = 1", Ejercicio.Tipo.PROCEDIMIENTO,
                Arrays.asList(
                        paso(true,  t("x ÷ 5 − 2 = 1")),
                        paso(false, t("x ÷ 5 = 1 "), op("+"), t(" 2")),
                        paso(false, t("x ÷ 5 = "), num("3")),
                        paso(false, t("x = 3 "), op("×"), t(" 5")),
                        paso(false, t("x = "), num("15"))
                ),
                "La división pasa como multiplicación al otro lado de la igualdad.",
                2
        );
    }

    /** 8x + 4 = 20  →  x = 2 */
    private static Ejercicio ej4_5() {
        return new Ejercicio(
                405, "8x + 4 = 20", Ejercicio.Tipo.PROCEDIMIENTO,
                Arrays.asList(
                        paso(true,  t("8x + 4 = 20")),
                        paso(false, t("8x = 20 "), op("−"), t(" 4")),
                        paso(false, t("8x = "), num("16")),
                        paso(false, t("x = 16 "), op("÷"), t(" 8")),
                        paso(false, t("x = "), num("2"))
                ),
                "Recuerda: primero elimina la suma, luego divide entre el coeficiente de x.",
                2
        );
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  HELPERS DE CONSTRUCCIÓN (reducen verbosidad)
    // ═══════════════════════════════════════════════════════════════════════

    private static Paso paso(boolean esHeader, PasoToken... tokens) {
        return new Paso(Arrays.asList(tokens), esHeader);
    }

    private static PasoToken t(String texto) {
        return PasoToken.texto(texto);
    }

    private static PasoToken op(String expected) {
        return PasoToken.blancoOp(expected);
    }

    private static PasoToken num(String expected) {
        return PasoToken.blancoNum(expected);
    }
}

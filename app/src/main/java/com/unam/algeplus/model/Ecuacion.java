package com.unam.algeplus.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

/**
 * Entidad Room que representa un intento de resolución de ecuación.
 * Se persiste en la tabla "Ecuacion" para el historial del usuario.
 */
@Entity(tableName = "Ecuacion")
public class Ecuacion {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    private String ecuacion;   // expresión de la ecuación, p.ej. "x + 5 = 12"

    @NonNull
    private String resultado;  // valor de x resuelto, p.ej. "7"

    @NonNull
    private String fecha;      // fecha ISO-8601 del intento

    public Ecuacion(@NonNull String ecuacion, @NonNull String resultado, @NonNull String fecha) {
        this.ecuacion = ecuacion;
        this.resultado = resultado;
        this.fecha = fecha;
    }

    // ── Getters y setters ───────────────────────────────────────────────────

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    @NonNull public String getEcuacion() { return ecuacion; }
    public void setEcuacion(@NonNull String ecuacion) { this.ecuacion = ecuacion; }

    @NonNull public String getResultado() { return resultado; }
    public void setResultado(@NonNull String resultado) { this.resultado = resultado; }

    @NonNull public String getFecha() { return fecha; }
    public void setFecha(@NonNull String fecha) { this.fecha = fecha; }
}

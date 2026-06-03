package com.unam.algeplus.adapter;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.unam.algeplus.R;
import com.unam.algeplus.model.Leccion;
import com.unam.algeplus.model.ProgresoLeccion;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Clase LeccionesAdapter.
 * Adaptador para el RecyclerView que muestra la lista de lecciones disponibles.
 *
 * Responsabilidades:
 * Inflar y reciclar vistas de ítem de lección.
 * Mostrar nombre, descripción, nivel de dificultad y estado de progreso.
 * Colorear la barra lateral y los puntos de dificultad según el nivel.
 * Notificar a la OnLeccionClickListener cuando el usuario selecciona una lección.
 *
 * @author Movilísticos - ICAT, UNAM
 * @version 1.0.1
 */

public class LeccionesAdapter extends ListAdapter<Leccion, LeccionesAdapter.LeccionViewHolder> {

    public interface OnLeccionClickListener {
        void onLeccionClick(Leccion leccion);
    }

    /**Listener que recibe las pulsaciones sobre los ítems de la lista.*/
    private final OnLeccionClickListener listener;

    /**
     * Mapa que asocia el id de cada lección con su ProgresoLeccion actual.
     * Se actualiza mediante setProgreso(List) cada vez que Room emite cambios.
     */
    private final Map<Integer, ProgresoLeccion> progresoPorLeccion = new HashMap<>();

    /**
     * Criterio de diferenciación usado por DiffUtil para determinar
     * qué ítems han cambiado y actualizar solo esos.
     */
    private static final DiffUtil.ItemCallback<Leccion> DIFF =
            new DiffUtil.ItemCallback<Leccion>() {

                /**
                 * Método areItemsTheSame.
                 * Compara por id (identidad del objeto de dominio).
                 * @param a Item a comparar
                 * @param b Item a comparar
                 * @return true son los mismos items, false es caso contrario
                 * */
                @Override
                public boolean areItemsTheSame(@NonNull Leccion a, @NonNull Leccion b) {
                    return a.getId() == b.getId();
                }

                /**
                 * Método areContentsTheSame.
                 * Compara id y nombre (contenido visible).
                 * @param a Item a comparar
                 * @param b Item a comparar
                 * @return true si el contenido es el mismo, false en caso contrario
                 * */
                @Override
                public boolean areContentsTheSame(@NonNull Leccion a, @NonNull Leccion b) {
                    return a.getId() == b.getId() && a.getNombre().equals(b.getNombre());
                }
            };

    // ─────────────────────────────────────────────────────────────────────────
    //  Constructor
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Método LeccionesAdapter.
     * Crea el adaptador con el listener de clics.
     *
     * @param listener callback que se invoca al seleccionar una lección; no debe ser null.
     */
    public LeccionesAdapter(OnLeccionClickListener listener) {
        super(DIFF);
        this.listener = listener;
    }


    // ─────────────────────────────────────────────────────────────────────────
    //  API de datos
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Método setProgreso.
     * Actualiza el mapa de progreso y recarga todos los ítems visibles.
     *
     * Debe llamarse cada vez que Room emite una nueva lista de ProgresoLeccion
     * para que las etiquetas "Iniciada" / "Completada" reflejen el estado actual.
     *
     * @param progresos lista de progresos del usuario activo; puede ser null
     */
    public void setProgreso(List<ProgresoLeccion> progresos) {
        progresoPorLeccion.clear();
        if (progresos != null) {
            for (ProgresoLeccion progreso : progresos) {
                progresoPorLeccion.put(progreso.getLeccionId(), progreso);
            }
        }
        notifyDataSetChanged();
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  RecyclerView.Adapter
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Método onCreateViewHolder. Infla la vista del ítem de lección y crea el ViewHolder correspondiente.
     *
     * @param parent contenedor del RecyclerView.
     * @param viewType tipo de vista.
     * @return nuevo LeccionViewHolder listo para enlazar datos.
     */
    @NonNull
    @Override
    public LeccionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_leccion, parent, false);
        return new LeccionViewHolder(v);
    }


    /**
     * Enlaza los datos de la lección en el position-ésimo ítem de la lista.
     *
     * @param holder ViewHolder en el que se escriben los datos.
     * @param position índice de la lección en la lista actual.
     */
    @Override
    public void onBindViewHolder(@NonNull LeccionViewHolder holder, int position) {
        Leccion leccion = getItem(position);
        holder.bind(leccion, progresoPorLeccion.get(leccion.getId()), listener);
    }


    // ─────────────────────────────────────────────────────────────────────────
    //  ViewHolder
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * ViewHolder que mantiene las referencias a las vistas del ítem de lección
     */
    static class LeccionViewHolder extends RecyclerView.ViewHolder {

        /** Barra lateral de color que indica el nivel de dificultad. */
        private final View difficultyBar;
        /** Número de la lección mostrado con dos dígitos (01, 02…). */
        private final TextView tvNumero;
        /** Nombre de la lección. */
        private final TextView tvNombre;
        /** Descripción breve de los conceptos practicados. */
        private final TextView tvDescripcion;
        /** Etiqueta de dificultad y estado (p. ej. "Fácil - Completada"). */
        private final TextView tvEtiqueta;
        /** Botón de inicio de la lección. */
        private final Button btnPlay;
        /** Primer punto de dificultad (siempre activo). */
        private final View dot1;
        /** Segundo punto de dificultad (activo a partir de nivel 2). */
        private final View dot2;
        /** Tercer punto de dificultad (activo solo en nivel 3). */
        private final View dot3;


        /**
         * Crea el ViewHolder y enlaza las referencias a las vistas del ítem.
         *
         * @param itemView vista raíz inflada desde item_leccion.xml.
         */
        LeccionViewHolder(@NonNull View itemView) {
            super(itemView);
            difficultyBar = itemView.findViewById(R.id.difficultyBar);
            tvNumero = itemView.findViewById(R.id.tvNumero);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvDescripcion = itemView.findViewById(R.id.tvDescripcion);
            tvEtiqueta = itemView.findViewById(R.id.tvEtiqueta);
            btnPlay = itemView.findViewById(R.id.btnPlay);
            dot1 = itemView.findViewById(R.id.dot1);
            dot2 = itemView.findViewById(R.id.dot2);
            dot3 = itemView.findViewById(R.id.dot3);
        }


        /**
         * Método bind.
         * Rellena las vistas con los datos de la lección y su progreso.
         *
         * Aplica el color de dificultad a la barra lateral y a los puntos.
         * Muestra la etiqueta combinada de dificultad y estado.
         * Registra los listeners de clic en el botón y en el ítem completo.
         *
         * @param leccion  datos de la lección a mostrar.
         * @param progreso progreso persistido del usuario, o null si nunca la inició.
         * @param listener callback invocado al pulsar la lección.
         */
        void bind(Leccion leccion, ProgresoLeccion progreso, OnLeccionClickListener listener) {
            tvNumero.setText(String.format("%02d", leccion.getId()));
            tvNombre.setText(leccion.getNombre());
            tvDescripcion.setText(leccion.getDescripcion());
            tvEtiqueta.setText(getEtiqueta(leccion, progreso));

            switch (leccion.getNivelDificultad()) {
                case 1:
                    difficultyBar.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.colorEasyBack));
                    tvEtiqueta.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.colorEasy));
                    setDots(
                            ContextCompat.getColor(itemView.getContext(), R.color.colorEasy),
                            ContextCompat.getColor(itemView.getContext(), R.color.colorEasyBack),
                            false,
                            false
                    );
                    break;
                case 2:
                    difficultyBar.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.colorMediumBack));
                    tvEtiqueta.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.colorMedium));
                    setDots(
                            ContextCompat.getColor(itemView.getContext(), R.color.colorMedium),
                            ContextCompat.getColor(itemView.getContext(), R.color.colorMediumBack),
                            true,
                            false
                    );
                    break;
                default:
                    difficultyBar.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.colorHardBack));
                    tvEtiqueta.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.colorHard));
                    setDots(
                            ContextCompat.getColor(itemView.getContext(), R.color.colorHard),
                            ContextCompat.getColor(itemView.getContext(), R.color.colorHardBack),
                            true,
                            true
                    );
                    break;
            }

            btnPlay.setOnClickListener(v -> listener.onLeccionClick(leccion));
            itemView.setOnClickListener(v -> listener.onLeccionClick(leccion));
        }

        /**
         * Método getEtiqueta.
         * Construye la etiqueta de dificultad y estado que se muestra en el ítem.
         *
         * Si hay progreso registrado, añade el estado "Completada" o
         * "Iniciada".
         *
         * @param leccion  lección de la que se obtiene la etiqueta base de dificultad.
         * @param progreso progreso del usuario, o null si no ha iniciado la lección.
         * @return cadena combinada.
         */
        private String getEtiqueta(Leccion leccion, ProgresoLeccion progreso) {
            String etiqueta = leccion.getEtiquetaDificultad();
            if (progreso == null) return etiqueta;
            String estado = progreso.isCompletada() ? "Completada" : "Iniciada";
            return etiqueta + " - " + estado;
        }


        /**
         * Método setDots.
         * Aplica los colores a los tres puntos indicadores de dificultad.
         *
         * @param colorActivo  color de los puntos que representan el nivel alcanzado.
         * @param colorInactivo color de los puntos que representan niveles no alcanzados.
         * @param dot2Active   true si el segundo punto debe mostrarse activo.
         * @param dot3Active   true si el tercer punto debe mostrarse activo.
         */
        private void setDots(int colorActivo, int colorInactivo,
                             boolean dot2Active, boolean dot3Active) {
            dot1.setBackgroundTintList(ColorStateList.valueOf(colorActivo));
            dot2.setBackgroundTintList(ColorStateList.valueOf(dot2Active ? colorActivo : colorInactivo));
            dot3.setBackgroundTintList(ColorStateList.valueOf(dot3Active ? colorActivo : colorInactivo));
        }
    }
}

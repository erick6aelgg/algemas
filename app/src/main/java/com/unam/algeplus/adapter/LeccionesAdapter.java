package com.unam.algeplus.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import androidx.core.content.ContextCompat;
import android.content.res.ColorStateList;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.unam.algeplus.R;
import com.unam.algeplus.model.Leccion;

/**
 * Adapter para el RecyclerView de lecciones.
 *
 * Patrón Infinite List: usa ListAdapter + DiffUtil para actualizaciones eficientes.
 * Patrón List Inlay: muestra nombre + descripción + barra de dificultad por cada ítem.
 */
public class LeccionesAdapter extends ListAdapter<Leccion, LeccionesAdapter.LeccionViewHolder> {

    public interface OnLeccionClickListener {
        void onLeccionClick(Leccion leccion);
    }

    private final OnLeccionClickListener listener;

    private static final DiffUtil.ItemCallback<Leccion> DIFF =
            new DiffUtil.ItemCallback<Leccion>() {
                @Override public boolean areItemsTheSame(@NonNull Leccion a, @NonNull Leccion b) {
                    return a.getId() == b.getId();
                }
                @Override public boolean areContentsTheSame(@NonNull Leccion a, @NonNull Leccion b) {
                    return a.getId() == b.getId() && a.getNombre().equals(b.getNombre());
                }
            };

    public LeccionesAdapter(OnLeccionClickListener listener) {
        super(DIFF);
        this.listener = listener;
    }

    @NonNull
    @Override
    public LeccionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_leccion, parent, false);
        return new LeccionViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull LeccionViewHolder holder, int position) {
        holder.bind(getItem(position), listener);
    }

    // ── ViewHolder ────────────────────────────────────────────────────────────

    static class LeccionViewHolder extends RecyclerView.ViewHolder {

        private final View     difficultyBar;
        private final TextView tvNumero;
        private final TextView tvNombre;
        private final TextView tvDescripcion;
        private final TextView tvEtiqueta;
        //private final ProgressBar pbDificultad;
        private final Button btnPlay;

        private final View dot1, dot2, dot3;

        LeccionViewHolder(@NonNull View itemView) {
            super(itemView);
            difficultyBar = itemView.findViewById(R.id.difficultyBar);
            tvNumero      = itemView.findViewById(R.id.tvNumero);
            tvNombre      = itemView.findViewById(R.id.tvNombre);
            tvDescripcion = itemView.findViewById(R.id.tvDescripcion);
            tvEtiqueta    = itemView.findViewById(R.id.tvEtiqueta);
            //pbDificultad  = itemView.findViewById(R.id.pbDificultad);
            btnPlay       = itemView.findViewById(R.id.btnPlay);
            dot1 = itemView.findViewById(R.id.dot1);
            dot2 = itemView.findViewById(R.id.dot2);
            dot3 = itemView.findViewById(R.id.dot3);
        }

        void bind(Leccion leccion, OnLeccionClickListener listener) {
            tvNumero.setText(String.format("%02d", leccion.getId()));
            tvNombre.setText(leccion.getNombre());
            tvDescripcion.setText(leccion.getDescripcion());
            tvEtiqueta.setText(leccion.getEtiquetaDificultad());

            switch (leccion.getNivelDificultad()) {
                case 1:
                    difficultyBar.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.colorEasyBack));
                    tvEtiqueta.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.colorEasy));
                    setDots(
                            ContextCompat.getColor(itemView.getContext(), R.color.colorEasy),
                            ContextCompat.getColor(itemView.getContext(), R.color.colorEasyBack),
                            false, false
                    );
                    break;
                case 2:
                    difficultyBar.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.colorMediumBack));
                    tvEtiqueta.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.colorMedium));
                    setDots(
                            ContextCompat.getColor(itemView.getContext(), R.color.colorMedium),
                            ContextCompat.getColor(itemView.getContext(), R.color.colorMediumBack),
                            true, false
                    );
                    break;
                default:
                    difficultyBar.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.colorHardBack));
                    tvEtiqueta.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.colorHard));
                    setDots(
                            ContextCompat.getColor(itemView.getContext(), R.color.colorHard),
                            ContextCompat.getColor(itemView.getContext(), R.color.colorHardBack),
                            true, true
                    );
                    break;
            }

            btnPlay.setOnClickListener(v -> listener.onLeccionClick(leccion));
            itemView.setOnClickListener(v -> listener.onLeccionClick(leccion));
        }
        private void setDots(int colorActivo , int colorInactivo, boolean dot2Active, boolean dot3Active) {
            dot1.setBackgroundTintList(ColorStateList.valueOf(colorActivo));
            dot2.setBackgroundTintList(ColorStateList.valueOf(dot2Active ? colorActivo : colorInactivo));
            dot3.setBackgroundTintList(ColorStateList.valueOf(dot3Active ? colorActivo : colorInactivo));
        }
    }
}

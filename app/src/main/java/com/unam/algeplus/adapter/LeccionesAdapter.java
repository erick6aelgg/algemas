package com.unam.algeplus.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
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
        private final ProgressBar pbDificultad;
        private final Button btnPlay;

        LeccionViewHolder(@NonNull View itemView) {
            super(itemView);
            difficultyBar = itemView.findViewById(R.id.difficultyBar);
            tvNumero      = itemView.findViewById(R.id.tvNumero);
            tvNombre      = itemView.findViewById(R.id.tvNombre);
            tvDescripcion = itemView.findViewById(R.id.tvDescripcion);
            tvEtiqueta    = itemView.findViewById(R.id.tvEtiqueta);
            pbDificultad  = itemView.findViewById(R.id.pbDificultad);
            btnPlay       = itemView.findViewById(R.id.btnPlay);
        }

        void bind(Leccion leccion, OnLeccionClickListener listener) {
            tvNumero.setText(String.format("%02d", leccion.getId()));
            tvNombre.setText(leccion.getNombre());
            tvDescripcion.setText(leccion.getDescripcion()); // List Inlay
            //tvEtiqueta.setText(leccion.getEtiquetaDificultad());

            // Barra de progreso verde→rojo según dificultad
            int colorBar, progressValue;
            switch (leccion.getNivelDificultad()) {
                case 1:
                    colorBar      = Color.parseColor("#4CAF50");
                    progressValue = 33;
                    break;
                case 2:
                    colorBar      = Color.parseColor("#FF9800");
                    progressValue = 66;
                    break;
                default:
                    colorBar      = Color.parseColor("#F44336");
                    progressValue = 100;
                    break;
            }
            difficultyBar.setBackgroundColor(colorBar);
            pbDificultad.setProgress(progressValue);
            // Cambiar color de la barra de progreso
            pbDificultad.getProgressDrawable().setColorFilter(
                    colorBar, android.graphics.PorterDuff.Mode.SRC_IN);

            tvEtiqueta.setTextColor(colorBar);

            btnPlay.setOnClickListener(v -> listener.onLeccionClick(leccion));
            itemView.setOnClickListener(v -> listener.onLeccionClick(leccion));
        }
    }
}

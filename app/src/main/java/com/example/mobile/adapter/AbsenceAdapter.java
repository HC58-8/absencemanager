package com.example.mobile.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile.R;
import com.example.mobile.model.Absence;

import java.util.List;

public class AbsenceAdapter extends RecyclerView.Adapter<AbsenceAdapter.AbsenceViewHolder> {
    private List<Absence> absences;

    // Constructeur pour passer la liste des absences
    public AbsenceAdapter(List<Absence> absences) {
        this.absences = absences;
    }

    @NonNull
    @Override
    public AbsenceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Gonfler la vue à partir de l'élément XML
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_absence, parent, false);
        return new AbsenceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AbsenceViewHolder holder, int position) {
        // Obtenez l'objet absence pour la position actuelle
        Absence absence = absences.get(position);

        // Remplir les données dans les TextViews
        holder.dateTextView.setText("Date : " + absence.getDate());
        holder.heureTextView.setText("Heure : " + absence.getHeure());
        holder.classeTextView.setText("Classe : " + absence.getClasse());
        holder.nomEnseignantTextView.setText("Enseignant : " + absence.getNomEnseignant());
    }

    @Override
    public int getItemCount() {
        // Nombre total d'éléments dans la liste
        return absences.size();
    }

    // Classe interne pour définir le ViewHolder
    public static class AbsenceViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView;
        TextView heureTextView;
        TextView classeTextView;
        TextView nomEnseignantTextView;

        public AbsenceViewHolder(@NonNull View itemView) {
            super(itemView);

            // Lier les IDs des TextViews
            dateTextView = itemView.findViewById(R.id.dateTextView);
            heureTextView = itemView.findViewById(R.id.heureTextView);
            classeTextView = itemView.findViewById(R.id.classeTextView);
            nomEnseignantTextView = itemView.findViewById(R.id.nomEnseignantTextView);
        }
    }
}

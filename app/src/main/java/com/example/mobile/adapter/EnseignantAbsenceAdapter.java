package com.example.mobile.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile.R;
import com.example.mobile.model.Absence;
import com.example.mobile.model.User;

import java.util.List;
import java.util.Map;

public class EnseignantAbsenceAdapter extends RecyclerView.Adapter<EnseignantAbsenceAdapter.ViewHolder> {

    private Map<User, List<Absence>> enseignantsAbsences;

    public EnseignantAbsenceAdapter(Map<User, List<Absence>> enseignantsAbsences) {
        this.enseignantsAbsences = enseignantsAbsences;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_enseignant, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = (User) enseignantsAbsences.keySet().toArray()[position];  // Récupère l'enseignant en fonction de la position
        List<Absence> userAbsences = enseignantsAbsences.get(user);

        // Affichage du nom de l'enseignant
        holder.tvEnseignantName.setText("Nom de l'enseignant : " + user.getName());

        // Affichage du nombre d'absences
        holder.tvAbsencesCount.setText("Absences : " + userAbsences.size());

        // Vérifie si l'enseignant a des absences
        if (userAbsences != null && !userAbsences.isEmpty()) {
            StringBuilder absencesDetails = new StringBuilder();
            for (Absence absence : userAbsences) {
                absencesDetails.append("Date : ").append(absence.getDate())
                        .append("\nHeure : ").append(absence.getHeure())
                        .append("\nAgent : ").append(absence.getNomAgent())
                        .append("\n\n");
            }

            // Affiche les détails des absences
            holder.tvAbsenceDate.setText(absencesDetails.toString());
            holder.tvAbsenceHeure.setVisibility(View.GONE);  // Inutile car déjà dans les détails
            holder.tvAbsenceAgent.setVisibility(View.GONE);  // Inutile car déjà dans les détails
        } else {
            // Cache les détails des absences si aucune absence
            holder.tvAbsenceDate.setText("");
            holder.tvAbsenceHeure.setVisibility(View.GONE);
            holder.tvAbsenceAgent.setVisibility(View.GONE);
        }
    }


    @Override
    public int getItemCount() {
        return enseignantsAbsences.size();  // Retourne le nombre d'enseignants (taille de la map)
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvEnseignantName;
        public TextView tvAbsencesCount;
        public TextView tvAbsenceDate;
        public TextView tvAbsenceHeure;
        public TextView tvAbsenceAgent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEnseignantName = itemView.findViewById(R.id.tvEnseignantName);
            tvAbsencesCount = itemView.findViewById(R.id.tvAbsencesCount);
            tvAbsenceDate = itemView.findViewById(R.id.tvAbsenceDate);
            tvAbsenceHeure = itemView.findViewById(R.id.tvAbsenceHeure);
            tvAbsenceAgent = itemView.findViewById(R.id.tvAbsenceAgent);
        }
    }
}

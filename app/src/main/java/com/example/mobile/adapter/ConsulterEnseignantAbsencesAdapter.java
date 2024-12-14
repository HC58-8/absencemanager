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

public class ConsulterEnseignantAbsencesAdapter extends RecyclerView.Adapter<ConsulterEnseignantAbsencesAdapter.ViewHolder> {

    private List<Absence> absenceList;

    // Constructeur avec la liste des absences
    public ConsulterEnseignantAbsencesAdapter(List<Absence> absenceList) {
        this.absenceList = absenceList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate le layout item_absence pour chaque élément de la liste
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_absence, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Absence absence = absenceList.get(position);

        // Affichage des informations sur l'absence
        holder.tvAbsenceDate.setText("Date: " + absence.getDate());
        holder.tvAbsenceHeure.setText("Heure: " + absence.getHeure());
        holder.tvAbsenceAgent.setText("Agent: " + absence.getNomAgent());
    }

    @Override
    public int getItemCount() {
        return absenceList.size();  // Retourne la taille de la liste des absences
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvAbsenceDate;
        public TextView tvAbsenceHeure;
        public TextView tvAbsenceAgent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAbsenceDate = itemView.findViewById(R.id.tvAbsenceDate);
            tvAbsenceHeure = itemView.findViewById(R.id.tvAbsenceHeure);
            tvAbsenceAgent = itemView.findViewById(R.id.tvAbsenceAgent);
        }
    }
}

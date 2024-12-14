package com.example.mobile.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile.R;
import com.example.mobile.viewmodel.ManageUsersViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StatisticsActivity extends AppCompatActivity {

    private ManageUsersViewModel viewModel;
    private RecyclerView recyclerView;
    private StatisticsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        recyclerView = findViewById(R.id.recyclerViewStatistics);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new StatisticsAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(ManageUsersViewModel.class);

        // Observer pour afficher les absences par enseignant
        viewModel.getAbsencesParEnseignant().observe(this, stats -> {
            if (stats != null) {
                updateStatistics("Absences par Enseignant", stats);
            }
        });

        // Observer pour afficher les absences par classe
        viewModel.getAbsencesParClasse().observe(this, stats -> {
            if (stats != null) {
                updateStatistics("Absences par Classe", stats);
            }
        });

        // Observer pour afficher les absences par période
        viewModel.getAbsencesParPeriode().observe(this, stats -> {
            if (stats != null) {
                updateStatistics("Absences par Période", stats);
            }
        });

        // Générer les statistiques
        viewModel.generateStatistics();
    }

    private void updateStatistics(String title, Map<String, Integer> stats) {
        List<String> data = new ArrayList<>();
        data.add(title);
        for (Map.Entry<String, Integer> entry : stats.entrySet()) {
            data.add(entry.getKey() + ": " + entry.getValue());
        }
        adapter.updateData(data);
    }

    private static class StatisticsAdapter extends RecyclerView.Adapter<StatisticsAdapter.ViewHolder> {

        private final List<String> data;

        public StatisticsAdapter(List<String> data) {
            this.data = data;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.bind(data.get(position));
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        public void updateData(List<String> newData) {
            data.clear();
            data.addAll(newData);
            notifyDataSetChanged();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {

            private final TextView textView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                textView = itemView.findViewById(android.R.id.text1);
            }

            public void bind(String text) {
                textView.setText(text);
            }
        }
    }
}

package com.example.mobile.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile.R;
import com.example.mobile.adapter.UsersAdapter;
import com.example.mobile.adapter.EnseignantAbsenceAdapter;
import com.example.mobile.model.Absence;
import com.example.mobile.model.User;
import com.example.mobile.viewmodel.ManageUsersViewModel;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ManageUsersActivity extends AppCompatActivity implements UsersAdapter.OnUserActionListener {

    private RecyclerView agentsRecyclerView;
    private RecyclerView enseignantsRecyclerView;
    private UsersAdapter agentsAdapter;
    private EnseignantAbsenceAdapter enseignantsAbsencesAdapter;
    private ManageUsersViewModel manageUsersViewModel;
    private Button btnAjouterAbsence;
    private Button btnConsulterStatistique; // Déclaration du bouton

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_users);

        // Initialisation des vues
        agentsRecyclerView = findViewById(R.id.agentsRecyclerView);
        enseignantsRecyclerView = findViewById(R.id.enseignantsRecyclerView);
        btnAjouterAbsence = findViewById(R.id.btnAjouterAbsence); // Initialisation du bouton
        btnConsulterStatistique = findViewById(R.id.btnConsulterStatistique); // Initialisation du bouton

        // Configuration des RecyclerViews
        agentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        enseignantsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialisation du ViewModel
        manageUsersViewModel = new ViewModelProvider(this).get(ManageUsersViewModel.class);

        // Observer la liste des utilisateurs
        manageUsersViewModel.getUsers().observe(this, users -> {
            configureAgentsRecyclerView(users);
            configureEnseignantsRecyclerView(users);
        });

        // Observer les absences des enseignants
        manageUsersViewModel.getEnseignantsAvecAbsences().observe(this, enseignantsAbsences -> {
            configureEnseignantsRecyclerViewWithAbsences(enseignantsAbsences);
        });

        // Récupérer les données depuis Firestore
        manageUsersViewModel.fetchUsers();
        manageUsersViewModel.fetchAbsences();

        // Gérer le clic sur le bouton "Ajouter Absence"
        btnAjouterAbsence.setOnClickListener(v -> {
            Intent intent = new Intent(ManageUsersActivity.this, AddAbsenceActivity.class);
            startActivity(intent);
        });

        // Gérer le clic sur le bouton "Consulter Statistique"
        btnConsulterStatistique.setOnClickListener(v -> {
            Intent intent = new Intent(ManageUsersActivity.this, StatisticsActivity.class); // StatistiquesActivity doit être une activité existante pour afficher les statistiques
            startActivity(intent);
        });
    }

    private void configureAgentsRecyclerView(List<User> users) {
        List<User> agents = filterUsersByRole(users, "Agent");
        agentsAdapter = new UsersAdapter(agents, this);
        agentsRecyclerView.setAdapter(agentsAdapter);
    }

    private void configureEnseignantsRecyclerView(List<User> users) {
        List<User> enseignants = filterUsersByRole(users, "Enseignant");
        agentsAdapter = new UsersAdapter(enseignants, this); // Remplacer agentsAdapter par un autre si nécessaire
        enseignantsRecyclerView.setAdapter(agentsAdapter);
    }

    private void configureEnseignantsRecyclerViewWithAbsences(Map<User, List<Absence>> enseignantsAbsences) {
        enseignantsAbsencesAdapter = new EnseignantAbsenceAdapter(enseignantsAbsences);
        enseignantsRecyclerView.setAdapter(enseignantsAbsencesAdapter);
    }

    @Override
    public void onUserClicked(User user) {
        updateUserRole(user.getEmail(), "admin");
    }

    @Override
    public void onUserLongClicked(User user) {
        deleteUser(user.getEmail());
    }

    private List<User> filterUsersByRole(List<User> users, String role) {
        return users.stream()
                .filter(user -> role.equals(user.getRole()))
                .collect(Collectors.toList());
    }

    private void updateUserRole(String email, String role) {
        Toast.makeText(this, "Mise à jour du rôle de " + email + " en " + role, Toast.LENGTH_SHORT).show();
        // Implémentez ici la logique pour mettre à jour le rôle dans Firestore
    }

    private void deleteUser(String email) {
        Toast.makeText(this, "Suppression de l'utilisateur " + email, Toast.LENGTH_SHORT).show();
        // Implémentez ici la logique pour supprimer un utilisateur dans Firestore
    }
}

package com.example.mobile.view;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile.R;
import com.example.mobile.adapter.ConsulterEnseignantAbsencesAdapter;
import com.example.mobile.model.Absence;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ConsulterEnseignantAbsencesActivity extends AppCompatActivity {

    private RecyclerView absencesRecyclerView;
    private ConsulterEnseignantAbsencesAdapter absenceAdapter;
    private List<Absence> absences;
    private Button addAbsenceButton;
    private TextView userNameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_absence_management);

        // Initialisation des vues
        absencesRecyclerView = findViewById(R.id.absencesRecyclerView);
        addAbsenceButton = findViewById(R.id.addAbsenceButton);
        userNameTextView = findViewById(R.id.userNameTextView);

        // Configuration du RecyclerView
        absencesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        absences = new ArrayList<>();
        absenceAdapter = new ConsulterEnseignantAbsencesAdapter(absences);
        absencesRecyclerView.setAdapter(absenceAdapter);

        // Récupérer et afficher le nom de l'agent/admin connecté
        fetchCurrentUserName();

        // Récupérer les absences
        fetchAbsencesFromFirestore();
    }

    // Méthode pour récupérer les absences
    private void fetchAbsencesFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("absences")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    absences.clear(); // Nettoyer la liste avant d'ajouter les nouvelles données
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Absence absence = document.toObject(Absence.class);
                        absences.add(absence);
                    }
                    absenceAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Erreur lors de la récupération des absences", Toast.LENGTH_SHORT).show();
                });
    }

    // Méthode pour récupérer le nom de l'agent/admin connecté
    private void fetchCurrentUserName() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String userEmail = auth.getCurrentUser().getEmail(); // Email de l'utilisateur connecté

        if (userEmail != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users")
                    .whereEqualTo("email", userEmail)
                    .whereIn("role", List.of("Enseignant")) // Vérifier si le rôle est Agent ou Admin
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                String userName = document.getString("name"); // Récupérer le nom
                                userNameTextView.setText("Nom de l'ensigant  " + userName);
                                break;
                            }
                        } else {
                            userNameTextView.setText("Utilisateur non trouvé.");
                        }
                    })
                    .addOnFailureListener(e -> {
                        userNameTextView.setText("Erreur lors de la récupération du nom.");
                        Toast.makeText(this, "Erreur : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            userNameTextView.setText("Utilisateur non authentifié.");
        }
    }
}

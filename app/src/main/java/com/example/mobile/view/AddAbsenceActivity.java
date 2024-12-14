package com.example.mobile.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.mobile.R;
import com.example.mobile.view.ManageAbsencesActivity;
import com.example.mobile.viewmodel.AbsenceViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddAbsenceActivity extends AppCompatActivity {

    private EditText dateEditText, heureEditText, classeEditText;
    private AutoCompleteTextView nomEnseignantEditText;
    private Button addAbsenceButton, viewAbsencesButton; // Boutons
    private AbsenceViewModel absenceViewModel;
    private FirebaseFirestore db;
    private TextView agentNameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_absence);

        // Initialisation des vues
        dateEditText = findViewById(R.id.dateEditText);
        heureEditText = findViewById(R.id.heureEditText);
        classeEditText = findViewById(R.id.classeEditText);
        nomEnseignantEditText = findViewById(R.id.nomEnseignantEditText);
        addAbsenceButton = findViewById(R.id.addAbsenceButton);
        viewAbsencesButton = findViewById(R.id.viewAbsencesButton);
        agentNameTextView = findViewById(R.id.agentNameTextView);

        // Initialisation du ViewModel et FirebaseFirestore
        absenceViewModel = new ViewModelProvider(this).get(AbsenceViewModel.class);
        db = FirebaseFirestore.getInstance();

        // Obtenir la date et l'heure actuelles
        Calendar calendar = Calendar.getInstance();

        // Format pour la date (ex: "yyyy-MM-dd")
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = dateFormat.format(calendar.getTime());
        dateEditText.setText(currentDate);

        // Format pour l'heure (ex: "HH:mm")
        SimpleDateFormat heureFormat = new SimpleDateFormat("HH:mm");
        String currentHeure = heureFormat.format(calendar.getTime());
        heureEditText.setText(currentHeure);

        // Récupérer l'utilisateur actuel (agent) connecté
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            // Récupérer l'email de l'utilisateur
            String agentEmail = currentUser.getEmail();

            if (agentEmail != null) {
                db.collection("users")
                        .whereEqualTo("email", agentEmail)
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            if (!queryDocumentSnapshots.isEmpty()) {
                                QuerySnapshot documents = queryDocumentSnapshots;
                                QueryDocumentSnapshot document = (QueryDocumentSnapshot) documents.getDocuments().get(0);
                                String nomAgent = document.getString("name");
                                if (nomAgent != null) {
                                    agentNameTextView.setText("Nom de l'agent : " + nomAgent);
                                }
                            }
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Erreur lors de la récupération du nom de l'agent", Toast.LENGTH_SHORT).show();
                        });
            }
        }

        // Observer la liste des enseignants
        absenceViewModel.getEnseignantsListLiveData().observe(this, enseignants -> {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, enseignants);
            nomEnseignantEditText.setAdapter(adapter);
        });

        // Observer le statut de l'ajout d'absence
        absenceViewModel.getStatusLiveData().observe(this, status -> {
            Toast.makeText(this, status, Toast.LENGTH_SHORT).show();
            if (status.equals("Absence ajoutée avec succès")) {
                Intent intent = new Intent(AddAbsenceActivity.this, ManageAbsencesActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Gestion du clic sur le bouton "Voir la liste des absences"
        viewAbsencesButton.setOnClickListener(view -> {
            Intent intent = new Intent(AddAbsenceActivity.this, ManageAbsencesActivity.class);
            startActivity(intent);
            finish();
        });

        // Gestion du clic sur le bouton Ajouter Absence
        addAbsenceButton.setOnClickListener(view -> saveAbsence());

        // Observer la saisie dans le champ de texte pour rechercher des enseignants
        nomEnseignantEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (charSequence.length() >= 1) {
                    absenceViewModel.searchEnseignants(charSequence.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }

    private void saveAbsence() {
        String date = dateEditText.getText().toString();
        String heure = heureEditText.getText().toString();
        String classe = classeEditText.getText().toString();
        String nomEnseignant = nomEnseignantEditText.getText().toString();

        // Vérification de la validité des champs
        if (date.isEmpty() || heure.isEmpty() || classe.isEmpty() || nomEnseignant.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        // Récupérer l'UID de l'enseignant et l'ajouter
        absenceViewModel.getEnseignantUid(nomEnseignant, uid -> {
            if (uid != null) {
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                String nomAgent = currentUser != null ? currentUser.getDisplayName() : "Inconnu";
                absenceViewModel.addAbsence(date, heure, classe, nomEnseignant);
            } else {
                Toast.makeText(this, "Enseignant non trouvé", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

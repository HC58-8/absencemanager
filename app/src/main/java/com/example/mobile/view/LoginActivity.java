package com.example.mobile.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mobile.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private TextView signUpLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialisation des vues
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        Button loginButton = findViewById(R.id.loginButton);
        signUpLink = findViewById(R.id.signUpLink);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Gestion du clic sur le bouton Login
        loginButton.setOnClickListener(view -> login());

        // Gestion du lien pour s'inscrire
        signUpLink.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(intent);
        });
    }

    private void login() {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (!email.isEmpty() && !password.isEmpty()) {
            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            if (firebaseUser != null) {
                                fetchUserRoleAndRedirect(firebaseUser.getUid());
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, "Erreur de connexion", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(LoginActivity.this, "Erreur : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchUserRoleAndRedirect(String userId) {
        db.collection("users").document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String role = document.getString("role");

                            if (role != null) {
                                Intent intent = null;
                                switch (role) {
                                    case "Agent":
                                        // Rediriger vers AddAbsenceActivity pour les agents
                                        intent = new Intent(LoginActivity.this, AddAbsenceActivity.class);
                                        break;
                                    case "Admin":
                                        // Rediriger vers ManageUsersActivity pour les administrateurs
                                        intent = new Intent(LoginActivity.this, ManageUsersActivity.class);
                                        break;
                                    case "Enseignant":
                                        // Rediriger vers AddAbsenceActivity pour les enseignants
                                        intent = new Intent(LoginActivity.this, ConsulterEnseignantAbsencesActivity.class);
                                        break;
                                    default:
                                        Toast.makeText(LoginActivity.this, "Rôle non reconnu", Toast.LENGTH_SHORT).show();
                                        return;
                                }

                                if (intent != null) {
                                    startActivity(intent);
                                    finish(); // Fermer l'activité de connexion après la redirection
                                }
                            } else {
                                Toast.makeText(LoginActivity.this, "Rôle introuvable pour cet utilisateur", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, "Utilisateur introuvable dans Firestore", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Erreur lors de la récupération des données", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(LoginActivity.this, "Erreur : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}

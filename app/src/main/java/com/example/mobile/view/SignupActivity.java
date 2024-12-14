package com.example.mobile.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mobile.R;
import com.example.mobile.model.User;
import com.example.mobile.viewmodel.UserViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignupActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText, nameEditText;
    private RadioButton adminRadioButton, agentRadioButton, enseignantRadioButton;
    private UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialisation des vues
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        nameEditText = findViewById(R.id.nameEditText);
        adminRadioButton = findViewById(R.id.adminRadioButton);
        agentRadioButton = findViewById(R.id.agentRadioButton);
        enseignantRadioButton = findViewById(R.id.enseignantRadioButton);
        Button signupButton = findViewById(R.id.signupButton);

        userViewModel = new UserViewModel();  // Initialisation du ViewModel

        signupButton.setOnClickListener(view -> signup());
    }

    private void signup() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String name = nameEditText.getText().toString().trim();
        String role = "";

        // Vérification de l'email et du mot de passe
        if (TextUtils.isEmpty(email) || !email.contains("@")) {
            Toast.makeText(this, "Veuillez entrer un email valide", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password) || password.length() < 6) {
            Toast.makeText(this, "Le mot de passe doit contenir au moins 6 caractères", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Veuillez entrer votre nom", Toast.LENGTH_SHORT).show();
            return;
        }
        if (adminRadioButton.isChecked()) {
            role = "Admin";
        } else if (agentRadioButton.isChecked()) {
            role = "Agent";
        } else if (enseignantRadioButton.isChecked()) {
            role = "Enseignant";
        } else {
            Toast.makeText(this, "Veuillez sélectionner un rôle", Toast.LENGTH_SHORT).show();
            return;
        }

        // Création de l'utilisateur
        User user = new User(email, password, name, role);

        userViewModel.createUser(user, new UserViewModel.CreateUserCallback() {
            @Override
            public void onSuccess(FirebaseUser firebaseUser) {
                // Si la création de l'utilisateur est réussie
                Toast.makeText(SignupActivity.this, "Compte créé avec succès", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                finish();
            }

            @Override
            public void onFailure(Exception e) {
                // Affichage de l'erreur dans un toast
                Toast.makeText(SignupActivity.this, "Erreur lors de la création du compte: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}

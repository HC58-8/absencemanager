package com.example.mobile.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mobile.model.Absence;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AbsenceViewModel extends ViewModel {

    private MutableLiveData<String> statusLiveData;
    private MutableLiveData<List<String>> enseignantsListLiveData;
    private FirebaseFirestore db;

    public AbsenceViewModel() {
        statusLiveData = new MutableLiveData<>();
        enseignantsListLiveData = new MutableLiveData<>(new ArrayList<>());
        db = FirebaseFirestore.getInstance();
    }

    public LiveData<String> getStatusLiveData() {
        return statusLiveData;
    }

    public LiveData<List<String>> getEnseignantsListLiveData() {
        return enseignantsListLiveData;
    }

    // Méthode pour rechercher les enseignants en fonction de la saisie dans l'AutoCompleteTextView
    // ViewModel - Recherche des enseignants
    public void searchEnseignants(String query) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .whereEqualTo("role", "enseignant") // Filtrer par rôle
                .whereGreaterThanOrEqualTo("name", query) // Recherche par nom
                .whereLessThan("name", query + '\uf8ff') // Compléter la recherche par préfixe
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<String> enseignants = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String enseignant = document.getString("name");
                        if (enseignant != null) {
                            enseignants.add(enseignant);
                        }
                    }
                    // Mettre à jour le LiveData
                    enseignantsListLiveData.setValue(enseignants);
                })
                .addOnFailureListener(e -> {
                    Log.e("AddAbsenceActivity", "Erreur lors de la recherche d'enseignants", e);
                });
    }

    // Méthode pour récupérer l'UID de l'enseignant à partir de son nom
    public void getEnseignantUid(String nomEnseignant, OnEnseignantFoundListener listener) {
        db.collection("users")
                .whereEqualTo("name", nomEnseignant)
                .whereEqualTo("role", "Enseignant")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Récupérer l'UID de l'enseignant
                        // Utilisation directe de QueryDocumentSnapshot sans cast explicite
                        String enseignantUid = queryDocumentSnapshots.getDocuments().get(0).getId();
                        listener.onEnseignantFound(enseignantUid);
                    } else {
                        listener.onEnseignantFound(null);  // Aucun enseignant trouvé
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("AbsenceViewModel", "Erreur lors de la récupération de l'enseignant", e);
                    listener.onEnseignantFound(null);
                });
    }

    // Ajouter une absence dans Firestore
    // Ajouter une absence dans Firestore avec récupération du nom de l'agent
    public void addAbsence(String date, String heure, String classe, String nomEnseignant)
    {
        // Récupérer l'utilisateur connecté
        String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Rechercher le nom de l'agent via son UID
        db.collection("users")
                .document(currentUserUid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Récupérer le nom de l'agent
                        String nomAgent = documentSnapshot.getString("name");
                        if (nomAgent != null) {
                            // Créer une nouvelle absence avec le nom de l'agent
                            Absence newAbsence = new Absence(date, heure, classe, nomEnseignant, nomAgent);
                            db.collection("absences").add(newAbsence)
                                    .addOnSuccessListener(documentReference -> {
                                        statusLiveData.setValue("Absence ajoutée avec succès");
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("AbsenceViewModel", "Erreur lors de l'ajout de l'absence", e);
                                        statusLiveData.setValue("Erreur lors de l'ajout de l'absence");
                                    });
                        } else {
                            Log.e("AbsenceViewModel", "Nom de l'agent introuvable");
                            statusLiveData.setValue("Nom de l'agent introuvable");
                        }
                    } else {
                        Log.e("AbsenceViewModel", "Document utilisateur introuvable");
                        statusLiveData.setValue("Utilisateur introuvable");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("AbsenceViewModel", "Erreur lors de la récupération de l'agent", e);
                    statusLiveData.setValue("Erreur lors de la récupération de l'agent");
                });
    }


    // Interface pour passer l'UID de l'enseignant trouvé
    public interface OnEnseignantFoundListener {
        void onEnseignantFound(String enseignantUid);
    }
}

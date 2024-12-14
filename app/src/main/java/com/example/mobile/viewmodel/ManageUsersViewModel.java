package com.example.mobile.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mobile.model.Absence;
import com.example.mobile.model.User;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ManageUsersViewModel extends ViewModel {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private final MutableLiveData<List<User>> usersList = new MutableLiveData<>();
    private final MutableLiveData<Map<User, List<Absence>>> enseignantsAvecAbsences = new MutableLiveData<>();
    private final MutableLiveData<Map<String, Integer>> absencesParEnseignant = new MutableLiveData<>();
    private final MutableLiveData<Map<String, Integer>> absencesParClasse = new MutableLiveData<>();
    private final MutableLiveData<Map<String, Integer>> absencesParPeriode = new MutableLiveData<>();

    public LiveData<List<User>> getUsers() {
        return usersList;
    }

    public LiveData<Map<User, List<Absence>>> getEnseignantsAvecAbsences() {
        return enseignantsAvecAbsences;
    }

    public LiveData<Map<String, Integer>> getAbsencesParEnseignant() {
        return absencesParEnseignant;
    }

    public LiveData<Map<String, Integer>> getAbsencesParClasse() {
        return absencesParClasse;
    }

    public LiveData<Map<String, Integer>> getAbsencesParPeriode() {
        return absencesParPeriode;
    }

    // Récupérer tous les utilisateurs avec rôles (Agent ou Enseignant)
    public void fetchUsers() {
        db.collection("users")
                .whereIn("role", List.of("Agent", "Enseignant"))
                .get()
                .addOnSuccessListener(querySnapshots -> {
                    List<User> users = new ArrayList<>();
                    querySnapshots.forEach(doc -> {
                        users.add(new User(
                                doc.getId(),
                                doc.getString("email"),
                                null,
                                doc.getString("name"),
                                doc.getString("role")
                        ));
                    });
                    usersList.setValue(users);
                })
                .addOnFailureListener(Throwable::printStackTrace);
    }

    // Récupérer les absences pour les enseignants
    public void fetchAbsences() {
        db.collection("users")
                .whereEqualTo("role", "Enseignant")
                .get()
                .addOnSuccessListener(userSnapshots -> {
                    Map<User, List<Absence>> enseignantsAbsences = new HashMap<>();
                    List<User> enseignants = new ArrayList<>();

                    for (var userDoc : userSnapshots) {
                        User enseignant = new User(
                                userDoc.getId(),
                                userDoc.getString("email"),
                                null,
                                userDoc.getString("name"),
                                "Enseignant"
                        );
                        enseignants.add(enseignant);
                    }

                    db.collection("absences")
                            .get()
                            .addOnSuccessListener(absenceSnapshots -> {
                                enseignants.forEach(enseignant -> {
                                    List<Absence> absences = new ArrayList<>();
                                    absenceSnapshots.forEach(absenceDoc -> {
                                        if (enseignant.getName().equals(absenceDoc.getString("nomEnseignant"))) {
                                            absences.add(new Absence(
                                                    absenceDoc.getString("date"),
                                                    absenceDoc.getString("heure"),
                                                    absenceDoc.getString("classe"),
                                                    absenceDoc.getString("nomEnseignant"),
                                                    absenceDoc.getString("nomAgent")
                                            ));
                                        }
                                    });
                                    enseignantsAbsences.put(enseignant, absences);
                                });
                                enseignantsAvecAbsences.setValue(enseignantsAbsences);
                            })
                            .addOnFailureListener(Throwable::printStackTrace);
                })
                .addOnFailureListener(Throwable::printStackTrace);
    }

    // Générer des statistiques
    public void generateStatistics() {
        db.collection("absences")
                .get()
                .addOnSuccessListener(querySnapshots -> {
                    Map<String, Integer> parEnseignant = new HashMap<>();
                    Map<String, Integer> parClasse = new HashMap<>();
                    Map<String, Integer> parPeriode = new HashMap<>();

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM", Locale.getDefault());

                    querySnapshots.forEach(doc -> {
                        String enseignant = doc.getString("nomEnseignant");
                        String classe = doc.getString("classe");
                        String date = doc.getString("date");

                        parEnseignant.put(enseignant, parEnseignant.getOrDefault(enseignant, 0) + 1);
                        parClasse.put(classe, parClasse.getOrDefault(classe, 0) + 1);

                        try {
                            String periode = dateFormat.format(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(date));
                            parPeriode.put(periode, parPeriode.getOrDefault(periode, 0) + 1);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });

                    absencesParEnseignant.setValue(parEnseignant);
                    absencesParClasse.setValue(parClasse);
                    absencesParPeriode.setValue(parPeriode);
                })
                .addOnFailureListener(Throwable::printStackTrace);
    }
}

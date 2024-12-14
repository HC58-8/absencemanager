package com.example.mobile.viewmodel;

import androidx.lifecycle.ViewModel;
import com.example.mobile.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.List;

public class UserViewModel extends ViewModel {
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    public UserViewModel() {
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    // Créer un utilisateur avec nom et rôle
    public void createUser(User user, CreateUserCallback callback) {
        auth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = auth.getCurrentUser();
                        if (firebaseUser != null) {
                            // Ajouter l'UID au modèle utilisateur
                            user.setUid(firebaseUser.getUid());
                            user.setPassword(null); // Supprime le mot de passe avant la sauvegarde

                            // Enregistrer l'utilisateur dans Firestore avec l'UID comme clé
                            db.collection("users").document(firebaseUser.getUid())
                                    .set(user)
                                    .addOnSuccessListener(aVoid -> callback.onSuccess(firebaseUser))
                                    .addOnFailureListener(callback::onFailure);
                        }
                    } else {
                        callback.onFailure(task.getException());
                    }
                });
    }

    // Connexion de l'utilisateur
    public void loginUser(String email, String password, LoginUserCallback callback) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = auth.getCurrentUser();
                        callback.onSuccess(firebaseUser);
                    } else {
                        callback.onFailure(task.getException());
                    }
                });
    }

    // Récupérer la liste des utilisateurs
    public void getUsers(GetUsersCallback callback) {
        db.collection("users")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<User> users = queryDocumentSnapshots.toObjects(User.class);
                    callback.onSuccess(users);
                })
                .addOnFailureListener(callback::onFailure);
    }

    // Supprimer un utilisateur
    public void deleteUser(String email, DeleteUserCallback callback) {
        db.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        db.collection("users").document(queryDocumentSnapshots.getDocuments().get(0).getId())
                                .delete()
                                .addOnSuccessListener(aVoid -> callback.onSuccess())
                                .addOnFailureListener(callback::onFailure);
                    } else {
                        callback.onFailure(new Exception("Utilisateur non trouvé"));
                    }
                })
                .addOnFailureListener(callback::onFailure);
    }

    // Modifier le rôle d'un utilisateur
    public void updateUserRole(String userId, String newRole, UpdateRoleCallback callback) {
        db.collection("users").document(userId)
                .update("role", newRole)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(callback::onFailure);
    }

    // Interfaces pour les callbacks
    public interface CreateUserCallback {
        void onSuccess(FirebaseUser user);
        void onFailure(Exception e);
    }

    public interface LoginUserCallback {
        void onSuccess(FirebaseUser user);
        void onFailure(Exception e);
    }

    public interface GetUsersCallback {
        void onSuccess(List<User> users);
        void onFailure(Exception e);
    }

    public interface DeleteUserCallback {
        void onSuccess();
        void onFailure(Exception e);
    }

    public interface UpdateRoleCallback {
        void onSuccess();
        void onFailure(Exception e);
    }
}

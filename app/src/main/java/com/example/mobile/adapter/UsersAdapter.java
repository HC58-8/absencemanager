package com.example.mobile.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile.R;
import com.example.mobile.model.User;

import java.util.ArrayList;
import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder> {
    private List<User> users = new ArrayList<>();
    private OnUserActionListener listener;

    // Constructeur de l'adaptateur avec une liste d'utilisateurs et un listener
    public UsersAdapter(List<User> users, OnUserActionListener listener) {
        this.users = users;
        this.listener = listener;
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_agent, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        User user = users.get(position);
        holder.nameTextView.setText("Nom de l'agent : " + user.getName());
        holder.emailTextView.setText("Email de l'agent : " + user.getEmail());  // Affichage de l'email
        holder.roleTextView.setText("Role : " +  user.getRole());    // Affichage du rôle

        // Gestion du clic long
        holder.itemView.setOnLongClickListener(v -> {
            listener.onUserLongClicked(user);  // Appel de l'action longue
            return true;
        });

        // Gestion du clic simple
        holder.itemView.setOnClickListener(v -> {
            listener.onUserClicked(user);  // Appel de l'action de clic
        });
    }

    @Override
    public int getItemCount() {
        return users.size();  // Retourne la taille de la liste des utilisateurs
    }

    // Méthode pour mettre à jour la liste des utilisateurs
    public void setUsers(List<User> users) {
        this.users = users;
        notifyDataSetChanged();  // Notifie que les données ont été mises à jour
    }

    // ViewHolder qui lie les éléments de la vue aux objets correspondants
    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView emailTextView;  // Ajout de la TextView pour l'email
        TextView roleTextView;

        public UserViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.textViewName);
            emailTextView = itemView.findViewById(R.id.textViewEmail);  // Initialisation de l'email
            roleTextView = itemView.findViewById(R.id.textViewRole);
        }
    }

    // Interface pour écouter les actions sur l'utilisateur
    public interface OnUserActionListener {
        void onUserClicked(User user);      // Action de clic sur un utilisateur
        void onUserLongClicked(User user);  // Action de clic long sur un utilisateur
    }
}

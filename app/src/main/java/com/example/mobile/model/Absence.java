package com.example.mobile.model;

public class Absence {
    private String id; // ID généré automatiquement par Firestore
    private String date;
    private String heure;
    private String classe;
    private String nomEnseignant; // Nom de l'enseignant absent
    private String nomAgent;      // Nom de l'agent récupéré via UID depuis Firestore

    // Constructeur vide requis par Firestore
    public Absence() {
    }

    // Constructeur avec paramètres
    public Absence(String date, String heure, String classe, String nomEnseignant, String nomAgent) {
        this.date = date;
        this.heure = heure;
        this.classe = classe;
        this.nomEnseignant = nomEnseignant;
        this.nomAgent = nomAgent;
    }

    // Getters et setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHeure() {
        return heure;
    }

    public void setHeure(String heure) {
        this.heure = heure;
    }


    public String getClasse() {
        return classe;
    }

    public void setClasse(String classe) {
        this.classe = classe;
    }

    public String getNomEnseignant() {
        return nomEnseignant;
    }

    public void setNomEnseignant(String nomEnseignant) {
        this.nomEnseignant = nomEnseignant;
    }

    public String getNomAgent() {
        return nomAgent;
    }

    public void setNomAgent(String nomAgent) {
        this.nomAgent = nomAgent;
    }
}

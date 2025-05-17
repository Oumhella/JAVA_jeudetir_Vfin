package model;

public class JoueurModel {
    private int id;
    private String nom;
    private int maxScore;

    public JoueurModel(int id, String nom, int maxScore) {
        this.id = id;
        this.nom = nom;
        this.maxScore = maxScore;
    }

    public int getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public int getMaxScore() {
        return maxScore;
    }
}
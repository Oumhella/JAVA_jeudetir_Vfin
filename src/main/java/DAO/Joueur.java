package DAO;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Joueur {
    private int id;
    private String nom;
    private String password;
    private int maxScore;
    private int coins;
    private String avionName;
    private String difficulty;
    private Connection connection;

    public Joueur() {
    }

    public Joueur(int id, String nom, int maxScore, int coins) {
        this.id = id;
        this.nom = nom;
        this.maxScore = maxScore;
        this.coins = coins;
    }

    public Joueur(int id, String nom, int maxScore) {
        this.id = id;
        this.nom = nom;
        this.maxScore = maxScore;
    }

    public Joueur(Connection connection) {
        this.connection = connection;
    }

    // Getters and setters for JavaFX property binding
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public int getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(int maxScore) {
        this.maxScore = maxScore;
    }

    public void setAvion(String avionName) {
        this.avionName = avionName;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getAvionName() {
        return avionName;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public List<Joueur> getAllJoueurs() {
        List<Joueur> joueurs = new ArrayList<>();
        String query = "SELECT id, nom, maxScore FROM players ORDER BY maxScore DESC";

        try {
            Connection conn = (connection != null) ? connection : ConnexionBD.seconnecter();
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {

                while (rs.next()) {
                    int id = rs.getInt("id");
                    String nom = rs.getString("nom");
                    int maxScore = rs.getInt("maxScore");
                    joueurs.add(new Joueur(id, nom, maxScore));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return joueurs;
    }

    public Joueur getJoueur(int id) {
        Joueur joueur = null;
        try {
            Statement stm = connection.createStatement();
            ResultSet rs = stm.executeQuery("select * from players where id= " + id + " ");
            while (rs.next()) {
                int idd = rs.getInt(1);
                String nom1 = rs.getString(2);
                int maxScore1 = rs.getInt(3);
                joueur = new Joueur(idd, nom1, maxScore1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return joueur;
    }
}

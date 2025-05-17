package DAO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class JoueurHandler {

    public static boolean register(String nom, String password) {
        try {
            Statement stm = ConnexionBD.seconnecter().createStatement();
            ResultSet check = stm.executeQuery("SELECT id FROM players WHERE nom = '" + nom + "'");
            if (check.next()) {
                return false;
            } else {
                stm.executeUpdate("INSERT INTO players(nom,password,maxScore,coins) VALUES ('" + nom + "', '" + password + "', 0, 1)");
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public static Joueur login(String nom, String password) {
        try {
            Statement stm = ConnexionBD.seconnecter().createStatement();
            ResultSet player = stm.executeQuery("SELECT * FROM players WHERE nom = '" + nom + "' AND password = '" + password + "' ");
            if (player.next()){
                Joueur j = new Joueur();
                j.setId(player.getInt(1));
                j.setNom(player.getString(2));
                j.setMaxScore(player.getInt(4));
                return j;
            }
            else {
                return null;
            }
        }catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    public void sauvgarder(Joueur joueur) {
        try{
            Statement stm = ConnexionBD.seconnecter().createStatement();
            stm.executeUpdate("update players set maxScore = " + joueur.getMaxScore() + " WHERE id = " + joueur.getId());
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

}

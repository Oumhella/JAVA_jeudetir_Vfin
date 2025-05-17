package DAO;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class LevelGame {
    public int joueurId;
    public int avionId;
    public int level;
    public int score;

    public LevelGame(){}
    public LevelGame(int joueurId, int avionId, int level, int score) {
        this.joueurId = joueurId;
        this.avionId = avionId;
        this.level = level;
        this.score = score;
    }

    public LevelGame(int i) {

    }


    public void ajouter(LevelGame game){
        try {
            Connection conn = ConnexionBD.seconnecter();
            Statement stm = conn.createStatement();

            String checkSql = "SELECT COUNT(*) FROM levelgame WHERE playerId = " + game.joueurId + " AND avionId = " + game.avionId + " AND hardness = " + game.level;
            ResultSet rs = stm.executeQuery(checkSql);

            if (rs.next() && rs.getInt(1) == 0) {

                String insertSql = "INSERT INTO levelgame VALUES (" +
                        game.joueurId + ", " +
                        game.avionId + ", " +
                        game.level + ", " +
                        game.score + ")";
                stm.executeUpdate(insertSql);
                System.out.println("Inserted successfully.");
            } else {
                System.out.println("Duplicate entry found. Insert skipped.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public List<LevelGame> dashboard(){
        List<LevelGame> games = new ArrayList<LevelGame>();
        try {
            Statement stm = ConnexionBD.seconnecter().createStatement();
            ResultSet rs = stm.executeQuery("SELECT * FROM levelgame ");
            while (rs.next()){
                int jId = rs.getInt(1);
                int aId = rs.getInt(2);
                int hard = rs.getInt(3);
                int Score = rs.getInt(4);
                games.add(new LevelGame(jId, aId, hard, Score));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return games;
    }
    public void sauvgarderScore(int score,LevelGame game) {
        try{
            Statement stm = ConnexionBD.seconnecter().createStatement();
            stm.executeUpdate("update LevelGame set score = "+score+" WHERE playerId = "+ game.joueurId +" AND avionId = "+game.avionId+" AND hardness="+game.level+" ");

        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

}

package DAO;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AvionLevelUser {
    public int idUser;
    public int idAvion;
    public int idAvionLevel;

    public AvionLevelUser() {}
    public AvionLevelUser(int idUser, int idAvion, int idAvionLevel) {
        this.idUser = idUser;
        this.idAvion = idAvion;
        this.idAvionLevel = idAvionLevel;
    }
    public void ajouter(AvionLevelUser avionLevelUser) {
        try {
            Statement stm = ConnexionBD.seconnecter().createStatement();
            stm.executeUpdate("INSERT INTO avionleveluser VALUES("+ avionLevelUser.idUser +"," + avionLevelUser.idAvion + ", " + avionLevelUser.idAvionLevel + ")");
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<AvionLevelUser> getavionLevels(){
        List<AvionLevelUser> avionLevelUsers = new ArrayList<AvionLevelUser>();
        try{
            Statement stm = ConnexionBD.seconnecter().createStatement();
            ResultSet rs = stm.executeQuery("SELECT * FROM avionleveluser");
            while (rs.next()){
                int user = rs.getInt(1);
                int avion = rs.getInt(2);
                int level = rs.getInt(3);
                avionLevelUsers.add(new AvionLevelUser(user,avion,level));
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return avionLevelUsers;
    }

}

package DAO;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Avion {
    public int id;
    public String name;
    public double speed;
    public double health;
    public double dammage;

    public Avion() {}

    public Avion(int id, String name, double speed, double health, double dammage) {
        this.id = id;
        this.name = name;
        this.speed = speed;
        this.health = health;
        this.dammage = dammage;
    }

    public void ajouterAvion(Avion a) {
        try {
            Statement stm = ConnexionBD.seconnecter().createStatement();
            stm.executeUpdate("INSERT INTO avion VALUES (" + a.id + ", '" + a.name + "', " + a.speed + ", " + a.health + ", " + a.dammage + ")");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void supprimerAvion(int id) {
        try {
            Statement stm = ConnexionBD.seconnecter().createStatement();
            stm.executeUpdate("DELETE FROM avion WHERE id = " + id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Avion> getAllAvions() {
        List<Avion> avions = new ArrayList<>();
        try {
            Statement stm = ConnexionBD.seconnecter().createStatement();
            ResultSet rs = stm.executeQuery("SELECT * FROM avion");
            while (rs.next()) {
                int id = rs.getInt(1);
                String name = rs.getString(2);
                double speed = rs.getDouble(3);
                double health = rs.getDouble(4);
                double dammage = rs.getDouble(5);
                avions.add(new Avion(id, name, speed, health, dammage));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return avions;
    }

    public Avion getAvionByName(String name) {
        Avion avion = null;
        try {
            Statement stm = ConnexionBD.seconnecter().createStatement();
            ResultSet rs = stm.executeQuery("SELECT * FROM avion WHERE name = '" + name + "'");
            if (rs.next()) {
                int id = rs.getInt(1);
                String n = rs.getString(2);
                double speed = rs.getDouble(3);
                double health = rs.getDouble(4);
                double dammage = rs.getDouble(5);
                avion = new Avion(id, n, speed, health, dammage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return avion;
    }
}

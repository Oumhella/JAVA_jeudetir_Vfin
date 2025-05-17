package DAO;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnexionBD {
    static String url="jdbc:mysql://localhost:3306/game";
    static  String login="root";
    static String password="";
    static Connection connexion=null;
    public static Connection seconnecter(){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connexion = DriverManager.getConnection(url, login, password);
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        return connexion;
    }
    public static void sedeconnecter(){
        try {
            connexion.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
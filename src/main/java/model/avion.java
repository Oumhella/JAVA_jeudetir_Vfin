package model;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.List;

public class avion {

    private final int BORDER_SIZE = 20;
    private ImageView imageavion;
    private List<projectil> projectiles;
    private long dernierTirTime = 0;
    private final long DELAI_TIR = 300; // 300 ms entre chaque tir
    private int vies = 3;


    public avion() {
        Image Avion = new Image("images/2D_Space_Shooter_3.0_Free_1.3/Export/Base/Large/Ship_2_E_Large.png");
        imageavion = new ImageView(Avion);
        imageavion.setFitWidth(85);
        imageavion.setFitHeight(85);
        imageavion.setLayoutX(300);
        imageavion.setLayoutY(500);
        projectiles = new ArrayList<>();
    }
    public avion(String imagePath) {
        Image Avion = new Image(imagePath);
        imageavion = new ImageView(Avion);
        imageavion.setFitWidth(85);
        imageavion.setFitHeight(85);
        imageavion.setLayoutX(300);
        imageavion.setLayoutY(500);
        projectiles = new ArrayList<>();
    }
    public avion(int x) {
        Image Avion = new Image("images/2D_Space_Shooter_3.0_Free_1.3/Export/Base/Large/Ship_2_E_Large.png");
        imageavion = new ImageView(Avion);
        imageavion.setFitWidth(85);
        imageavion.setFitHeight(85);
        imageavion.setLayoutX(x-imageavion.getFitWidth()/2);
        imageavion.setLayoutY(500);
        projectiles = new ArrayList<>();
    }


    public ImageView getImageAvion() {

        return imageavion;
    }

    public List<projectil> getProjectiles() {

        return projectiles;
    }

    public int getVies() {
        return vies;
    }

    public void moveLeft() {
        if (imageavion.getLayoutX() > BORDER_SIZE) {
            imageavion.setLayoutX(imageavion.getLayoutX() - 10);
        }
    }
    public void moveRight() {
        if(imageavion.getLayoutX()+imageavion.getFitWidth()+BORDER_SIZE < 800){
            imageavion.setLayoutX(imageavion.getLayoutX() + 10);

        }
    }
    public void moveLeft(double x) {
        if (imageavion.getLayoutX() > x+BORDER_SIZE) {
            imageavion.setLayoutX(imageavion.getLayoutX() - 10);
        }
    }
    public void moveRight(double x) {
        if(imageavion.getLayoutX()+imageavion.getFitWidth()+BORDER_SIZE < x){
            imageavion.setLayoutX(imageavion.getLayoutX() + 10);

        }
    }
    public void moveUp() {
        if (imageavion.getLayoutY() > BORDER_SIZE ){
            imageavion.setLayoutY(imageavion.getLayoutY() - 10);
        }
    }
    public void moveDown() {
        if ((imageavion.getLayoutY() + imageavion.getFitHeight() +BORDER_SIZE ) < 600){
            imageavion.setLayoutY(imageavion.getLayoutY() + 10);

        }
    }


    public void perdreVie() {
        vies--;
    }

    public void gagnerVie() {
        if (vies < 3) {
            vies++;
        }
    }
    public void tirer(Pane root) {
        long tempsActuel = System.currentTimeMillis();
        if (tempsActuel - dernierTirTime < DELAI_TIR) {
            return;
        }
        dernierTirTime = tempsActuel;
        double x = imageavion.getLayoutX() + imageavion.getFitWidth() / 2 - 7;
        double y = imageavion.getLayoutY() - 20;
        projectil p = new projectil(root, x, y);
        projectiles.add(p);
        Thread t = new Thread(p);
        t.setDaemon(true);
        t.start();
    }
}
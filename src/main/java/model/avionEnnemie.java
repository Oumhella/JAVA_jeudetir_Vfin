package model;


import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class avionEnnemie {

    protected ImageView imageAvionEnnemie;
    protected double moveSpeed;

    public avionEnnemie(double x, double y) {
        this(x, y, DifficultySettings.getSettings(1).getEnemyMoveSpeed());
    }

    public avionEnnemie(double x, double y, double moveSpeed) {
        Image ennemiImage = new Image("images/Ship_1_B_Large.png");
        imageAvionEnnemie = new ImageView(ennemiImage);
        imageAvionEnnemie.setFitWidth(70);
        imageAvionEnnemie.setFitHeight(70);
        imageAvionEnnemie.setLayoutX(x);
        imageAvionEnnemie.setLayoutY(y);
        this.moveSpeed = moveSpeed;
    }

    public ImageView getImageAvionEnnemie() {
        return imageAvionEnnemie;
    }

    public void moveDown() {
        imageAvionEnnemie.setLayoutY(imageAvionEnnemie.getLayoutY() + moveSpeed);
    }

    public void setMoveSpeed(double moveSpeed) {
        this.moveSpeed = moveSpeed;
    }
}
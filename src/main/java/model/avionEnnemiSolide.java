package model;

import javafx.scene.image.Image;

public class avionEnnemiSolide extends avionEnnemie {

    private int vie = 2;

    public avionEnnemiSolide(double x, double y) {
        this(x, y, DifficultySettings.getSettings(1).getEnemyMoveSpeed());
    }

    public avionEnnemiSolide(double x, double y, double moveSpeed) {
        super(x, y, moveSpeed);

        Image solideImage = new Image("images/Enemy_4_D_Large.png");
        getImageAvionEnnemie().setImage(solideImage);

        getImageAvionEnnemie().setFitWidth(80);
        getImageAvionEnnemie().setFitHeight(80);
    }

    public boolean perdreVie() {
        vie--;
        return vie <= 0;
    }
}

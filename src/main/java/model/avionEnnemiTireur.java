package model;

import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

public class avionEnnemiTireur extends avionEnnemie {

    private Pane root;

    public avionEnnemiTireur(Pane root, double x, double y) {
        this(root, x, y, DifficultySettings.getSettings(1).getEnemyMoveSpeed());
    }

    public avionEnnemiTireur(Pane root, double x, double y, double moveSpeed) {
        super(x, y, moveSpeed);
        this.root = root;

        Image tireurImage = new Image(getClass().getResourceAsStream("/images/Ship_1_C_Large.png"));
        getImageAvionEnnemie().setImage(tireurImage);

        getImageAvionEnnemie().setFitWidth(80);
        getImageAvionEnnemie().setFitHeight(80);
    }
}

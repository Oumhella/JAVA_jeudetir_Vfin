package model;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class projectileEnnemi {
    private ImageView imageProjectile;
    private Pane root;
    private boolean actif = true;
    private double moveSpeed;

    public projectileEnnemi(Pane root, double posX, double posY) {
        this(root, posX, posY, DifficultySettings.getSettings(1).getEnemyProjectileSpeed());
    }

    public projectileEnnemi(Pane root, double posX, double posY, double moveSpeed) {
        this.root = root;
        this.moveSpeed = moveSpeed;
        Image projImage = new Image(getClass().getResourceAsStream("/images/kenney_space-shooter-redux/PNG/Lasers/laserBlue16.png"));
        imageProjectile = new ImageView(projImage);
        imageProjectile.setFitWidth(15);
        imageProjectile.setFitHeight(35);
        imageProjectile.setLayoutX(posX);
        imageProjectile.setLayoutY(posY);

        Platform.runLater(() -> root.getChildren().add(imageProjectile));
    }

    public ImageView getImageView() {
        return imageProjectile;
    }

    public boolean isActif() {
        return actif;
    }

    public void move() {
        imageProjectile.setLayoutY(imageProjectile.getLayoutY() + moveSpeed);
        if (imageProjectile.getLayoutY() > 600) {
            actif = false;
        }
    }

    public void setMoveSpeed(double moveSpeed) {
        this.moveSpeed = moveSpeed;
    }
}


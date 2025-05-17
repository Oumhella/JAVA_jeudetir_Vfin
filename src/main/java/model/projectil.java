package model;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class projectil implements Runnable  {
    private ImageView imageProjectil;
    private Pane root;
    private boolean actif = true;


    public projectil(Pane root, double posX, double posY) {
        this.root = root;
        Image projImage = new Image("images/kenney_space-shooter-redux/PNG/Lasers/laserRed16.png");
        imageProjectil = new ImageView(projImage);
        imageProjectil.setFitWidth(15);
        imageProjectil.setFitHeight(35);
        imageProjectil.setLayoutX(posX);
        imageProjectil.setLayoutY(posY);

        Platform.runLater(() -> root.getChildren().add(imageProjectil));
    }

    //getter
    public ImageView getImageView() {
        return imageProjectil;
    }
    public void move() {
        imageProjectil.setLayoutY(imageProjectil.getLayoutY() - 5); // Move upward
    }

    public boolean isOutOfBounds(int screenHeight) {
        return imageProjectil.getLayoutY() < -imageProjectil.getFitHeight() ||
                imageProjectil.getLayoutY() > screenHeight;
    }
    @Override
    public void run() {
        while (actif) {
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Platform.runLater(() -> {
                imageProjectil.setLayoutY(imageProjectil.getLayoutY() - 10);

                if (imageProjectil.getLayoutY() < -30) {
                    root.getChildren().remove(imageProjectil);
                }
            });
        }
    }

}

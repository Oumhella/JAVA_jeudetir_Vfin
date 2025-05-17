package model;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class BonusVie {
    private ImageView imageheart;

    public BonusVie(double x, double y) {
        Image bonusImage = new Image("images/heart.png");
        imageheart = new ImageView(bonusImage);
        imageheart.setFitWidth(40);
        imageheart.setFitHeight(40);
        imageheart.setLayoutX(x);
        imageheart.setLayoutY(y);
    }

    public void moveDown() {
        imageheart.setLayoutY(imageheart.getLayoutY() + 2);
    }

    public ImageView getImage() {
        return imageheart;
    }
}

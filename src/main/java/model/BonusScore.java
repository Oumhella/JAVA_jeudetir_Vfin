package model;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class BonusScore {
    private ImageView imageStar;

    public BonusScore(double x, double y) {
        Image bonusImage = new Image("images/Prinbles_Buttons_Cosmo (v 1.0) (9_10_2023)/Prinbles_Buttons_Cosmo (v 1.0) (9_10_2023)/png@2x/Buttons-Square/Star/Default@2x.png");
        imageStar = new ImageView(bonusImage);
        imageStar.setFitWidth(35);
        imageStar.setFitHeight(35);
        imageStar.setLayoutX(x);
        imageStar.setLayoutY(y);
    }

    public void moveDown() {
        imageStar.setLayoutY(imageStar.getLayoutY() + 2);
    }

    public ImageView getImage() {
        return imageStar;
    }
}


package view;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class LoadingScene {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    private AnchorPane loadingPane;
    private Scene loadingScene;
    private Stage loadingStage;

    public LoadingScene(String str) {
        loadingPane = new AnchorPane();
        loadingScene = new Scene(loadingPane, WIDTH, HEIGHT);
        loadingStage = new Stage();
        loadingStage.setScene(loadingScene);
        loadingStage.setTitle("loading");
        loadingStage.getIcons().add(new Image("images/2D_Space_Shooter_3.0_Free_1.3/Export/NoLight/Large/Ship_2_E_Large_NoLight.png"));

        createLoadingContent(str);
        createBackground();
    }

    private void createLoadingContent(String str) {
        Label loadingLabel = new Label(str);
        loadingLabel.setFont(Font.font("Arial", FontWeight.BOLD, 29));
        loadingLabel.setTextFill(Color.WHITE);

        ProgressBar progressBar = new ProgressBar();
        progressBar.setPrefWidth(300);

        VBox vbox = new VBox(20, loadingLabel, progressBar);
        vbox.setLayoutX(WIDTH/2 - 150);
        vbox.setLayoutY(HEIGHT/2 - 50);

        loadingPane.getChildren().add(vbox);
    }

    private void createBackground() {
        Image backroungimage = new Image("images/background.png");
        BackgroundImage backgroundimage = new BackgroundImage(backroungimage, null, null, null, BackgroundSize.DEFAULT);
        loadingPane.setBackground(new Background(backgroundimage));
    }

    public Stage getStage() {
        return loadingStage;
    }
}
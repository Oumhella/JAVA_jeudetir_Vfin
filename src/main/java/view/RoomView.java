package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import DAO.Joueur;

public class RoomView {
    private Stage roomStage;

    public RoomView() {
        roomStage = new Stage();
        createUI();
    }

    private void createUI() {
        BorderPane root = new BorderPane();
        // Fond spatial
        BackgroundImage backgroundImage = new BackgroundImage(
                new Image("file:src/main/resources/images/space_background_800x600.png", 800, 600, false, true),
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(800, 600, false, false, false, false)
        );
        root.setBackground(new Background(backgroundImage));
        // Bouton retour en haut à gauche
        Button backButton = new Button("← Retour");
        backButton.setStyle("-fx-background-radius: 20; -fx-background-color: #4f8cff; -fx-text-fill: white; -fx-font-weight: bold;");
        backButton.setOnAction(e -> {
            roomStage.close();
            HomeView home = new HomeView(new Joueur());
            home.getStage().show();
        });

        HBox topBar = new HBox(backButton);
        topBar.setAlignment(Pos.TOP_LEFT);
        topBar.setPadding(new Insets(10));
        root.setTop(topBar);


        Button createRoomBtn = new Button("Create Room");
        Button joinRoomBtn = new Button("Join Room");
        createRoomBtn.setStyle("-fx-font-size: 18px; -fx-background-radius: 20; -fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 30;");
        joinRoomBtn.setStyle("-fx-font-size: 18px; -fx-background-radius: 20; -fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 30;");

        VBox centerBox = new VBox(30, createRoomBtn, joinRoomBtn);
        centerBox.setAlignment(Pos.CENTER);

        root.setCenter(centerBox);

        Scene scene = new Scene(root, 800, 600);
        roomStage.setTitle("Room");
        roomStage.setScene(scene);
    }

    public Stage getStage() {
        return roomStage;
    }
}
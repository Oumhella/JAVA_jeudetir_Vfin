package view;

import DAO.Joueur;
import javafx.animation.PauseTransition;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

public class HomeView {
    private static final int longeur = 800;
    private static final int largeur = 600;
    private Stage Homestage;
    private Scene Homescene;
    private AnchorPane Homepane;
    private StackPane bouttonContainer;


    public HomeView(Joueur joueur) {
        Homepane = new AnchorPane();
        Homescene = new Scene(Homepane, longeur, largeur);
        Homestage = new Stage();
        Homestage.setScene(Homescene);
        Homestage.setTitle("Menu Principal");
        Homestage.getIcons().add(new Image("images/2D_Space_Shooter_3.0_Free_1.3/Export/NoLight/Large/Ship_2_E_Large_NoLight.png"));

        setBackground();
        buildHOME(joueur);
    }

    public Stage getStage() {
        return Homestage;
    }

    private void setBackground() {
        Image bg = new Image("images/space_background_800x600.png");
        BackgroundImage bgImage = new BackgroundImage(bg, null, null, null, BackgroundSize.DEFAULT);
        Homepane.setBackground(new Background(bgImage));
    }

    private void buildHOME(Joueur joueur) {
        // Message de bienvenue
        Label message = new Label("Welcome, " + joueur.getNom() + " ! \nReady to play?");
        message.setFont(Font.font("Orbitron", FontWeight.BOLD, 35));
        message.setTextFill(Color.WHITE);
        message.setLayoutX(20);
        message.setLayoutY(80);
        message.setStyle("-fx-background-color: rgba(30,222,146,0.39);" +  // fond semi-transparent
                "-fx-padding: 8;" +                          // marges internes
                "-fx-background-radius: 10;"                  // coins arrondis
        );

        // Bouton : Jouer en solo
        StackPane soloBtn = createButton("SOLO", 330, 230);
        soloBtn.setOnMouseClicked(e -> {
            Homestage.hide();
            SoloSelectionView menu = new SoloSelectionView(joueur);
            menu.getStage().show();
        });

        StackPane groupeBtn = createButton("ROOM", 330, 340);
        groupeBtn.setOnMouseClicked(e -> {

                LoadingScene loadingScreen = new LoadingScene("LOADING THE GAME...");
                loadingScreen.getStage().show();
                Homestage.hide();
                // For testing locally, use a fixed room ID or let user input one
                TextInputDialog dialog = new TextInputDialog("1234");
                dialog.setTitle("Room ID");
                dialog.setHeaderText("Enter Room ID");
                dialog.setContentText("Enter a room ID to join or create:");

                dialog.showAndWait().ifPresent(roomId -> {
                    try {
                        int roomIdInt = Integer.parseInt(roomId);
                        PauseTransition delay = new PauseTransition(Duration.seconds(2));
                        delay.setOnFinished(event1 -> {
                            MultijoueurView game = new MultijoueurView(joueur, roomIdInt);
                            loadingScreen.getStage().hide();
                            game.getMainStage().show();
                        });
                        delay.play();
                    } catch (NumberFormatException e1) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Invalid Room ID");
                        alert.setHeaderText(null);
                        alert.setContentText("Please enter a valid number for the room ID");
                        alert.showAndWait();
                        Homestage.show();
                        loadingScreen.getStage().hide();
                    }
                });

        });

        // Bouton : Dashboard
        StackPane dashboardBtn = createButton("DASHBOARD", 330, 460);
        dashboardBtn.setOnMouseClicked(e -> {
            Homestage.hide();
            DashboardView dashboard = new DashboardView(Homestage);
            dashboard.show();
        });

        Homepane.getChildren().addAll(message, soloBtn, groupeBtn, dashboardBtn);
    }

    private StackPane createButton(String text, double x, double y) {
        Image imageboutton = new Image("images/Prinbles_Buttons_Cosmo (v 1.0) (9_10_2023)/Prinbles_Buttons_Cosmo (v 1.0) (9_10_2023)/png@2x/Dummy-Rect/Default@2x.png");
        ImageView image = new ImageView(imageboutton);
        image.setFitWidth(180);
        image.setPreserveRatio(true);

        Label label = new Label(text);
        label.setFont(Font.font("Orbitron", FontWeight.BOLD, 23));
        label.setTextFill(Color.WHITE);
        label.setTranslateY(-2.5);

        bouttonContainer = new StackPane(image, label);
        bouttonContainer.setLayoutX(x);
        bouttonContainer.setLayoutY(y);

        return bouttonContainer;
    }
}


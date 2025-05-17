package view;

import DAO.Avion;
import DAO.LevelGame;
import DAO.Joueur;
import javafx.animation.PauseTransition;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;


public class SoloSelectionView {
    private static final int longeur = 800;
    private static final int largeur = 600;
    private Stage SoloSelectionStage;
    private Scene SoloSelectionScene;
    private AnchorPane SoloSelectionPANE;
    private StackPane bouttonContainer;
    private LevelGame game;

    public SoloSelectionView(Joueur joueur) {
        SoloSelectionPANE = new AnchorPane();
        SoloSelectionScene = new Scene(SoloSelectionPANE, longeur, largeur);
        SoloSelectionStage = new Stage();
        SoloSelectionStage.setScene(SoloSelectionScene);
        SoloSelectionStage.setTitle("Choix d'avion");
        SoloSelectionStage.getIcons().add(new Image("images/2D_Space_Shooter_3.0_Free_1.3/Export/NoLight/Large/Ship_2_E_Large_NoLight.png"));

        setBackground();
        buildSolo(joueur);
    }

    public Stage getStage() {
        return SoloSelectionStage;
    }

    private void setBackground() {
        Image bg = new Image("images/space_background_800x600.png");
        BackgroundImage bgImage = new BackgroundImage(bg, null, null, null, BackgroundSize.DEFAULT);
        SoloSelectionPANE.setBackground(new Background(bgImage));
    }

    private void buildSolo(Joueur joueur) {
        Label chooseLabel = new Label("Choisis ton avion");
        chooseLabel.setFont(Font.font("Orbitron", FontWeight.BOLD, 24));
        chooseLabel.setTextFill(Color.WHITE);
        chooseLabel.setLayoutX((longeur - 220) / 2);
        chooseLabel.setLayoutY(80);
        chooseLabel.setStyle("-fx-background-color: rgba(30,222,146,0.39);" +  // fond semi-transparent
                "-fx-padding: 8;" + // marges internes
                "-fx-background-radius: 10;" // coins arrondis
        );

        ToggleGroup groupAvion = new ToggleGroup();
        RadioButton f16 = createRadioAvion("F-16", "images/2D_Space_Shooter_3.0_Free_1.3/Export/Base/Large/Ship_2_B_Large.png", groupAvion, 60);
        RadioButton mirage = createRadioAvion("Mirage 2000", "images/2D_Space_Shooter_3.0_Free_1.3/Export/Base/Large/Ship_2_D_Large.png", groupAvion, 280);
        RadioButton su27 = createRadioAvion("Su-27", "images/2D_Space_Shooter_3.0_Free_1.3/Export/Base/Large/Ship_2_E_Large.png", groupAvion, 555);
        SoloSelectionPANE.getChildren().addAll(f16, mirage, su27,chooseLabel);

        Label difficulteLabel = new Label("Choisis la difficulté");
        difficulteLabel.setFont(Font.font("Orbitron", FontWeight.BOLD, 24));
        difficulteLabel.setTextFill(Color.WHITE);
        difficulteLabel.setLayoutX((longeur - 233) / 2);
        difficulteLabel.setLayoutY(330);
        difficulteLabel.setStyle("-fx-background-color: rgba(30,222,146,0.39);" +  // fond semi-transparent
                "-fx-padding: 8;" + // marges internes
                "-fx-background-radius: 10;" // coins arrondis
        );


        ComboBox<String> difficulteBox = new ComboBox<>();
        difficulteBox.getItems().addAll("Easy", "Medium", "Hard");
        difficulteBox.setValue("Easy");
        difficulteBox.setLayoutX(350);
        difficulteBox.setLayoutY(410);
        difficulteBox.setStyle("""
            -fx-background-color: rgba(255,255,255,0.41);
            -fx-border-color: #ffffff;
            -fx-border-width: 3px;
            -fx-border-radius: 8;
            -fx-background-radius: 8;
            -fx-text-fill: white;
            -fx-font-size: 14px;
            -fx-font-weight: bold;
        """);
        SoloSelectionPANE.getChildren().addAll(difficulteLabel, difficulteBox);


        StackPane startButton = createStartButton("START", 350, 470);
        SoloSelectionPANE.getChildren().add(startButton);

        startButton.setOnMouseClicked(e -> {
            RadioButton selectedAvion = (RadioButton) groupAvion.getSelectedToggle();
            String difficulty = difficulteBox.getValue();
            int difficultyInt = switch (difficulty) {
                case "Easy" -> 1;
                case "Medium" -> 2;
                case "Hard" -> 3;
                default -> 1;
            };

            if (selectedAvion != null) {
                String avionName = selectedAvion.getText();
                String avionPath = getImagePath(avionName);

                joueur.setDifficulty(difficulty);
                joueur.setAvion(avionName);

                game = new LevelGame(joueur.getId(), new Avion().getAvionByName(avionName).id, difficultyInt, 0);
                new LevelGame().ajouter(game);

                SoloSelectionStage.close();

                LoadingScene loadingScreen = new LoadingScene("LOADING THE GAME");
                loadingScreen.getStage().show();

                PauseTransition delay = new PauseTransition(Duration.seconds(2));
                delay.setOnFinished(event1 -> {
                    gameview jeu = new gameview(joueur,game,avionPath);
                    loadingScreen.getStage().close();
                    jeu.getMainStage().show();
                });
                delay.play();
            }
            else {
                Label message = new Label();
                message.setLayoutX(320);
                message.setLayoutY(525);
                message.setFont(Font.font("Orbitron", FontWeight.BOLD, 15));
                message.setTextFill(Color.WHITE);
                message.setText("Please choisir une avion");
                SoloSelectionPANE.getChildren().add(message);
            }
        });
    }

    private RadioButton createRadioAvion(String name, String path, ToggleGroup group, double x) {
        Image avion = new Image(path);
        ImageView img = new ImageView(avion);
        img.setFitHeight(100);
        img.setFitWidth(100);

        RadioButton btn = new RadioButton(name);
        btn.setToggleGroup(group);
        btn.setGraphic(img);
        btn.setLayoutX(x);
        btn.setLayoutY(170);
        btn.setTextFill(Color.WHITE);
        btn.setStyle(""" 
                -fx-background-color: rgba(255,255,255,0.25);
                -fx-background-radius: 10px;
                -fx-border-color: #ffffff;
                -fx-border-width: 3px;
                -fx-border-radius: 10px;
                -fx-text-fill: white;
                -fx-font-size: 14px;
                -fx-font-weight: bold;
                -fx-padding: 10px;
        """);

        btn.setOnMouseEntered(e -> btn.setStyle("""
                -fx-background-color: rgba(255,255,255,0.1);
                -fx-background-radius: 10px;
                -fx-border-color: #fce293;
                -fx-border-width: 3px;
                -fx-border-radius: 10px;
                -fx-text-fill: #fde192;
                -fx-font-size: 14px;
                -fx-font-weight: bold;
                -fx-padding: 10px;
        """));

        btn.setOnMouseExited(e -> btn.setStyle("""
                -fx-background-color: rgba(255,255,255,0.25);
                -fx-background-radius: 10px;
                -fx-border-color: #ffffff;
                -fx-border-width: 3px;
                -fx-border-radius: 10px;
                -fx-text-fill: white;
                -fx-font-size: 14px;
                -fx-font-weight: bold;
                -fx-padding: 10px;
        """));

        return btn;
    }

    private StackPane createStartButton(String text, double x, double y) {
        Image imageboutton = new Image("images/Prinbles_Buttons_Cosmo (v 1.0) (9_10_2023)/Prinbles_Buttons_Cosmo (v 1.0) (9_10_2023)/png@2x/Dummy-Rect/Default@2x.png");
        ImageView image = new ImageView(imageboutton);
        image.setFitWidth(100);
        image.setPreserveRatio(true);

        Label label = new Label(text);
        label.setFont(Font.font("Orbitron", FontWeight.BOLD, 14));
        label.setTextFill(Color.WHITE);
        label.setTranslateY(-2.5);

        bouttonContainer = new StackPane(image, label);
        bouttonContainer.setLayoutX(x);
        bouttonContainer.setLayoutY(y);

        return bouttonContainer;
    }

    private String getImagePath(String name) {
        return switch (name) {
            case "F-16" -> "images/2D_Space_Shooter_3.0_Free_1.3/Export/Base/Large/Ship_2_B_Large.png";
            case "Mirage 2000" -> "images/2D_Space_Shooter_3.0_Free_1.3/Export/Base/Large/Ship_2_D_Large.png";
            case "Su-27" -> "images/2D_Space_Shooter_3.0_Free_1.3/Export/Base/Large/Ship_2_E_Large.png";
            default -> "/images/defaultAvion.png";
        };
    }
}


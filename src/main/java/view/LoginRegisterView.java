package view;

import DAO.Avion;
import DAO.Joueur;
import DAO.JoueurHandler;
import DAO.LevelGame;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import model.avion;

public class LoginRegisterView {
    private static final int longeur = 800;
    private static final int largeur = 600;
    private Joueur joueur;
    private LevelGame game;
    private String selectedAvionImagePath;



    public Joueur showLogin(Stage primaryStage) {
        // Fields
        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setFont(Font.font("Segoe UI", 14));
        usernameField.setStyle("-fx-background-radius: 10; -fx-background-color: #3b3b4f; -fx-text-fill: white; -fx-prompt-text-fill: #bbb; -fx-padding: 8 12;");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setFont(Font.font("Segoe UI", 14));
        passwordField.setStyle("-fx-background-radius: 10; -fx-background-color: #3b3b4f; -fx-text-fill: white; -fx-prompt-text-fill: #bbb; -fx-padding: 8 12;");

        Label messageLabel = new Label();
        messageLabel.setTextFill(Color.RED);
        messageLabel.setFont(Font.font("Segoe UI", 12));


        VBox loginVBox = new VBox(15);
        loginVBox.setAlignment(Pos.CENTER);
        loginVBox.setStyle("-fx-background-color: rgba(0,0,0,0.5); -fx-background-radius: 15; -fx-padding: 30;");
        loginVBox.setMaxWidth(350);

        // Background
        Image backroungimage = new Image("images/background.png");
        BackgroundImage backgroundimage = new BackgroundImage(backroungimage, null, null, null, BackgroundSize.DEFAULT);
        Background background1 = new Background(backgroundimage);

        AnchorPane loginPane = new AnchorPane();
        loginPane.setBackground(background1);
        loginPane.getChildren().add(loginVBox);
        AnchorPane.setTopAnchor(loginVBox, 100.0);
        AnchorPane.setLeftAnchor(loginVBox, 75.0);
        AnchorPane.setRightAnchor(loginVBox, 75.0);

        Button loginButton = new Button("Login");
        loginButton.setStyle("-fx-background-radius: 8; -fx-background-color: #4CAF50; -fx-text-fill: white;");
        loginButton.setOnMouseEntered(e -> loginButton.setStyle("-fx-background-color: #45a049; -fx-text-fill: white;"));
        loginButton.setOnMouseExited(e -> loginButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;"));

        Button registerButton = new Button("Register");
        registerButton.setStyle("-fx-background-radius: 8; -fx-background-color: #0d974a; -fx-text-fill: white;");
        registerButton.setOnMouseEntered(e -> registerButton.setStyle("-fx-background-color: #45a049; -fx-text-fill: white;"));
        registerButton.setOnMouseExited(e -> registerButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;"));

        loginVBox.getChildren().addAll(usernameField, passwordField, loginButton, registerButton, messageLabel);

        DropShadow shadow = new DropShadow();
        shadow.setOffsetY(2.0);
        shadow.setColor(Color.color(0, 0, 0, 0.6));
        loginVBox.setEffect(shadow);

        Scene scene = new Scene(loginPane, longeur, largeur);;
        primaryStage.setScene(scene);
        primaryStage.setTitle(" Game Login ");
        Image icon = new Image("images/2D_Space_Shooter_3.0_Free_1.3/Export/NoLight/Large/Ship_2_E_Large_NoLight.png");
        primaryStage.getIcons().add(icon);


        loginButton.setOnAction(e -> {
            Joueur loggedIn = JoueurHandler.login(usernameField.getText(), passwordField.getText());
            if (loggedIn != null) {
                joueur = loggedIn;
                showAvionAndDifficultyScene(primaryStage);
            } else {
                messageLabel.setText("Invalid credentials!");
            }
        });

        registerButton.setOnAction(e -> {
            boolean success = JoueurHandler.register(usernameField.getText(), passwordField.getText());
            if (success) {
                Joueur loggedIn = JoueurHandler.login(usernameField.getText(), passwordField.getText());
                if (loggedIn != null) {
                    joueur = loggedIn;
                    showAvionAndDifficultyScene(primaryStage);
                }
            } else {
                messageLabel.setText("Username already taken!");
            }
        });

        primaryStage.showAndWait();
        return joueur;
    }

    private RadioButton createAvionOption(String name, String imagePath, ToggleGroup group) {
        ImageView imageView = new ImageView(new Image(imagePath));
        imageView.setFitWidth(100);
        imageView.setFitHeight(75);

        RadioButton button = new RadioButton(name);
        button.setGraphic(imageView);
        button.setToggleGroup(group);
        button.setStyle("-fx-text-fill: white; -fx-background-color: transparent; -fx-border-color: #666; -fx-border-radius: 10; -fx-padding: 10;");
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: #333; -fx-border-color: #2ab566; -fx-border-radius: 10; -fx-padding: 10; -fx-text-fill: white;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: transparent; -fx-border-color: #666; -fx-border-radius: 10; -fx-padding: 10; -fx-text-fill: white;"));

        return button;
    }

    private String getAvionImagePath(String avionName) {
        return switch (avionName) {
            case "F-16" -> "/images/f16.png";
            case "Mirage 2000" -> "images/2D_Space_Shooter_3.0_Free_1.3/Export/Base/Large/Ship_2_E_Large.png";
            case "Su-27" -> "images/2D_Space_Shooter_3.0_Free_1.3/Export/Base/Large/Ship_2_D_Large.png";
            default -> "images/2D_Space_Shooter_3.0_Free_1.3/Export/Base/Large/Ship_2_E_Large.png";
        };
    }

    private void showAvionAndDifficultyScene(Stage primaryStage) {
        VBox setupBox = new VBox(20);
        setupBox.setAlignment(Pos.CENTER);

        Image backroungimage = new Image("images/background.png");
        BackgroundImage backgroundimage = new BackgroundImage(backroungimage, null, null, null, BackgroundSize.DEFAULT);
        Background background2 = new Background(backgroundimage);
        setupBox.setBackground(background2);


        Label chooseAvion = new Label("CHOOSE YOUR PLANE");
        chooseAvion.setFont(Font.font("Arial", FontWeight.BOLD, 32.5));
        chooseAvion.setTextFill(Color.WHITE);

        HBox avionSelection = new HBox(20);
        avionSelection.setAlignment(Pos.CENTER);

        ToggleGroup avionToggleGroup = new ToggleGroup();

        RadioButton mirage = createAvionOption("Mirage 2000", "images/2D_Space_Shooter_3.0_Free_1.3/Export/Base/Large/Ship_2_E_Large.png", avionToggleGroup);
        RadioButton f16 = createAvionOption("F-16", "/images/f16.png", avionToggleGroup);
        RadioButton su27 = createAvionOption("Su-27", "images/2D_Space_Shooter_3.0_Free_1.3/Export/Base/Large/Ship_2_D_Large.png", avionToggleGroup);

        avionSelection.getChildren().addAll(f16, mirage, su27);

        Label chooseDifficulty = new Label("CHOOSE DIFFICULTY");
        chooseDifficulty.setFont(Font.font("Arial", FontWeight.BOLD, 25));
        chooseDifficulty.setTextFill(Color.WHITE);

        ComboBox<String> difficultyBox = new ComboBox<>();
        difficultyBox.getItems().addAll("Easy", "Medium", "Hard");
        difficultyBox.setStyle("-fx-background-radius: 10; -fx-background-color: #dfe1e5; -fx-text-fill: white;");

        Button startButton = new Button("NEXT");
        startButton.setStyle("-fx-background-color: #2ab566; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 8;");
        startButton.setOnMouseEntered(e -> startButton.setStyle("-fx-background-color: #2ab566; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 8;"));
        startButton.setOnMouseExited(e -> startButton.setStyle("-fx-background-color: #2ab566; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 8;"));

        setupBox.getChildren().addAll(chooseAvion, avionSelection, chooseDifficulty, difficultyBox, startButton);

        Scene scene = new Scene(setupBox, longeur, largeur);
        primaryStage.setScene(scene);

        startButton.setOnAction(e -> {
            RadioButton selectedAvion = (RadioButton) avionToggleGroup.getSelectedToggle();
            String difficulty = difficultyBox.getValue();

            if (selectedAvion == null || difficulty == null) {
                new Alert(Alert.AlertType.WARNING, "Please select avion and difficulty.").show();
                return;
            }

            String avionName = selectedAvion.getText();
            joueur.setAvion(avionName);
            joueur.setDifficulty(difficulty);

            String imagePath = getAvionImagePath(avionName);
            selectedAvionImagePath = imagePath;

            avion playerAvion = new avion(imagePath);

            int difficultyLevel = switch (difficulty) {
                case "Easy" -> 1;
                case "Medium" -> 2;
                case "Hard" -> 3;
                default -> 1;
            };

            game = new LevelGame(joueur.getId(), new Avion().getAvionByName(avionName).id, difficultyLevel, 0);
            new LevelGame().ajouter(game);

            primaryStage.close();
        });
    }

    public LevelGame getGame() {
        return game;
    }

    public String getSelectedAvionImagePath() {
        return selectedAvionImagePath;
    }
}

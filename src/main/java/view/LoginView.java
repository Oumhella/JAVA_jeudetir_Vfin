package view;

import DAO.Joueur;
import DAO.JoueurHandler;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class LoginView {
    private static final int longeur = 800;
    private static final int largeur = 600;
    private AnchorPane loginPane;
    private Scene loginScene;
    private Stage loginStage;
    private Joueur joueur;
    private StackPane loginContainer;
    private StackPane regesterContainer;
    private StackPane bouttonContainer;

    public LoginView() {
        loginPane = new AnchorPane();
        loginScene = new Scene(loginPane, longeur, largeur);
        loginStage = new Stage();
        loginStage.setScene(loginScene);
        loginStage.setTitle("Login");
        loginStage.getIcons().add(new Image("images/2D_Space_Shooter_3.0_Free_1.3/Export/NoLight/Large/Ship_2_E_Large_NoLight.png"));
        background();
        Nom();
        buildInterface();
    }

    public Stage getLoginStage() {
        return loginStage;
    }

    private void background() {
        Image bg = new Image("images/space_background_800x600.png");
        BackgroundImage bgImage = new BackgroundImage(bg, null, null, null, BackgroundSize.DEFAULT);
        loginPane.setBackground(new Background(bgImage));
    }


    private void Nom() {
        Label labelNOM = new Label("GALAXY ATTACK");
        labelNOM.setFont(Font.font("Orbitron", FontWeight.BOLD, 48));
        labelNOM.setTextFill(Color.WHITE);
        labelNOM.setLayoutX((longeur - 400) / 2);
        labelNOM.setLayoutY(100);
        labelNOM.setStyle("-fx-background-color: rgba(30,222,146,0.39);" +  // fond semi-transparent
                        "-fx-padding: 8;" + // marges internes
                        "-fx-background-radius: 10;" // coins arrondis
        );
        loginPane.getChildren().add(labelNOM);
    }


    private void buildInterface() {
        TextField username = new TextField();
        username.setPromptText("Nom d'utilisateur");
        username.setPrefWidth(300);
        username.setPrefHeight(50);
        username.setLayoutX((longeur - 300) / 2);
        username.setLayoutY(220);
        username.setStyle("-fx-background-radius: 10;" + "-fx-background-color: rgba(255,255,255,0.2);" + "-fx-text-fill: white;" + "-fx-font-size: 16px;" +
                "-fx-prompt-text-fill: #cccccc;" + "-fx-border-color: white;" + "-fx-border-radius: 10;" + "-fx-border-width: 1.5;" +
                "-fx-padding: 8 12;"
        );

        PasswordField password = new PasswordField();
        password.setPromptText("Mot de passe");
        password.setPrefWidth(300);
        password.setPrefHeight(50);
        password.setLayoutX((longeur - 300) / 2);
        password.setLayoutY(290);
        password.setStyle("-fx-background-radius: 10;" + "-fx-background-color: rgba(255,255,255,0.2);" + "-fx-text-fill: white;" + "-fx-font-size: 16px;" +
                        "-fx-prompt-text-fill: #cccccc;" + "-fx-border-color: white;" + "-fx-border-radius: 10;" + "-fx-border-width: 1.5;" +
                        "-fx-padding: 8 12;"
        );

        Label message = new Label();
        message.setLayoutX((longeur - 225) / 2);
        message.setLayoutY(440);
        message.setFont(Font.font("Orbitron", FontWeight.BOLD, 15));
        message.setTextFill(Color.WHITE);

        Label message1 = new Label();
        message1.setLayoutX((longeur - 165) / 2);
        message1.setLayoutY(440);
        message1.setFont(Font.font("Orbitron", FontWeight.BOLD, 15));
        message1.setTextFill(Color.WHITE);

        // Bouton Login
        loginContainer = createButton("LOGIN", 285, 370);
        loginContainer.setOnMouseClicked(e -> {
            message.setText("");
            message1.setText("");
            Joueur j = JoueurHandler.login(username.getText(), password.getText());
            if (j != null) {
                joueur = j;
                loginStage.close();
                HomeView Home = new HomeView(joueur);
                Home.getStage().show();
            } else {
                message.setText("Nom ou mot de passe incorrect");
            }
        });

        // Bouton Register
        regesterContainer = createButton("REGISTER", 410, 370);
        regesterContainer.setOnMouseClicked(e -> {
            message1.setText("");
            message.setText("");
            boolean success = JoueurHandler.register(username.getText(), password.getText());
            if (success) {
                joueur = JoueurHandler.login(username.getText(), password.getText());
                loginStage.close();
                HomeView Home = new HomeView(joueur);
                Home.getStage().show();
            } else {
                message1.setText("Compte déjà existant");
            }
        });

        loginPane.getChildren().addAll(username, password, message, message1, regesterContainer, loginContainer);
    }

    private StackPane createButton(String text, double x, double y) {
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
}


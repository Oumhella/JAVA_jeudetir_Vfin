package view;

import DAO.Joueur;
import DAO.LevelGame;
import javafx.animation.PauseTransition;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;

public class home {

    private static final int MENU_WIDTH = 800;
    private static final int MENU_HEIGHT = 600;
    private static final int MENU_BUTTONX = MENU_WIDTH / 2;
    private static final int MENU_BUTTONY = MENU_HEIGHT / 8;
    private static int MENU_BUTTON_DEPLACE =MENU_HEIGHT / 8 ;

    private Joueur joueur;
    private LevelGame gamee;
    private String avion;
    private AnchorPane menuPane;
    private Scene menuScene;
    private Stage menuStage;

    public home() {
        Stage loginStage = new Stage();
        LoginRegisterView loginView = new LoginRegisterView();
        joueur = loginView.showLogin(loginStage);
        gamee = loginView.getGame();
        avion = loginView.getSelectedAvionImagePath();

        if (joueur == null) {
            System.exit(0);
        }


        menuPane = new AnchorPane();
        menuScene = new Scene(menuPane, MENU_WIDTH, MENU_HEIGHT);
        menuStage = new Stage();
        menuStage.setScene(menuScene);

        CreateBackGround();
        CreateButtonS();
    }


    private void CreateBackGround() {
            Image backroungimage = new Image("images/background.png");
            BackgroundImage backgroundimage = new BackgroundImage(backroungimage, null, null, null, BackgroundSize.DEFAULT);
            if (backroungimage.isError()) {
                System.out.println("Erreur chargement image : " + backroungimage.getException());
            }
            menuPane.setBackground(new Background(backgroundimage));
    }

    private void CreateButtonS() {
        CreateStartButton();
        CreatStartOnline();
        CreateExiteButton();
    }

    private void CreateStartButton() {
        Image normalImage = new Image("BUTTONS/DEFAULT.png");
        Image pressedImage = new Image("BUTTONS/HOVER.png");
        ImageView button = new ImageView(normalImage);
        button.setPreserveRatio(true);
        button.setFitWidth(250);

        Label startLabel = new Label("START");
        startLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28 ));
        startLabel.setTextFill(Color.WHITE);

        StackPane startContainer = new StackPane(button, startLabel);
        startContainer.setAlignment(Pos.CENTER);
        startLabel.setTranslateY(-5);

        startContainer.setLayoutX(MENU_BUTTONX -(button.getFitWidth()/2));

        int Y = MENU_BUTTONY +MENU_BUTTON_DEPLACE;
        startContainer.setLayoutY(Y);


        // Mouse PRESSED: change image and move down
        button.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            button.setImage(pressedImage);
            button.setY(Y + 4);
        });

        // Mouse RELEASED: return to normal image and position
        button.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
            button.setImage(normalImage);
            button.setY(Y);
        });

        // Mouse CLICKED: open Loading and next scene
        button.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            LoadingScene loadingScreen = new LoadingScene("LOADING THE GAME");
            loadingScreen.getStage().show();
            menuStage.hide();

            PauseTransition delay = new PauseTransition(Duration.seconds(2));
            delay.setOnFinished(event1 -> {
                gameview game = new gameview(joueur,gamee,avion);
                loadingScreen.getStage().hide();
                game.getMainStage().show();
            });
            delay.play();
        });

        // Mouse ENTERED (hover): scale up
        button.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> {
            button.setScaleX(1.05);
            button.setScaleY(1.05);
        });

        // Mouse EXITED: scale back to normal
        button.addEventHandler(MouseEvent.MOUSE_EXITED, e -> {
            button.setScaleX(1.0);
            button.setScaleY(1.0);
            button.setImage(normalImage);
            button.setY(Y);
        });

        menuPane.getChildren().add(startContainer);
    }

    private void CreatStartOnline() {
        Image normalImage = new Image("BUTTONS/DEFAULT.png");
        Image pressedImage = new Image("BUTTONS/HOVER.png");
        ImageView button = new ImageView(normalImage);
        button.setPreserveRatio(true);
        button.setFitWidth(250);

        Label startLabel = new Label("START ONLINE");
        startLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28 ));
        startLabel.setTextFill(Color.WHITE);

        StackPane startContainer = new StackPane(button, startLabel);
        startContainer.setAlignment(Pos.CENTER);
        startLabel.setTranslateY(-5);

        startContainer.setLayoutX(MENU_BUTTONX-button.getFitWidth()/2);

        int Y = MENU_BUTTONY + MENU_BUTTON_DEPLACE + 135;

        startContainer.setLayoutY(Y);

        startContainer.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            button.setImage(pressedImage);
            startContainer.setLayoutY(Y + 4);
        });

        // Mouse RELEASED: return to normal image and position
        startContainer.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
            button.setImage(normalImage);
            startContainer.setLayoutY(Y);
        });

        // Mouse CLICKED: open Loading and next scene
        startContainer.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            LoadingScene loadingScreen = new LoadingScene("LOADING THE GAME...");
            loadingScreen.getStage().show();
            menuStage.hide();
            Joueur joueur = new Joueur(1324,"abdellaatif",121,0);

            PauseTransition delay = new PauseTransition(Duration.seconds(2));
            delay.setOnFinished(event1 -> {
                MultijoueurView game = new MultijoueurView(joueur,1234);
                loadingScreen.getStage().hide();
                game.getMainStage().show();
            });
            delay.play();
        });

        startContainer.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> {
            startContainer.setScaleX(1.05);
            startContainer.setScaleY(1.05);
        });

        startContainer.addEventHandler(MouseEvent.MOUSE_EXITED, e -> {
            startContainer.setScaleX(1.0);
            startContainer.setScaleY(1.0);
            button.setImage(normalImage);
            startContainer.setLayoutY(Y);
        });

        menuPane.getChildren().add(startContainer);

    }

    public void CreateExiteButton() {
        Image normalImage = new Image("BUTTONS/DEFAULT.png");
        Image pressedImage = new Image("BUTTONS/HOVER.png");
        ImageView button = new ImageView(normalImage);
        button.setPreserveRatio(true);
        button.setFitWidth(normalImage.getWidth()*0.7*0.4);

        Label exitLabel = new Label("EXIT");
        exitLabel.setFont(Font.font("Arial", FontWeight.BOLD, 25 ));
        exitLabel.setTextFill(Color.WHITE);

        StackPane exitContainer = new StackPane(button, exitLabel);
        exitContainer.setAlignment(Pos.CENTER);
        exitLabel.setTranslateY(-3);

        exitContainer.setLayoutX(MENU_BUTTONX-button.getFitWidth()/2);

        int Y = MENU_BUTTONY +( MENU_BUTTON_DEPLACE + 2*135);

        exitContainer.setLayoutY(Y);

        exitContainer.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            button.setImage(pressedImage);
            exitContainer.setLayoutY(Y + 4);
        });

        // Mouse RELEASED: return to normal image and position
        exitContainer.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
            button.setImage(normalImage);
            exitContainer.setLayoutY(Y);
        });

        // Mouse CLICKED: open Loading and next scene
        exitContainer.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            System.exit(0);
        });

        // Mouse ENTERED (hover): scale up
        exitContainer.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> {
            exitContainer.setScaleX(1.05);
            exitContainer.setScaleY(1.05);
        });

        // Mouse EXITED: scale back to normal
        exitContainer.addEventHandler(MouseEvent.MOUSE_EXITED, e -> {
            exitContainer.setScaleX(1.0);
            exitContainer.setScaleY(1.0);
            button.setImage(normalImage);
            exitContainer.setLayoutY(Y);
        });

        menuPane.getChildren().add(exitContainer);

        Image icon = new Image("images/2D_Space_Shooter_3.0_Free_1.3/Export/NoLight/Large/Ship_2_E_Large_NoLight.png");
        menuStage.getIcons().add(icon);

    }

    public Stage getStage() {
        return menuStage;
    }

    public static int getMenuWidth() {
        return MENU_WIDTH;
    }

    public static int getMenuHeight() {
        return MENU_HEIGHT;
    }
}
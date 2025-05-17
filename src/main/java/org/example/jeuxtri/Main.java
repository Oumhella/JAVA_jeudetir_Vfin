package org.example.jeuxtri;

import javafx.application.Application;
import javafx.stage.Stage;
import view.LoginView;
import view.home;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            /*home home = new home();
            primaryStage = home.getStage();
            primaryStage.setOnCloseRequest(e -> {
                System.exit(0);
            });
            primaryStage.show();*/

            LoginView login = new LoginView();
            primaryStage = login.getLoginStage();
            primaryStage.setTitle("Jeu de Tir");
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
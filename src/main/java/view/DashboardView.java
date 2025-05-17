package view;

import javafx.animation.ScaleTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import DAO.Joueur;
import DAO.ConnexionBD;
import java.sql.Connection;
import java.util.List;
import java.util.ArrayList;

public class DashboardView {
    private Stage primaryStage;
    private BorderPane root;
    private ObservableList<Joueur> users;
    private List<Joueur> userList;
    private VBox tableContainer;
    private TableView<Joueur> tableView;
    private boolean isExpanded = false;

    public DashboardView(Stage primaryStage) {
        this.primaryStage = primaryStage;
        createUI();
    }

    private void createUI() {
        root = new BorderPane();
        // Fond spatial
        BackgroundImage backgroundImage = new BackgroundImage(
                new Image("file:src/main/resources/images/space_background_800x600.png", 800, 600, false, true),
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(800, 600, false, false, false, false)
        );
        root.setBackground(new Background(backgroundImage));

        // Boutons modernes
        Button refreshButton = new Button("🔄 Rafraîchir");
        refreshButton.setStyle("-fx-background-color: #4f8cff; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20; -fx-padding: 8 20; -fx-effect: dropshadow(gaussian, #b3c6ff, 8, 0.2, 0, 2);");
        refreshButton.setOnMouseEntered(e -> refreshButton.setStyle("-fx-background-color: #357ae8; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20; -fx-padding: 8 20; -fx-effect: dropshadow(gaussian, #b3c6ff, 12, 0.3, 0, 4);"));
        refreshButton.setOnMouseExited(e -> refreshButton.setStyle("-fx-background-color: #4f8cff; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20; -fx-padding: 8 20; -fx-effect: dropshadow(gaussian, #b3c6ff, 8, 0.2, 0, 2);"));

        Button showAllButton = new Button("📜 Afficher tout");
        showAllButton.setStyle("-fx-background-color: #ffffffcc; -fx-text-fill: #357ae8; -fx-font-weight: bold; -fx-background-radius: 20; -fx-padding: 8 20; -fx-effect: dropshadow(gaussian, #b3c6ff, 8, 0.2, 0, 2);");
        showAllButton.setOnMouseEntered(e -> showAllButton.setStyle("-fx-background-color: #e0eafc; -fx-text-fill: #357ae8; -fx-font-weight: bold; -fx-background-radius: 20; -fx-padding: 8 20; -fx-effect: dropshadow(gaussian, #b3c6ff, 12, 0.3, 0, 4);"));
        showAllButton.setOnMouseExited(e -> showAllButton.setStyle("-fx-background-color: #ffffffcc; -fx-text-fill: #357ae8; -fx-font-weight: bold; -fx-background-radius: 20; -fx-padding: 8 20; -fx-effect: dropshadow(gaussian, #b3c6ff, 8, 0.2, 0, 2);"));

        HBox topBar = new HBox(refreshButton);
        topBar.setStyle("-fx-padding: 10px; -fx-background-color: transparent;");
        root.setTop(topBar);

        // TableView stylée
        tableView = new TableView<>();
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.setStyle("-fx-background-color: transparent; -fx-table-cell-border-color: transparent;");

        TableColumn<Joueur, Integer> IDColumn = new TableColumn<>("ID");
        IDColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<Joueur, String> NomColumn = new TableColumn<>("Nom");
        NomColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        TableColumn<Joueur, Integer> scoreColumn = new TableColumn<>("Max Score");
        scoreColumn.setCellValueFactory(new PropertyValueFactory<>("maxScore"));
        NomColumn.setResizable(false);
        scoreColumn.setResizable(false);
        tableView.getColumns().addAll(IDColumn, NomColumn, scoreColumn);

        // Style header et lignes
        tableView.setRowFactory(tv -> {
            TableRow<Joueur> row = new TableRow<>();
            row.setStyle("-fx-background-color: rgba(255,255,255,0.7); -fx-background-radius: 8; -fx-border-color: #e0eafc; -fx-border-radius: 8;");
            row.setOnMouseEntered(e -> row.setStyle("-fx-background-color: #f0f7ff; -fx-background-radius: 8; -fx-border-color: #b3c6ff; -fx-border-radius: 8;"));
            row.setOnMouseExited(e -> row.setStyle("-fx-background-color: rgba(255,255,255,0.7); -fx-background-radius: 8; -fx-border-color: #e0eafc; -fx-border-radius: 8;"));
            return row;
        });

        // 📥 Remplir avec les données (affichage initial : 3 premiers joueurs)
        users = FXCollections.observableArrayList();
        Connection connection = ConnexionBD.seconnecter();
        userList = new ArrayList<>();

        if (connection != null) {
            Joueur joueurDAO = new Joueur(connection);
            userList = joueurDAO.getAllJoueurs();

            // Ajouter seulement les trois premiers joueurs
            if (userList.size() > 3) {
                users.addAll(userList.subList(0, 3));
            } else {
                users.addAll(userList);
            }
        }
        tableView.setItems(users);

        // Tailles initiales
        tableView.setMinSize(300, 120);
        tableView.setMaxSize(300, 120);

        // Effet glassmorphism pour le container
        tableContainer = new VBox(tableView, showAllButton);
        tableContainer.setAlignment(Pos.CENTER);
        tableContainer.setStyle(
                "-fx-padding: 20px; " +
                        "-fx-background-color: rgba(255, 255, 255, 0.5); " +
                        "-fx-background-radius: 20; " +
                        "-fx-effect: dropshadow(gaussian, #b3c6ff, 20, 0.3, 0, 8); " +
                        "-fx-border-radius: 20; " +
                        "-fx-border-color: #e0eafc; "
        );
        tableContainer.setMinSize(350, 180);
        tableContainer.setMaxSize(350, 180);

        root.setCenter(tableContainer);

        // Bouton retour en bas à gauche
        Button backButton = new Button("← Retour");
        backButton.setStyle("-fx-background-radius: 20; -fx-background-color: #4f8cff; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 20;");
        backButton.setOnAction(e -> {
            primaryStage.close();
            HomeView home = new HomeView(new Joueur());
            home.getStage().show();
        });
        HBox bottomBar = new HBox(backButton);
        bottomBar.setAlignment(Pos.BOTTOM_LEFT);
        bottomBar.setPadding(new Insets(10));
        root.setBottom(bottomBar);

        // Action pour rafraîchir la table
        refreshButton.setOnAction(e -> {
            users.clear();
            List<Joueur> refreshedList = new Joueur().getAllJoueurs();
            users.addAll(refreshedList.size() > 3 ? refreshedList.subList(0, 3) : refreshedList);
        });

        // Action pour afficher toute la liste
        showAllButton.setOnAction(e -> {
            if (!isExpanded) {
                users.clear();
                users.addAll(userList);
                resizeComponent(tableView, 300, 120, 400, 800);
                resizeComponent(tableContainer, 350, 180, 450, 850);
                isExpanded = true;
            }
        });

        // Création de la scène et affichage
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("Dashboard Joueurs");
        primaryStage.setScene(scene);
    }

    // Animation largeur + hauteur
    private void resizeComponent(Region node, double fromWidth, double fromHeight, double toWidth, double toHeight) {
        ScaleTransition transition = new ScaleTransition(Duration.seconds(0.5), node);
        transition.setFromX(fromWidth / toWidth);
        transition.setFromY(fromHeight / toHeight);
        transition.setToX(1);
        transition.setToY(1);
        transition.play();
    }

    public void show() {
        primaryStage.show();
    }
}
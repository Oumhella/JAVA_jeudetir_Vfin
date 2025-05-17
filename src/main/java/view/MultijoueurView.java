package view;

import DAO.Joueur;
import DAO.LevelGame;
import javafx.animation.AnimationTimer;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.avion;
import model.avionEnnemie;
import model.projectil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class MultijoueurView {
    private static final int longeur = 800;
    private static final int largeur = 600;

    // Chat-related fields
    private TextArea chatArea;
    private TextField messageInput;
    private VBox chatBox;
    private Button sendButton;
    private static final int CHAT_HEIGHT = 100;
    private static final int CHAT_WIDTH = 180;

    private boolean startGame = false;

    public static final int limits = longeur / 2 ;

    private PrintWriter out ;

    private AnchorPane mainPane;
    private Scene mainScene;
    private Stage mainStage;

    private int playerServerId ;

    private avion playerAvion;
    private avion enemyAvion;

    private boolean gameOverPlayer =false ;
    private boolean gameOverEnnemi =false ;


    private boolean gameOver = false;

    private boolean waitingForRestartPlayer=false;
    private boolean waitingForRestartEnnemi=false;

    private boolean waitingForRestart = false;

    private int scorePlayer;
    private int scoreEnnemie;

    private Label playerScoreLabel;
    private Label enemyScoreLabel;

    private StackPane playerScoreContainer;
    private StackPane enemyScoreContainer;

    private Label gameOverText;
    private StackPane gameOverContainer;

    private AnimationTimer gameLoop;
    private boolean leftPressed, rightPressed, upPressed, downPressed, spacePressed;
    private List<ImageView> playerHearts = new ArrayList<>();
    private List<ImageView> enemyHearts = new ArrayList<>();
    private final int MAX_VIES = 3;

    private ImageView pauseButton;
    private ImageView replayButton;
    private ImageView homeButton;
    private boolean isPaused = false;

    BufferedReader in ;

    private int roomId ;

    private Joueur joueur;

    private LevelGame game;
    private String avionPath;

    private long lastShootTime = 0;
    private static final long SHOOT_COOLDOWN = 300; // 300ms cooldown between shots
    private List<ImageView> enemyProjectiles = new ArrayList<>(); // Track enemy projectiles

    public MultijoueurView(Joueur joueur, int roomId) {
        this.joueur = joueur;
        this.roomId = roomId;
        this.game = new LevelGame(1);
        this.avionPath = "images/Ship_1_B_Large.png";

        mainPane = new AnchorPane();
        mainScene = new Scene(mainPane, longeur, largeur);
        mainStage = new Stage();
        mainStage.setScene(mainScene);

        setupGame();
        if (!connectToServer(joueur.getId(), roomId)) {
            System.out.println("Failed to connect to server!");
            return;
        }
    }

    private void setupGame() {
        NomIcon();
        backgroud();
        createPlayers();
        setKeyListeners();
        createControlButtons();
        startGameLoop();
        createChatUI();
    }

    private void createPlayers() {
        playerAvion = new avion(avionPath);
        enemyAvion = new avion("images/Ship_1_C_Large.png");

        playerAvion.getImageAvion().setLayoutX(longeur/2 - playerAvion.getImageAvion().getFitWidth()/2);
        playerAvion.getImageAvion().setLayoutY(largeur - 100);

        enemyAvion.getImageAvion().setLayoutX(longeur/2 - enemyAvion.getImageAvion().getFitWidth()/2);
        enemyAvion.getImageAvion().setLayoutY(100);

        enemyAvion.getImageAvion().setRotate(180);

        mainPane.getChildren().addAll(playerAvion.getImageAvion(), enemyAvion.getImageAvion());
        createHearts();
    }

    private void createHearts() {
        for (int i = 0; i < playerAvion.getVies(); i++) {
            ImageView heart = createHeart(10 + i * 50, largeur - 100);
            playerHearts.add(heart);
        }
        mainPane.getChildren().addAll(playerHearts);

        for (int j = 0; j < enemyAvion.getVies(); j++) {
            ImageView heart = createHeart(longeur - 150 + j * 50, 10);
            enemyHearts.add(heart);
        }
        mainPane.getChildren().addAll(enemyHearts);
    }

    private ImageView createHeart(double x, double y) {
        Image heartImage = new Image("images/heart.png");
        ImageView heart = new ImageView(heartImage);
        heart.setFitWidth(40);
        heart.setFitHeight(40);
        heart.setLayoutX(x);
        heart.setLayoutY(y);
        return heart;
    }

    private void createScoreBoards() {
        playerScoreLabel = new Label();
        enemyScoreLabel = new Label();

        playerScoreContainer = createScore(scorePlayer, playerScoreLabel, 10, largeur - 90);
        enemyScoreContainer = createScore(scoreEnnemie, enemyScoreLabel, longeur - 110, 10);
    }

    private StackPane createScore(int score, Label scoreLabel, double x, double y) {
        Image scoreBackground = new Image("BUTTONS/DEFAULT.png");
        ImageView backgroundView = new ImageView(scoreBackground);
        backgroundView.setPreserveRatio(true);
        backgroundView.setFitWidth(100);

        scoreLabel.setText("SCORE " + score);
        scoreLabel.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        scoreLabel.setTextFill(Color.WHITE);
        scoreLabel.setTranslateY(-2.5);

        StackPane container = new StackPane(backgroundView, scoreLabel);
        container.setLayoutX(x);
        container.setLayoutY(y);

        mainPane.getChildren().add(container);
        container.toFront();

        return container;
    }

    private void startGameLoop() {
        if (gameLoop != null) {
            gameLoop.stop();
        }

        gameLoop = new AnimationTimer() {
            private long lastUpdate = 0;

            @Override
            public void handle(long now) {
                if (gameOver || isPaused) return;

                // Update every 16ms (approximately 60 FPS)
                if (now - lastUpdate >= 16_000_000) {
                    handlePlayerMovement();
                    handleCollisions();
                    updateProjectiles();
                    lastUpdate = now;
                }
            }
        };
        gameLoop.start();
    }

    private void handlePlayerMovement() {
        if (playerAvion == null) return;

        double oldX = playerAvion.getImageAvion().getLayoutX();
        double oldY = playerAvion.getImageAvion().getLayoutY();
        boolean moved = false;

        if (leftPressed) {
            playerAvion.getImageAvion().setLayoutX(Math.max(0, oldX - 5));
            moved = true;
        }
        if (rightPressed) {
            playerAvion.getImageAvion().setLayoutX(Math.min(longeur - playerAvion.getImageAvion().getFitWidth(), oldX + 5));
            moved = true;
        }
        if (upPressed) {
            playerAvion.getImageAvion().setLayoutY(Math.max(largeur/2, oldY - 5));
            moved = true;
        }
        if (downPressed) {
            playerAvion.getImageAvion().setLayoutY(Math.min(largeur - playerAvion.getImageAvion().getFitHeight(), oldY + 5));
            moved = true;
        }

        if (moved) {
            sendPlayerState();
        }
    }

    private void handleCollisions() {
        checkProjectileCollisions(playerAvion, enemyAvion, true);
        checkProjectileCollisions(enemyAvion, playerAvion, false);

    }

    private void checkProjectileCollisions(avion shooter, avion target, boolean isPlayerShooting) {
        List<projectil> projectiles = shooter.getProjectiles();
        Iterator<projectil> itProj = projectiles.iterator();

        while (itProj.hasNext()) {
            projectil p = itProj.next();
            if (p.getImageView().getBoundsInParent().intersects(target.getImageAvion().getBoundsInParent())) {
                Platform.runLater(() -> {
                    mainPane.getChildren().remove(p.getImageView());
                    handleHit(target, isPlayerShooting);
                });
                itProj.remove();
            }
        }
    }

    private void handleHit(avion target, boolean isPlayerShooting) {
        target.perdreVie();
        updateHearts(isPlayerShooting);

        if (target.getVies() == 0) {
            handleGameOver(isPlayerShooting);
        }
    }

    private void updateHearts(boolean isPlayerHearts) {
        List<ImageView> hearts = isPlayerHearts ? enemyHearts : playerHearts;
        int currentVies = isPlayerHearts ? enemyAvion.getVies() : playerAvion.getVies();

        Platform.runLater(() -> {
            for (int i = 0; i < MAX_VIES; i++) {
                hearts.get(i).setVisible(i < currentVies);
            }
        });
    }

    private void handleGameOver(boolean playerWon) {
        gameOver = true;
        gameLoop.stop();

        out.println("ROOM:" + roomId + ";PLAYER_SERVER_ID:" + playerServerId +
                ";GAME_OVER:" + (playerWon ? "WIN" : "LOSE") + ";");

        Platform.runLater(() -> {
            String message = playerWon ? "YOU WIN!" : "YOU LOSE!";
            showGameOverScreen(message);
        });
    }

    private void showGameOverScreen(String message) {
        Image bgImage = new Image("BUTTONS/DEFAULT.png");
        ImageView background = new ImageView(bgImage);
        background.setFitWidth(250);
        background.setPreserveRatio(true);

        gameOverText = new Label(message);
        gameOverText.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        gameOverText.setTextFill(Color.WHITE);

        gameOverContainer = new StackPane(background, gameOverText);
        gameOverContainer.setLayoutX((longeur - background.getFitWidth()) / 2);
        gameOverContainer.setLayoutY(largeur / 3);

        mainPane.getChildren().add(gameOverContainer);

        createReplayButton(gameOverContainer.getLayoutX() + 60, gameOverContainer.getLayoutY() + 100);
        createHomeButton(gameOverContainer.getLayoutX() + 150, gameOverContainer.getLayoutY() + 100);
    }

    private boolean connectToServer(int playerId, int roomId) {
        try {
            Socket socket = new Socket("localhost", 5555);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out.println(roomId);
            out.println(playerId);

            playerServerId = Integer.parseInt(in.readLine());
            if (playerServerId == 0) return false;

            startListeningToServer();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void startListeningToServer() {


        new Thread(() -> {
            try {
                String line;
                while ((line = in.readLine()) != null) {
                    handleServerMessage(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }


    private void handleServerMessage(String message) {
        String[] parts = message.split(";");
        Platform.runLater(() -> {
            for (String part : parts) {
                if (part.startsWith("PLAYER_MOVE:")) {
                    updateEnemyPosition(part);
                } else if (part.startsWith("PLAYER_SHOOT:")) {
                    if (enemyAvion != null && !gameOver) {
                        createEnemyProjectile();
                    }
                } else if (part.startsWith("GAME_START")) {
                    resumeGame();
                } else if (part.startsWith("GAME_STOP")) {
                    pauseGame();
                } else if (part.startsWith("PLAYER_DISCONNECTED")) {
                    handlePlayerDisconnect(part);
                } else if (part.startsWith("GAME_OVER")) {
                    gameOver = true;
                    if (gameLoop != null) {
                        gameLoop.stop();
                    }
                    cleanupAllProjectiles();
                    String result = part.split(":")[1];
                    String finalMessage = result.equals("WIN") ? "YOU LOSE!" : "YOU WIN!";
                    showGameOverScreen(finalMessage);
                } else if (part.startsWith("GAME_RESTART")) {
                    restartGame();
                } else if (part.startsWith("CHAT:")) {
                    // Handle chat messages
                    String chatMessage = part.split(":")[1];
                    String senderName = "Player " + (playerServerId == 1 ? "2" : "1");
                    displayChatMessage(senderName, chatMessage);
                }
            }
        });
    }

    private void updateEnemyPosition(String positionData) {
        String[] coords = positionData.split(":")[1].split(",");
        double x = Double.parseDouble(coords[0]);
        double y = Double.parseDouble(coords[1]);

        double mirroredX = longeur - x - enemyAvion.getImageAvion().getFitWidth();

        enemyAvion.getImageAvion().setLayoutX(mirroredX);
        enemyAvion.getImageAvion().setLayoutY(100);
    }

    private void sendPlayerState() {
        if (out != null && playerAvion != null && !gameOver) {
            double x = playerAvion.getImageAvion().getLayoutX();
            double y = playerAvion.getImageAvion().getLayoutY();
            out.println("ROOM:" + roomId + ";PLAYER_SERVER_ID:" + playerServerId +
                    ";PLAYER_MOVE:" + x + "," + y + ";");
        }
    }

    private void sendShootEvent() {
        if (out != null && !gameOver) {
            out.println("ROOM:" + roomId + ";PLAYER_SERVER_ID:" + playerServerId +
                    ";PLAYER_SHOOT:;");
        }
    }

    private void updateScore(int score, Label playerScoreLabel, StackPane scoreContainer) {
        int finalScore = score;
        Platform.runLater(() -> {
            playerScoreLabel.setText("SCORE  " + finalScore);
        });
    }

    private void createControlButtons() {
        createPauseButton(longeur - 100,70);
        createHomeButton(longeur - 50,70);
    }

    private void createReplayButton(double x, double y) {
        replayButton = new ImageView(new Image("images/Prinbles_Buttons_Cosmo (v 1.0) (9_10_2023)/Prinbles_Buttons_Cosmo (v 1.0) (9_10_2023)/png@2x/Buttons-Square/Repeat/Default@2x.png"));
        replayButton.setFitWidth(40);
        replayButton.setFitHeight(40);
        replayButton.setLayoutX(x);
        replayButton.setLayoutY(y);

        replayButton.setOnMouseClicked(e -> {
            out.println("ROOM:" + roomId + ";PLAYER_SERVER_ID:" + playerServerId + ";GAME_RESTART;");
            restartGame();
        });

        mainPane.getChildren().add(replayButton);
    }

    private void createPauseButton(double x, double y) {
        pauseButton = new ImageView(new Image("images/Prinbles_Buttons_Cosmo (v 1.0) (9_10_2023)/Prinbles_Buttons_Cosmo (v 1.0) (9_10_2023)/png@2x/Buttons-Square/Pause/Default@2x.png"));
        pauseButton.setFitWidth(40);
        pauseButton.setFitHeight(40);
        pauseButton.setLayoutX(x);
        pauseButton.setLayoutY(y);

        pauseButton.setOnMouseClicked(event -> toPause());

        mainPane.getChildren().add(pauseButton);
    }

    private void createHomeButton(double x, double y) {
        homeButton = new ImageView(new Image("images/Prinbles_Buttons_Cosmo (v 1.0) (9_10_2023)/Prinbles_Buttons_Cosmo (v 1.0) (9_10_2023)/png@2x/Buttons-Square/Home/Default@2x.png"));
        homeButton.setFitWidth(40);
        homeButton.setFitHeight(40);
        homeButton.setLayoutX(x);
        homeButton.setLayoutY(y);

        homeButton.setOnMouseClicked(event -> {
            gameLoop.stop();
            mainStage.close();
            HomeView menu = new HomeView(joueur);
            menu.getStage().show(); 
        });

        mainPane.getChildren().add(homeButton);
    }

    private void toPause() {
        if (!isPaused) {
            out.println("ROOM:"+roomId+";PLAYER_SERVER_ID:"+playerServerId+";GAME_STOP;") ;
            gameLoop.stop();
            pauseButton.setImage(new Image("images/Prinbles_Buttons_Cosmo (v 1.0) (9_10_2023)/Prinbles_Buttons_Cosmo (v 1.0) (9_10_2023)/png@2x/Buttons-Square/Play/Default@2x.png"));
        } else {
            out.println("ROOM:"+roomId+";PLAYER_SERVER_ID:"+playerServerId+";GAME_START;");
            gameLoop.start();
            pauseButton.setImage(new Image("images/Prinbles_Buttons_Cosmo (v 1.0) (9_10_2023)/Prinbles_Buttons_Cosmo (v 1.0) (9_10_2023)/png@2x/Buttons-Square/Pause/Default@2x.png"));
        }
        isPaused = !isPaused;
    }

    private void restartGame() {
        Platform.runLater(() -> {
            gameOver = false;
            waitingForRestart = false;
            scorePlayer = 0;
            scoreEnnemie = 0;
            lastShootTime = 0;

            enemyProjectiles.clear();


            mainPane.getChildren().clear();

            backgroud();

            playerAvion = new avion(avionPath);
            enemyAvion = new avion("images/Ship_1_C_Large.png");

            playerAvion.getImageAvion().setLayoutX(longeur/2 - playerAvion.getImageAvion().getFitWidth()/2);
            playerAvion.getImageAvion().setLayoutY(largeur - 100);

            enemyAvion.getImageAvion().setLayoutX(longeur/2 - enemyAvion.getImageAvion().getFitWidth()/2);
            enemyAvion.getImageAvion().setLayoutY(100);

            enemyAvion.getImageAvion().setRotate(180);

            mainPane.getChildren().addAll(playerAvion.getImageAvion(), enemyAvion.getImageAvion());

            createHearts();
            createControlButtons();
            createChatUI();



            gameOverContainer = null;
            gameOverText = null;


            isPaused = false;


            if (gameLoop != null) {
                gameLoop.stop();
            }
            startGameLoop();
        });
    }

    public Stage getMainStage() {
        return mainStage;
    }

    private void NomIcon() {
        mainStage.setTitle("Jeu de tire");
        Image icon = new Image("/playerShip3_blue.png");
        mainStage.getIcons().add(icon);
    }

    private void backgroud() {
        Image backroungimage = new Image("images/background.png");
        BackgroundImage backgroundimage = new BackgroundImage(backroungimage, null, null, null, BackgroundSize.DEFAULT);
        Background background1 = new Background(backgroundimage);
        mainPane.setBackground(background1);
    }

    private void setKeyListeners() {
        mainScene.setOnKeyPressed(event -> {

            if (gameOver || messageInput.isFocused()) return;

            switch (event.getCode()) {
                case LEFT -> {
                    leftPressed = true;
                    sendPlayerState();
                }
                case RIGHT -> {
                    rightPressed = true;
                    sendPlayerState();
                }
                case UP -> {
                    upPressed = true;
                    sendPlayerState();
                }
                case DOWN -> {
                    downPressed = true;
                    sendPlayerState();
                }
                case SPACE -> {
                    spacePressed = true;
                    long currentTime = System.currentTimeMillis();

                    if (currentTime - lastShootTime >= SHOOT_COOLDOWN) {
                        if (playerAvion != null) {
                            playerAvion.tirer(mainPane);
                            sendShootEvent();
                            lastShootTime = currentTime;
                        }
                    }
                }
            }
        });

        mainScene.setOnKeyReleased(event -> {
            // Ignore game input if chat is focused
            if (messageInput.isFocused()) return;
            
            switch (event.getCode()) {
                case LEFT -> leftPressed = false;
                case RIGHT -> rightPressed = false;
                case UP -> upPressed = false;
                case DOWN -> downPressed = false;
                case SPACE -> spacePressed = false;
            }
            sendPlayerState();
        });
    }

    private void updateProjectiles() {
        if (gameOver) return;

        if (playerAvion != null) {
            List<projectil> projectiles = playerAvion.getProjectiles();
            Iterator<projectil> it = projectiles.iterator();
            while (it.hasNext()) {
                projectil p = it.next();
                p.move();
                if (p.isOutOfBounds(largeur)) {
                    Platform.runLater(() -> mainPane.getChildren().remove(p.getImageView()));
                    it.remove();
                }
            }
        }

        Iterator<ImageView> enemyProjIt = enemyProjectiles.iterator();
        while (enemyProjIt.hasNext()) {
            ImageView proj = enemyProjIt.next();
            if (proj.getLayoutY() > largeur || !mainPane.getChildren().contains(proj)) {
                Platform.runLater(() -> {
                    if (mainPane.getChildren().contains(proj)) {
                        mainPane.getChildren().remove(proj);
                    }
                });
                enemyProjIt.remove();
            }
        }
    }

    private void handlePlayerDisconnect(String message) {
        int disconnectedPlayer = Integer.parseInt(message.split(":")[1]);
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Player Disconnected");
            alert.setHeaderText(null);
            alert.setContentText("The other player has disconnected. Returning to main menu.");
            alert.showAndWait();

            gameLoop.stop();
            mainStage.close();
            new HomeView(joueur).getStage().show();
        });
    }

    private void resumeGame() {
        isPaused = false;
        if (gameLoop != null) {
            gameLoop.start();
        }
        Platform.runLater(() -> {
            if (pauseButton != null) {
                pauseButton.setImage(new Image("images/Prinbles_Buttons_Cosmo (v 1.0) (9_10_2023)/Prinbles_Buttons_Cosmo (v 1.0) (9_10_2023)/png@2x/Buttons-Square/Pause/Default@2x.png"));
            }
        });
    }

    private void pauseGame() {
        isPaused = true;
        if (gameLoop != null) {
            gameLoop.stop();
        }
        Platform.runLater(() -> {
            if (pauseButton != null) {
                pauseButton.setImage(new Image("images/Prinbles_Buttons_Cosmo (v 1.0) (9_10_2023)/Prinbles_Buttons_Cosmo (v 1.0) (9_10_2023)/png@2x/Buttons-Square/Play/Default@2x.png"));
            }
        });
    }

    private void createEnemyProjectile() {
        double enemyX = enemyAvion.getImageAvion().getLayoutX() + enemyAvion.getImageAvion().getFitWidth()/2 - 7.5; // Center horizontally
        double enemyY = enemyAvion.getImageAvion().getLayoutY() + enemyAvion.getImageAvion().getFitHeight(); // Bottom of ship

        Image projImage = new Image("images/kenney_space-shooter-redux/PNG/Lasers/laserRed16.png");
        ImageView imageProjectil = new ImageView(projImage);
        imageProjectil.setFitWidth(15);
        imageProjectil.setFitHeight(35);
        imageProjectil.setLayoutX(enemyX);
        imageProjectil.setLayoutY(enemyY);
        imageProjectil.setRotate(180);

        mainPane.getChildren().add(imageProjectil);
        enemyProjectiles.add(imageProjectil);

        Thread projectileThread = new Thread(() -> {
            boolean active = true;
            while (active && !gameOver) {
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Platform.runLater(() -> {
                    imageProjectil.setLayoutY(imageProjectil.getLayoutY() + 10);

                    if (imageProjectil.getBoundsInParent().intersects(playerAvion.getImageAvion().getBoundsInParent())) {
                        mainPane.getChildren().remove(imageProjectil);
                        enemyProjectiles.remove(imageProjectil);
                    }

                    if (imageProjectil.getLayoutY() > largeur) {
                        mainPane.getChildren().remove(imageProjectil);
                        enemyProjectiles.remove(imageProjectil);
                    }
                });

                if (!mainPane.getChildren().contains(imageProjectil)) {
                    active = false;
                }
            }
        });

        projectileThread.setDaemon(true);
        projectileThread.start();
    }

    private void cleanupAllProjectiles() {
        if (playerAvion != null) {
            for (projectil p : playerAvion.getProjectiles()) {
                Platform.runLater(() -> {
                    if (mainPane.getChildren().contains(p.getImageView())) {
                        mainPane.getChildren().remove(p.getImageView());
                    }
                });
            }
            playerAvion.getProjectiles().clear();
        }

        for (ImageView proj : enemyProjectiles) {
            Platform.runLater(() -> {
                if (mainPane.getChildren().contains(proj)) {
                    mainPane.getChildren().remove(proj);
                }
            });
        }
        enemyProjectiles.clear();
    }

    private void createChatUI() {
        chatArea = new TextArea();
        chatArea.setEditable(false);
        chatArea.setPrefSize(CHAT_WIDTH, CHAT_HEIGHT - 32);
        chatArea.setWrapText(true);
        chatArea.setStyle("-fx-control-inner-background: rgba(255, 255, 255, 0.85); " +
                         "-fx-font-size: 11px;");

        messageInput = new TextField();
        messageInput.setPromptText("Type message...");
        messageInput.setPrefWidth(CHAT_WIDTH - 45);
        messageInput.setStyle("-fx-font-size: 11px;");

        sendButton = new Button("→");
        sendButton.setPrefWidth(35);
        sendButton.setStyle("-fx-font-size: 14px; -fx-background-radius: 3;");
        sendButton.setOnAction(e -> sendChatMessage());

        HBox inputBox = new HBox(5, messageInput, sendButton);
        inputBox.setAlignment(Pos.CENTER_LEFT);

        chatBox = new VBox(3, chatArea, inputBox);
        chatBox.setPadding(new Insets(3));
        chatBox.setStyle("-fx-background-color: rgba(0, 0, 0, 0.75); " +
                        "-fx-background-radius: 3; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.4), 6, 0, 0, 2);");
        
        chatBox.setLayoutX(5);
        chatBox.setLayoutY(5);

        mainPane.getChildren().add(chatBox);

        messageInput.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                sendChatMessage();
            } else if (event.getCode() == KeyCode.TAB) {
                event.consume();
                messageInput.getParent().requestFocus();
            }
        });

        messageInput.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                messageInput.setStyle("-fx-background-color: rgba(255, 255, 255, 0.9); -fx-font-size: 11px;");
            } else {
                messageInput.setStyle("-fx-background-color: rgba(255, 255, 255, 0.7); -fx-font-size: 11px;");
            }
        });

        mainScene.addEventFilter(javafx.scene.input.KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.TAB && !messageInput.isFocused()) {
                event.consume();
                messageInput.requestFocus();
            }
        });
    }

    private void sendChatMessage() {
        String message = messageInput.getText().trim();
        if (!message.isEmpty()) {
            out.println("ROOM:" + roomId + ";PLAYER_SERVER_ID:" + playerServerId + ";CHAT:" + message + ";");
            messageInput.clear();
        }
    }

    private void displayChatMessage(String sender, String message) {
        Platform.runLater(() -> {
            chatArea.appendText(sender + ": " + message + "\n");
            chatArea.setScrollTop(Double.MAX_VALUE);
        });
    }
}
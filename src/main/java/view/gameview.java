package view;

import DAO.Joueur;
import DAO.JoueurHandler;
import DAO.LevelGame;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import model.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class gameview {
    private static final int longeur = 800;
    private static final int largeur = 600;
    private Joueur joueur;
    private LevelGame game;
    private String avionPath;
    private AnchorPane mainPane;
    private AnchorPane scorePane;
    private Scene mainScene;
    private Stage mainStage;
    private avion playerAvion;
    private List<avionEnnemie> ennemis;
    private boolean gameOver = false;
    private boolean waitingForRestart = false;
    private int score;
    private Label scoreLabel;
    private StackPane scoreContainer;
    private Label gameOverText;
    private Label levelUpText;
    private StackPane levelUpContainer;
    private StackPane gameOverContainer;
    private AnimationTimer gameLoop;
    private boolean leftPressed, rightPressed, upPressed, downPressed, spacePressed;
    private List<ImageView> hearts = new ArrayList<>();
    private final int MAX_VIES = 3;
    private ImageView pauseButton;
    private ImageView replayButton;
    private ImageView homeButton;
    private boolean isPaused = false;
    private int level = 1;
    private final int MAX_LEVEL = 4;
    private List<projectileEnnemi> projectilesEnnemis = new ArrayList<>();
    private List<BonusVie> bonusVies = new ArrayList<>();
    private List<BonusScore> bonusScores = new ArrayList<>();
    private DifficultySettings difficultySettings;

    public gameview(Joueur joueur, LevelGame game, String avionPath) {
        this.game = game;
        this.joueur = joueur;
        this.avionPath = avionPath;
        this.difficultySettings = DifficultySettings.getSettings(game.level);
        mainPane = new AnchorPane();
        mainScene = new Scene(mainPane, longeur, largeur);
        mainStage = new Stage();
        mainStage.setScene(mainScene);
        NomIcon();
        backgroud();
        createPlayer();
        setKeyListeners();
        ennemis = new ArrayList<>();
        CreeScore(longeur - 105,10);
        createControlButtons();
        startGameLoop();
    }

    public Stage getMainStage() {
        return mainStage;
    }

    private void NomIcon() {
        mainStage.setTitle("Jeu de tire");
        Image icon = new Image("images/2D_Space_Shooter_3.0_Free_1.3/Export/NoLight/Large/Ship_2_E_Large_NoLight.png");
        mainStage.getIcons().add(icon);
    }

    private void backgroud() {
        Image backroungimage = new Image("images/background.png");
        BackgroundImage backgroundimage = new BackgroundImage(backroungimage, null, null, null, BackgroundSize.DEFAULT);
        Background background1 = new Background(backgroundimage);
        mainPane.setBackground(background1);
    }

    private void createPlayer() {
        playerAvion = new avion(avionPath);
        mainPane.getChildren().add(playerAvion.getImageAvion());
        creerHearts();
    }

    private void creerHearts() {
        for (int i = 0; i < MAX_VIES; i++) {
            Image heartImage = new Image("images/heart.png");
            ImageView heart = new ImageView(heartImage);
            heart.setFitWidth(40);
            heart.setFitHeight(40);
            heart.setLayoutX(10 + i * 50);
            heart.setLayoutY(10);
            hearts.add(heart);
            mainPane.getChildren().add(heart);
        }
    }

    private void setKeyListeners() {
        mainScene.setOnKeyPressed(event -> {
            if (gameOver) return;

            switch (event.getCode()) {
                case LEFT -> leftPressed = true;
                case RIGHT -> rightPressed = true;
                case UP -> upPressed = true;
                case DOWN -> downPressed = true;
                case SPACE -> spacePressed = true;
            }
        });

        mainScene.setOnKeyReleased(event -> {
            switch (event.getCode()) {
                case LEFT -> leftPressed = false;
                case RIGHT -> rightPressed = false;
                case UP -> upPressed = false;
                case DOWN -> downPressed = false;
                case SPACE -> spacePressed = false;
            }
        });
    }

    private void ajouteEnnemis() {
        Random rand = new Random();
        double x = rand.nextDouble() * (largeur - 50);
        double y = -60.0;

        if (level == 1) {
            avionEnnemie ennemi = new avionEnnemie(x, y, difficultySettings.getEnemyMoveSpeed());
            ennemis.add(ennemi);
            mainPane.getChildren().add(ennemi.getImageAvionEnnemie());
        } else if (level == 2) {
            if (rand.nextBoolean()) {
                avionEnnemie ennemi = new avionEnnemie(x, y, difficultySettings.getEnemyMoveSpeed());
                ennemis.add(ennemi);
                mainPane.getChildren().add(ennemi.getImageAvionEnnemie());
            } else {
                avionEnnemiTireur tireur = new avionEnnemiTireur(mainPane, x, y, difficultySettings.getEnemyMoveSpeed());
                ennemis.add(tireur);
                mainPane.getChildren().add(tireur.getImageAvionEnnemie());
            }
        } else if (level == 3) {
            int r = rand.nextInt(3);
            if (r == 0) {
                avionEnnemiSolide solide = new avionEnnemiSolide(x, y, difficultySettings.getEnemyMoveSpeed());
                ennemis.add(solide);
                mainPane.getChildren().add(solide.getImageAvionEnnemie());
            } else if (r == 1) {
                avionEnnemiTireur tireur = new avionEnnemiTireur(mainPane, x, y, difficultySettings.getEnemyMoveSpeed());
                ennemis.add(tireur);
                mainPane.getChildren().add(tireur.getImageAvionEnnemie());
            } else {
                avionEnnemie normal = new avionEnnemie(x, y, difficultySettings.getEnemyMoveSpeed());
                ennemis.add(normal);
                mainPane.getChildren().add(normal.getImageAvionEnnemie());
            }
        }
    }

    private void startGameLoop() {
        gameLoop = new AnimationTimer() {
            private long lastEnemyTime = 0;
            private long lastTireTime = 0;
            private long lastBonusTime = 0;

            @Override
            public void handle(long now) {
                if (leftPressed) playerAvion.moveLeft();
                if (rightPressed) playerAvion.moveRight();
                if (upPressed) playerAvion.moveUp();
                if (downPressed) playerAvion.moveDown();
                if (spacePressed) playerAvion.tirer(mainPane);

                if (now - lastEnemyTime >= difficultySettings.getEnemySpawnInterval()) {
                    ajouteEnnemis();
                    lastEnemyTime = now;
                }
                moveEnnemis();
                moveProjectilesEnnemis();

                if (now - lastTireTime >= difficultySettings.getEnemyShootInterval()) {
                    tirerParEnnemis();
                    lastTireTime = now;
                }
                if (now - lastBonusTime >= 10_000_000_000L ) {
                    ajouterBonus();
                    lastBonusTime = now;
                }
                moveBonus();
                detecterCollisionProjectileVsEnnemi();
                detecterCollisionBonus();
                detecterCollisionEnnemiVsJoueur();
                detecterCollisionProjectileEnnemiVsJoueur();
            }
        };
        gameLoop.start();
    }

    private void moveEnnemis() {
        for (avionEnnemie ennemi : ennemis) {
            ennemi.moveDown();
        }
    }

    private void moveProjectilesEnnemis() {
        Iterator<projectileEnnemi> it = projectilesEnnemis.iterator();
        while (it.hasNext()) {
            projectileEnnemi p = it.next();
            p.move();
            if (!p.isActif()) {
                mainPane.getChildren().remove(p.getImageView());
                it.remove();
            }
        }
    }

    private void tirerParEnnemis() {
        for (avionEnnemie ennemi : ennemis) {
            if (ennemi instanceof avionEnnemiTireur) {
                double x = ennemi.getImageAvionEnnemie().getLayoutX() + ennemi.getImageAvionEnnemie().getFitWidth() / 2 - 7;
                double y = ennemi.getImageAvionEnnemie().getLayoutY() + ennemi.getImageAvionEnnemie().getFitHeight();
                projectileEnnemi p = new projectileEnnemi(mainPane, x, y, difficultySettings.getEnemyProjectileSpeed());
                projectilesEnnemis.add(p);
            }
        }
    }

    private void ajouterBonus() {
        Random rand = new Random();
        double x = rand.nextDouble() * (largeur - 40);

        if (playerAvion.getVies() < MAX_VIES && rand.nextBoolean()) {
            BonusVie b = new BonusVie(x, -40);
            bonusVies.add(b);
            mainPane.getChildren().add(b.getImage());
        } else {
            BonusScore b = new BonusScore(x, -40);
            bonusScores.add(b);
            mainPane.getChildren().add(b.getImage());
        }
    }

    private void moveBonus() {
        for (BonusVie b : bonusVies) b.moveDown();
        for (BonusScore b : bonusScores) b.moveDown();
    }

    private void detecterCollisionBonus() {
        Bounds boundsJoueur = playerAvion.getImageAvion().getBoundsInParent();

        Iterator<BonusVie> itVie = bonusVies.iterator();
        while (itVie.hasNext()) {
            BonusVie b = itVie.next();
            if (b.getImage().getBoundsInParent().intersects(boundsJoueur)) {
                if (playerAvion.getVies() < MAX_VIES) {
                    playerAvion.gagnerVie();
                    updateHearts();
                }
                Platform.runLater(() -> mainPane.getChildren().remove(b.getImage()));
                itVie.remove();
            }
        }

        Iterator<BonusScore> itScore = bonusScores.iterator();
        while (itScore.hasNext()) {
            BonusScore b = itScore.next();
            if (b.getImage().getBoundsInParent().intersects(boundsJoueur)) {
                updateScore(10);
                Platform.runLater(() -> {
                    mainPane.getChildren().remove(b.getImage());
                });
                itScore.remove();
            }
        }
    }

    private void detecterCollisionProjectileVsEnnemi() {
        List<projectil> projectiles = playerAvion.getProjectiles();
        Iterator<projectil> itProj = projectiles.iterator();

        while (itProj.hasNext()) {
            projectil p = itProj.next();
            Bounds boundsP = p.getImageView().getBoundsInParent();

            Iterator<avionEnnemie> itEnnemi = ennemis.iterator();
            while (itEnnemi.hasNext()) {
                avionEnnemie ennemi = itEnnemi.next();
                Bounds boundsE = ennemi.getImageAvionEnnemie().getBoundsInParent();

                if (boundsP.intersects(boundsE)) {
                    updateScore(1);
                    Platform.runLater(() -> {
                        mainPane.getChildren().remove(p.getImageView());
                        if (ennemi instanceof avionEnnemiSolide solide) {
                            if (solide.perdreVie()) {
                                Platform.runLater(() -> {
                                    mainPane.getChildren().remove(solide.getImageAvionEnnemie());
                                    afficherExplosion(solide.getImageAvionEnnemie().getLayoutX(), solide.getImageAvionEnnemie().getLayoutY());
                                });
                                itEnnemi.remove();
                            }
                        } else {
                            Platform.runLater(() -> {
                                mainPane.getChildren().remove(ennemi.getImageAvionEnnemie());
                                afficherExplosion(ennemi.getImageAvionEnnemie().getLayoutX(), ennemi.getImageAvionEnnemie().getLayoutY());
                            });
                            itEnnemi.remove();
                        }
                    });
                    itProj.remove();
                    break;
                }
            }
        }
    }

    private void detecterCollisionProjectileEnnemiVsJoueur() {
        if (gameOver) return;

        Bounds boundsJoueur = playerAvion.getImageAvion().getBoundsInParent();
        Iterator<projectileEnnemi> it = projectilesEnnemis.iterator();
        while (it.hasNext()) {
            projectileEnnemi proj = it.next();
            Bounds boundsP = proj.getImageView().getBoundsInParent();

            if (boundsJoueur.intersects(boundsP)) {
                Platform.runLater(() -> {
                    mainPane.getChildren().remove(proj.getImageView());
                });
                it.remove();

                playerAvion.perdreVie();
                updateHearts();

                if (playerAvion.getVies() <= 0) {
                    playerMort();
                }
                break;
            }
        }
    }

    private void detecterCollisionEnnemiVsJoueur() {
        if (gameOver) return;

        Bounds boundsJoueur = playerAvion.getImageAvion().getBoundsInParent();
        for (avionEnnemie ennemi : ennemis) {
            Bounds boundsEnnemi = ennemi.getImageAvionEnnemie().getBoundsInParent();

            if (boundsJoueur.intersects(boundsEnnemi)) {
                Platform.runLater(() -> {
                    mainPane.getChildren().remove(ennemi.getImageAvionEnnemie());
                    afficherExplosion(ennemi.getImageAvionEnnemie().getLayoutX(), ennemi.getImageAvionEnnemie().getLayoutY());
                });
                ennemis.remove(ennemi);

                playerAvion.perdreVie();

                Platform.runLater(() -> updateHearts());

                if (playerAvion.getVies() <= 0) {
                    playerMort();
                }
                break;
            }
        }
    }

    private void updateHearts() {
        int currentVies = playerAvion.getVies();
        for (int i = 0; i < hearts.size(); i++) {
            hearts.get(i).setVisible(i < currentVies);
        }
    }

    private void endGame() {
        gameLoop.stop();
        gameOver = true;

        JoueurHandler handler = new JoueurHandler();

        if(joueur.getMaxScore() < this.score) {
            joueur.setMaxScore(this.score);
            handler.sauvgarder(joueur);
        }
        game.sauvgarderScore(this.score,game);
    }

    private void playerMort() {
        gameOver = true;
        waitingForRestart = true;

        Platform.runLater(() -> {
            mainPane.getChildren().remove(playerAvion.getImageAvion());
            afficherExplosion(playerAvion.getImageAvion().getLayoutX(), playerAvion.getImageAvion().getLayoutY());
            gameOver();
            endGame();
        });
    }

    private void afficherExplosion(double x, double y) {
        Image explosionImage = new Image("images/explosion.png");
        ImageView explosion = new ImageView(explosionImage);
        explosion.setFitWidth(100);
        explosion.setFitHeight(100);
        explosion.setLayoutX(x);
        explosion.setLayoutY(y);
        mainPane.getChildren().add(explosion);

        new Thread(() -> {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Platform.runLater(() -> mainPane.getChildren().remove(explosion));
        }).start();
    }

    private void gameOver() {
        Image imageGameOver = new Image("images/Prinbles_Buttons_Cosmo (v 1.0) (9_10_2023)/Prinbles_Buttons_Cosmo (v 1.0) (9_10_2023)/png@2x/Dummy-Rect/Default@2x.png");
        ImageView gameOverBackground = new ImageView(imageGameOver);
        gameOverBackground.setPreserveRatio(true);
        gameOverBackground.setFitWidth(250);

        gameOverText = new Label("GAME OVER");
        gameOverText.setFont(Font.font("Arial", FontWeight.BOLD, 32.5));
        gameOverText.setTextFill(Color.WHITE);
        gameOverText.setTranslateY(-10);

        gameOverContainer = new StackPane(gameOverBackground, gameOverText);
        double centerX = (longeur - gameOverBackground.getFitWidth()) / 2;
        double centerY = largeur / 3;
        gameOverContainer.setLayoutX(centerX);
        gameOverContainer.setLayoutY(centerY);

        mainPane.getChildren().add(gameOverContainer);
        gameOverContainer.toFront();

        // Score final
        CreeScore(longeur / 2 - 53,centerY + 137);

        // Bouton Rejouer
        createRejouerButton(longeur / 2 - 60,centerY + 200);

        // Bouton Home
        createHomeButton(longeur / 2 + 10,centerY + 200);
    }

    private void CreeScore(double x, double y) {
        Image imagescoore = new Image("images/Prinbles_Buttons_Cosmo (v 1.0) (9_10_2023)/Prinbles_Buttons_Cosmo (v 1.0) (9_10_2023)/png@2x/Dummy-Rect/Default@2x.png");
        ImageView scoreBackground = new ImageView(imagescoore);
        scoreBackground.setPreserveRatio(true);
        scoreBackground.setFitWidth(100);

        scoreLabel = new Label("SCORE  " + score);
        scoreLabel.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        scoreLabel.setTextFill(Color.WHITE);
        scoreLabel.setTranslateY(-2.5);

        scoreContainer = new StackPane(scoreBackground, scoreLabel);
        scoreContainer.setLayoutX(x);
        scoreContainer.setLayoutY(y);


        mainPane.getChildren().add(scoreContainer);
        scoreContainer.toFront();
    }

    private void updateScore(int points) {
        score += points;

        if (score >= 50 && level == 1) {
            changerLevel(2);
        } else if (score >= 100 && level == 2) {
            changerLevel(3);
        }

        Platform.runLater(() -> CreeScore(longeur - 105, 10));
    }

    private void changerLevel(int newLevel) {
        level = newLevel;

        difficultySettings = DifficultySettings.getSettings(game.level);

        for (avionEnnemie ennemi : ennemis) {
            ennemi.setMoveSpeed(difficultySettings.getEnemyMoveSpeed());
        }

        for (projectileEnnemi proj : projectilesEnnemis) {
            proj.setMoveSpeed(difficultySettings.getEnemyProjectileSpeed());
        }

        Image imagelevelUp = new Image("images/Prinbles_Buttons_Cosmo (v 1.0) (9_10_2023)/Prinbles_Buttons_Cosmo (v 1.0) (9_10_2023)/png@2x/Dummy-Rect/Default@2x.png");
        ImageView levelUp = new ImageView(imagelevelUp);
        levelUp.setFitWidth(250);
        levelUp.setPreserveRatio(true);

        levelUpText = new Label("LEVEL " + level);
        levelUpText.setFont(Font.font("Arial", FontWeight.BOLD, 32.5));
        levelUpText.setTextFill(Color.WHITE);
        levelUpText.setTranslateY(-10);

        levelUpContainer = new StackPane(levelUp, levelUpText);
        double centerX = (longeur - levelUp.getFitWidth()) / 2;
        double centerY = largeur / 3;
        levelUpContainer.setLayoutX(centerX);
        levelUpContainer.setLayoutY(centerY);

        mainPane.getChildren().add(levelUpContainer);
        levelUpContainer.toFront();

        new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Platform.runLater(() -> mainPane.getChildren().remove(levelUpContainer));
        }).start();
    }

    private void createControlButtons() {
        createPauseButton(longeur - 100,70);
        createHomeButton(longeur - 50,70);
    }

    private void createRejouerButton(double x, double y) {
        replayButton = new ImageView(new Image("images/Prinbles_Buttons_Cosmo (v 1.0) (9_10_2023)/Prinbles_Buttons_Cosmo (v 1.0) (9_10_2023)/png@2x/Buttons-Square/Repeat/Default@2x.png"));
        replayButton.setFitWidth(40);
        replayButton.setFitHeight(40);
        replayButton.setLayoutX(x);
        replayButton.setLayoutY(y);

        replayButton.setOnMouseClicked(e -> restartGame());

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
            HomeView home = new HomeView(joueur);
            home.getStage().show();
        });

        mainPane.getChildren().add(homeButton);
    }

    private void toPause() {
        if (!isPaused) {
            gameLoop.stop();
            pauseButton.setImage(new Image("images/Prinbles_Buttons_Cosmo (v 1.0) (9_10_2023)/Prinbles_Buttons_Cosmo (v 1.0) (9_10_2023)/png@2x/Buttons-Square/Play/Default@2x.png"));
        } else {
            gameLoop.start();
            pauseButton.setImage(new Image("images/Prinbles_Buttons_Cosmo (v 1.0) (9_10_2023)/Prinbles_Buttons_Cosmo (v 1.0) (9_10_2023)/png@2x/Buttons-Square/Pause/Default@2x.png"));
        }
        isPaused = !isPaused;
    }

    private void restartGame() {

        gameOver = false;
        waitingForRestart = false;
        score = 0;
        level = 1;

        Platform.runLater(() -> {

            if (gameOverContainer != null) {
                mainPane.getChildren().remove(gameOverContainer);
            }
            if (scoreContainer != null) {
                mainPane.getChildren().remove(scoreContainer);
                scoreContainer = null;
            }
            if (replayButton != null) {
                mainPane.getChildren().remove(replayButton);
                replayButton = null;
            }
            if (homeButton != null) {
                mainPane.getChildren().remove(homeButton);
                homeButton = null;
            }

            for (avionEnnemie ennemi : ennemis) {
                mainPane.getChildren().remove(ennemi.getImageAvionEnnemie());
                levelUpContainer = null;
            }
            ennemis.clear();


            for (projectileEnnemi p : projectilesEnnemis) {
                mainPane.getChildren().remove(p.getImageView());
            }
            projectilesEnnemis.clear();

            for (projectil p : playerAvion.getProjectiles()) {
                mainPane.getChildren().remove(p.getImageView());
            }
            playerAvion.getProjectiles().clear();

            for (BonusVie b : bonusVies) {
                mainPane.getChildren().remove(b.getImage());
            }
            bonusVies.clear();

            for (BonusScore b : bonusScores) {
                mainPane.getChildren().remove(b.getImage());
            }
            bonusScores.clear();

            // Réinitialiser les vies et l'affichage
            mainPane.getChildren().remove(playerAvion.getImageAvion());
            playerAvion = new avion(avionPath);
            mainPane.getChildren().add(playerAvion.getImageAvion());

            if (levelUpContainer != null) {
                mainPane.getChildren().remove(levelUpContainer);
                levelUpContainer = null;
            }
            
            // Réinitialiser les cœurs
            updateHearts();

            // Réinitialiser le score
            if (scoreContainer != null) {
                mainPane.getChildren().remove(scoreContainer);
            }
            CreeScore(longeur - 105, 10);

            // Réinitialiser l'état de pause
            isPaused = false;
            if (pauseButton != null) {
                pauseButton.setImage(new Image("images/Prinbles_Buttons_Cosmo (v 1.0) (9_10_2023)/Prinbles_Buttons_Cosmo (v 1.0) (9_10_2023)/png@2x/Buttons-Square/Pause/Default@2x.png"));
            }

            // Arreter la boucle de jeu et la redemarrer
            if (gameLoop != null) {
                gameLoop.stop();
                gameLoop = null;
            }
            startGameLoop();
        });
    }
}
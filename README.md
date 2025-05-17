# Jeu de Tir

Un jeu de tir développé en JavaFX avec stockage MySQL.

## Prérequis

- Java JDK 17 ou supérieur
- Maven 3.6 ou supérieur (uniquement pour la compilation)
- MySQL Server

## Pour les développeurs

### Configuration de la base de données

1. Assurez-vous que MySQL Server est installé et en cours d'exécution
2. Créez une base de données pour le jeu
3. Configurez les informations de connexion dans le fichier de configuration de l'application

### Compilation et création du JAR

Pour compiler le projet et créer le JAR exécutable :

```bash
mvn clean package
```

Cette commande va :
- Nettoyer les fichiers compilés précédents
- Compiler le projet
- Créer un JAR exécutable unique contenant toutes les dépendances

### Développement
Pendant le développement, vous pouvez utiliser :
```bash
mvn javafx:run
```

## Pour les utilisateurs

### Installation
1. Assurez-vous d'avoir Java 17 ou supérieur installé sur votre ordinateur
2. Téléchargez et installez MySQL Server si ce n'est pas déjà fait
3. Copiez les fichiers suivants dans un même dossier :
   - `jeu-de-tir-1.0-SNAPSHOT.jar`
   - `start-game.bat`

### Lancement du jeu
Double-cliquez simplement sur `start-game.bat` pour lancer le jeu.

## Distribution

Pour distribuer le jeu, créez un dossier contenant :
1. Le fichier JAR : `jeu-de-tir-1.0-SNAPSHOT.jar`
2. Le fichier batch : `start-game.bat`
3. Un fichier README simple expliquant :
   - Le prérequis Java 17+
   - Comment installer MySQL si nécessaire
   - Comment lancer le jeu (double-clic sur start-game.bat)

## Structure du projet

```
src/
├── main/
│   ├── java/        # Code source Java
│   ├── resources/   # Ressources du projet (images, FXML, etc.)
│   └── config/      # Fichiers de configuration
```

## Technologies utilisées

- JavaFX 21.0.1 : Interface graphique
- MySQL Connector 8.0.33 : Connexion à la base de données
- Maven : Gestion des dépendances et build

## Configuration Maven

Le projet utilise plusieurs plugins Maven :
- `maven-compiler-plugin` : Pour la compilation du code
- `maven-assembly-plugin` : Pour créer un JAR exécutable avec toutes les dépendances
- `maven-surefire-plugin` : Pour l'exécution des tests

La configuration complète se trouve dans le fichier `pom.xml`. 
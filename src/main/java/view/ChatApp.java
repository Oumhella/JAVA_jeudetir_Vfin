package view;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.Server.Client;

import java.net.Socket;

public class ChatApp {
    private Scene chatScene;
    private Stage chatStage;
    private final int width = home.getMenuWidth();
    private final int height = home.getMenuHeight();

    private TextArea chatArea;
    private TextField chatInput;
    private Button sendButton;
    private Client client;

    public ChatApp() {
        chatStage = new Stage();

        chatArea = new TextArea();
        chatArea.setEditable(false);
        chatArea.setWrapText(true);
        chatArea.setPrefHeight(height - 100);

        chatInput = new TextField();
        chatInput.setPromptText("Type your message...");
        chatInput.setOnAction(e -> sendMessage());

        sendButton = new Button("Send");
        sendButton.setOnAction(e -> sendMessage());

        HBox inputBox = new HBox(10, chatInput, sendButton);
        inputBox.setPadding(new Insets(10));
        HBox.setHgrow(chatInput, Priority.ALWAYS);

        VBox chatBox = new VBox(10, chatArea, inputBox);
        chatBox.setPadding(new Insets(10));
        VBox.setVgrow(chatArea, Priority.ALWAYS);

        chatScene = new Scene(chatBox, width, height);
        chatStage.setTitle("Game Chat");
        chatStage.setScene(chatScene);

        Platform.runLater(() -> connectToServer());
    }

    private void connectToServer() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Enter Username");
        dialog.setHeaderText("Connect to the chat server");
        dialog.setContentText("Username:");

        dialog.showAndWait().ifPresent(username -> {
            try {
                Socket socket = new Socket("localhost", 1234);
                client = new Client(socket, username);
                client.setMessageListener(message ->
                        Platform.runLater(() -> chatArea.appendText(message + "\n"))
                );
                client.listenForMessage();
                chatStage.show();
            } catch (Exception e) {
                showAlert("Connection Error", "Could not connect to server.");
            }
        });
    }

    private void sendMessage() {
        String message = chatInput.getText();
        if (!message.isEmpty() && client != null) {
            client.sendMessage(message);
            chatArea.appendText("You: " + message + "\n");
            chatInput.clear();
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public Stage getChatStage() {
        return chatStage;
    }
}
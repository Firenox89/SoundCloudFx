package firenox.ui;

import firenox.io.LogInHandler;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * Created by firenox on 10/6/15.
 */
public class LoginScreen extends Scene {

    public LoginScreen(Parent root, double width, double height) {
        super(root, width, height);
        createLogin(root);
    }

    private void createLogin(Parent root) {
        Label loginLabel = new Label("Login: ");
        Label passLabel = new Label("Password: ");
        TextField loginField = new TextField();
        PasswordField passField = new PasswordField();

        Button connectButton = new Button("Connect");

        HBox loginBox = new HBox();
        loginBox.getChildren().add(loginLabel);
        loginBox.getChildren().add(loginField);
        HBox passBox = new HBox();
        passBox.getChildren().add(passLabel);
        passBox.getChildren().add(passField);
        VBox connectBox = new VBox();
        connectBox.getChildren().add(loginBox);
        connectBox.getChildren().add(passBox);
        connectBox.getChildren().add(connectButton);
        connectButton.setOnAction(event -> LogInHandler.connect(loginField.getText(), passField.getText()));

        StackPane pane = (StackPane) root;
        pane.getChildren().add(connectBox);
    }
}

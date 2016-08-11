package firenox.ui;

import firenox.io.SessionHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

import static com.soundcloud.api.CloudAPI.InvalidTokenException;

/**
 * Created by firenox on 10/6/15.
 */
public class LoginScreen {

  public LoginScreen(Stage stage, int defaultWidth, int defaultHeight) {
    GridPane grid = new GridPane();

    grid.setStyle("-fx-background-color: #161618;");
    grid.setAlignment(Pos.CENTER);
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setPadding(new Insets(25, 25, 25, 25));

    Scene scene = new Scene(grid, defaultWidth, defaultHeight);
    scene.getStylesheets().add("style.css");
    stage.setScene(scene);
    stage.show();

    Text scenetitle = new Text("Login:");
    scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
    scenetitle.setFill(Color.WHITE);
    grid.add(scenetitle, 0, 0, 2, 1);

    Label userName = new Label("User Name:");
    grid.add(userName, 0, 1);

    TextField userTextField = new TextField();
    grid.add(userTextField, 1, 1);

    Label pw = new Label("Password:");
    grid.add(pw, 0, 2);

    PasswordField pwBox = new PasswordField();
    grid.add(pwBox, 1, 2);
    Button btn = new Button("Sign in");
    HBox hbBtn = new HBox(10);
    hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
    hbBtn.getChildren().add(btn);
    grid.add(hbBtn, 1, 4);

    final Text actiontarget = new Text();
    grid.add(actiontarget, 1, 6);

    btn.setOnAction(event -> {
      login(userTextField.getText(), pwBox.getText(), stage, actiontarget);
    });
    //event == press enter
    pwBox.setOnAction(event -> {
      login(userTextField.getText(), pwBox.getText(), stage, actiontarget);
    });
  }

  private void login(String user, String pw, Stage stage, Text actiontarget) {
    try {
      SessionHandler.connect(user, pw);
      UIManager.initPlayerUI(stage);
    } catch (InvalidTokenException e) {
      actiontarget.setFill(Color.FIREBRICK);
      actiontarget.setText("Password incorrect.");
    } catch (IOException e) {
      actiontarget.setFill(Color.FIREBRICK);
      actiontarget.setText(e.getMessage());
    }

  }
}

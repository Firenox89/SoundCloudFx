package firenox;

import firenox.io.BackgroundLoader;
import firenox.io.SessionHandler;
import firenox.ui.UIManager;
import javafx.application.Application;
import javafx.stage.Stage;

public class Player extends Application {

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) {
    BackgroundLoader.init();
    SessionHandler.init();
    UIManager.init(primaryStage);
  }
}
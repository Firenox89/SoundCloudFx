package firenox.ui;

import firenox.model.ModelManager;
import firenox.model.Track;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by firenox on 10/6/15.
 */
public class UIManager {

    private static UIManager instance;

    private static Stage stage;
    private static FXMLController controller;
    private static Parent root;

    private UIManager(Stage stage) {
        this.stage = stage;
    }

    public static void init(Stage stage) {
        instance = new UIManager(stage);
        FXMLLoader loader = new FXMLLoader(UIManager.class.getResource("favorites.fxml"));

        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        controller = loader.getController();
        stage.setScene(new Scene(root, 800, 600));
        stage.show();
    }

    public static void showFavorites(ArrayList<Track> favList) {
        if (favList == null) {
            favList = ModelManager.getLikes();
        }
        new FavoriteScene(favList);
    }

    public static FXMLController getController() {
        return controller;
    }

    public static void showPlaylists() {
        ModelManager.getPlaylists();
    }
}

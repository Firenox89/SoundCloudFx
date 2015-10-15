package firenox.ui;

import firenox.media.AudioManager;
import firenox.model.ModelManager;
import firenox.model.Track;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
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
        AudioManager.getPlayerFx().bindVolume(controller.getVolumeSlider().valueProperty());
        stage.setScene(new Scene(root, 800, 600));
        stage.show();
    }

    public static void showFavorites() {
        new FavoriteScene();
    }

    public static FXMLController getController() {
        return controller;
    }

    public static void showPlaylists() {
        ScrollPane pane = controller.getMainScrollPane();

        try {
            Canvas canvas = WaveRenderer.init(
                    new FileInputStream(new File("/home/firenox/soundcloudFx/waves/0DDVFp5Ibia6_m.png")),
                    650, 80).renderToFxCanvas();
            pane.setContent(canvas);
        } catch (IOException e) {
            e.printStackTrace();
        }

//        ModelManager.getPlaylists();
    }
}

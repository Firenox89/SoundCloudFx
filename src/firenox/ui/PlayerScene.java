package firenox.ui;

import firenox.model.Track;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

/**
 * Created by firenox on 10/6/15.
 */
public class PlayerScene extends Scene {

    private final int artwork_width = 100;
    private final int artwork_height = 100;
    HBox trackInfo = new HBox();
    private Track currentTrack;

    public PlayerScene(StackPane root, double width, double height) {
        super(root, width, height);
        createPlayer(root);
    }

    private void createPlayer(StackPane root) {

        Button nextButton = new Button("Next");
        Button playButton = new Button("Play");
        Button prevButton = new Button("Prev");

        HBox controls = new HBox();
        controls.getChildren().add(nextButton);
        controls.getChildren().add(playButton);
        controls.getChildren().add(prevButton);

        VBox playerBox = new VBox();
        playerBox.getChildren().add(trackInfo);
        playerBox.getChildren().add(controls);

        root.getChildren().add(playerBox);
    }

    public void setTrack(Track track) {
        this.currentTrack = track;
        Image artwork = null;
        try {
            artwork = new Image(track.getArtwork().getLargeAsStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Label title = new Label(track.getTitle());
        trackInfo.getChildren().clear();
        trackInfo.getChildren().add(new ImageView(artwork));
        trackInfo.getChildren().add(title);
    }
}

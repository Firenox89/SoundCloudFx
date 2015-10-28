package firenox.ui;

import firenox.media.AudioManager;
import firenox.model.PagedList;
import firenox.model.Track;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DecimalFormat;

/**
 * Created by firenox on 10/6/15.
 */
public class UIManager {

    private static FXMLController controller;
    private static Parent root;
    private static LikesPane likesPane;
    private static StreamPane streamPane;
    private static PlayerPane playerPane;
    private static Track currenTrack;
    private static PagedList<Track> currentPlaylist;

    public static void init(Stage stage) {
        FXMLLoader loader = new FXMLLoader(UIManager.class.getClassLoader().getResource("favorites.fxml"));

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

    public static FXMLController getController() {
        return controller;
    }

    public static void showFavorites() {
        if (likesPane == null)
        {
            likesPane = new LikesPane();
        }
        //TODO: remove listener before setting new content
        getController().getMainScrollPane().setContent(likesPane);
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

    public static void showPlayer() {
        if (playerPane == null)
        {
            playerPane = new PlayerPane();
        }
        getController().getMainScrollPane().setContent(playerPane);
    }

    public static void showStream() {
        if (streamPane == null)
        {
            streamPane = new StreamPane();
        }
        getController().getMainScrollPane().setContent(streamPane);
    }

    public static void showProfile() {
    }

    public static void setTrackForPlayerUI(Track track, PagedList<Track> playlist) {
        currenTrack = track;
        currentPlaylist = playlist;
        try {
            controller.getArtWork().setImage(new Image(track.getArtwork().getLargeAsStream(), 40, 40, true, true));
        } catch (IOException e) {
            e.printStackTrace();
        }
        //duration in seconds
        int duration = track.getDuration()/1000;
        DecimalFormat secondFormater = new DecimalFormat("00");

        controller.getTitleLabel().setText(track.getTitle());
        controller.getProgressSlider().setMax(duration);

        controller.getTrackTime().setText((duration/60) + ":" + secondFormater.format(duration % 60));

        AudioManager.getPlayerFx().setProgressTimeListener((observable, oldValue, newValue) ->
        {
            int seconds = (int) newValue.toSeconds();
            controller.getProgressSlider().setValue(seconds);
            controller.getPlaybackTime().setText((seconds/60) + ":" + secondFormater.format(seconds % 60));
            track.getWaveform().progressAnimation(newValue.toSeconds() / (track.getDuration() / 1000));
        });
    }

    public static Track getCurrentTrack() {
        return currenTrack;
    }

    public static PagedList<Track> getCurrentPlaylist() {
        return currentPlaylist;
    }
}

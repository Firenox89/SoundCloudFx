package firenox.ui;

import firenox.media.AudioManager;
import firenox.model.ModelManager;
import firenox.model.PagedList;
import firenox.model.Track;
import firenox.model.User;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.DecimalFormat;

/**
 * Created by firenox on 10/6/15.
 */
public class UIManager {

    private static FXMLController controller;
    private static Parent root;
    private static TracksPane likesPane;
    private static TracksPane streamPane;
    private static PlayerPane playerPane;
    private static PlaylistPane playlistPane;
    private static Track currentTrack;
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
        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add("style.css");

        stage.setScene(scene);
        stage.show();

//        showProfile();
        showFavorites();
    }

    public static FXMLController getController() {
        return controller;
    }

    public static void showFavorites() {
        if (likesPane == null) {
            likesPane = new TracksPane(ModelManager.getMyLikes());
        }
        //TODO: remove listener before setting new content
        getController().getMainScrollPane().setContent(likesPane);
    }

    public static void showTrackList(PagedList<Track> tracks) {
        //TODO: reset scrollpane viewport
        getController().getMainScrollPane().setContent(new TracksPane(tracks));
    }

    public static void showPlaylists() {
        if (playlistPane == null) {
            playlistPane = new PlaylistPane(ModelManager.getMe().getPlaylists());
        }
        getController().getMainScrollPane().setContent(playlistPane);
    }

    public static void showPlayer() {
        if (playerPane == null) {
            playerPane = new PlayerPane();
        }
        getController().getMainScrollPane().setContent(playerPane);
    }

    public static void showStream() {
        if (streamPane == null) {
            streamPane = new TracksPane(ModelManager.getMyStream());
        }
        getController().getMainScrollPane().setContent(streamPane);
    }

    public static void showProfile() {
        showUser(ModelManager.getMe());
    }

    public static void showUser(User user) {
        getController().getMainScrollPane().setContent(new UserPane(user));
    }

    public static void showTrack(Track track) {
        getController().getMainScrollPane().setContent(new TrackPane(track));
    }

    public static void setTrackForPlayerUI(Track track, PagedList<Track> playlist) {
        Platform.runLater(() -> {
            currentTrack = track;
            currentPlaylist = playlist;
            try {
                controller.getArtWork().setImage(new Image(track.getArtwork().getLargeAsStream(), 40, 40, true, true));
            } catch (IOException e) {
                e.printStackTrace();
            }
            //duration in seconds
            int duration = track.getDuration() / 1000;
            DecimalFormat secondFormater = new DecimalFormat("00");

            controller.getTitleLabel().setText(track.getTitle());
            controller.getTitleLabel().setOnMouseClicked(event -> showTrack(track));
            controller.getUserLabel().setText(track.getUser_name());
            controller.getUserLabel().setOnMouseClicked(event -> showUser(track.getUser()));
            controller.getProgressSlider().setMax(duration);

            controller.getTrackTime().setText((duration / 60) + ":" + secondFormater.format(duration % 60));

            AudioManager.getPlayerFx().setProgressTimeListener((observable, oldValue, newValue) ->
            {
                int seconds = (int) newValue.toSeconds();
                controller.getProgressSlider().setValue(seconds);
                controller.getPlaybackTime().setText((seconds / 60) + ":" + secondFormater.format(seconds % 60));
                track.getWaveform().progressAnimation(newValue.toSeconds() / (track.getDuration() / 1000));
            });
        });
    }

    public static Track getCurrentTrack() {
        return currentTrack;
    }

    public static PagedList<Track> getCurrentPlaylist() {
        return currentPlaylist;
    }
}

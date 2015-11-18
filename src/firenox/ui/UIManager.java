package firenox.ui;

import firenox.logger.Logger;
import firenox.media.AudioManager;
import firenox.model.ModelManager;
import firenox.model.PagedList;
import firenox.model.Track;
import firenox.model.User;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by firenox on 10/6/15.
 */
public class UIManager {

    private static Logger log = Logger.getLogger(UIManager.class.getName());
    private static FXMLController controller;
    private static Parent root;
    private static TracksPane likesPane;
    private static TracksPane streamPane;
    private static PlaylistPane playlistPane;
    private static Track currentTrack;
    private static PagedList<Track> currentPlaylist;

    public static void init(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(UIManager.class.getClassLoader().getResource("player.fxml"));

            root = loader.load();
            controller = loader.getController();
            AudioManager.getPlayerFx().bindVolume(controller.getVolumeSlider().valueProperty());
            Scene scene = new Scene(root, 800, 600);
            scene.getStylesheets().add("style.css");

            stage.setScene(scene);
            stage.show();

            showProfile();
//            showMyLikes();
        } catch (IOException e) {
            log.e(e);
        }
    }

    private static ArrayList<Node> paneHistory = new ArrayList<>();

    public static void setPane(Node node) {
        removeViewportVListeners();
        removeWidthListeners();
        //TODO: dice out some history limit
        paneHistory.add(node);
        controller.getMainScrollPane().setContent(node);
        //reset Viewport
        controller.getMainScrollPane().vvalueProperty().set(0);
        ((PlayerPane) node).setListener();
    }

    public static void back() {
        int historySize = paneHistory.size();
        if (historySize > 1) {
            paneHistory.remove(historySize - 1);
            controller.getMainScrollPane().setContent(paneHistory.get(historySize - 2));
        }
    }

    private static ArrayList<ChangeListener<? super Number>> viewportVListeners = new ArrayList<>();

    public static double getScrollPaneHeight() {
        return controller.getMainScrollPane().getHeight();
    }

    public static void addViewportVListener(ChangeListener<? super Number> listener) {
        viewportVListeners.add(listener);
        controller.getMainScrollPane().vvalueProperty().addListener(listener);
    }

    public static void removeViewportVListeners() {
        viewportVListeners.forEach(changeListener ->
                controller.getMainScrollPane().vvalueProperty().removeListener(changeListener));
        viewportVListeners.clear();
    }

    private static ArrayList<ChangeListener<? super Number>> widthListeners = new ArrayList<>();

    public static void addWidthListener(ChangeListener<? super Number> listener) {
        widthListeners.add(listener);
        controller.getMainScrollPane().widthProperty().addListener(listener);
    }

    public static void removeWidthListeners() {
        widthListeners.forEach(changeListener ->
                controller.getMainScrollPane().widthProperty().removeListener(changeListener));
        widthListeners.clear();
    }

    public static double getWidth() {
        return controller.getMainScrollPane().getWidth();
    }

    public static void showMyLikes() {
        if (likesPane == null) {
            likesPane = new TracksPane(ModelManager.getMyLikes());
        }
        setPane(likesPane);
    }

    public static void showTrackList(PagedList<Track> tracks) {
        setPane(new TracksPane(tracks));
    }

    public static void showMyPlaylists() {
        if (playlistPane == null) {
            playlistPane = new PlaylistPane(ModelManager.getMyPlaylists());
        }
        setPane(playlistPane);
    }

    public static void showMyStream() {
        if (streamPane == null) {
            streamPane = new TracksPane(ModelManager.getMyStream());
        }
        setPane(streamPane);
    }

    public static void showProfile() {
        showUser(ModelManager.getMe());
    }

    public static void showUser(User user) {
        setPane(new UserPane(user));
    }

    public static void showTrack(Track track) {
        setPane(new TrackPane(track));
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
}

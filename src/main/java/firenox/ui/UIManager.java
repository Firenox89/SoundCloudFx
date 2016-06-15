package firenox.ui;

import firenox.logger.LogType;
import firenox.logger.Logger;
import firenox.media.AudioManager;
import firenox.model.*;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageBuilder;
import javafx.util.Duration;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by firenox on 10/6/15.
 */
public class UIManager {

  private static Logger log = Logger.getLogger(UIManager.class.getName());
  private static MenuBars root;
  private static TracksPane likesPane;
  private static TracksPane streamPane;
  private static PlaylistPane playlistPane;
  private static Track currentTrack;
  private static PagedList<PagedListEntry> currentPlaylist;
  private static ArrayList<ChangeListener<? super Number>> widthListeners = new ArrayList<>();
  private static ArrayList<ChangeListener<? super Number>> viewportVListeners = new ArrayList<>();
  private static ArrayList<Node> paneHistory = new ArrayList<>();

  public static void init(Stage stage) {
    root = new MenuBars();

    AudioManager.getPlayerFx().bindVolume(root.getVolumeSlider().valueProperty());

    Scene scene = new Scene(root, 800, 600);
    scene.getStylesheets().add("style.css");

//    stage.initStyle(StageStyle.UNDECORATED);
    //TODO: shutdown vm on stage exit
    stage.setScene(scene);
    stage.show();

//    showProfile();
    showMyLikes();
//    showMyStream();
  }

  public static void setPane(Node node) {
    removeViewportVListeners();
    removeWidthListeners();
    //TODO: dice out some history limit
    paneHistory.add(node);
    root.getMainScrollPane().setContent(node);
    //reset Viewport
    root.getMainScrollPane().vvalueProperty().set(0);
    ((PlayerPane) node).init();
  }

  public static void back() {
    int historySize = paneHistory.size();
    if (historySize > 1) {
      paneHistory.remove(historySize - 1);
      root.getMainScrollPane().setContent(paneHistory.get(historySize - 2));
    }
  }

  public static double getScrollPaneHeight() {
    return root.getMainScrollPane().getHeight();
  }

  public static double getScrollPaneWidth() {
    return root.getMainScrollPane().getWidth();
  }

  public static void addViewportVListener(ChangeListener<? super Number> listener) {
    viewportVListeners.add(listener);
    root.getMainScrollPane().vvalueProperty().addListener(listener);
  }

  public static void removeViewportVListeners() {
    viewportVListeners.forEach(changeListener ->
        root.getMainScrollPane().vvalueProperty().removeListener(changeListener));
    viewportVListeners.clear();
  }

  public static void addWidthListener(ChangeListener<? super Number> listener) {
    widthListeners.add(listener);
    root.getMainScrollPane().widthProperty().addListener(listener);
  }

  public static void removeWidthListeners() {
    widthListeners.forEach(changeListener ->
        root.getMainScrollPane().widthProperty().removeListener(changeListener));
    widthListeners.clear();
  }

  public static void showMyLikes() {
    log.log(LogType.UI, "show My Likes");
    if (likesPane == null) {
      likesPane = new TracksPane(ModelManager.getMyLikes());
    }
    setPane(likesPane);
  }

  public static void showTrackList(PagedList<PagedListEntry> tracks) {
    log.log(LogType.UI, "show Tracklist");
    setPane(new TracksPane(tracks));
  }

  public static void showMyPlaylists() {
    log.log(LogType.UI, "show My Playlists");
    if (playlistPane == null) {
      playlistPane = new PlaylistPane(ModelManager.getMyPlaylists());
    }
    setPane(playlistPane);
  }

  public static void showMyStream() {
    log.log(LogType.UI, "show My Stream");
    if (streamPane == null) {
      streamPane = new TracksPane(ModelManager.getMyStream());
    }
    setPane(streamPane);
  }

  public static void showProfile() {
    log.log(LogType.UI, "show Playlist");
    showUser(ModelManager.getMe());
  }

  public static void showUser(User user) {
    log.log(LogType.UI, "show Playlist");
    setPane(new UserPane(user));
  }

  public static void showTrack(Track track) {
    log.log(LogType.UI, "show Track " + track);
    setPane(new TrackPane(track));
  }

  public static void setTrackForPlayerUI(Track track, PagedList<PagedListEntry> playlist) {
    Platform.runLater(() -> {
      currentTrack = track;
      currentPlaylist = playlist;
      try {
        root.getArtWork().setImage(new Image(track.getArtwork().getLargeAsStream(), 40, 40, true, true));
      } catch (IOException e) {
        e.printStackTrace();
      }
      //duration in seconds
      int duration = track.getDuration() / 1000;
      DecimalFormat secondFormater = new DecimalFormat("00");

      root.getTitleLabel().setText(track.getTitle());
      root.getTitleLabel().setOnMouseClicked(event -> showTrack(track));
      root.getUserLabel().setText(track.getUserName());
      root.getUserLabel().setOnMouseClicked(event -> showUser(track.getUser()));
      root.getProgressSlider().setMax(duration);
      root.getProgressSlider().valueProperty().addListener((observable1, oldValue1, newValue1) ->
      {
        if (newValue1.intValue() - oldValue1.intValue() > 1 || oldValue1.intValue() > newValue1.intValue() && newValue1.intValue() != 0)
          AudioManager.getPlayerFx().seek(new Duration(newValue1.doubleValue() * 1000));
      });

      root.getTrackTime().setText((duration / 60) + ":" + secondFormater.format(duration % 60));

      AudioManager.getPlayerFx().setProgressTimeListener((observable, oldValue, newValue) ->
      {
        int seconds = (int) newValue.toSeconds();
        root.getProgressSlider().setValue(seconds);
        root.getPlaybackTime().setText((seconds / 60) + ":" + secondFormater.format(seconds % 60));
        track.getWaveform().progressAnimation(newValue.toSeconds() / (track.getDuration() / 1000));
      });
    });
  }

  public static void setPlayPauseIcon(boolean isPlaying) {
    Button button = root.getPlayButton();
    button.setStyle("-fx-background-image: null;");
    Platform.runLater(() -> {
      if (isPlaying)
        button.setGraphic(UIUtils.SVGPaths.play.get());
      else
        button.setGraphic(UIUtils.SVGPaths.pause.get());
    });
  }

  public static void showPlaylist(PlayList entry) {
    log.log(LogType.UI, "show Playlist");
    //TODO: should look somewhat similar to the UserPane
  }

  public static void showVisualizer() {
    log.log(LogType.UI, "show Visualizer");
    setPane(new VisualizerPane());
  }

  public static void showStats() {
    log.log(LogType.UI, "show Statistics");
    setPane(new StatsPane());
  }
}

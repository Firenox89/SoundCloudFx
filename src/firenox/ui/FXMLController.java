package firenox.ui;

/**
 * Created by firenox on 10/11/15.
 */

import firenox.media.AudioManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;

public class FXMLController {
    @FXML private Label titleLabel;
    @FXML private Label userLabel;
    @FXML private ImageView artWork;
    @FXML private Slider progressSlider;
    @FXML private Slider volumeSlider;
    @FXML private Button volumeBtn;
    @FXML private ContextMenu volumeContext;
    @FXML private ScrollPane mainScrollPane;
    @FXML private Label trackTime;
    @FXML private Label playbackTime;

    @FXML protected void handlePrevButton(ActionEvent event) {
        AudioManager.getPlayerFx().previous();
    }

    @FXML protected void handlePlayButton(ActionEvent event) {
        AudioManager.getPlayerFx().play();
    }

    @FXML protected void handleNextPlayButton(ActionEvent event) {
        AudioManager.getPlayerFx().next();
    }


    @FXML protected void handleVolumeButton(ActionEvent actionEvent) {
        volumeBtn.setContextMenu(volumeContext);
    }

    @FXML public void handlePlaylistButton(ActionEvent actionEvent) {
        UIManager.showPlaylists();
    }

    @FXML public void handleLikesButton(ActionEvent actionEvent) {
        UIManager.showFavorites();
    }

    @FXML public void handlePlayerButton(ActionEvent actionEvent) {
        UIManager.showPlayer();
    }

    @FXML public void handleStreamButton(ActionEvent actionEvent) {
        UIManager.showStream();
    }

    @FXML public void handleProfileButton(ActionEvent actionEvent) {
        UIManager.showProfile();
    }

    public Label getTitleLabel() {
        return titleLabel;
    }

    public ImageView getArtWork() {
        return artWork;
    }

    public Slider getProgressSlider() {
        return progressSlider;
    }

    public Slider getVolumeSlider() {
        return volumeSlider;
    }

    public ScrollPane getMainScrollPane() {
        return mainScrollPane;
    }

    public Label getTrackTime() {
        return trackTime;
    }

    public Label getPlaybackTime() {
        return playbackTime;
    }

    public Label getUserLabel() {
        return userLabel;
    }

    public void handleShuffle(ActionEvent actionEvent) {
        AudioManager.getPlayerFx().toggleShuffle();
    }

    public void handleRepeat(ActionEvent actionEvent) {
        AudioManager.getPlayerFx().toggleRepeat();
    }
}

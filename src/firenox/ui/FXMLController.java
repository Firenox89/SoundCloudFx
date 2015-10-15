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
    @FXML private ImageView artWork;
    @FXML private Slider progressSlider;
    @FXML private Slider volumeSlider;
    @FXML private Button volumeBtn;
    @FXML private ContextMenu volumeContext;
    @FXML private ScrollPane mainScrollPane;

    @FXML protected void handlePrevButton(ActionEvent event) {
        AudioManager.getPlayerFx().previous();
    }

    @FXML protected void handlePlayButton(ActionEvent event) {
        AudioManager.getPlayerFx().play();
    }

    @FXML protected void handleNextPlayButton(ActionEvent event) {
        AudioManager.getPlayerFx().next();
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

    @FXML
    protected void handleVolumeButton(ActionEvent actionEvent) {
        volumeBtn.setContextMenu(volumeContext);
    }

    public void handlePlaylistButton(ActionEvent actionEvent) {
        UIManager.showPlaylists();
    }

    public void handleLikesButton(ActionEvent actionEvent) {
        UIManager.showFavorites();
    }
}

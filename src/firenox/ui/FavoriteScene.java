package firenox.ui;

import firenox.media.AudioManager;
import firenox.model.ArtWork;
import firenox.model.Track;
import firenox.model.WaveForm;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by firenox on 10/8/15.
 */
public class FavoriteScene {

    private final int waveWidth = 650;
    private final int waveHeigth = 80;
    private final int artWidth = 100;
    private final int artHeigth = 100;
    int count = 0;
    private VBox vbox;
    private FXMLController controller;
    private ArrayList<Track> favList;

    public FavoriteScene(ArrayList<Track> favList) {
        controller = UIManager.getController();
        vbox = new VBox();
        controller.getMainScrollPane().setContent(vbox);
        setFavorites(favList);
        this.favList = favList;
        //TODO: use paging to load the rest of the list

        controller.getVolumeSlider().valueProperty().addListener((observable, oldValue, newValue) ->
                AudioManager.getPlayerFx().setVolume(newValue.doubleValue()));
    }

    public void setFavorites(ArrayList<Track> favList) {
        favList.stream()
                .filter(t -> count++ < 10)
                .allMatch(t -> vbox.getChildren().add(buildTrackContainer(t)));
    }

    private BorderPane buildTrackContainer(Track track) {
        BorderPane box = new BorderPane();
        box.setStyle("-fx-padding: 10,10,10,10;");
        ArtWork artwork = track.getArtwork();
        WaveForm waveForm = track.getWaveform();
        ImageView artwork_view = null;
        ImageView wave_view = null;
        try {
            Image waveImage = new Image(waveForm.getRenderedWaveAsStream(waveWidth, waveHeigth));
            Image artworkImage = new Image(artwork.getLargeAsStream());
            if (artworkImage.getHeight() != artHeigth || artworkImage.getWidth() != artWidth) {
                artworkImage = new Image(artwork.getLargeAsStream(), 100, 100, true, true);
            }
            wave_view = new ImageView(waveImage);
            artwork_view = new ImageView(artworkImage);
            artwork_view.setOnMouseClicked(mouseEvent -> setTrack(track));
            wave_view.setOnMouseClicked(mouseEvent -> setTrack(track));
        } catch (IOException e) {
            e.printStackTrace();
        }

        box.setLeft(artwork_view);
        VBox wave_con = new VBox();
        wave_con.setStyle("-fx-alignment: center;");
        Label label = new Label(track.getTitle());
        wave_con.getChildren().add(label);
        wave_con.getChildren().add(wave_view);
        box.setCenter(wave_con);

        return box;
    }

    private void playNextTrack(Track track) {
        int current = favList.indexOf(track);
        setTrack(favList.get(++current));
    }

    private void setTrack(Track track) {
        try {
//            AudioManager.getPlayerFx().open(track.getTempFileURL());
            AudioManager.getPlayerFx().open(track.getStreamURL());
            controller.getArtWork().setImage(new Image(track.getArtwork().getLargeAsStream(), 40, 40, true, true));
            controller.getTitleLabel().setText(track.getTitle());
            controller.getProgressSlider().setMax(track.getDuration() / 1000);
            AudioManager.getPlayerFx().setMediaEndListener(() -> playNextTrack(track));
            AudioManager.getPlayerFx().setProgressTimeListener((observable, oldValue, newValue) ->
                    controller.getProgressSlider().setValue(observable.getValue().toSeconds()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void asyncAdd() {
        //TODO: load Artwork and Wave asynchronous
    }
}

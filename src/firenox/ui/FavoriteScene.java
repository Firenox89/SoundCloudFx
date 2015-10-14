package firenox.ui;

import firenox.io.BackgroundLoader;
import firenox.media.AudioManager;
import firenox.model.ArtWork;
import firenox.model.ModelManager;
import firenox.model.Track;
import firenox.model.WaveForm;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by firenox on 10/8/15.
 */
public class FavoriteScene {

    private final int waveWidth = 650;
    private final int waveHeigth = 80;
    private final int artWidth = 100;
    private final int artHeigth = 100;
    private final ScrollPane scrollPane;
    private VBox vbox;
    private FXMLController controller;
    private ArrayList<Track> favList;

    public FavoriteScene() {
        controller = UIManager.getController();
        vbox = new VBox();
        scrollPane = controller.getMainScrollPane();
        scrollPane.setContent(vbox);
        favList = ModelManager.getLikes();
        setFavorites(favList);

        controller.getVolumeSlider().valueProperty().addListener((observable, oldValue, newValue) ->
                AudioManager.getPlayerFx().setVolume(newValue.doubleValue()));
    }

    public void setFavorites(ArrayList<Track> favList) {
        favList.forEach(t -> vbox.getChildren().add(buildTrackContainer(t)));

        //if viewport reaches the bottom, fetch and a new tracks
        scrollPane.vvalueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.doubleValue() > 0.98)
            {
                ArrayList<Track> newTracks = ModelManager.loadNextFav();
                if (newTracks != null)
                {
                    newTracks.forEach(t -> vbox.getChildren().add(buildTrackContainer(t)));
                }
            }
        });
    }


    private BorderPane buildTrackContainer(Track track) {
        BorderPane box = new BorderPane();
        box.setStyle("-fx-padding: 10,10,10,10;");
        ArtWork artwork = track.getArtwork();
        WaveForm waveForm = track.getWaveform();
        ImageView artwork_view = new ImageView();
        ImageView wave_view = new ImageView();

        try {
            asyncAdd(wave_view, waveForm.getRenderedWaveAsStream(waveWidth, waveHeigth), 0,0);
            asyncAdd(artwork_view, artwork.getLargeAsStream(), artWidth, artHeigth);

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

    private void asyncAdd(ImageView view, InputStream image, int width, int heigth) {
        BackgroundLoader.addTask(() ->
        {
            Image img = new Image(image);
            if (width != 0 && heigth != 0 && img.getWidth() != width && img.getHeight() != heigth && image.markSupported())
            {
                try {
                    //FIXME stream reset is not supported
                    image.reset();
                    img = new Image(image, width, heigth, true, true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            view.setImage(img);
        });
    }
}

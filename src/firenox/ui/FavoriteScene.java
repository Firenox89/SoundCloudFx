package firenox.ui;

import com.soundcloud.api.CloudAPI;
import firenox.io.BackgroundLoader;
import firenox.media.AudioManager;
import firenox.model.ArtWork;
import firenox.model.ModelManager;
import firenox.model.Track;
import firenox.model.WaveForm;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
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

    }

    public void setFavorites(ArrayList<Track> favList) {
        favList.forEach(t -> vbox.getChildren().add(buildTrackContainer(t)));

        //if viewport reaches the bottom, fetch and a new tracks
        scrollPane.vvalueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.doubleValue() > 0.98) {
                loadNextFav();
            }
        });
    }

    private void loadNextFav() {
        ArrayList<Track> newTracks = ModelManager.loadNextFav();
        if (newTracks != null) {
            newTracks.forEach(t -> vbox.getChildren().add(buildTrackContainer(t)));
        }
    }


    private BorderPane buildTrackContainer(Track track) {
        BorderPane box = new BorderPane();
        box.setStyle("-fx-padding: 10,10,10,10;");
        ArtWork artwork = track.getArtwork();
        WaveForm waveForm = track.getWaveform();
        ImageView artwork_view = new ImageView();
        Canvas wave_view = waveForm.getCanvas(waveWidth, waveHeigth);


        asyncArtworkAdd(artwork_view, artwork, artWidth, artHeigth);

        artwork_view.setOnMouseClicked(mouseEvent -> setTrack(track));
        wave_view.setOnMouseClicked(mouseEvent -> setTrack(track));

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
        if (current == favList.size()) {
            loadNextFav();
        }
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
            {
                controller.getProgressSlider().setValue(newValue.toSeconds());
                track.getWaveform().progressAnimation(newValue.toSeconds()/(track.getDuration()/1000));
            });
        } catch (CloudAPI.ResolverException re) {
            re.printStackTrace();
            playNextTrack(track);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void asyncArtworkAdd(ImageView view, ArtWork artWork, int width, int heigth) {
        BackgroundLoader.addTask(() ->
        {
            try {
                Image img = new Image(artWork.getLargeAsStream());
                if (width != 0 && heigth != 0 && img.getWidth() != width && img.getHeight() != heigth) {
                    img = new Image(artWork.getLargeAsStream(), width, heigth, true, true);
                }
                view.setImage(img);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}

package firenox.ui;

import firenox.io.BackgroundLoader;
import firenox.media.AudioManager;
import firenox.model.*;
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
    private PagedList<Track> favList;
    private Track currentTrack;

    public FavoriteScene() {
        controller = UIManager.getController();
        vbox = new VBox();
        scrollPane = controller.getMainScrollPane();
        scrollPane.setContent(vbox);
        favList = ModelManager.getLikes();
        setFavorites();
    }

    public void setFavorites() {
        favList.forEach(t -> vbox.getChildren().add(buildTrackContainer(t)));

        //update container on list changes
        favList.setNewEntriesLoadedListener(() ->
                favList.getLastLoadedEntries().forEach(t -> vbox.getChildren().add(buildTrackContainer(t))));

        //if viewport reaches the bottom, request new tracks
                scrollPane.vvalueProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue.doubleValue() > 0.98) {
                        favList.loadNextEntries();
                    }
                });
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


    private void setTrack(Track track) {
        currentTrack = track;
        AudioManager.getPlayerFx().open(favList, favList.indexOf(track));
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

package firenox.ui;

import firenox.media.AudioManager;
import firenox.model.ArtWork;
import firenox.model.PagedList;
import firenox.model.Track;
import firenox.model.WaveForm;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by firenox on 10/20/15.
 */
public class PlayerPane extends Pane {

    Track currentTrack;
    PagedList<Track> currentPlaylist;

    public PlayerPane()
    {
        currentTrack = UIManager.getCurrentTrack();
        currentPlaylist = UIManager.getCurrentPlaylist();
        BorderPane bPane = buildPlayerContainer(currentTrack, currentPlaylist, 1200, 200);
        getChildren().add(bPane);
    }

    private BorderPane buildPlayerContainer(Track track,
                                                 PagedList<Track> list,
                                                 int waveWidth,
                                                 int waveHeigth) {
        BorderPane box = new BorderPane();
        box.setStyle("-fx-padding: 10,10,10,10;");
        ArtWork artWork = track.getArtwork();
        WaveForm waveForm = track.getWaveform();
        ImageView artwork_view = new ImageView();
        Canvas wave_view = waveForm.getCanvas(waveWidth, waveHeigth);


        try {

            Image img = new Image(artWork.getT500AsStream(), 1000, 1000, true, true);
            setBackground(new Background(new BackgroundImage(img, null, null, null, null)));
        } catch (IOException e) {
            e.printStackTrace();
        }

        box.setLeft(artwork_view);
        VBox wave_con = new VBox();
        wave_con.setStyle("-fx-alignment: center;");
        Label label = new Label(track.getTitle());
        ScrollPane wave_scroll = new ScrollPane();
        //don't show scrollbar
        wave_scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        //consume scroll event to disable manual scroll
        wave_scroll.addEventFilter(ScrollEvent.SCROLL, event -> event.consume());
        wave_scroll.setContent(wave_view);
        wave_con.getChildren().add(label);
        wave_con.getChildren().add(wave_scroll);
        box.setCenter(wave_con);

        return box;
    }

}

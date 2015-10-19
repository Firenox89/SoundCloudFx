package firenox.ui;

import firenox.io.BackgroundLoader;
import firenox.media.AudioManager;
import firenox.model.ArtWork;
import firenox.model.PagedList;
import firenox.model.Track;
import firenox.model.WaveForm;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

/**
 * Created by firenox on 10/19/15.
 */
public class UIUtils {


    public static BorderPane buildTrackContainer(Track track,
                                                 PagedList<Track> list,
                                                 int waveWidth,
                                                 int waveHeigth,
                                                 int artWidth,
                                                 int artHeigth) {
        BorderPane box = new BorderPane();
        box.setStyle("-fx-padding: 10,10,10,10;");
        ArtWork artwork = track.getArtwork();
        WaveForm waveForm = track.getWaveform();
        ImageView artwork_view = new ImageView();
        Canvas wave_view = waveForm.getCanvas(waveWidth, waveHeigth);


        asyncArtworkAdd(artwork_view, artwork, artWidth, artHeigth);

        artwork_view.setOnMouseClicked(mouseEvent -> setTrack(track, list));
        wave_view.setOnMouseClicked(mouseEvent -> setTrack(track, list));

        box.setLeft(artwork_view);
        VBox wave_con = new VBox();
        wave_con.setStyle("-fx-alignment: center;");
        Label label = new Label(track.getTitle());
        wave_con.getChildren().add(label);
        wave_con.getChildren().add(wave_view);
        box.setCenter(wave_con);

        return box;
    }

    private static void asyncArtworkAdd(ImageView view, ArtWork artWork, int width, int heigth) {
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

        private static void setTrack(Track track, PagedList<Track> list) {
        AudioManager.getPlayerFx().open(list, list.indexOf(track));
    }
}

package firenox.ui;

import firenox.io.BackgroundLoader;
import firenox.logger.Logger;
import firenox.media.AudioManager;
import firenox.model.*;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by firenox on 10/19/15.
 */
public class UIUtils {

    private static Logger log = Logger.getLogger(UIUtils.class.getName());

    public static BorderPane buildTrackContainer(Track track,
                                                 PagedList<Track> list,
                                                 int waveWidth,
                                                 int waveHeight,
                                                 int artWidth,
                                                 int artHeight) {
        BorderPane box = new BorderPane();
        box.setStyle("-fx-padding: 5, 5, 5, 5;");
        ArtWork artwork = track.getArtwork();
        WaveForm waveForm = track.getWaveform();
        ImageView artwork_view = new ImageView();
        artwork_view.setFitWidth(artWidth);
        artwork_view.setFitHeight(artHeight);
        Canvas wave_view = waveForm.getCanvas(waveWidth, waveHeight);
        Label title = new Label(track.getTitle());
        Label userName = new Label(track.getUser_name());


        asyncArtworkAdd(artwork_view, artwork, artWidth, artHeight);

        artwork_view.setOnMouseClicked(mouseEvent -> setTrack(track, list));
        wave_view.setOnMouseClicked(mouseEvent -> setTrack(track, list));
        title.setOnMouseClicked(event -> UIManager.showTrack(track));
        userName.setOnMouseClicked(event -> UIManager.showUser(track.getUser()));

        box.setLeft(artwork_view);
        VBox wave_con = new VBox();
        wave_con.setStyle("-fx-alignment: center;");
        wave_con.getChildren().add(userName);
        wave_con.getChildren().add(title);
        wave_con.getChildren().add(wave_view);
        box.setCenter(wave_con);

        return box;
    }



    private static void asyncArtworkAdd(ImageView view, ArtWork artWork, int width, int heigth) {
        BackgroundLoader.addTask(() ->
        {
            try {
                InputStream is;
                //TODO: use ranges
                if (width == 100) {
                    is = artWork.getLargeAsStream();
                } else if (width == 300) {
                    is = artWork.getT300AsStream();
                } else if (width == 500){
                    is = artWork.getT500AsStream();
                } else {
                    is = artWork.getCustomSize(width, heigth);
                }
                Image img = new Image(is);
                view.setImage(img);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private static void setTrack(Track track, PagedList<Track> list) {
        AudioManager.getPlayerFx().open(list, list.indexOf(track));
    }

    public static BorderPane buildTrackTile(Track track,
                                            PagedList<Track> list,
                                            int artWidth,
                                            int artHeight) {
        BorderPane box = new BorderPane();
        ArtWork artwork = track.getArtwork();
        ImageView artwork_view = new ImageView();
        artwork_view.setFitWidth(artWidth);
        artwork_view.setFitHeight(artHeight);

        asyncArtworkAdd(artwork_view, artwork, artWidth, artHeight);

        artwork_view.setOnMouseClicked(mouseEvent -> setTrack(track, list));

        box.setCenter(artwork_view);
        Label label = new Label(track.getTitle());
        label.setPrefWidth(artWidth);
        box.setBottom(label);

        return box;
    }

    public static BorderPane buildPlayListTile(PlayList playList, int width, int heigth) {
        BorderPane box = new BorderPane();
        ArtWork artwork = playList.getArtwork();
        ImageView artwork_view = new ImageView();
        artwork_view.setFitWidth(width);
        artwork_view.setFitHeight(heigth);

        asyncArtworkAdd(artwork_view, artwork, width, heigth);

        artwork_view.setOnMouseClicked(mouseEvent -> UIManager.showTrackList(playList.getTrackList()));

        box.setCenter(artwork_view);
        Label label = new Label(playList.getTitle());
        label.setPrefWidth(width);
        box.setBottom(label);

        return box;
    }
}

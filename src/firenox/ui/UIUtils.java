package firenox.ui;

import firenox.io.BackgroundLoader;
import firenox.logger.Logger;
import firenox.media.AudioManager;
import firenox.model.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.FillRule;
import javafx.scene.shape.SVGPath;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by firenox on 10/19/15.
 */
public class UIUtils {

    private static Logger log = Logger.getLogger(UIUtils.class.getName());

    private static String likePath = "M10.805 3C8.785 3 8 5.345 8 5.345S7.214 3 5.197 3C3.494 3 1.748 4.096 2.03 6.514c.344 2.953 5.725 6.48 5.963 6.487.238.01 5.738-3.72 5.988-6.5.208-2.3-1.473-3.5-3.175-3.5z";
    private static String repostPath = "M2 6v5c0 1.105.902 2 2.01 2h6.986H10l-2-2H4V6h-.5H6L3 3 0 6h2zm4-3h-.996 6.987C13.1 3 14 3.895 14 5v5h-2V5H8L6 3zm10 7h-6l3 3 3-3z";
    private static String addToPlaylistPath = "M12 3V1h2v2h2v2h-2v2h-2V5h-2V3h2zM0 3v2h8V3H0zm0 4v2h10V7H0zm0 4v2h10v-2H0z";
    private static int count = 0;

    public static BorderPane buildTrackContainer(AbstractPagedListEntry entry,
                                                 PagedList<AbstractPagedListEntry> list,
                                                 int waveWidth,
                                                 int waveHeight,
                                                 int artWidth,
                                                 int artHeight) {
        BorderPane box = new BorderPane();
        box.setStyle("-fx-padding: 5, 5, 5, 5;");
        ArtWork artwork = entry.getArtwork();
        WaveForm waveForm = entry.getWaveform();
        ImageView artwork_view = new ImageView();
        artwork_view.setFitWidth(artWidth);
        artwork_view.setFitHeight(artHeight);
        Canvas wave_view = waveForm.getCanvas(waveWidth, waveHeight);
        Label title = new Label(entry.getTitle());
        Label userName = new Label(entry.getUser().getUsername());

        asyncArtworkAdd(artwork_view, artwork, artWidth, artHeight);

        artwork_view.setOnMouseClicked(mouseEvent -> setTrack(entry, list));
        wave_view.setOnMouseClicked(mouseEvent -> setTrack(entry, list));
        if (entry instanceof Track)
            title.setOnMouseClicked(event -> UIManager.showTrack((Track) entry));
        else if (entry instanceof PlayList)
            title.setOnMouseClicked(event -> UIManager.showPlaylist((PlayList) entry));
        userName.setOnMouseClicked(event -> UIManager.showUser(entry.getUser()));

        SVGPath likeSVG = new SVGPath();
        likeSVG.setContent(likePath);
        likeSVG.setFillRule(FillRule.EVEN_ODD);
        if (entry instanceof Track && ((Track)entry).doILike())        {
            likeSVG.setFill(Color.CORAL);
        }
        else {
            likeSVG.setFill(Color.GRAY);
        }

        SVGPath repostSVG = new SVGPath();
        repostSVG.setContent(repostPath);
        repostSVG.setFillRule(FillRule.EVEN_ODD);
        repostSVG.setFill(Color.GRAY);

        SVGPath addToPlaylistSVG = new SVGPath();
        addToPlaylistSVG.setContent(addToPlaylistPath);
        addToPlaylistSVG.setFillRule(FillRule.EVEN_ODD);
        addToPlaylistSVG.setFill(Color.GRAY);

        GridPane gridPane = new GridPane();

        gridPane.add(likeSVG, 0, 0, 1, 2);
        gridPane.add(repostSVG, 1, 0, 1, 2);
        gridPane.add(addToPlaylistSVG, 2, 0, 1, 2);

        gridPane.add(userName, 3, 0, 1, 1);
        gridPane.add(title, 3, 1, 1, 1);

        VBox wave_con = new VBox();
        wave_con.getChildren().add(gridPane);
        wave_con.getChildren().add(wave_view);
        if (entry instanceof PlayList) {
            count = 0;
            PlayList playlist = (PlayList) entry;
            playlist.getTrackList()
                    .stream()
                    .filter(e -> count++ < 5)
                    .map(e -> (Track) e)
                    .forEach(t -> {
                        try {
                            ImageView art = new ImageView(new Image(t.getArtwork().getCustomSize(20, 20)));
                            art.setOnMouseClicked(mouseEvent -> setTrack(t, playlist.getTrackList()));
                            Label userLabel = new Label(count + 1 + " " + t.getUser().getUsername() + " - ");
                            userLabel.setOnMouseClicked(mouseEvent -> UIManager.showUser(t.getUser()));
                            userLabel.setStyle("-fx-text-fill: #828282;");
                            Label titleLabel = new Label(t.getTitle());
                            titleLabel.setOnMouseClicked(mouseEvent -> setTrack(t, playlist.getTrackList()));
                            wave_con.getChildren().add(new HBox(
                                    art,
                                    userLabel,
                                    titleLabel));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        }

        box.setLeft(artwork_view);
        box.setCenter(wave_con);

        return box;
    }

    private static void asyncArtworkAdd(ImageView view, ArtWork artWork, int width, int heigth) {
        BackgroundLoader.addTask(() ->
        {
            try {
                InputStream is;
                if (width == 100) {
                    is = artWork.getLargeAsStream();
                } else if (width == 300) {
                    is = artWork.getT300AsStream();
                } else if (width == 500) {
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

    private static void setTrack(AbstractPagedListEntry entry, PagedList<AbstractPagedListEntry> list) {
        if (entry instanceof Track)
            AudioManager.getPlayerFx().open(list, list.indexOf(entry));
        else if (entry instanceof PlayList)
            AudioManager.getPlayerFx().open(((PlayList) entry).getTrackList(), 0);
    }

    public static BorderPane buildTrackTile(AbstractPagedListEntry track,
                                            PagedList<AbstractPagedListEntry> list,
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
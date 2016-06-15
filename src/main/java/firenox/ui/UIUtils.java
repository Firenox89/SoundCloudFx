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

  private static int count = 0;

  public static BorderPane buildTrackContainer(PagedListEntry entry,
                                               PagedList<PagedListEntry> list,
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
    wave_view.setOnMouseClicked(mouseEvent -> {
      double s = mouseEvent.getX() / wave_view.getWidth();
      AudioManager.getPlayerFx().openAndSeek(list, list.indexOf(entry), s);
    });
    if (entry instanceof Track)
      title.setOnMouseClicked(event -> UIManager.showTrack((Track) entry));
    else if (entry instanceof PlayList)
      title.setOnMouseClicked(event -> UIManager.showPlaylist((PlayList) entry));
    userName.setOnMouseClicked(event -> UIManager.showUser(entry.getUser()));

    SVGPath likeSVG = SVGPaths.likePath;
    if (entry instanceof Track && ((Track) entry).doILike()) {
      likeSVG.setFill(Color.ORANGE);
      likeSVG.setOnMouseClicked(event -> toggleTrackLike((Track) entry, likeSVG));
    } else if (entry instanceof Track) {
      likeSVG.setFill(Color.GRAY);
      likeSVG.setOnMouseClicked(event -> toggleTrackLike((Track) entry, likeSVG));
    } else {
      likeSVG.setFill(Color.GRAY);
    }

    SVGPath repostSVG = SVGPaths.repostPath;
    repostSVG.setFill(Color.GRAY);

    SVGPath addToPlaylistSVG = SVGPaths.addToPlaylistPath;
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

  private static void toggleTrackLike(Track track, SVGPath path) {
    if (track.doILike()) {
      path.setFill(Color.GRAY);
    } else {
      path.setFill(Color.ORANGE);
    }
    track.toggleLike();
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

  private static void setTrack(PagedListEntry entry, PagedList<PagedListEntry> list) {
    if (entry instanceof Track)
      AudioManager.getPlayerFx().open(list, list.indexOf(entry));
    else if (entry instanceof PlayList)
      AudioManager.getPlayerFx().open(((PlayList) entry).getTrackList(), 0);
  }


  public static BorderPane buildTrackTile(PagedListEntry track,
                                          PagedList<PagedListEntry> list,
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

  public static class SVGPaths {
    public static final SVGPath likePath = new SVGPath();
    public static final SVGPath repostPath = new SVGPath();
    public static final SVGPath addToPlaylistPath = new SVGPath();
    public static final SVGPath cog = new SVGPath();
    public static final SVGPath heart = new SVGPath();
    public static final SVGPath equalizer2 = new SVGPath();
    public static final SVGPath pause = new SVGPath();
    public static final SVGPath play = new SVGPath();
    public static final SVGPath previous = new SVGPath();
    public static final SVGPath next = new SVGPath();
    public static final SVGPath shuffle = new SVGPath();
    public static final SVGPath volumeSpeaker = new SVGPath();

    static {
      likePath.setContent("M10.805 3C8.785 3 8 5.345 8 5.345S7.214 3 5.197 3C3.494 3 1.748 4.096 2.03 6.514c.344 2.953 5.725 6.48 5.963 6.487.238.01 5.738-3.72 5.988-6.5.208-2.3-1.473-3.5-3.175-3.5z");
      repostPath.setContent("M2 6v5c0 1.105.902 2 2.01 2h6.986H10l-2-2H4V6h-.5H6L3 3 0 6h2zm4-3h-.996 6.987C13.1 3 14 3.895 14 5v5h-2V5H8L6 3zm10 7h-6l3 3 3-3z");
      addToPlaylistPath.setContent("M12 3V1h2v2h2v2h-2v2h-2V5h-2V3h2zM0 3v2h8V3H0zm0 4v2h10V7H0zm0 4v2h10v-2H0z");
      cog.setContent("M29.181 19.070c-1.679-2.908-0.669-6.634 2.255-8.328l-3.145-5.447c-0.898 0.527-1.943 0.829-3.058 0.829-3.361 0-6.085-2.742-6.085-6.125h-6.289c0.008 1.044-0.252 2.103-0.811 3.070-1.679 2.908-5.411 3.897-8.339 2.211l-3.144 5.447c0.905 0.515 1.689 1.268 2.246 2.234 1.676 2.903 0.672 6.623-2.241 8.319l3.145 5.447c0.895-0.522 1.935-0.82 3.044-0.82 3.35 0 6.067 2.725 6.084 6.092h6.289c-0.003-1.034 0.259-2.080 0.811-3.038 1.676-2.903 5.399-3.894 8.325-2.219l3.145-5.447c-0.899-0.515-1.678-1.266-2.232-2.226zM16 22.479c-3.578 0-6.479-2.901-6.479-6.479s2.901-6.479 6.479-6.479c3.578 0 6.479 2.901 6.479 6.479s-2.901 6.479-6.479 6.479z");
      heart.setContent("M23.6 2c-3.363 0-6.258 2.736-7.599 5.594-1.342-2.858-4.237-5.594-7.601-5.594-4.637 0-8.4 3.764-8.4 8.401 0 9.433 9.516 11.906 16.001 21.232 6.13-9.268 15.999-12.1 15.999-21.232 0-4.637-3.763-8.401-8.4-8.401z");
      equalizer2.setContent("M28 14h0.5c0.825 0 1.5-0.675 1.5-1.5v-5c0-0.825-0.675-1.5-1.5-1.5h-0.5v-6h-4v6h-0.5c-0.825 0-1.5 0.675-1.5 1.5v5c0 0.825 0.675 1.5 1.5 1.5h0.5v18h4v-18zM24 8h4v4h-4v-4zM18.5 26c0.825 0 1.5-0.675 1.5-1.5v-5c0-0.825-0.675-1.5-1.5-1.5h-0.5v-18h-4v18h-0.5c-0.825 0-1.5 0.675-1.5 1.5v5c0 0.825 0.675 1.5 1.5 1.5h0.5v6h4v-6h0.5zM14 20h4v4h-4v-4zM8.5 14c0.825 0 1.5-0.675 1.5-1.5v-5c0-0.825-0.675-1.5-1.5-1.5h-0.5v-6h-4v6h-0.5c-0.825 0-1.5 0.675-1.5 1.5v5c0 0.825 0.675 1.5 1.5 1.5h0.5v18h4v-18h0.5zM4 8h4v4h-4v-4z");
      pause.setContent("M4 4h10v24h-10zM18 4h10v24h-10z");
      play.setContent("M6 4l20 12-20 12z");
      previous.setContent("M8 28v-24h4v11l10-10v22l-10-10v11z");
      next.setContent("M24 4v24h-4v-11l-10 10v-22l10 10v-11z");
      shuffle.setContent("M24 22h-3.172l-5-5 5-5h3.172v5l7-7-7-7v5h-4c-0.53 0-1.039 0.211-1.414 0.586l-5.586 5.586-5.586-5.586c-0.375-0.375-0.884-0.586-1.414-0.586h-6v4h5.172l5 5-5 5h-5.172v4h6c0.53 0 1.039-0.211 1.414-0.586l5.586-5.586 5.586 5.586c0.375 0.375 0.884 0.586 1.414 0.586h4v5l7-7-7-7v5z");
      volumeSpeaker.setContent("M13 30c-0.26 0-0.516-0.102-0.707-0.293l-7.707-7.707h-3.586c-0.552 0-1-0.448-1-1v-10c0-0.552 0.448-1 1-1h3.586l7.707-7.707c0.286-0.286 0.716-0.372 1.090-0.217s0.617 0.519 0.617 0.924v26c0 0.404-0.244 0.769-0.617 0.924-0.124 0.051-0.254 0.076-0.383 0.076z");

      likePath.setFillRule(FillRule.EVEN_ODD);
      repostPath.setFillRule(FillRule.EVEN_ODD);
      addToPlaylistPath.setFillRule(FillRule.EVEN_ODD);
      cog.setFillRule(FillRule.EVEN_ODD);
      heart.setFillRule(FillRule.EVEN_ODD);
      equalizer2.setFillRule(FillRule.EVEN_ODD);
      play.setFillRule(FillRule.EVEN_ODD);
      previous.setFillRule(FillRule.EVEN_ODD);
      next.setFillRule(FillRule.EVEN_ODD);
      shuffle.setFillRule(FillRule.EVEN_ODD);
      volumeSpeaker.setFillRule(FillRule.EVEN_ODD);

      play.setFill(Color.WHITE);
      pause.setFill(Color.WHITE);
      previous.setFill(Color.WHITE);
      next.setFill(Color.WHITE);
      shuffle.setFill(Color.WHITE);
      volumeSpeaker.setFill(Color.WHITE);
    }
  }
}
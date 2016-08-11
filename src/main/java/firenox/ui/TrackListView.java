package firenox.ui;

import firenox.io.BackgroundLoader;
import firenox.media.AudioManager;
import firenox.model.*;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;

import java.io.IOException;

/**
 * Created by firenox on 6/18/16.
 */
public class TrackListView extends VBox {
  private static int count = 0;
  private PagedList<PagedListEntry> trackList;
  private ListContainerDimensions dimensions;


  public TrackListView(PagedList<PagedListEntry> trackList, ListContainerDimensions dimensions) {
    this.trackList = trackList;
    this.dimensions = dimensions;

    trackList.forEach(entry -> {
      Pane pane = new Pane();
      getChildren().add(pane);
      BackgroundLoader.createTask(() -> fillTrackContainer(entry, pane, -1));
    });

    trackList.addListener((ListChangeListener<PagedListEntry>) c -> {
      while (c.next()) {
        c.getAddedSubList().forEach(entry -> {
          Pane pane = new Pane();
          getChildren().add(pane);
          BackgroundLoader.createTask(() -> fillTrackContainer(entry, pane, -1));
        });
      }
    });
  }

  private static void toggleTrackLike(Track track, SVGPath path) {
    if (track.doILike()) {
      path.setFill(Color.GRAY);
    } else {
      path.setFill(Color.ORANGE);
    }
    track.toggleLike();
  }

  public void fillTrackContainer(PagedListEntry entry,
                                 Pane target,
                                 int position) {
    HBox box = new HBox();

    box.setSpacing(5);
    box.setPadding(new Insets(5));

    ArtWork artwork = entry.getArtwork();
    WaveForm waveForm = entry.getWaveform();
    ImageView artwork_view = new ImageView();
    artwork_view.setFitWidth(dimensions.getArtWidth());
    artwork_view.setFitHeight(dimensions.getArtHeigth());
    Canvas wave_view = waveForm.getCanvas(dimensions.getWaveWidth(), dimensions.getWaveHeigth());
    Label title = new Label(entry.getTitle());
    Label userName = new Label(entry.getUser().getUsername());

    UIUtils.asyncArtworkAdd(artwork_view, artwork, dimensions.getArtWidth(), dimensions.getArtHeigth());

    artwork_view.setOnMouseClicked(mouseEvent -> UIUtils.setTrack(entry, trackList));
    wave_view.setOnMouseClicked(mouseEvent -> {
      double s = mouseEvent.getX() / wave_view.getWidth();
      AudioManager.getPlayerFx().openAndSeek(trackList, trackList.indexOf(entry), s);
    });
    if (entry instanceof Track)
      title.setOnMouseClicked(event -> UIManager.showTrack((Track) entry));
    else if (entry instanceof PlayList)
      title.setOnMouseClicked(event -> UIManager.showPlaylist((PlayList) entry));
    userName.setOnMouseClicked(event -> UIManager.showUser(entry.getUser()));

    SVGPath likeSVG = UIUtils.SVGPaths.likePath.get();
    if (entry instanceof Track && ((Track) entry).doILike()) {
      likeSVG.setFill(Color.ORANGE);
      likeSVG.setOnMouseClicked(event -> toggleTrackLike((Track) entry, likeSVG));
    } else if (entry instanceof Track) {
      likeSVG.setFill(Color.GRAY);
      likeSVG.setOnMouseClicked(event -> toggleTrackLike((Track) entry, likeSVG));
    } else {
      likeSVG.setFill(Color.GRAY);
    }

    SVGPath repostSVG = UIUtils.SVGPaths.repostPath.get();
    repostSVG.setFill(Color.GRAY);

    SVGPath addToPlaylistSVG = UIUtils.SVGPaths.addToPlaylistPath.get();
    addToPlaylistSVG.setFill(Color.GRAY);

    HBox icons = new HBox();
    icons.setSpacing(2);

    icons.getChildren().add(likeSVG);
    icons.getChildren().add(repostSVG);
    icons.getChildren().add(addToPlaylistSVG);

    VBox trackInfo = new VBox();
    trackInfo.getChildren().add(userName);
    trackInfo.getChildren().add(title);
    icons.getChildren().add(trackInfo);

    VBox wave_con = new VBox();
    wave_con.getChildren().add(icons);
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
              art.setOnMouseClicked(mouseEvent -> UIUtils.setTrack(t, playlist.getTrackList()));
              Label userLabel = new Label(count + 1 + " " + t.getUser().getUsername() + " - ");
              userLabel.setOnMouseClicked(mouseEvent -> UIManager.showUser(t.getUser()));
              userLabel.setStyle("-fx-text-fill: #828282;");
              Label titleLabel = new Label(t.getTitle());
              titleLabel.setOnMouseClicked(mouseEvent -> UIUtils.setTrack(t, playlist.getTrackList()));
              wave_con.getChildren().add(new HBox(
                  art,
                  userLabel,
                  titleLabel));
            } catch (IOException e) {
              e.printStackTrace();
            }
          });
    }

    box.getChildren().addAll(artwork_view, wave_con);

    Platform.runLater(() -> {
      if (position == -1) {
        target.getChildren().add(box);
      } else {
        target.getChildren().add(position, box);
      }
    });
  }

}

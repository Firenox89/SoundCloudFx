package firenox.ui;

import firenox.logger.Logger;
import firenox.model.Comment;
import firenox.model.PagedList;
import firenox.model.Track;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

/**
 * Created by firenox on 11/11/15.
 */
public class TrackPane extends VBox implements PlayerPane {
  private final Track track;
  private final int avatar_width = 200;
  private final int avatar_height = 200;
  private Logger log = Logger.getLogger(getClass().getName());
  private Image avatar;

  public TrackPane(Track track) {
    this.track = track;
  }

  @Override
  public void init() {
    if (getChildren().size() == 0)
      createDetails();
    setListener();
  }

  @Override
  public void setListener() {

  }

  private void createDetails() {

    try {
      BorderPane bPane = new BorderPane();
      avatar = new Image(track.getArtwork().getCustomSize(avatar_width, avatar_height));
      bPane.setLeft(new ImageView(avatar));
      VBox pane = new VBox();
      pane.getChildren().addAll(
          new Label(track.getUser().getUsername()),
          new Label(track.getTitle()),
          track.getWaveform().getCanvas(600, 160)
      );
      pane.setStyle("-fx-alignment: center;");
      bPane.setCenter(pane);

      getChildren().add(bPane);

      PagedList<Comment> comments = track.getComments();
      VBox commentBox = new VBox();

      comments.forEach(comment -> {
        BorderPane commentPane = new BorderPane();
        try {
          commentPane.setLeft(new ImageView(new Image(comment.getUser().getAvatar().getT67AsStream())));
        } catch (IOException e) {
          e.printStackTrace();
        }
        commentPane.setCenter(new Label(comment.getBody()));
        commentBox.getChildren().add(commentPane);
      });
      getChildren().add(commentBox);

    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}

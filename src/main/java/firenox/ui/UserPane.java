package firenox.ui;

import firenox.io.BackgroundLoader;
import firenox.logger.Logger;
import firenox.model.User;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

/**
 * Created by firenox on 10/8/15.
 */
public class UserPane extends VBox implements PlayerPane {

  private final int avatar_width = 200;
  private final int avatar_height = 200;
  private Logger log = Logger.getLogger(getClass().getName());
  private User user;
  private Image avatar;
  private String name;
  private TrackTab repostTab;
  private TrackTab tracksTab;
  private PlaylistPane playlistPane;
  private TrackTab likesTab;
  private TrackTab allTab;

  public UserPane(User user) {
    this.user = user;
  }

  @Override
  public void init() {
    if (getChildren().size() == 0)
      createDetails();
    BackgroundLoader.createTask(() -> setListener());
  }

  @Override
  public void setListener() {
    likesTab.init();
  }

  private void createDetails() {

    try {
      BorderPane bPane = new BorderPane();
      avatar = new Image(user.getAvatar().getCustomSize(avatar_width, avatar_height));
      bPane.setLeft(new ImageView(avatar));
      VBox pane = new VBox();
      pane.getChildren().addAll(
          new Label(user.getUsername()),
          new Label(user.getCity() + ", " + user.getCountry()),
          new Label("TODO: Put more infos into this space and format it")
      );
      bPane.setCenter(pane);

      getChildren().add(bPane);

      TabPane tabPane = new TabPane();
      //TODO: do that dynamic
      tabPane.setMinWidth(800);

      tabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
        UIManager.removeViewportVListeners();
        if (newValue instanceof TrackTab) {
          ((TrackTab) newValue).init();
        } else {
          playlistPane.init();
        }
      });

      getChildren().add(tabPane);

      BackgroundLoader.createTask(() ->
      {
        likesTab = new TrackTab("Likes", user.getLikes());
        Platform.runLater(() -> tabPane.getTabs().add(likesTab));
        tracksTab = new TrackTab("Tracks", user.getTracks());
        Platform.runLater(() -> tabPane.getTabs().add(tracksTab));
        if (user.getReposts() != null) {
          repostTab = new TrackTab("Reposts", user.getReposts());
          Platform.runLater(() -> tabPane.getTabs().add(repostTab));
        }
        playlistPane = new PlaylistPane(user.getPlaylists());
        Tab playlistsTab = new Tab("Playlists", playlistPane);
        playlistsTab.setClosable(false);
        Platform.runLater(() -> tabPane.getTabs().add(playlistsTab));
      });
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}

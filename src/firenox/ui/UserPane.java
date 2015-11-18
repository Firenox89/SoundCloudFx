package firenox.ui;

import firenox.logger.Logger;
import firenox.model.User;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.io.IOException;

/**
 * Created by firenox on 10/8/15.
 */
public class UserPane extends VBox {

    private Logger log = Logger.getLogger(getClass().getName());
    private final int avatar_width = 200;
    private final int avatar_height = 200;
    private User user;
    private Image avatar;
    private String name;

    public UserPane(User user) {
        this.user = user;
        createDetails();
    }

    private void createDetails() {

        try {
            BorderPane bPane = new BorderPane();
            avatar = new Image(user.getAvatar().getCustomSize(avatar_width, avatar_height));
            bPane.setLeft(new ImageView(avatar));
            VBox pane = new VBox();
            pane.getChildren().addAll(
                    new Label(user.getUsername()),
                    new Label(user.getCity()+", "+user.getCountry()),
                    new Label("TODO: Put more infos into this space and format it")

            );
            pane.setStyle("-fx-background-color: linear-gradient(from 25% 25% to 100% 100%, #dc143c, #661a33)");
            bPane.setCenter(pane);

            getChildren().add(bPane);

            TrackTab repostTab = new TrackTab("Reposts", user.getReposts());
            TrackTab tracksTab = new TrackTab("Tracks", user.getTracks());
            TrackTab likesTab = new TrackTab("Likes", user.getLikes());
            PlaylistPane playlistPane = new PlaylistPane(user.getPlaylists());

            TabPane tabPane = new TabPane();
            //TODO: do that dynamic
            tabPane.setMinWidth(800);
            Tab playlistsTab = new Tab("Playlists", playlistPane);
            playlistsTab.setClosable(false);

            tabPane.getTabs().addAll(likesTab, tracksTab, playlistsTab, repostTab);

            getChildren().add(tabPane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

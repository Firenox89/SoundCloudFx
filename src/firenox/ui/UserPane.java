package firenox.ui;

import firenox.logger.Logger;
import firenox.model.User;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.io.IOException;

/**
 * Created by firenox on 10/8/15.
 */
public class UserPane extends VBox implements PlayerPane{

    private Logger log = Logger.getLogger(getClass().getName());
    private final int avatar_width = 200;
    private final int avatar_height = 200;
    private User user;
    private Image avatar;
    private String name;
    private TrackTab repostTab;
    private TrackTab tracksTab;
    private PlaylistPane playlistPane;
    private TrackTab likesTab;

    public UserPane(User user) {
        this.user = user;
        createDetails();
    }

    @Override
    public void setListener() {
        repostTab.setListener();
        tracksTab.setListener();
        playlistPane.setListener();
        likesTab.setListener();
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
            bPane.setCenter(pane);

            getChildren().add(bPane);

            repostTab = new TrackTab("Reposts", user.getReposts());
            tracksTab = new TrackTab("Tracks", user.getTracks());
            likesTab = new TrackTab("Likes", user.getLikes());
            playlistPane = new PlaylistPane(user.getPlaylists());

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

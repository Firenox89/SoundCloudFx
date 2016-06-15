package firenox.ui;

import firenox.model.ModelManager;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

/**
 * Created by firenox on 11/18/15.
 */
public class StatsPane extends GridPane implements PlayerPane{

    public StatsPane()
    {
        getStyleClass().add("grid-pane");

        Label user = new Label("User");
        Label userNr = new Label(""+ModelManager.getUserCount());
        Label tracks = new Label("Tracks");
        Label tracksNr = new Label(""+ModelManager.getTrackCount());
        Label playlists = new Label("Playlists");
        Label playlistsNr = new Label(""+ModelManager.getPlaylistCount());

        add(user, 0, 0);
        add(userNr, 1, 0);
        add(tracks, 0, 1);
        add(tracksNr, 1, 1);
        add(playlists, 0, 2);
        add(playlistsNr, 1, 2);
    }

    @Override
    public void init() {

    }

    @Override
    public void setListener() {

    }
}

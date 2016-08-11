package firenox.ui;

import firenox.model.PagedList;
import firenox.model.PagedListEntry;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;

/**
 * Created by firenox on 6/18/16.
 */
public class Sidebar extends ScrollPane {
  ListView listView = new ListView();

  public Sidebar() {
    getChildren().add(listView);
  }


  public void setCurrentPlaylist(PagedList<PagedListEntry> currentPlaylist) {
    listView.getItems().clear();
    listView.getItems().addAll(currentPlaylist);
  }
}

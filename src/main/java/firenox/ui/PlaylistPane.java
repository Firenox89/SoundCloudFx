package firenox.ui;

import firenox.model.PagedList;
import firenox.model.PagedListEntry;
import firenox.model.PlayList;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.TilePane;

/**
 * Created by firenox on 10/28/15.
 */
public class PlaylistPane extends BorderPane implements PlayerPane {

  private final int artWidth = 200;
  private final int artHeigth = 200;
  private PagedList<PlayList> playLists;
  private TilePane tilePane = new TilePane();

  public PlaylistPane(PagedList<PlayList> playLists) {
    this.playLists = playLists;
  }

  @Override
  public void init() {
    if (getCenter() == null)
      setCenter(buildTileView());
    setListener();
    getLastParent(this).requestLayout();
  }

  @Override
  public void setListener() {
    //if viewport reaches the bottom, request new tracks
    UIManager.addViewportVListener(
        (observable, oldValue, newValue) ->
        {
          double playlistsSize = playLists.size();
          double d = (playlistsSize - 3) / playlistsSize;
          if (newValue.doubleValue() > d && !playLists.loadingComplete()) {
            playLists.loadNextEntries();
          }
        });

    UIManager.addWidthListener(
        (observable, oldValue, newValue) ->
        {
          int columns = newValue.intValue() / (artWidth + 20);
          int newHgap = (newValue.intValue() % (artWidth + 20)) / (columns + 1);
          tilePane.setPrefColumns(columns);
          tilePane.setHgap(newHgap);
          tilePane.setPadding(new Insets(5, newHgap, 5, newHgap));
          tilePane.requestLayout();
        });
  }

  private TilePane buildTileView() {
    tilePane.getStyleClass().add("style.css");
    tilePane.setPadding(new Insets(5, 5, 5, 5));
    tilePane.setVgap(10);

    int panelWidth = (int) UIManager.getScrollPaneWidth();
    int columns = panelWidth / (artWidth + 20);
    int hgap = (panelWidth % (artWidth + 20)) / (columns + 1);
    tilePane.setPrefColumns(columns);
    tilePane.setHgap(hgap);
    tilePane.setPadding(new Insets(5, hgap, 5, hgap));

    //build Track container for list view
    playLists.forEach(playList -> tilePane.getChildren().add(UIUtils.buildPlayListTile(
        playList, artWidth, artHeigth)));

    //update container on list changes
    playLists.addListener((ListChangeListener<PagedListEntry>) c -> {
      while (c.next()) {
        c.getAddedSubList().forEach(entry -> Platform.runLater(() -> {
          tilePane.getChildren().add(UIUtils.buildPlayListTile((PlayList) entry, artWidth, artHeigth));
//          tabs don't update their content automatically, silly tabs...
//          Vbox -> PlaylistPane -> TabSkin -> TabPane
          getLastParent(tilePane).requestLayout();
        }));
      }
    });

    int hfit = (int) (UIManager.getScrollPaneHeight() / (artHeigth + 20) * columns);
    while (playLists.size() < hfit && !playLists.loadingComplete()) {
      playLists.loadNextEntries();
    }
    return tilePane;
  }

  private Parent getLastParent(Parent parent) {
    if (parent.getParent() != null)
      parent = getLastParent(parent.getParent());
    return parent;
  }
}

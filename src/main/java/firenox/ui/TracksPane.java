package firenox.ui;

import firenox.logger.Logger;
import firenox.model.PagedList;
import firenox.model.PagedListEntry;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.TilePane;

/**
 * Created by firenox on 10/8/15.
 */
public class TracksPane extends BorderPane implements PlayerPane {

  private final int waveWidth = 650;
  private final int waveHeigth = 70;
  private final int artWidth = 100;
  private final int artHeigth = 100;
  private final ListContainerDimensions dimensions = new ListContainerDimensions(waveWidth,
      waveHeigth,
      artWidth,
      artHeigth);
  private Logger log = Logger.getLogger(getClass().getName());
  private DisplayMode defaultMode = DisplayMode.List;
  private PagedList<PagedListEntry> trackList;
  private TilePane tilePane;

  public TracksPane(PagedList<PagedListEntry> trackList) {
    this.trackList = trackList;
  }

  @Override
  public void init() {
    if (getCenter() == null) {
      if (defaultMode == DisplayMode.List) {
        setCenter(buildListView());
      } else {
        setCenter(buildTileView());
      }

      FlowPane flowPane = new FlowPane();
      Button listButton = new Button("List");
      listButton.setOnAction(event -> setCenter(buildListView()));
      Button tileButton = new Button("Tile");
      tileButton.setOnAction(event -> setCenter(buildTileView()));

      flowPane.getChildren().add(listButton);
      flowPane.getChildren().add(tileButton);

      setTop(flowPane);

      requestLayout();
    }
    setListener();
  }

  @Override
  public void setListener() {
    //if viewport reaches the bottom, request new tracks
    UIManager.addViewportVListener(
        (observable, oldValue, newValue) ->
        {
          double trackListSize = trackList.size();
          double d = (trackListSize - 3) / trackListSize;
          if (newValue.doubleValue() > d) {
            trackList.loadNextEntries();
          }
        });

    if (tilePane != null) {
      UIManager.addWidthListener(
          (observable, oldValue, newValue) -> {
            int width = artWidth * 2;
            int columns = newValue.intValue() / (width + 20);
            int hgap = (newValue.intValue() % (width + 20)) / (columns + 1);
            tilePane.setPrefColumns(columns);
            tilePane.setHgap(hgap);
            tilePane.setPadding(new Insets(5, hgap, 5, hgap));
            tilePane.requestLayout();
          });
    }
  }

  private Node buildListView() {
    return new TrackListView(trackList, dimensions);
  }

  private TilePane buildTileView() {
    tilePane = new TilePane();
    tilePane.setPadding(new Insets(5, 5, 5, 5));
    tilePane.setHgap(10);
    tilePane.setVgap(10);

    int width = artWidth * 2;
    int heigth = artHeigth * 2;

    int panelWidth = (int) UIManager.getScrollPaneWidth();
    int colums = panelWidth / (width + 20);
    int hgap = (panelWidth % (width + 20)) / (colums + 1);
    tilePane.setPrefColumns(colums);
    tilePane.setHgap(hgap);
    tilePane.setPadding(new Insets(5, hgap, 5, hgap));


    //build Track container for list view
    trackList.forEach(t -> tilePane.getChildren().add(UIUtils.buildTrackTile(
        t, trackList, width, heigth)));

    //update container on list changes
//    trackList.addNewEntriesLoadedListener(list ->
//        Platform.runLater(() ->
//            list.forEach(t ->
//                tilePane.getChildren().add(
//                    UIUtils.buildTrackTile((Track) t, trackList, width, heigth)))));
    return tilePane;
  }
}

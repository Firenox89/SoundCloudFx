package firenox.ui;

import firenox.logger.Logger;
import firenox.model.PagedList;
import firenox.model.PagedListEntry;
import javafx.application.Platform;
import javafx.scene.control.Tab;

/**
 * Created by firenox on 11/10/15.
 */
public class TrackTab extends Tab {

  private final int waveWidth = 650;
  private final int waveHeigth = 70;
  private final int artWidth = 100;
  private final int artHeigth = 100;
  private final ListContainerDimensions dimensions =
      new ListContainerDimensions(waveWidth, waveHeigth, artWidth, artHeigth);
  private Logger log = Logger.getLogger(getClass().getName());
  private PagedList<PagedListEntry> trackList;

  public TrackTab(String name, PagedList<PagedListEntry> trackList) {
    super(name);
    this.trackList = trackList;
    setClosable(false);
  }

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
  }

  public void init() {
    if (getContent() == null) {
      Platform.runLater(() -> setContent(new TrackListView(trackList, dimensions)));
    }
    setListener();
  }
}

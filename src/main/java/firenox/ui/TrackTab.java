package firenox.ui;

import firenox.logger.Logger;
import firenox.model.PagedListEntry;
import firenox.model.PagedList;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.layout.VBox;

/**
 * Created by firenox on 11/10/15.
 */
public class TrackTab extends Tab {

    private Logger log = Logger.getLogger(getClass().getName());
    private final int waveWidth = 650;
    private final int waveHeigth = 70;
    private final int artWidth = 100;
    private final int artHeigth = 100;
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
                    double d = (trackListSize-3)/trackListSize;
                    if (newValue.doubleValue() > d) {
                        trackList.loadNextEntries();
                    }
                });
    }

    private Node buildListView() {
        VBox vbox = new VBox();

        //update container on list changes
        trackList.addNewEntriesLoadedListener(list ->
                Platform.runLater(() -> {
                    list.forEach(t ->
                            vbox.getChildren().add(UIUtils.buildTrackContainer(
                                    (PagedListEntry) t,
                                    trackList,
                                    waveWidth,
                                    waveHeigth,
                                    artWidth,
                                    artHeigth)));
                    //tabs don't update their content automatically, silly tabs...
                    if (getTabPane() != null) {
                        getTabPane().requestLayout();
                    }
                }));

        //build Track container for list view
        trackList.forEach(t -> vbox.getChildren().add(UIUtils.buildTrackContainer(
                t, trackList, waveWidth, waveHeigth, artWidth, artHeigth)));
        trackList.addEntryAddAt0Listener(() -> vbox.getChildren().add(0, UIUtils.buildTrackContainer(
                trackList.get(0),
                trackList,
                waveWidth,
                waveHeigth,
                artWidth,
                artHeigth)));
        if (getTabPane() != null) {
            getTabPane().requestLayout();
        }
        return vbox;
    }

    public void init() {
        if (getContent() == null)
        {
            Platform.runLater(() -> setContent(buildListView()));
        }
        setListener();
    }
}

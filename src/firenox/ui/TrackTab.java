package firenox.ui;

import firenox.logger.Logger;
import firenox.model.PagedList;
import firenox.model.Track;
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
    private final String name;

    public TrackTab(String name, PagedList<Track> favList) {
        super(name);
        this.name = name;
        setClosable(false);
        setContent(buildListView(favList));

        //if viewport reaches the bottom, request new tracks
        UIManager.getController().getMainScrollPane().vvalueProperty().addListener(
                (observable, oldValue, newValue) ->
                {
                    if (newValue.doubleValue() > 0.98) {
                        favList.loadNextEntries();
                    }
                });
    }

    private Node buildListView(PagedList<Track> trackList) {
        VBox vbox = new VBox();

        //update container on list changes
        trackList.setNewEntriesLoadedListener(list ->
                Platform.runLater(() -> {
                    list.forEach(t ->
                            vbox.getChildren().add(UIUtils.buildTrackContainer(
                                    (Track) t, trackList, waveWidth, waveHeigth, artWidth, artHeigth)));
                    //tabs don't update their content automatically, silly tabs...
                    if (getTabPane() != null) {
                        getTabPane().requestLayout();
                    }
                }));

        //build Track container for list view
        trackList.forEach(t -> vbox.getChildren().add(UIUtils.buildTrackContainer(
                t, trackList, waveWidth, waveHeigth, artWidth, artHeigth)));
        if (getTabPane() != null) {
            getTabPane().requestLayout();
        }
        return vbox;
    }
}

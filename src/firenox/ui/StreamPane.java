package firenox.ui;

import firenox.model.ModelManager;
import firenox.model.PagedList;
import firenox.model.Track;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 * Created by firenox on 10/19/15.
 */
public class StreamPane extends Pane{

    private final int waveWidth = 650;
    private final int waveHeigth = 80;
    private final int artWidth = 100;
    private final int artHeigth = 100;

    public StreamPane() {
        VBox vbox = new VBox();
        PagedList<Track> streamList = ModelManager.getStream();

        getChildren().add(vbox);

        //build Track container for list view
        streamList.forEach(t -> vbox.getChildren().add(UIUtils.buildTrackContainer(
                t, streamList, waveWidth, waveHeigth, artWidth, artHeigth)));

        //update container on list changes
        streamList.setNewEntriesLoadedListener(() ->
                streamList.getLastLoadedEntries().forEach(t -> vbox.getChildren().add(UIUtils.buildTrackContainer(
                        t, streamList, waveWidth, waveHeigth, artWidth, artHeigth))));

        //if viewport reaches the bottom, request new tracks
        UIManager.getController().getMainScrollPane().vvalueProperty().addListener(
                (observable, oldValue, newValue) ->
                {
                    if (newValue.doubleValue() > 0.98) {
                        streamList.loadNextEntries();
                    }
                });
    }
}

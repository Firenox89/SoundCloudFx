package firenox.ui;

import firenox.logger.Logger;
import firenox.model.PagedList;
import firenox.model.Track;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;

/**
 * Created by firenox on 10/8/15.
 */
public class TracksPane extends BorderPane {

    private Logger log = Logger.getLogger(getClass().getName());
    private final int waveWidth = 650;
    private final int waveHeigth = 80;
    private final int artWidth = 100;
    private final int artHeigth = 100;
    private DisplayMode defaultMode = DisplayMode.List;

    public TracksPane(PagedList<Track> favList) {
        if (defaultMode == DisplayMode.List) {
            setCenter(buildListView(favList));
        } else {
            setCenter(buildTileView(favList));
        }

        FlowPane flowPane = new FlowPane();
        Button listButton = new Button("List");
        listButton.setOnAction(event -> setCenter(buildListView(favList)));
        Button tileButton = new Button("Tile");
        tileButton.setOnAction(event -> setCenter(buildTileView(favList)));

        flowPane.getChildren().add(listButton);
        flowPane.getChildren().add(tileButton);

        setTop(flowPane);

        //if viewport reaches the bottom, request new tracks
        UIManager.getController().getMainScrollPane().vvalueProperty().addListener(
                (observable, oldValue, newValue) ->
                {
                    if (newValue.doubleValue() > 0.98) {
                        favList.loadNextEntries();
                    }
                });
        requestLayout();
    }

    private Node buildListView(PagedList<Track> favList) {
        System.out.println("TracksPane.buildListView");
        VBox vbox = new VBox();

        //build Track container for list view
        favList.forEach(t -> vbox.getChildren().add(UIUtils.buildTrackContainer(
                t, favList, waveWidth, waveHeigth, artWidth, artHeigth)));

        //update container on list changes
        favList.setNewEntriesLoadedListener(list ->
                Platform.runLater(() ->
                        list.forEach(t ->
                                vbox.getChildren().add(UIUtils.buildTrackContainer(
                                        (Track) t, favList, waveWidth, waveHeigth, artWidth, artHeigth)))));
        return vbox;
    }

    private TilePane buildTileView(PagedList<Track> favList) {
        TilePane tilePane = new TilePane();
        tilePane.setPadding(new Insets(5, 5, 5, 5));
        tilePane.setHgap(10);
        tilePane.setVgap(10);

        int width = artWidth * 2;
        int heigth = artHeigth * 2;

        int panelWidth = (int) UIManager.getController().getMainScrollPane().getWidth();
        int colums = panelWidth / (width + 20);
        int hgap = (panelWidth % (width + 20)) / (colums + 1);
        tilePane.setPrefColumns(colums);
        tilePane.setHgap(hgap);
        tilePane.setPadding(new Insets(5, hgap, 5, hgap));

        UIManager.getController().getMainScrollPane().widthProperty().addListener(
                (observable, oldValue, newValue) -> {
                    int newColums = newValue.intValue() / (width + 20);
                    int newHgap = (newValue.intValue() % (width + 20)) / (colums + 1);
                    tilePane.setPrefColumns(newColums);
                    tilePane.setHgap(newHgap);
                    tilePane.setPadding(new Insets(5, newHgap, 5, newHgap));
                    tilePane.requestLayout();
                });

        //build Track container for list view
        favList.forEach(t -> tilePane.getChildren().add(UIUtils.buildTrackTile(
                t, favList, width, heigth)));

        //update container on list changes
        favList.setNewEntriesLoadedListener(list ->
                Platform.runLater(() ->
                        list.forEach(t ->
                                tilePane.getChildren().add(
                                        UIUtils.buildTrackTile((Track) t, favList, width, heigth)))));
        return tilePane;
    }
}

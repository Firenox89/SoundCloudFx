package firenox.ui;

import firenox.model.PagedList;
import firenox.model.PlayList;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.TilePane;

/**
 * Created by firenox on 10/28/15.
 */
public class PlaylistPane extends BorderPane {

    private final int artWidth = 100;
    private final int artHeigth = 100;

    public PlaylistPane(PagedList<PlayList> playLists) {
        setCenter(buildTileView(playLists));

        //if viewport reaches the bottom, request new tracks
        UIManager.getController().getMainScrollPane().vvalueProperty().addListener(
                (observable, oldValue, newValue) ->
                {
                    if (newValue.doubleValue() > 0.98) {
                        playLists.loadNextEntries();
                    }
                });
    }

    private TilePane buildTileView(PagedList<PlayList> playLists) {
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
                (observable, oldValue, newValue) ->
                {
                    int newColums = newValue.intValue() / (width + 20);
                    int newHgap = (newValue.intValue() % (width + 20)) / (colums + 1);
                    tilePane.setPrefColumns(newColums);
                    tilePane.setHgap(newHgap);
                    tilePane.setPadding(new Insets(5, newHgap, 5, newHgap));
                    tilePane.requestLayout();
                });

        //build Track container for list view
        playLists.forEach(playList -> tilePane.getChildren().add(UIUtils.buildPlayListTile(
                playList, width, heigth)));

        //update container on list changes
        playLists.setNewEntriesLoadedListener((list) ->
                Platform.runLater(() -> {
                    list.forEach(playList ->
                            tilePane.getChildren().add(
                                    UIUtils.buildPlayListTile((PlayList) playList, width, heigth)));
                    //tabs don't update their content automatically, silly tabs...
                    //Vbox -> TracksPane -> TabSkin -> TabPane
                    tilePane.getParent().getParent().getParent().requestLayout();
                }));
        return tilePane;
    }
}

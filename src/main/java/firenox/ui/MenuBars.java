package firenox.ui;

import firenox.media.AudioManager;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

/**
 * Created by firenox on 12/30/15.
 */
public class MenuBars extends BorderPane {


  public ScrollPane scrollPane = new ScrollPane();
  Slider progressSlider = new Slider();
  Slider volumeSlider = new Slider();
  ImageView titleView = new ImageView();
  Label artist = new Label("");
  Label track = new Label("");
  Label trackTime = new Label("00:00");
  Label currentTime = new Label("00:00");
  Button playButton = new Button();

  public MenuBars() {
    setTop(buildTopPane());
    setCenter(scrollPane);
    scrollPane.getStylesheets().add("style.css");
    setBottom(buildBottomPane());
  }

  public ToolBar buildTopPane() {
    ToolBar toolBar = new ToolBar();

    toolBar.getStylesheets().add("menuPanel.css");
    //TODO: use css
    toolBar.setStyle("-fx-background-color: #212121;");

    Button backButton = new Button("Back");
    backButton.setOnMouseClicked(event -> UIManager.back());

    Button profileButton = new Button("Profile");
    profileButton.setOnMouseClicked(event -> UIManager.showProfile());

    Button streamButton = new Button("Stream");
    streamButton.setOnMouseClicked(event -> UIManager.showMyStream());

    Button playlistButton = new Button("Playlists");
    playlistButton.setOnMouseClicked(event -> UIManager.showMyPlaylists());

    Button likesButton = new Button("Likes");
    likesButton.setOnMouseClicked(event -> UIManager.showMyLikes());

    Button statisticsButton = new Button("Statistics");
    statisticsButton.setOnMouseClicked(event -> UIManager.showStats());

    Button visualizerButton = new Button("Visualizer");
    visualizerButton.setOnMouseClicked(event -> UIManager.showVisualizer());

    int height = 25;
    toolBar.setPrefHeight(height);
    toolBar.setMinHeight(height);
    toolBar.setMaxHeight(height);
    toolBar.getItems().addAll(backButton,
        profileButton,
        streamButton,
        playlistButton,
        likesButton,
        statisticsButton,
        visualizerButton);

    return toolBar;
  }


  public FlowPane buildBottomPane() {
    FlowPane bottom = new FlowPane();

    bottom.getStylesheets().add("mediaButtons.css");
    //TODO: use css
    bottom.setStyle("-fx-background-color: #212121;");
    bottom.setAlignment(Pos.CENTER);

    Button previousButton = new Button();
    previousButton.setGraphic(UIUtils.SVGPaths.previous.get());
    previousButton.setOnMouseClicked(event -> AudioManager.getPlayerFx().previous());

    Button nextButton = new Button();
    nextButton.setGraphic(UIUtils.SVGPaths.next.get());
    nextButton.setOnMouseClicked(event -> AudioManager.getPlayerFx().next());

    playButton.setGraphic(UIUtils.SVGPaths.play.get());
    playButton.setOnMouseClicked(event -> AudioManager.getPlayerFx().play());

    Button repeatButton = new Button();
    repeatButton.setGraphic(UIUtils.SVGPaths.repostPath.get());
    repeatButton.setOnMouseClicked(event -> AudioManager.getPlayerFx().toggleRepeat());

    Button shuffleButton = new Button();
    shuffleButton.setGraphic(UIUtils.SVGPaths.shuffle.get());
    shuffleButton.setOnMouseClicked(event -> AudioManager.getPlayerFx().toggleShuffle());

    volumeSlider.setMax(1);
    volumeSlider.setValue(0.2);
    MenuButton volumeButton = new MenuButton();
    volumeButton.setGraphic(UIUtils.SVGPaths.volumeSpeaker.get());
    volumeButton.getItems().add(new CustomMenuItem(volumeSlider));


    VBox artistTrackBox = new VBox();
    artistTrackBox.getChildren().addAll(artist, track);

    bottom.getChildren().addAll(previousButton,
        playButton,
        nextButton,
        repeatButton,
        shuffleButton,
        progressSlider,
        volumeButton,
        titleView,
        artistTrackBox);

    return bottom;
  }

  public Slider getVolumeSlider() {
    return volumeSlider;
  }

  public ScrollPane getMainScrollPane() {
    return scrollPane;
  }

  public ImageView getArtWork() {
    return titleView;
  }

  public Label getTitleLabel() {
    return track;
  }

  public Label getUserLabel() {
    return artist;
  }

  public Slider getProgressSlider() {
    return progressSlider;
  }

  public Label getTrackTime() {
    return trackTime;
  }

  public Label getPlaybackTime() {
    return currentTime;
  }

  public Button getPlayButton() {
    return playButton;
  }
}

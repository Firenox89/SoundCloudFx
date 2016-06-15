package firenox.ui;

import firenox.media.AudioManager;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.media.AudioSpectrumListener;
import javafx.scene.paint.Color;

/**
 * Created by firenox on 3/6/16.
 */
public class VisualizerPane extends Pane implements PlayerPane, AudioSpectrumListener {
  Canvas canvas;
  double width;
  int height;
  private int spacing = 1;
  private double stripWidth = 3;
  private GraphicsContext gc;

  @Override
  public void spectrumDataUpdate(double timestamp,
                                 double duration,
                                 float[] magnitudes,
                                 float[] phases) {
    gc.setFill(javafx.scene.paint.Color.BLACK);
    gc.fillRect(0, 0, width, height);

    Platform.runLater(() -> {
      for (int i = 0; i < magnitudes.length; i++) {

        double x = spacing * i + (stripWidth * i - 1) + 25;
        double y = magnitudes[i] + 60;
        double h = height - y * 15 > 0 ? height - 1 - y * 15 : 0;

        gc.setFill(Color.hsb(i * 2, 1, 1));
        gc.fillRect(x, h, stripWidth, height - 1);
      }
    });
  }

  @Override
  public void init() {

    width = UIManager.getScrollPaneWidth();
    height = 400;
    canvas = new Canvas(width, height);
    getChildren().add(canvas);
    gc = canvas.getGraphicsContext2D();

    gc.setFill(javafx.scene.paint.Color.BLACK);
    gc.fillRect(0, 0, width, height);

    setListener();
  }

  @Override
  public void setListener() {
    AudioManager.getPlayerFx().setAudioSpectrumListener(this);
  }
}

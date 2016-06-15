package firenox.ui;

import firenox.io.BackgroundLoader;
import firenox.logger.Logger;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by firenox on 10/9/15.
 */
public class WaveRenderer {

    private final static int stripWidth = 2;
    private final static int spacing = 1;

    private int stripNr;
    private BufferedImage original;
    private int oldWidth;
    private int oldHeight;
    private double newWidth;
    private double newHeight;
    private Canvas canvasRender;
    private Logger log = Logger.getLogger(getClass().getName());

    public WaveRenderer(InputStream is, int width, int height) {
        try {
            BufferedImage image = ImageIO.read(is);
            original = image;
            oldHeight = image.getHeight();
            oldWidth = image.getWidth();
            newHeight = height;
            newWidth = width;

            stripNr = (int) (newWidth / (spacing + stripWidth));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static WaveRenderer init(InputStream is, int width, int height) throws IOException {
        return new WaveRenderer(is, width, height);
    }

    public BufferedImage renderWave() {
        BufferedImage result = new BufferedImage((int) newWidth, (int) newHeight, BufferedImage.TYPE_INT_ARGB);
        render(result);

        return result;
    }


    public WritableImage renderWaveFx() {
        return SwingFXUtils.toFXImage(renderWave(), null);
    }

    public File renderWaveToFile(String path) throws IOException {
        BufferedImage bufferedImage = renderWave();
        File file = new File(path);
        ImageIO.write(bufferedImage, "png", file);
        return file;
    }

    private int getFirstNonOpaquePixel(int x) {
        double yinc = oldHeight / newHeight / 2;
        double xinc = oldWidth / newWidth;
        double yc = 0;
        double xc = x * xinc;
        int y = 0;

        int value = original.getRGB((int) xc, (int) yc);

        while (value != 0 && (yc + yinc) < original.getHeight()) {
            yc += yinc;
            y++;
            value = original.getRGB((int) xc, (int) yc);
        }
        return y;
    }

    private void render(BufferedImage image) {

        Graphics g = image.getGraphics();
        g.setColor(new Color(0xA1A1A1));

        for (int i = 1; i <= stripNr; i++) {
            int x = spacing * i + (stripWidth * i - 1);
            int y = getFirstNonOpaquePixel(x);
            g.fillRect(x, y, stripWidth, (int) newHeight);
        }
    }

    public Canvas renderToFxCanvas() {
        Canvas canvas = new Canvas(newWidth, newHeight);

//        BackgroundLoader.addTask(() ->
//        {
            GraphicsContext gc = canvas.getGraphicsContext2D();

            for (int i = 1; i <= stripNr; i++) {
                double x = spacing * i + (stripWidth * i - 1);
                double y = getFirstNonOpaquePixel((int) x);
                gc.setFill(javafx.scene.paint.Color.LIGHTGRAY);
                gc.fillRect(x, y, stripWidth, newHeight * 0.7 - y);

                gc.setFill(javafx.scene.paint.Color.GRAY);
                gc.fillRect(x, newHeight * 0.7 + 1, stripWidth, (newHeight * 0.3) - (y * 0.3));
            }

//        });
        canvasRender = canvas;
        return canvas;
    }

    public void renderProgress(double progress) {
        GraphicsContext gc = canvasRender.getGraphicsContext2D();

        for (int i = 1; i <= stripNr * progress; i++) {
            double x = spacing * i + (stripWidth * i - 1);
            double y = getFirstNonOpaquePixel((int) x);
            gc.setFill(javafx.scene.paint.Color.LIGHTCORAL.brighter().brighter());
            gc.fillRect(x, y, stripWidth, newHeight * 0.7 - y);

            gc.setFill(javafx.scene.paint.Color.CORAL);
            gc.fillRect(x, newHeight * 0.7 + 1, stripWidth, (newHeight * 0.3) - (y * 0.3));
        }
    }

    public void resetProgres() {
        GraphicsContext gc = canvasRender.getGraphicsContext2D();

        for (int i = 1; i <= stripNr; i++) {
            double x = spacing * i + (stripWidth * i - 1);
            double y = getFirstNonOpaquePixel((int) x);
            gc.setFill(javafx.scene.paint.Color.LIGHTGRAY);
            gc.fillRect(x, y, stripWidth, newHeight * 0.7 - y);

            gc.setFill(javafx.scene.paint.Color.GRAY);
            gc.fillRect(x, newHeight * 0.7 + 1, stripWidth, (newHeight * 0.3) - (y * 0.3));
        }
    }
}

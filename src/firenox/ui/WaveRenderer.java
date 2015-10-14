package firenox.ui;

import javafx.embed.swing.SwingFXUtils;
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

    static int stripWidth = 2;
    static int spacing = 1;
    static BufferedImage original;
    static int oldWidth;
    static int oldHeight;
    static int newWidth;
    static int newHeight;

    public static BufferedImage renderWave(InputStream is, int width, int height) throws IOException {
        BufferedImage image = ImageIO.read(is);
        original = image;
        oldHeight = image.getHeight();
        oldWidth = image.getWidth();
        newHeight = height;
        newWidth = width;
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        render(result);

        return result;
    }

    public static WritableImage renderWaveFx(InputStream is, int width, int height) throws IOException {
        return SwingFXUtils.toFXImage(renderWave(is, width, height), null);
    }

    public static File renderWaveToFile(InputStream is, int width, int height, String path) throws IOException {
        BufferedImage bufferedImage = renderWave(is, width, height);
        File file = new File(path);
        ImageIO.write(bufferedImage, "png", file);
        return file;
    }

    private static int getFirstNonOpaquePixel(int x) {
        double yinc = oldHeight / newHeight / 2;
        double xinc = oldWidth / newWidth;
        double yc = 0;
        double xc = x * xinc;
        int y = 0;
        int value = original.getRGB((int) xc, (int) yc);

        while (value != 0) {
            yc += yinc;
            y++;
            value = original.getRGB((int) xc, (int) yc);
        }
        return y;
    }

    private static void render(BufferedImage image) {
        int stripNr = newWidth / (spacing + stripWidth);

        Graphics g = image.getGraphics();
        g.setColor(new Color(0xA1A1A1));

        for (int i = 1; i <= stripNr; i++) {
            int x = spacing * i + (stripWidth * i - 1);
            int y = getFirstNonOpaquePixel(x);
            g.fillRect(x, y, stripWidth, newHeight);
        }
    }
}

package firenox.model;

import firenox.logger.Logger;
import firenox.ui.WaveRenderer;
import javafx.scene.canvas.Canvas;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * Created by firenox on 10/11/15.
 */
public class WaveForm extends CacheableImage {
    private static Logger log = new Logger(WaveForm.class.getName());
    private static Map<String, File> cacheIndex = CacheableImage.indexCache(ModelManager.WAVE_CACHE_PATH);
    private final String orginalWaveUrl;
    private WaveRenderer waveRender;

    public WaveForm(String url) {
        orginalWaveUrl = url;
    }

    public Canvas getCanvas(int width, int height)
    {
        try {
        if (waveRender == null)
        {
            waveRender = WaveRenderer.init(getResourceAsStream(orginalWaveUrl, cacheIndex), width, height);
        }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return waveRender.renderToFxCanvas();
    }

    public void progressAnimation(double progress)
    {
        waveRender.renderProgress(progress);
    }

    @Override
    protected String getFallbackUrl() {
        return null;
    }

    @Override
    protected boolean useFallback() {
        return false;
    }

    @Override
    protected String getCachePath() {
        return ModelManager.WAVE_CACHE_PATH;
    }
}

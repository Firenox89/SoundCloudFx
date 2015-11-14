package firenox.model;

import firenox.logger.Logger;
import firenox.ui.WaveRenderer;
import javafx.scene.canvas.Canvas;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by firenox on 10/11/15.
 */
public class WaveForm extends CacheableImage {
    private static Logger log = new Logger(WaveForm.class.getName());
    private static Map<String, File> cacheIndex = CacheableImage.indexCache(ModelManager.WAVE_CACHE_PATH);
    private final String orginalWaveUrl;
    private HashMap<String, WaveRenderer> rendererList = new HashMap<>();

    public WaveForm(String url) {
        orginalWaveUrl = url;
    }

    public Canvas getCanvas(int width, int height) {
        String size = width + "x" + height;
        WaveRenderer waveRender = null;
        try {
            if (rendererList.get(size) == null) {
                waveRender = WaveRenderer.init(getResourceAsStream(orginalWaveUrl, cacheIndex), width, height);
                rendererList.put(size, waveRender);
            } else {
                waveRender = rendererList.get(size);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return waveRender.renderToFxCanvas();
    }

    public void resetProgress()
    {
        //TODO: fill me
    }
    public void progressAnimation(double progress) {
        rendererList.forEach((s, waveRenderer) -> waveRenderer.renderProgress(progress));
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

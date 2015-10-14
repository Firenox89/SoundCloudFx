package firenox.model;

import firenox.logger.Logger;
import firenox.ui.WaveRenderer;

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

    public WaveForm(String url) {
        orginalWaveUrl = url;
    }

    public InputStream getRenderedWaveAsStream(int width, int height) throws IOException {
        String filePath = getCachePath() + File.separator + getNameFromUrl(orginalWaveUrl);
        filePath = filePath.substring(0, filePath.length() - 4) + "R" + width + "x" + height + ".png";
        File file = WaveRenderer.renderWaveToFile(getResourceAsStream(orginalWaveUrl, cacheIndex), width, height, filePath);
        return new FileInputStream(file);
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

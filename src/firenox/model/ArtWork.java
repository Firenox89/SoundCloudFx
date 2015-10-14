package firenox.model;

import firenox.logger.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * Created by firenox on 10/10/15.
 */
public class ArtWork extends CacheableImage {
    protected static Map<String, File> cacheIndex = CacheableImage.indexCache(ModelManager.ARTWORKS_CACHE_PATH);
    private static Logger log = new Logger(ArtWork.class.getName());
    private final String urlT500x500;
    private final String urlCrop;
    private final String urlT300x300;
    private final String urlLarge;
    private final String urlT67x67;
    private final String urlFallBack = "/home/firenox/IdeaProjects/SoundCloudPlayer/src/firenox/ui/shinbuAva.png";
    private final boolean useFallback;

    public ArtWork(String url) {
        if (url.equals("null")) {
            urlLarge = urlFallBack;
            urlT500x500 = urlFallBack;
            urlCrop = urlFallBack;
            urlT300x300 = urlFallBack;
            urlT67x67 = urlFallBack;
            useFallback = true;
        } else {
            urlLarge = url;
            urlT500x500 = url.replace("large", "t500x500");
            urlCrop = url.replace("large", "crop");
            urlT300x300 = url.replace("large", "t300x300");
            urlT67x67 = url.replace("large", "t67x67");
            useFallback = false;
        }
    }

    public InputStream getLargeAsStream() throws IOException {
        return getResourceAsStream(urlLarge, cacheIndex);
    }

    @Override
    protected String getFallbackUrl() {
        return urlFallBack;
    }

    @Override
    protected boolean useFallback() {
        return useFallback;
    }

    @Override
    protected String getCachePath() {
        return ModelManager.ARTWORKS_CACHE_PATH;
    }
}

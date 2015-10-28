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
    private final String urlT500;
    private final String urlCrop;
    private final String urlT300;
    private final String urlLarge;
    private final String urlT67;
    private final String urlFallBack = "/home/firenox/IdeaProjects/SoundCloudPlayer/src/firenox/ui/shinbuAva.png";
    private final boolean useFallback;

    public ArtWork(String url) {
        if (url.equals("null")) {
            urlLarge = urlFallBack;
            urlT500 = urlFallBack;
            urlCrop = urlFallBack;
            urlT300 = urlFallBack;
            urlT67 = urlFallBack;
            useFallback = true;
        } else {
            urlLarge = url;
            urlT500 = url.replace("large", "t500x500");
            urlCrop = url.replace("large", "crop");
            urlT300 = url.replace("large", "t300x300");
            urlT67 = url.replace("large", "t67x67");
            useFallback = false;
        }
    }

    public InputStream getT500AsStream() throws IOException {
        return getResourceAsStream(urlT500, cacheIndex);
    }

    public InputStream getCropAsStream() throws IOException {
        return getResourceAsStream(urlCrop, cacheIndex);
    }

    public InputStream getT300AsStream() throws IOException {
        return getResourceAsStream(urlT300, cacheIndex);
    }

    public InputStream getLargeAsStream() throws IOException {
        return getResourceAsStream(urlLarge, cacheIndex);
    }

    public InputStream getT67AsStream() throws IOException {
        return getResourceAsStream(urlT67, cacheIndex);
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

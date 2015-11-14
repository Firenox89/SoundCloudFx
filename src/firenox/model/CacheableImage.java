package firenox.model;

import firenox.io.RequestManager;
import firenox.logger.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by firenox on 10/10/15.
 */
public abstract class CacheableImage {

    private static Logger log = new Logger(CacheableImage.class.getName());

    protected static Map indexCache(String path) {
        File cacheDir = new File(path);
        Map index = null;
        log.i("ModelManager.ARTWORKS_CACHE_PATH = " + path);
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }
        index = Arrays.stream(cacheDir.listFiles())
                .filter(File::isFile)
                .collect(Collectors.toMap(File::getName, Function.<File>identity()));
        return index;
    }

    protected File cacheFile(String url) throws IOException {
        File fileToCache = new File(getCachePath() + File.separator + getNameFromUrl(url));
        InputStream is;
        if (useFallback()) {
            is = new FileInputStream(new File(getFallbackUrl()));
        } else {
            is = RequestManager.getResource(url);
        }
//        FileOutputStream fos = new FileOutputStream(fileToCache);

        if (is == null)
            is = new FileInputStream(new File(getFallbackUrl()));
        //read in image to check for image corruption
        BufferedImage bi = ImageIO.read(is);

        String imageType;
        if (url.endsWith(".png")) {
            imageType = "png";
        } else if (url.endsWith(".jpg")) {
            imageType = "jpg";
        } else {
            throw new IllegalStateException("Unsupported image type. url = " + url);
        }

        ImageIO.write(bi, imageType, fileToCache);

//        byte[] buffer = new byte[1024];
//        int read;
//        while ((read = is.read(buffer)) != -1) {
//            fos.write(buffer);
//            Traffic.addImageTraffic(read);
//        }

        return fileToCache;
    }

    protected String getNameFromUrl(String url) {
        return url.substring(url.lastIndexOf("/") + 1, url.length());
    }

    public InputStream getResourceAsStream(String url, Map<String, File> cacheIndex) throws IOException {
        File file = cacheIndex.get(getNameFromUrl(url));
        if (file == null) {
            file = cacheFile(url);
            cacheIndex.put(getNameFromUrl(url), file);
        }
        return new FileInputStream(file);
    }

    protected abstract String getFallbackUrl();

    protected abstract boolean useFallback();

    protected abstract String getCachePath();
}

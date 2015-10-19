package firenox.model;

import firenox.io.LogInHandler;
import firenox.logger.Logger;
import firenox.statistics.Traffic;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
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
        if (cacheDir != null && cacheDir.exists() && cacheDir.isDirectory()) {
            index = Arrays.stream(cacheDir.listFiles())
                    .filter(File::isFile)
                    .collect(Collectors.toMap(File::getName, Function.<File>identity()));
        }
        return index;
    }

    protected File cacheFile(String url) throws IOException {
        File fileToCache = new File(getCachePath() + File.separator + getNameFromUrl(url));
        InputStream is;
        if (useFallback()) {
            is = new FileInputStream(new File(getFallbackUrl()));
        } else {
            is = LogInHandler.getResource(url);
        }
        FileOutputStream fos = new FileOutputStream(fileToCache);

        //read in image to check for image corruption
        BufferedImage bi = ImageIO.read(is);

        ImageIO.write(bi, "png", fileToCache);

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

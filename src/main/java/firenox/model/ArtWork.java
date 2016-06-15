package firenox.model;

import com.mortennobel.imagescaling.ResampleOp;
import firenox.logger.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
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
  private final String urlFallback;
  private final boolean useFallback;

  public ArtWork(String url) {
    urlFallback = getClass().getResource("/shinbuAva.png").getFile();
    if (url.equals("null")) {
      urlLarge = urlFallback;
      urlT500 = urlFallback;
      urlCrop = urlFallback;
      urlT300 = urlFallback;
      urlT67 = urlFallback;
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

  public InputStream getCustomSize(int width, int heigth) throws IOException {
    String url = urlLarge.replace("large", "t" + width + "x" + heigth);
    InputStream out;

    if (cacheIndex.get(url) == null) {
      InputStream is = null;
      if (width <= 100) {
        is = getLargeAsStream();
      } else if (width <= 300) {
        is = getT300AsStream();
      } else {
        is = getT500AsStream();
      }

      BufferedImage img = ImageIO.read(is);
      ResampleOp resampleOp = new ResampleOp(width, heigth);
      img = resampleOp.filter(img, null);
      File fileToCache = new File(getCachePath() + File.separator + getNameFromUrl(url));
      ImageIO.write(img, "jpg", fileToCache);
      cacheIndex.put(getNameFromUrl(url), fileToCache);
    }
    out = new FileInputStream(cacheIndex.get(getNameFromUrl(url)));

    return out;
  }

  @Override
  protected String getFallbackUrl() {
    return urlFallback;
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

package firenox.ui;

/**
 * Created by firenox on 6/14/16.
 */
public class ListContainerDimensions {
  private final int waveWidth;
  private final int waveHeigth;
  private final int artWidth;
  private final int artHeigth;


  public ListContainerDimensions(int waveWidth, int waveHeigth, int artWidth, int artHeigth) {
    this.waveWidth = waveWidth;
    this.waveHeigth = waveHeigth;
    this.artWidth = artWidth;
    this.artHeigth = artHeigth;
  }

  public int getWaveWidth() {
    return waveWidth;
  }

  public int getWaveHeigth() {
    return waveHeigth;
  }

  public int getArtWidth() {
    return artWidth;
  }

  public int getArtHeigth() {
    return artHeigth;
  }
}

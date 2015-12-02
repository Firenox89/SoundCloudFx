package firenox.model;

/**
 * Created by firenox on 11/18/15.
 */
public abstract class AbstractPagedListEntry {
    public abstract ArtWork getArtwork();

    public abstract WaveForm getWaveform();

    public abstract String getTitle();

    public abstract String getUser_name();

    public abstract User getUser();
}

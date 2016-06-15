package firenox.model;

import java.util.Date;

/**
 * Created by firenox on 11/18/15.
 */
public interface PagedListEntry {

  int getId();

  Date getCreateAt();

  ArtWork getArtwork();

  WaveForm getWaveform();

  String getTitle();

  String getUserName();

  User getUser();
}

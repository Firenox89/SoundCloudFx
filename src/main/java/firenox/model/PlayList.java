package firenox.model;

import com.soundcloud.api.Endpoints;
import firenox.io.RequestManager;
import firenox.logger.LogType;
import firenox.logger.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by firenox on 10/13/15.
 */
public class PlayList implements PagedListEntry {
  Logger log = Logger.getLogger(getClass().getName());
  int id;
  int user_id;
  String title;
  String permalink;
  String uri;
  String permalink_url;
  ArtWork artwork;
  PagedList<PagedListEntry> tracks;
  private JSONObject jsonObject;
  private User user;
  private int LIMIT = 10;
  private String created_at;
  private Date created;

  public PlayList(JSONObject jsonObject) {
    this.jsonObject = jsonObject;
    parseJSON(jsonObject);
  }

  private void parseJSON(JSONObject jsonObject) {
    try {
      id = jsonObject.getInt("id");
      created_at = jsonObject.getString("created_at");
      user_id = jsonObject.getInt("user_id");
      permalink = jsonObject.getString("permalink");
      title = jsonObject.getString("title");
      uri = jsonObject.getString("uri");
      permalink_url = jsonObject.getString("permalink_url");
      String artwork_url = jsonObject.getString("artwork_url");
      //in case the artwork is null use the user avatar
      if (artwork_url.equals("null")) {
        //use artwork from first Track
        String response = RequestManager.getStringWithLimit(String.format(Endpoints.PLAYLIST_TRACKS, id), 1, 0);
        JSONObject firstTrack = new JSONArray(response).getJSONObject(0);
        artwork_url = firstTrack.getString("artwork_url");

        if (artwork_url.equals("null")) {
          //if that's null too use the avatar from the owner of the first track
          artwork_url = firstTrack.getJSONObject("user").getString("avatar_url");
        }
      }
      artwork = new ArtWork(artwork_url);

      log.log(LogType.INFO, "title " + title);
      log.log(LogType.INFO, "id " + id);
      log.log(LogType.INFO, "user_id " + user_id);
      log.log(LogType.INFO, "permalink " + permalink);
      log.log(LogType.INFO, "uri " + uri);
      log.log(LogType.INFO, "permalink_url " + permalink_url);
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  public int getId() {
    return id;
  }

  @Override
  public Date getCreateAt() {
    if (created != null) {
      DateFormat df = new SimpleDateFormat("yyyy/MM/EE kk:mm:ss", Locale.ENGLISH);
      try {
        created = df.parse(created_at.substring(0, created_at.length() - 6));
      } catch (ParseException e) {
        e.printStackTrace();
      }
    }
    return created;
  }


  public int getUser_id() {
    return user_id;
  }

  public String getPermalink() {
    return permalink;
  }

  public String getTitle() {
    return title;
  }

  @Override
  public String getUserName() {
    return getUser().getUsername();
  }

  @Override
  public User getUser() {
    if (user == null) {
      user = ModelManager.getUser(user_id);
    }
    return user;
  }

  public String getUri() {
    return uri;
  }

  public String getPermalink_url() {
    return permalink_url;
  }

  public ArtWork getArtwork() {
    return artwork;
  }

  @Override
  public WaveForm getWaveform() {
    //get wave from first track
    return getTrackList().get(0).getWaveform();
  }

  public PagedList<PagedListEntry> getTrackList() {
    if (tracks == null) {
      tracks = new PagedList<>(String.format(Endpoints.PLAYLIST_TRACKS, id), LIMIT, PagedListEntry.class);
    }
    return tracks;
  }
}

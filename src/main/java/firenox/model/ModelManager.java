package firenox.model;

import com.soundcloud.api.Endpoints;
import firenox.io.RequestManager;
import firenox.logger.LogType;
import firenox.logger.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

/**
 * Created by firenox on 10/6/15.
 */
public class ModelManager {

  public static String CACHE_PATH_FALLBACK = System.getProperty("java.io.tmpdir") + File.separator + "soundcloudFx";
  public static String ARTWORKS_CACHE_PATH;
  public static String WAVE_CACHE_PATH;
  private static Properties properties = new Properties();
  public static String CACHE_PATH;
  private static HashMap<String, User> userList = new HashMap<>();
  private static HashMap<String, Track> trackList = new HashMap<>();
  private static HashMap<String, PlayList> playlistsList = new HashMap<>();
  private static Logger log = Logger.getLogger(ModelManager.class.getName());

  static {
    try {
      properties.load(ModelManager.class.getClassLoader().getResourceAsStream("app.properties"));
    } catch (IOException e) {
      e.printStackTrace();
    }
    CACHE_PATH = properties.getProperty("caching.path", CACHE_PATH_FALLBACK);
    if (CACHE_PATH.isEmpty())
      CACHE_PATH = CACHE_PATH_FALLBACK;
    WAVE_CACHE_PATH = CACHE_PATH + File.separator + "waves";
    ARTWORKS_CACHE_PATH = CACHE_PATH + File.separator + "artworks";
    new File(WAVE_CACHE_PATH).mkdirs();
    new File(ARTWORKS_CACHE_PATH).mkdirs();
    log.log(LogType.RESOURCE, WAVE_CACHE_PATH);
  }

  static {
    log.log(LogType.DEBUG, "CACHE_PATH " + CACHE_PATH);
  }

  public static int getTrackCount() {
    return trackList.size();
  }

  public static int getUserCount() {
    return userList.size();
  }

  public static int getPlaylistCount() {
    return playlistsList.size();
  }

  public static User getMe() {
    return getUser(Endpoints.MY_DETAILS);
  }

  public static PagedList<PagedListEntry> getMyLikes() {
    return getMe().getLikes();
  }

  public static PagedList<PagedListEntry> getMyRepost() {
    return getMe().getReposts();
  }

  public static PagedList<PlayList> getMyPlaylists() {
    return getMe().getPlaylists();
  }

  public static PagedList<PagedListEntry> getMyStream() {
    return getMe().getStream();
  }

  public static User getUser(String url) {
    User user = userList.get(url);

    if (user == null) {
      user = loadUser(url);
      userList.put(url, user);
    }
    return user;
  }

  public static Track getTrack(String name) {
    Track track = trackList.get(name);

    if (track == null) {
      track = loadTrack(name);
      trackList.put(name, track);
    }
    return track;
  }

  public static PlayList getPlaylist(String name) {
    PlayList playList = playlistsList.get(name);

    if (playList == null) {
      playList = loadPlaylist(name);
      playlistsList.put(name, playList);
    }
    return playList;
  }

  public static User getUser(int id) {
    return getUser(String.format(Endpoints.USER_DETAILS, id));
  }

  public static Track getTrack(int id) {
    return getTrack(String.format(Endpoints.TRACK_DETAILS, id));
  }

  public static PlayList getPlayList(int id) {
    return getPlaylist(String.format(Endpoints.PLAYLIST_DETAILS, id));
  }

  public static Track getTrack(JSONObject jsonObject) throws JSONException {
    if (jsonObject.has("origin")) {
      //response from activities api
      //to less info
      if (jsonObject.isNull("origin")) {
        return null;
      }
      int id = jsonObject.getJSONObject("origin").getInt("id");
      return getTrack(id);
    } else if (jsonObject.has("track")) {
      //response from repost api
      //does not contain the stream_url
      int id = jsonObject.getJSONObject("track").getInt("id");
      return getTrack(id);
    }
    String trackUrl = String.format(Endpoints.TRACK_DETAILS, jsonObject.getInt("id"));

    Track track = trackList.get(trackUrl);

    if (track == null) {
      track = new Track(jsonObject);
      trackList.put(trackUrl, track);
    }
    return track;
  }

  public static PlayList getPlaylist(JSONObject jsonObject) throws JSONException {
    if (jsonObject.has("origin")) {
      int id = jsonObject.getJSONObject("origin").getInt("id");
      return getPlayList(id);
    }
    if (jsonObject.has("playlist")) {
      int id = jsonObject.getJSONObject("playlist").getInt("id");
      //TODO: contains track list with ids should be used to load tracks lazily
      return getPlayList(id);
    }

    String playlistUrl = String.format(Endpoints.TRACK_DETAILS, jsonObject.getInt("id"));

    PlayList playList = playlistsList.get(playlistUrl);

    if (playList == null) {
      playList = new PlayList(jsonObject);
      playlistsList.put(playlistUrl, playList);
    }
    return playList;
  }

  public static Comment getComment(JSONObject jsonObject) {
    //there is no api point to load them
    //and the list is inside the track they belong to
    return new Comment(jsonObject);
  }

  private static User loadUser(String url) {
    JSONObject jsonObject = RequestManager.getJSON(url);
    return new User(jsonObject);
  }

  private static Track loadTrack(String name) {
    return new Track(RequestManager.getJSON(name));
  }

  private static PlayList loadPlaylist(String name) {
    return new PlayList(RequestManager.getJSON(name));
  }
}

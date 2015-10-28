package firenox.model;

import com.soundcloud.api.Endpoints;
import firenox.logger.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by firenox on 10/6/15.
 */
public class User {
    private Logger log = Logger.getLogger(getClass().getName());
    private int id;
    private String permalink;
    private String username;
    private String uri;
    private String permalink_url;
    private String avatar_url;
    private PagedList<Track> likesList;
    private int LIMIT = 10;
    private PagedList<Track> streamList;
    private PagedList<PlayList> playLists;

    public User(JSONObject jsonObject) {
        parseJSON(jsonObject);

    }

    private void parseJSON(JSONObject jsonObject) {
        try {
//            jsonObject.keys().forEachRemaining(System.out::println);
            this.id = (int) jsonObject.get("id");
            this.permalink = jsonObject.getString("permalink");
            this.username = jsonObject.getString("username");
            this.uri = new String(jsonObject.getString("uri"));
            this.permalink_url = new String(jsonObject.getString("permalink_url"));
            this.avatar_url = new String(jsonObject.getString("avatar_url"));
            log.i("username " + username);
            log.i("id " + id);
            log.i("permalink " + permalink);
            log.i("uri " + uri);
            log.i("permalink_url " + permalink_url);
            log.i("avatar_url " + avatar_url);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public PagedList<Track> getLikesList() {
        if (likesList == null) {
            likesList = new PagedList<>(Endpoints.MY_FAVORITES, LIMIT, Track.class);
        }
        return likesList;
    }

    public int getId() {
        return id;
    }

    public String getPermalink() {
        return permalink;
    }

    public String getUsername() {
        return username;
    }

    public String getUri() {
        return uri;
    }

    public String getPermalink_url() {
        return permalink_url;
    }

    public String getAvatar_url() {
        return avatar_url;
    }

    public PagedList<Track> getStream() {
        if (streamList == null) {
            streamList = new PagedList<>(Endpoints.MY_ACTIVITIES, LIMIT, Track.class);
        }
        return streamList;
    }

    public PagedList<PlayList> getPlaylists() {

        if (playLists == null) {
            playLists = new PagedList<PlayList>(Endpoints.MY_PLAYLISTS, LIMIT, PlayList.class);
        }
        return playLists;
    }
}

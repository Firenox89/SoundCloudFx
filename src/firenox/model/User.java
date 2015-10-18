package firenox.model;

import com.soundcloud.api.Endpoints;
import firenox.io.Http;
import firenox.io.LogInHandler;
import firenox.logger.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by firenox on 10/6/15.
 */
public class User {
    Logger log = Logger.getLogger(getClass().getName());
    int id;
    String permalink;
    String username;
    String uri;
    String permalink_url;
    String avatar_url;
    PagedList<Track> favList;
    private int LIMIT = 10;

    public User(JSONObject jsonObject) {
        parseJSON(jsonObject);

        favList = new PagedList<>(Endpoints.MY_FAVORITES, LIMIT, Track.class);
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

    public PagedList<Track> getFavList() {
        return favList;
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
}

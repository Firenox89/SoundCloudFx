package firenox.model;

import com.soundcloud.api.Endpoints;
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
    ArrayList<Track> favList = new ArrayList<>();
    boolean favLoaded = false;
    private JSONObject jsonObject;

    public User(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
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

    private void loadTracks() {
        try {
            String json = LogInHandler.getStringWithLimit(Endpoints.MY_FAVORITES, 5);
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                favList.add(new Track((JSONObject) jsonArray.get(i)));
            }
            favLoaded = true;
//            json.keys().forEachRemaining(System.out::println);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Track> getFavList() {
        if (!favLoaded) {
            loadTracks();
        }
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

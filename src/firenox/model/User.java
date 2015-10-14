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
    ArrayList<Track> favList = new ArrayList<>();
    private int LIMIT = 10;

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

    private void loadTracks() {
        try {
            String json = LogInHandler.getStringWithLimit(Endpoints.MY_FAVORITES, LIMIT, 0);
//            System.out.println(Http.formatJSON(json));
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                favList.add(new Track((JSONObject) jsonArray.get(i)));
            }
//            json.keys().forEachRemaining(System.out::println);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String next_href = null;
    private boolean allLoaded = false;

    public ArrayList<Track> loadNextTracks() {
        ArrayList<Track> newTracks = null;
        if (!allLoaded) {
            try {
                newTracks = new ArrayList<>();
                int page = 1;
                String json;
                if (next_href == null) {
                    json = LogInHandler.getStringWithLimit(Endpoints.MY_FAVORITES, LIMIT, page);
                } else {
                    json = LogInHandler.getString(next_href);
                }
//                System.out.println(Http.formatJSON(json));
                JSONObject response = new JSONObject(json);
                JSONArray jsonArray = response.getJSONArray("collection");
                for (int i = 0; i < jsonArray.length(); i++) {
                    newTracks.add(new Track((JSONObject) jsonArray.get(i)));
                }
                next_href = response.getString("next_href");
                if (next_href == null) {
                    allLoaded = true;
                }
                favList.addAll(newTracks);
//            json.keys().forEachRemaining(System.out::println);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return newTracks;
    }

    public ArrayList<Track> getFavList() {
        if (favList.isEmpty() && !allLoaded) {
            loadNextTracks();
        }
        return favList;
    }

    public boolean isAllLoaded()
    {
      return allLoaded;
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

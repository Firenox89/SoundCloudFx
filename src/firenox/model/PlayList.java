package firenox.model;

import com.soundcloud.api.Endpoints;
import firenox.io.LogInHandler;
import firenox.logger.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by firenox on 10/13/15.
 */
public class PlayList {
    Logger log = Logger.getLogger(getClass().getName());
    int id;
    int user_id;
    String title;
    String permalink;
    String uri;
    String permalink_url;
    ArtWork artwork;
    PagedList<Track> tracks;
    private JSONObject jsonObject;
    private User user;
    private int LIMIT = 10;

    public PlayList(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
        parseJSON(jsonObject);
    }

    private void parseJSON(JSONObject jsonObject) {
        try {

            if (jsonObject.has("origin")) {
                long id = jsonObject.getJSONObject("origin").getLong("id");
                jsonObject = LogInHandler.getJSON("/playlists/" + id);
            }
            id = jsonObject.getInt("id");
            user_id = jsonObject.getInt("user_id");
            permalink = jsonObject.getString("permalink");
            title = jsonObject.getString("title");
            uri = jsonObject.getString("uri");
            permalink_url = jsonObject.getString("permalink_url");
            String artwork_url = jsonObject.getString("artwork_url");
            //in case the artwork is null use the user avatar
            if (artwork_url.equals("null")) {
                //use artwork from first Track
                String response = LogInHandler.getStringWithLimit(String.format(Endpoints.PLAYLIST_TRACKS, id), 1, 0);
                JSONObject firstTrack = new JSONArray(response).getJSONObject(0);
                artwork_url = firstTrack.getString("artwork_url");

                if (artwork_url.equals("null")) {
                    //if that's null too use the avatar from the owner of the first track
                    artwork_url = firstTrack.getJSONObject("user").getString("avatar_url");
                }
            }
            artwork = new ArtWork(artwork_url);

            log.i("title " + title);
            log.i("id " + id);
            log.i("user_id " + user_id);
            log.i("permalink " + permalink);
            log.i("uri " + uri);
            log.i("permalink_url " + permalink_url);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int getId() {
        return id;
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

    public String getUri() {
        return uri;
    }

    public String getPermalink_url() {
        return permalink_url;
    }

    public ArtWork getArtwork() {
        return artwork;
    }

    public PagedList<Track> getTrackList() {
        if (tracks == null) {
            tracks = new PagedList<Track>(String.format(Endpoints.PLAYLIST_TRACKS, id), LIMIT, Track.class);
        }
        return tracks;
    }
}

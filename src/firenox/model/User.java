package firenox.model;

import com.soundcloud.api.Endpoints;
import firenox.io.Http;
import firenox.io.RequestManager;
import firenox.logger.Logger;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by firenox on 10/6/15.
 */
public class User {

    private Logger log = Logger.getLogger(getClass().getName());

    private String avatar_url;
    private String city;
    private String country;
    private String first_name;
    private int followers_count;
    private int followings_count;
    private String full_name;
    private String locale;
    private boolean online;
    private int playlist_count;
    private int public_favorites_count;
    private int track_count;
    private int id;
    private String permalink;
    private String username;
    private String uri;
    private String permalink_url;
    private ArtWork avatar;
    private PagedList<Track> likesList;
    private int LIMIT = 9;
    private PagedList<Track> streamList;
    private PagedList<PlayList> playLists;
    private PagedList<Track> tracks;
    private PagedList<Track> reposts;

    public User(JSONObject jsonObject) {
        parseJSON(jsonObject);
    }

    private void parseJSON(JSONObject jsonObject) {
        try {
            //TODO: only save jsonObject and let parse them lazy
            avatar_url = jsonObject.getString("avatar_url");
            city = jsonObject.getString("city");
            country = jsonObject.getString("country");
            first_name = jsonObject.getString("first_name");
            followers_count = jsonObject.getInt("followers_count");
            followings_count = jsonObject.getInt("followings_count");
            full_name = jsonObject.getString("full_name");
//            locale = jsonObject.getString("locale");
            online = jsonObject.getBoolean("online");
            playlist_count = jsonObject.getInt("playlist_count");
            public_favorites_count = jsonObject.getInt("public_favorites_count");
            track_count = jsonObject.getInt("track_count");
            id = jsonObject.getInt("id");
            permalink = jsonObject.getString("permalink");
            username = jsonObject.getString("username");
            uri = jsonObject.getString("uri");
            permalink_url = jsonObject.getString("permalink_url");
            avatar = new ArtWork(avatar_url);
            log.i(Http.formatJSON(jsonObject.toString()));
        } catch (JSONException e) {
            log.e(e);
            log.e(Http.formatJSON(jsonObject.toString()));
        }
    }

    public PagedList<Track> getLikes() {
        if (likesList == null) {
            likesList = new PagedList<>(String.format(Endpoints.USER_FAVORITES, id), LIMIT, Track.class);
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
            playLists = new PagedList<>(String.format(Endpoints.USER_PLAYLISTS, id), LIMIT, PlayList.class);
        }
        return playLists;
    }

    public PagedList<Track> getTracks() {
        if (tracks == null) {
            tracks = new PagedList<>(String.format(Endpoints.USER_TRACKS, id), LIMIT, Track.class);
        }
        return tracks;
    }

    public PagedList<Track> getReposts() {
        if (reposts == null) {
            reposts = new PagedList<>(String.format(RequestManager.USER_REPOSTS, id), LIMIT, Track.class);
        }
        return reposts;
    }

    public ArtWork getAvatar() {
        return avatar;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public int getFollowers_count() {
        return followers_count;
    }

    public int getFollowings_count() {
        return followings_count;
    }

    public String getFull_name() {
        return full_name;
    }

    public String getLocale() {
        return locale;
    }

    public boolean isOnline() {
        return online;
    }

    public int getPlaylist_count() {
        return playlist_count;
    }

    public int getPublic_favorites_count() {
        return public_favorites_count;
    }

    public int getTrack_count() {
        return track_count;
    }
}

package firenox.model;

import com.soundcloud.api.Endpoints;
import firenox.io.RequestManager;
import firenox.logger.Logger;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

/**
 * Created by firenox on 10/6/15.
 */
public class ModelManager {


    public static String CACHE_PATH_FALLBACK = System.getProperty("java.io.tmpdir") + File.separator + "soundcloudFx";
    private static Properties properties = new Properties();
    public static String CACHE_PATH = properties.getProperty("caching.path", CACHE_PATH_FALLBACK);
    public static String ARTWORKS_CACHE_PATH = CACHE_PATH + File.separator + "artworks";
    public static String WAVE_CACHE_PATH = CACHE_PATH + File.separator + "waves";
    private static HashMap<String, User> userList = new HashMap<>();
    private static HashMap<String, Track> trackList = new HashMap<>();
    private static ModelManager instance = new ModelManager();
    private static Logger log = Logger.getLogger(ModelManager.class.getName());

    static {
        try {
            properties.load(ModelManager.class.getClassLoader().getResourceAsStream("app.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static {
        log.d("CACHE_PATH " + CACHE_PATH);
    }

    private ModelManager() {
    }

    public static User getUser(String name) {
        User user = userList.get(name);

        if (user == null) {
            user = instance.loadUser(name);
            userList.put(name, user);
        }
        return user;
    }

    public static User getMe() {
        return getUser(Endpoints.MY_DETAILS);
    }

    public static Track getTrack(String name) {
        Track track = null;

        track = trackList.get(name);

        if (track == null) {
            track = instance.loadTrack(name);
            trackList.put(name, track);
        }
        return track;
    }

    public static PagedList<Track> getLikes() {
        return getMe().getLikesList();
    }

    public static PagedList<PlayList> getMyPlaylists() {
        return getMe().getPlaylists();
    }

    public User loadUser(String name) {
        return new User(RequestManager.getJSON(name));
    }

    public Track loadTrack(String name) {
        return new Track(RequestManager.getJSON(name));
    }

    public static PagedList<Track> getStream() {
        return getMe().getStream();
    }
}

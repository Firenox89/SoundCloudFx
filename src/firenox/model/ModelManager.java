package firenox.model;

import com.soundcloud.api.Endpoints;
import firenox.io.Http;
import firenox.io.LogInHandler;
import firenox.logger.Logger;
import org.apache.http.HttpResponse;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
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
            properties.load(new FileInputStream(new File("app.properties")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static {
        log.d("CACHE_PATH " + CACHE_PATH);
    }

    private ModelManager() {
    }

    ;

    public static User getUser(String name) {
        User user = userList.get(name);

        if (user == null) {
            user = instance.loadUser(name);
            userList.put(name, user);
        }
        return user;
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

    public static ArrayList<Track> getLikes() {
        return getUser(Endpoints.MY_DETAILS).getFavList();
    }

    /**
     * Add next page to the FavList.
     *
     * @return true if there are more to load.
     */
    public static ArrayList<Track> loadNextFav()
    {
        return getUser(Endpoints.MY_DETAILS).loadNextTracks();
    }



    public static void getPlaylists() {
        try {
            HttpResponse resp = LogInHandler.requestWithLimit(Endpoints.PLAYLISTS, 5, 0);
            System.out.println(resp.getStatusLine().getReasonPhrase());
            Thread.sleep(1000);
            String response = Http.formatJSON(Http.getString(resp));
            System.out.println(response);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public User loadUser(String name) {
        return new User(LogInHandler.getJSON(name));
    }

    public Track loadTrack(String name) {
        return new Track(LogInHandler.getJSON(name));
    }
}

package firenox.model;

import com.soundcloud.api.Endpoints;
import com.soundcloud.api.Stream;
import firenox.io.Http;
import firenox.io.RequestManager;
import firenox.logger.Logger;
import firenox.statistics.Traffic;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;

/**
 * Created by firenox on 10/6/15.
 */
public class Track {
    Logger log = Logger.getLogger(getClass().getName());
    int id;
    int user_id;
    String title;
    String permalink;
    String uri;
    String permalink_url;
    ArtWork artwork;
    String stream_url;
    int duration;
    WaveForm waveform;
    private JSONObject jsonObject;
    private User user;
    private String user_name;

    public Track(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
        parseJSON(jsonObject);
    }

    private void parseJSON(JSONObject jsonObject) {
        try {
            if (jsonObject.has("origin")) {
                long id = jsonObject.getJSONObject("origin").getLong("id");
                jsonObject = RequestManager.getJSON(String.format(Endpoints.TRACK_DETAILS, id));
            }
            id = jsonObject.getInt("id");
            user_id = jsonObject.getInt("user_id");
            user_name = jsonObject.getJSONObject("user").getString("username");
            permalink = jsonObject.getString("permalink");
            title = jsonObject.getString("title");
            uri = jsonObject.getString("uri");
            permalink_url = jsonObject.getString("permalink_url");
            stream_url = jsonObject.getString("stream_url");
            duration = jsonObject.getInt("duration");
            waveform = new WaveForm(jsonObject.getString("waveform_url"));
            String artwork_url = jsonObject.getString("artwork_url");
            //in case the artwork is null use the user avatar
            if (artwork_url.equals("null")) {
                artwork_url = jsonObject.getJSONObject("user").getString("avatar_url");
            }
            artwork = new ArtWork(artwork_url);

            log.i("title " + title);
            log.i("id " + id);
            log.i("user_id " + user_id);
            log.i("permalink " + permalink);
            log.i("uri " + uri);
            log.i("permalink_url " + permalink_url);
            log.i("stream_url " + stream_url);
            log.i("duration " + duration);
            log.i("waveform " + waveform);
        } catch (JSONException e) {
            log.e(e);
            log.e(Http.formatJSON(jsonObject.toString()));
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

    public WaveForm getWaveform() {
        return waveform;
    }
//    public String getStream_url() {
//        return stream_url;
//    }


    public int getDuration() {
        return duration;
    }

    public InputStream getMP3Stream() throws IOException {
        Stream stream = RequestManager.requestStream(stream_url);
        HttpGet httpget = new HttpGet(stream.streamUrl);

        DefaultHttpClient httpclient = new DefaultHttpClient();
        HttpResponse response = httpclient.execute(httpget);
        HttpEntity entity = response.getEntity();
        InputStream is = entity.getContent();

        return is;
    }

    public String getStreamURL() {
        Stream stream = null;
        try {
            stream = RequestManager.requestStream(stream_url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return stream.streamUrl;
    }

    public String getTempFileURL() throws IOException {
        InputStream is = getMP3Stream();
        String url = null;
        File file = new File("tmp.mp3");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(new File("tmp.mp3"));
            byte[] buffer = new byte[1024 * 128];
            int read = 0;
            while ((read = is.read(buffer)) != -1) {
                fos.write(buffer, 0, read);
                Traffic.addMP3Traffic(read);
            }
            url = file.toURL().toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return url;
    }

    public User getUser() {
        if (user == null) {
            user = ModelManager.getUser(user_id);
        }
        return user;
    }

    public String getUser_name() {
        return user_name;
    }
}

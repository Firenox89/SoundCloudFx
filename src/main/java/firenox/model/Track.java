package firenox.model;

import com.soundcloud.api.CloudAPI;
import com.soundcloud.api.Endpoints;
import com.soundcloud.api.Request;
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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by firenox on 10/6/15.
 */
public class Track implements PagedListEntry {
    private Logger log = Logger.getLogger(getClass().getName());
    private int id;
    private int user_id;
    private String title;
    private String permalink;
    private String uri;
    private String permalink_url;
    private ArtWork artwork;
    private String stream_url;
    private boolean streamable;
    private int duration;
    private WaveForm waveform;
    private JSONObject jsonObject;
    private User user;
    private String user_name;
    private PagedList<Comment> comments;
    private int LIMIT = 10;
    private int iLike = -1;
    private String created_at;
    private Date created;

    public Track(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
        parseJSON(jsonObject);
    }

    private void parseJSON(JSONObject jsonObject) {
        try {
            id = jsonObject.getInt("id");
            created_at = jsonObject.getString("created_at");
            user_id = jsonObject.getInt("user_id");
            user_name = jsonObject.getJSONObject("user").getString("username");
            permalink = jsonObject.getString("permalink");
            title = jsonObject.getString("title");
            uri = jsonObject.getString("uri");
            permalink_url = jsonObject.getString("permalink_url");
            streamable = jsonObject.getBoolean("streamable");
            if (streamable)
                stream_url = jsonObject.getString("stream_url");
            duration = jsonObject.getInt("duration");
            waveform = new WaveForm(jsonObject.getString("waveform_url"));
            String artwork_url = jsonObject.getString("artwork_url");
            //in case the artwork is null use the user avatar
            if (artwork_url.equals("null")) {
                artwork_url = jsonObject.getJSONObject("user").getString("avatar_url");
            }
            artwork = new ArtWork(artwork_url);

        } catch (JSONException e) {
            log.e(e);
            log.e(Http.formatJSON(jsonObject.toString()));
        }
    }

    public int getId() {
        return id;
    }

    @Override
    public Date getCreateAt() {
        if (created != null) {
            DateFormat df = new SimpleDateFormat("yyyy/MM/EE kk:mm:ss", Locale.ENGLISH);
            try {
                created =  df.parse(created_at.substring(0, created_at.length()-6));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return created;
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

    public boolean doILike() {
        if (iLike == -1) {
            String url = "/me/favorites/" + id;
            try {
                JSONObject resp = Http.getJSON(RequestManager.request(Request.to(url)));
                if (resp.has("id")) {
                    iLike = 1;
                } else {
                    iLike = 0;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return iLike == 1 ? true : false;
    }

    public void toggleLike() {
        if (iLike == -1) {
            doILike();
        }
        if (iLike == 1) {
            RequestManager.unlikeTrack(id);
            iLike = 0;
            ModelManager.getMe().getLikes().remove(this);
        } else {
            RequestManager.likeTrack(id);
            iLike = 1;
            ModelManager.getMe().getLikes().add(0, this);
        }
    }

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
        } catch (CloudAPI.ResolverException e) {
            log.e("streamable = " + streamable);
            log.e("stream_url = " + stream_url);
            log.e(e);
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

    public PagedList<Comment> getComments() {
        if (comments == null) {
            comments = new PagedList<Comment>(String.format(Endpoints.TRACK_COMMENTS, id), LIMIT, Comment.class);
        }
        return comments;
    }

    public String getUser_name() {
        return user_name;
    }
}

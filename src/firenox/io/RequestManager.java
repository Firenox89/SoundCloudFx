package firenox.io;

import com.soundcloud.api.ApiWrapper;
import com.soundcloud.api.Request;
import com.soundcloud.api.Stream;
import firenox.logger.Logger;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

/**
 * Delegates API requests to the the logged in ApiWrapper
 * <p/>
 * Created by firenox on 10/29/15.
 */
public class RequestManager {


    public static String USER_REPOSTS = "/users/%d/reposts";
    private static String USER_REPOSTS_API = "https://api-v2.soundcloud.com/profile/soundcloud:users:%d?limit=%d&offset=%d";

    private static Logger log = Logger.getLogger(RequestManager.class.getName());
    private static ApiWrapper wrapper;

    /**
     * Set the ApiWrapper
     *
     * @param apiWrapper
     */
    static void init(ApiWrapper apiWrapper) {
        wrapper = apiWrapper;
    }

    /**
     * Returns a HttpResponse for the given API request URL.
     *
     * @param request the request URL
     * @return the HttpResponse for the given URL
     * @throws IOException
     */
    public static HttpResponse request(String request) throws IOException {
        LogInHandler.checkInit();
        HttpResponse resp = wrapper.get(Request.to(request));
        if (resp.getStatusLine().getStatusCode() != 200)
        {
            log.d(resp.getStatusLine());
            resp = null;
        }
        return resp;
    }

    /**
     * Executes a given request.
     *
     * @param request the request to be executed.
     * @return the respones for the given request.
     * @throws IOException
     */
    public static HttpResponse request(Request request) throws IOException {
        LogInHandler.checkInit();
        return wrapper.get(request);
    }

    /**
     * Returns a HttpResponse for the given API request URL,
     * with a response limit using the paging function of the Soundcloud APIs.
     *
     * @param requestUrl the request URL
     * @param limit      the response limit
     * @param page       the page offset
     * @return the HttpResponse for the given URL
     * @throws IOException
     */
    public static HttpResponse requestWithLimit(String requestUrl, int limit, int page) throws IOException {
        Request request;
        log.d("request = " + requestUrl);
        if (page == 0) {
            request = Request.to(requestUrl).add("limit", limit);
        } else {
            request = Request.to(requestUrl).add("limit", limit).add("linked_partitioning", page);
        }
        return wrapper.get(request);
    }

    /**
     * Returns a HttpResponse for the given API request URL,
     * with a response limit using the paging function of the Soundcloud APIs
     * and the compact representation filter for playlists.
     *
     * @param requestUrl the request URL
     * @param limit      the response limit
     * @param page       the page offset
     * @return the HttpResponse for the given URL
     * @throws IOException
     */
    public static HttpResponse requestPlayListsWithLimit(String requestUrl, int limit, int page) throws IOException {
        //TODO: check if that is a playlist request
        Request request;
        log.d("requestPlaylist = " + requestUrl);
        if (page == 0) {
            request = Request.to(requestUrl).add("limit", limit).add("representation", "compact");
        } else {
            request = Request.to(requestUrl).add("limit", limit).add("linked_partitioning", page)
                    .add("representation", "compact");
        }
        return wrapper.get(request);
    }

    /**
     * Request a Stream representation for the given URL.
     *
     * @param request the request URL
     * @return a Stream object for the given URL
     * @throws IOException
     */
    public static Stream requestStream(String request) throws IOException {
        //TODO: check if that is a playlist request
        LogInHandler.checkInit();
        log.d("requestStream = " + request);
        //TODO: do this with a timeout since it hangs from time to time
        return wrapper.resolveStreamUrl(request, false);
    }

    /**
     * Get a Resource e.g an Image, from the given URL and return an Inputstream to it.
     *
     * @param url the URL to get the stream from
     * @return InputStream to the resource
     * @throws IOException
     */
    public static InputStream getResource(String url) throws IOException {
        HttpResponse response = requestResource(url);
        InputStream is = null;
        if (response.getStatusLine().getStatusCode() == 200) {
            HttpEntity entity = response.getEntity();
            is = entity.getContent();
        } else {
            log.d("url = "+url+" response Status = "+response.getStatusLine());
        }

        return is;
    }

    public static HttpResponse requestResource(String url) throws IOException {
        HttpGet httpget = new HttpGet(url);
        DefaultHttpClient httpclient = new DefaultHttpClient();
        return httpclient.execute(httpget);
    }

    /**
     * Like {@link #requestWithLimit(String, int, int)} but directly returns a String.
     *
     * @param requestUrl the request URL
     * @param limit      the response limit
     * @param page       the page offset
     * @return the String representation of the response
     */
    public static String getStringWithLimit(String requestUrl, int limit, int page) {
        String string = null;
        try {
            if (requestUrl.endsWith("playlists")) {
                string = Http.getString(requestPlayListsWithLimit(requestUrl, limit, page));
            } else if (requestUrl.contains("reposts")) {
                int userId = Integer.parseInt(requestUrl.substring(7, requestUrl.length() - 8));
                string = Http.getString(requestResource(String.format(USER_REPOSTS_API, userId, limit, page)));
            } else {
                string = Http.getString(requestWithLimit(requestUrl, limit, page));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return string;
    }

    /**
     * Like {@link #request(String)} but directly returns a String.
     *
     * @param requestUrl the request URL
     * @return the String representation of the response
     */
    public static String getString(String requestUrl) {
        String string = null;
        HttpResponse resp;
        try {
            //TODO: should be handled in the soundcloud api wrapper
            if (requestUrl.startsWith("https://api-v2")) {
                resp = requestResource(requestUrl);
                if (resp != null)
                    string = Http.getString(resp);
            } else {
                resp = request(requestUrl);
                if (resp != null)
                    string = Http.getString(resp);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return string;
    }

    /**
     * Like {@link #request(String)} but directly returns a JSONObject.
     *
     * @param requestUrl the request URL
     * @return the JSONObject representation of the response
     */
    public static JSONObject getJSON(String requestUrl) {
        JSONObject jsonObject = null;
        try {
            jsonObject = Http.getJSON(request(requestUrl));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}

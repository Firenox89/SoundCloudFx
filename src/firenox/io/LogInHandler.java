package firenox.io;

import com.soundcloud.api.ApiWrapper;
import com.soundcloud.api.Request;
import com.soundcloud.api.Stream;
import com.soundcloud.api.Token;
import firenox.logger.Logger;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;

/**
 * Created by firenox on 10/1/15.
 */
public class LogInHandler {
    private static final File WRAPPER_SER = new File("wrapper.ser");
    private static Properties credentials = new Properties();
    static {
        try {
            credentials.load(LogInHandler.class.getClassLoader().getResourceAsStream("credentials.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static String CLIENT_ID = credentials.getProperty("client.id");
    private static String CLIENT_SECRET = credentials.getProperty("client.secret");
    private static String DEFAULT_LOGIN = credentials.getProperty("default.login");
    private static String DEFAULT_PASS = credentials.getProperty("default.pass");
    private static LogInHandler instance = new LogInHandler();
    private static Logger log = Logger.getLogger(LogInHandler.class.getName());


    private ApiWrapper wrapper;
    private boolean loggedIn;
    private Logger logger = Logger.getLogger(getClass().getName());

    private LogInHandler() {
    }

    public static void init() {
        instance.connect(DEFAULT_LOGIN, DEFAULT_PASS);
    }


    public static boolean isLoggedIn() {
        return instance.loggedIn;
    }

    public static HttpResponse request(String request) throws IOException {
        instance.checkInit();
        return instance.wrapper.get(Request.to(request));
    }

    public static HttpResponse request(Request request) throws IOException {
        instance.checkInit();
        return instance.wrapper.get(request);
    }

    public static HttpResponse requestWithLimit(String requestUrl, int limit, int page) throws IOException {
        Request request = null;
        if (page == 0)
        {
            request = Request.to(requestUrl).add("limit", limit);
        }
        else
        {
            request = Request.to(requestUrl).add("limit", limit).add("linked_partitioning", page);
        }
        return instance.wrapper.get(request);
    }

    public static Stream requestStream(String request) throws IOException {
        instance.checkInit();
        return instance.wrapper.resolveStreamUrl(request, false);
    }

    public static void connect(String login, String password) {
        if (WRAPPER_SER.exists()) {
            try {
                instance.wrapper = ApiWrapper.fromFile(WRAPPER_SER);
                if (instance.wrapper.getToken().getExpiresIn().before(new Date())) {
                    instance.wrapper.refreshToken();
                    log.d("Token refreshed valid until: " + instance.wrapper.getToken().getExpiresIn());
                    instance.wrapper.toFile(WRAPPER_SER);
                }
                log.d(instance.wrapper.getToken());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            instance.wrapper = instance.serialises(login, password);
        }
        if (instance.wrapper != null)
            instance.loggedIn = true;
    }

    public static InputStream getResource(String url) throws IOException {
        HttpGet httpget = new HttpGet(url);

        DefaultHttpClient httpclient = new DefaultHttpClient();
        HttpResponse response = httpclient.execute(httpget);
        HttpEntity entity = response.getEntity();
        InputStream is = entity.getContent();

        return is;
    }

    public static String getStringWithLimit(String url, int limit, int page) {
        String string = null;
        try {
            string = Http.getString(requestWithLimit(url, limit, page));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return string;
    }

    public static String getString(String url) {
        String string = null;
        try {
            string = Http.getString(request(url));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return string;
    }

    public static JSONObject getJSON(String url) {
        JSONObject jsonObject = null;
        try {
            jsonObject = Http.getJSON(request(url));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    private void checkInit() {
        if (loggedIn == false) {
            throw new IllegalStateException("Not logged in");
        }
    }

    public ApiWrapper serialises(String login, String pass) {
        if (CLIENT_ID == null || CLIENT_ID.equals(""))
            throw new IllegalStateException("Client_Id is empty.");
        if (CLIENT_SECRET == null || CLIENT_SECRET.equals(""))
            throw new IllegalStateException("Client_Id is empty.");
        if (login == null || login.equals(""))
            throw new IllegalStateException("Client_Id is empty.");
        if (pass == null || pass.equals(""))
            throw new IllegalStateException("Client_Id is empty.");

        final ApiWrapper wrapper = new ApiWrapper(
                CLIENT_ID /* client_id */,
                CLIENT_SECRET /* client_secret */,
                null    /* redirect URI */,
                null    /* token */);

        Token token = null;
        try {
            token = wrapper.login(login /* login */, pass /* password */);
            wrapper.toFile(WRAPPER_SER);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("got token from server: " + token);

        return wrapper;
    }
}

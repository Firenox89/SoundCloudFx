package firenox.io;

import com.soundcloud.api.ApiWrapper;
import com.soundcloud.api.Token;
import firenox.logger.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

/**
 * Created by firenox on 10/1/15.
 */
public class LogInHandler {
    private static final File WRAPPER_SER = new File(System.getProperty("java.io.tmpdir")+File.separator+"wrapper.ser");
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

    private static Logger log = Logger.getLogger(LogInHandler.class.getName());


    private static ApiWrapper wrapper;
    private static boolean loggedIn;
    private Logger logger = Logger.getLogger(getClass().getName());

    public static void init() {
        connect(DEFAULT_LOGIN, DEFAULT_PASS);
        RequestManager.init(wrapper);
    }


    public static boolean isLoggedIn() {
        return loggedIn;
    }

    public static void connect(String login, String password) {
        if (WRAPPER_SER.exists()) {
            try {
                wrapper = ApiWrapper.fromFile(WRAPPER_SER);
                if (wrapper.getToken().getExpiresIn().before(new Date())) {
                    wrapper.refreshToken();
                    log.d("Token refreshed valid until: " + wrapper.getToken().getExpiresIn());
                    wrapper.toFile(WRAPPER_SER);
                }
                log.d(wrapper.getToken());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            wrapper = serialises(login, password);
        }
        if (wrapper != null)
            loggedIn = true;
    }

    public static void checkInit() {
        if (isLoggedIn() == false) {
            throw new IllegalStateException("Not logged in");
        }
    }

    private static ApiWrapper serialises(String login, String pass) {
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

    public static void checkToken() throws IOException {
        if (wrapper.getToken().getExpiresIn().before(new Date())) {
            wrapper.refreshToken();
            log.d("Token refreshed valid until: " + wrapper.getToken().getExpiresIn());
            wrapper.toFile(WRAPPER_SER);
        }
    }
}

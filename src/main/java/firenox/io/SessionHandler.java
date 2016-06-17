package firenox.io;

import com.soundcloud.api.ApiWrapper;
import com.soundcloud.api.Token;
import firenox.logger.LogType;
import firenox.logger.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

/**
 * Created by firenox on 10/1/15.
 */
public class SessionHandler {
  private static final File WRAPPER_SER = new File(System.getProperty("java.io.tmpdir") + File.separator + "wrapper.ser");
  private static final boolean RESET_WRAPPER_SER = false;
  private static Properties credentials = new Properties();
  private static String CLIENT_ID;
  private static String CLIENT_SECRET;
  private static Logger log = Logger.getLogger(SessionHandler.class.getName());
  private static ApiWrapper wrapper;
  private static boolean loggedIn;
  private static CountDownLatch loginLatch = new CountDownLatch(1);

  static {
    try {
      credentials.load(SessionHandler.class.getClassLoader().getResourceAsStream("credentials.properties"));
    } catch (IOException e) {
      e.printStackTrace();
    }
    CLIENT_ID = credentials.getProperty("client.id");
    CLIENT_SECRET = credentials.getProperty("client.secret");

    if (RESET_WRAPPER_SER) WRAPPER_SER.delete();
  }

  public static void init() {
    if (WRAPPER_SER.exists()) {
      try {
        wrapper = ApiWrapper.fromFile(WRAPPER_SER);
        if (wrapper.getToken().getExpiresIn().before(new Date())) {
          wrapper.refreshToken();
          log.log(LogType.NETWORK, "Token refreshed valid until: " + wrapper.getToken().getExpiresIn());
          wrapper.toFile(WRAPPER_SER);
        }
        loggedIn = true;
        log.log(LogType.NETWORK, wrapper.getToken());
        RequestManager.init(wrapper);
        loginLatch.countDown();
      } catch (IOException e) {
        log.log(LogType.ERROR, e);
      } catch (ClassNotFoundException e) {
        log.log(LogType.ERROR, e);
      }
    }
  }

  public static void connect(String login, String password) throws IOException {
    wrapper = serialises(login, password);

    if (wrapper != null) {
      loggedIn = true;
      wrapper.toFile(WRAPPER_SER);
      RequestManager.init(wrapper);
      loginLatch.countDown();
    }
  }

  public static boolean loggedin()
  {
    return loggedIn;
  }

  public static boolean waitForLogin() {
    if (loginLatch.getCount() > 0) {
      try {
        log.log(LogType.DEBUG, "Wait for init");
        loginLatch.await();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    return loggedIn;
  }

  private static ApiWrapper serialises(String login, String pass) throws IOException {
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
    token = wrapper.login(login /* login */, pass /* password */);
    wrapper.toFile(WRAPPER_SER);

    System.out.println("got token from server: " + token);

    return wrapper;
  }

  public static void checkToken() throws IOException {
    if (wrapper.getToken().getExpiresIn().before(new Date())) {
      wrapper.refreshToken();
      log.log(LogType.NETWORK, "Token refreshed valid until: " + wrapper.getToken().getExpiresIn());
      wrapper.toFile(WRAPPER_SER);
    }
  }

  public static void logout() {
    loginLatch = new CountDownLatch(1);
    WRAPPER_SER.delete();

    loggedIn = false;
  }
}

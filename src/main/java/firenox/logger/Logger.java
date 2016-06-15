package firenox.logger;

/**
 * Created by firenox on 10/7/15.
 */
public class Logger {
    private final String TAG;
    private boolean logInfo = false;
    private boolean logDebug = true;
    private boolean logError = true;

    public Logger(String tag) {
        TAG = tag;
    }

    public static Logger getLogger(String tag) {
        return new Logger(tag);
    }

    public void i(String message) {
        if (logInfo) {
            System.out.println("[INFO] " + TAG + " " + message);
        }
    }

    public void d(String message) {
        if (logDebug) {
            System.out.println("[DEBUG] " + TAG + " " + message);
        }
    }

    public void d(Object o) {
        d(o + "");
    }

    public void e(String message) {
        if (logError) {
            System.out.println("[ERROR] " + TAG + " " + message);
        }
    }

    public void e(Throwable t) {
        if (logError) {
            System.out.println("[ERROR] " + TAG + " " + t.getMessage());
            t.printStackTrace();
        }
    }
}

package firenox.logger;

import java.io.PrintStream;

/**
 * Created by firenox on 10/7/15.
 */
public class Logger {
  private final String CALLER;

  private boolean logInfo = false;
  private boolean logDebug = true;
  private boolean logError = true;

  private boolean logUI = true;
  private boolean logPlayer = true;
  private boolean logConnection = true;
  private boolean logResource = true;

  private boolean showThread = true;

  private PrintStream printStream = System.out;

  public Logger(String caller) {
    CALLER = caller;
  }

  public static Logger getLogger(String tag) {
    return new Logger(tag);
  }

  public void log(LogType type, String s) {
    if (checkType(type))
      print(type, s);
  }

  public void log(LogType type, Throwable t) {
    if (checkType(type))
      print(type, t);
    t.printStackTrace();
  }

  public void log(LogType type, Object o) {
    if (checkType(type))
      print(type, o);
  }

  private void print(LogType type, Object message) {
    printStream.println(type + " " + Thread.currentThread().getName() + " " + CALLER + " " + message);
  }

  private boolean checkType(LogType type) {
    if (LogType.UI == type) {
      return logUI;
    }
    if (LogType.PLAYER == type) {
      return logPlayer;
    }
    if (LogType.RESOURCE == type) {
      return logResource;
    }
    if (LogType.NETWORK == type) {
      return logConnection;
    }
    if (LogType.ERROR == type) {
      return logError;
    }
    if (LogType.DEBUG == type) {
      return logDebug;
    }
    if (LogType.INFO == type) {
      return logInfo;
    }
    throw new IllegalArgumentException("Unknown type");
  }
}

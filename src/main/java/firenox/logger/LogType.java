package firenox.logger;

/**
 * Created by firenox on 6/13/16.
 */
public enum LogType {
  UI("[UI      ] "),
  PLAYER("[PLAYER  ] "),
  NETWORK("[NETWORK ] "),
  RESOURCE("[RESOURCE] "),
  JSON("[JSON    ] "),
  INFO("[INFO    ] "),
  DEBUG("[DEBUG   ] "),
  ERROR("[ERROR   ] ");

  String type;

  LogType(String type) {
    this.type = type;
  }

  @Override
  public String toString() {
    return type;
  }
}

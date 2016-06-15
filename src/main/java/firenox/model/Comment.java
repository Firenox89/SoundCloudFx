package firenox.model;

import firenox.io.Http;
import firenox.logger.LogType;
import firenox.logger.Logger;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by firenox on 11/11/15.
 */
public class Comment {
  private final JSONObject jsonObject;
  private Logger log = Logger.getLogger(getClass().getName());
  private String body;
  private User user;

  public Comment(JSONObject jsonObject) {
    this.jsonObject = jsonObject;
    parseJSON(jsonObject);
  }

  public User getUser() {
    return user;
  }

  public String getBody() {
    return body;
  }

  private void parseJSON(JSONObject jsonObject) {
    try {
      body = jsonObject.getString("body");
      user = ModelManager.getUser(jsonObject.getInt("user_id"));
    } catch (JSONException e) {
      log.log(LogType.JSON, e);
      log.log(LogType.JSON, Http.formatJSON(jsonObject.toString()));
    }
  }
}

package firenox.model;

import firenox.io.Http;
import firenox.logger.Logger;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by firenox on 11/11/15.
 */
public class Comment {
    private Logger log = Logger.getLogger(getClass().getName());
    private final JSONObject jsonObject;
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
            log.e(e);
            log.e(Http.formatJSON(jsonObject.toString()));
        }
    }
}

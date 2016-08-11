package firenox.model;

import firenox.io.BackgroundLoader;
import firenox.io.Http;
import firenox.io.RequestManager;
import firenox.logger.LogType;
import firenox.logger.Logger;
import javafx.application.Platform;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by firenox on 10/18/15.
 */
public class PagedList<E> extends SimpleListProperty<E> {

  private final static int LOADING_STEP_SIZE = 3;
  private final int limit;
  private final String url;
  private final Class<E> type;
  private String next_href = null;
  private boolean allLoaded = false;
  private Logger log = Logger.getLogger(getClass().getName());

  public PagedList(String url, int limit, Class<E> type) {
    super(FXCollections.observableArrayList());
    this.url = url;
    this.limit = LOADING_STEP_SIZE;
    this.type = type;

    BackgroundLoader.addTaskLimitQueue(() -> loadNextEntries(limit / LOADING_STEP_SIZE));
  }

  public void loadNextEntries() {
    BackgroundLoader.addTaskLimitQueue(() -> loadNextEntries(1));
  }

  private void loadNextEntries(int num) {
    long starttime = System.currentTimeMillis();
    for (int j = 0; j < num; j++) {
      if (!allLoaded) {
        String json = null;
        try {
          int page = 1;
          if (next_href == null) {
            json = RequestManager.getStringWithLimit(url, limit, page);
          } else {
            json = RequestManager.getString(next_href);
            //clear the href for the next round
            if (json == null)
              log.log(LogType.NETWORK, "Bad response for: " + next_href);
            next_href = null;
          }
          if (json != null) {
            JSONObject response = new JSONObject(json);
            BackgroundLoader.createTask(() -> parseResult(response));
            if (response.has("next_href")) {
              next_href = response.getString("next_href");
            }
            if (next_href == null) {
              allLoaded = true;
            }
          }
        } catch (JSONException e) {
          log.log(LogType.JSON, e);
          log.log(LogType.JSON, "url = " + url);
          log.log(LogType.JSON, "next_href = " + next_href);
          log.log(LogType.JSON, "body =\n" + Http.formatJSON(json));
        }
      }
    }
    long time = System.currentTimeMillis() - starttime;
    log.log(LogType.NETWORK, "Loading next entries for " + url + " took " + time + "ms.");
  }

  private void parseResult(JSONObject response) {
    try {
      JSONArray jsonArray = response.getJSONArray("collection");
      for (int i = 0; i < jsonArray.length(); i++) {
        String kind;
        if (jsonArray.getJSONObject(i).has("origin")) {
          //stream origins can be null...
          if (jsonArray.getJSONObject(i).isNull("origin"))
            kind = "null";
          else
            kind = jsonArray.getJSONObject(i).getJSONObject("origin").getString("kind");
        } else if (jsonArray.getJSONObject(i).has("kind")) {
          kind = jsonArray.getJSONObject(i).getString("kind");
        } else {
          kind = jsonArray.getJSONObject(i).getString("type");
        }
        if ((type == Track.class || type == PagedListEntry.class)
            && (kind.equals("track") || kind.equals("track-repost"))) {
          E entry = (E) ModelManager.getTrack(jsonArray.getJSONObject(i));
          Platform.runLater(() -> add(entry));
        } else if ((type == PlayList.class || type == PagedListEntry.class)
            && kind.equals("playlist") || kind.equals("playlist-repost")) {
          E entry = (E) ModelManager.getPlaylist(jsonArray.getJSONObject(i));
          Platform.runLater(() -> add(entry));
        } else if (type == Comment.class && kind.equals("comment")) {
          E entry = (E) ModelManager.getComment(jsonArray.getJSONObject(i));
          Platform.runLater(() -> add(entry));
        } else {
          log.log(LogType.JSON, "Kind-Type mismatch kind = " + kind + " type =" + type.getName());
//                            if (kind.equals("playlist"))
//                                log.e(Http.formatJSON(json));
        }
      }
    } catch (JSONException e) {
      log.log(LogType.JSON, e);
    }
  }

  @Override
  public E get(int index) {
    if ((size() - index) < 2) {
      loadNextEntries();
    }
    return super.get(index);
  }

  public boolean loadingComplete() {
    return allLoaded;
  }
}

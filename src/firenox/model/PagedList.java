package firenox.model;

import firenox.io.BackgroundLoader;
import firenox.io.Http;
import firenox.io.RequestManager;
import firenox.logger.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

/**
 * Created by firenox on 10/18/15.
 */
public class PagedList<E> extends ArrayList<E> {

    private final static int LOADING_STEP_SIZE = 3;
    private final int limit;
    private final String url;
    private final Class<E> type;
    private String next_href = null;
    private boolean allLoaded = false;
    private Logger log = Logger.getLogger(getClass().getName());
    private ArrayList<EntriesChangedListener> listeners = new ArrayList<>();

    public PagedList(String url, int limit, Class<E> type) {
        this.url = url;
        this.limit = LOADING_STEP_SIZE;
        this.type = type;

        loadNextEntries(limit / LOADING_STEP_SIZE);
    }

    public void loadNextEntries() {
        BackgroundLoader.addTask(() -> loadNextEntries(1));
    }

    private void loadNextEntries(int num) {
        for (int j = 0; j < num; j++) {
            ArrayList<E> newEntries;
            if (!allLoaded) {
                String json = null;
                try {
                    newEntries = new ArrayList<>();
                    int page = 1;
                    if (next_href == null) {
                        json = RequestManager.getStringWithLimit(url, limit, page);
                    } else {
                        json = RequestManager.getString(next_href);
                        //clear the href for the next round
                        next_href = null;
                    }
                    JSONObject response = new JSONObject(json);
                    JSONArray jsonArray = response.getJSONArray("collection");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        String kind;
                        if (jsonArray.getJSONObject(i).has("origin")) {
                            //that's the case from stream
                            kind = jsonArray.getJSONObject(i).getJSONObject("origin").getString("kind");
                        } else if (jsonArray.getJSONObject(i).has("kind")) {
                            kind = jsonArray.getJSONObject(i).getString("kind");
                        } else {
                            kind = jsonArray.getJSONObject(i).getString("type");
                        }
                        if (type == Track.class && (kind.equals("track") || kind.equals("track-repost"))) {
                            //apparently a track repost is called track-repost
                            E entry = (E) ModelManager.getTrack(jsonArray.getJSONObject(i));
                            newEntries.add(entry);
                        }
                        else if (type == PlayList.class && kind.equals("playlist")) {
                            //and a playlist repost is called playlist
                            E entry = (E) ModelManager.getPlaylist(jsonArray.getJSONObject(i));
                            newEntries.add(entry);
                        } else if (type == Comment.class && kind.equals("comment")) {
                            E entry = (E) ModelManager.getComment(jsonArray.getJSONObject(i));
                            newEntries.add(entry);
                        } else {
                            log.d("Kind-Type mismatch kind = " + kind + " type =" + type.getName());
//                            if (kind.equals("playlist"))
//                                log.e(Http.formatJSON(json));
                        }
                    }
                    if (response.has("next_href")) {
                        next_href = response.getString("next_href");
                    }
                    if (next_href == null) {
                        allLoaded = true;
                    }
                    addAll(newEntries);

                    final ArrayList<E> finalNewEntries = newEntries;
                    listeners.forEach(listener -> listener.entriesChanged(finalNewEntries));
                } catch (JSONException e) {
                    log.e(e);
                    log.e("url = " + url);
                    log.e("next_href = " + next_href);
                    log.e("json =\n" + Http.formatJSON(json));
                }
            }
        }
    }

    @Override
    public E get(int index) {
        if ((size() - index) < 2) {
            log.d("get next");
            loadNextEntries();
        }
        return super.get(index);
    }

    public void setNewEntriesLoadedListener(EntriesChangedListener listener) {
        log.i("setNewEntriesLoadedListener");
        listeners.add(listener);
    }

    public void removeListener(EntriesChangedListener listener) {
        log.i("removeListener");
        listeners.remove(listener);
    }
}

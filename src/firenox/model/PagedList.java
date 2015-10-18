package firenox.model;

import firenox.io.LogInHandler;
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

    private final int limit;
    private final String url;
    private final Class<E> type;
    private String next_href = null;
    private boolean allLoaded = false;
    private ArrayList<E> lastLoadedEntries;
    private Logger log = Logger.getLogger(getClass().getName());
    private EntrieChangedListener listener;

    public PagedList(String url, int limit, Class<E> type) {
        this.url = url;
        this.limit = limit;
        this.type = type;
        loadNextEntries();
    }

    public ArrayList<E> getLastLoadedEntries() {
        return lastLoadedEntries;
    }

    public void loadNextEntries() {
        log.d("loadNextEntries");
        ArrayList<E> newEntries = null;
        if (!allLoaded) {
            try {
                newEntries = new ArrayList<>();
                int page = 1;
                String json;
                if (next_href == null) {
                    json = LogInHandler.getStringWithLimit(url, limit, page);
                } else {
                    json = LogInHandler.getString(next_href);
                }
                JSONObject response = new JSONObject(json);
                JSONArray jsonArray = response.getJSONArray("collection");
                for (int i = 0; i < jsonArray.length(); i++) {
                    E entire = type.getConstructor(JSONObject.class).newInstance((JSONObject) jsonArray.get(i));
                    newEntries.add(entire);
                }
                next_href = response.getString("next_href");
                if (next_href == null) {
                    allLoaded = true;
                }
                lastLoadedEntries = newEntries;
                addAll(newEntries);
                if (listener != null) {
                    listener.entrieChanged();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public E get(int index) {
        if ((index / size()) >= 0.90) {
            loadNextEntries();
        }
        return super.get(index);
    }

    public void setNewEntriesLoadedListener(EntrieChangedListener listener) {
        log.d("setNewEntriesLoadedListener");
        this.listener = listener;
    }
}

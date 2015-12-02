package firenox.media;

import com.sun.media.jfxmedia.MediaManager;
import firenox.io.BackgroundLoader;
import firenox.logger.Logger;
import firenox.model.AbstractPagedListEntry;
import firenox.model.PagedList;
import firenox.model.Track;
import firenox.ui.UIManager;
import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

/**
 * Created by firenox on 10/6/15.
 */
public class AudioPlayerFx implements IAudioPlayer {

    private static Logger log = Logger.getLogger(AudioPlayerFx.class.getName());
    Media currentMedia;
    MediaPlayer player;
    private boolean isPlaying = false;
    private Property<Number> volumeSliderProb;
    private Runnable nextHandler;
    private Runnable previousHandler;
    private PagedList<AbstractPagedListEntry> currentPlaylist;
    private boolean repeat = false;
    private boolean shuffle = false;
    private Track currentTrack;

    AudioPlayerFx() {
    }

    @Override
    public void play() {
        if (player != null) {
            if (isPlaying) {
                player.pause();
                isPlaying = false;
                log.d("Pause");
            } else {
                player.play();
                isPlaying = true;
                log.d("Play");
            }
        }
    }

    @Override
    public void pause() {
        log.d("pause");
        player.pause();
    }

    @Override
    public void stop() {
        if (player != null) {
            player.stop();
        }
    }

    public void open(AbstractPagedListEntry track) {
        try {
            if (MediaManager.canPlayProtocol("https"))
                BackgroundLoader.addTaskWithTimeout(() ->
                {
                    currentTrack = (Track) track;
                    String url = currentTrack.getStreamURL();

                    log.d("open: " + url);
                    currentMedia = new Media(url);
                    stop();

                    currentTrack.getWaveform().resetProgress();
                    player = new MediaPlayer(currentMedia);

                    player.volumeProperty().bindBidirectional(volumeSliderProb);
                    player.setOnEndOfMedia(this::next);
                    //TODO: append listener to new player

                    player.play();
                    isPlaying = true;

                    UIManager.setTrackForPlayerUI(currentTrack, currentPlaylist);
                }, 3 * 1000);
        } catch (NoSuchMethodError error) {
            //the support came together with that method
            String version = com.sun.javafx.runtime.VersionInfo.getRuntimeVersion();
            log.e("JavaFx version does not support https protocol");
            log.e("Min version = 8.0.72. Current version "+version);
        }
    }

    public void open(PagedList<AbstractPagedListEntry> playlist, int startIndex) {
        currentPlaylist = playlist;
        open(currentPlaylist.get(startIndex));
    }

    @Override
    public void setVolume(double volume) {
    }

    public void bindVolume(Property<Number> volumeProb) {
        if (player != null) {
            player.volumeProperty().bindBidirectional(volumeProb);
        }
        volumeSliderProb = volumeProb;
    }

    @Override
    public void next() {
        log.d("next");
        if (nextHandler != null) {
            nextHandler.run();
        } else if (currentPlaylist != null && currentTrack != null) {
            if (shuffle) {
                open(currentPlaylist.get((int) (Math.random() * currentPlaylist.size())));
            } else {
                int current = currentPlaylist.indexOf(currentTrack);
                if (repeat) {
                    open(currentPlaylist.get(current));
                } else if (currentPlaylist.size() > current + 1) {
                    open(currentPlaylist.get(++current));
                } else {
                    //try to load new entries
                    currentPlaylist.loadNextEntries();
                    //TODO: create temp listener
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (currentPlaylist.size() > current + 1) {
                        open(currentPlaylist.get(++current));
                    } else {
                        log.d("end of list");
                    }
                }
            }
        }
    }

    @Override
    public void previous() {
        log.d("previous");
        if (previousHandler != null) {
            previousHandler.run();
        } else if (currentPlaylist != null && currentTrack != null) {
            int current = currentPlaylist.indexOf(currentTrack);
            if (current > 1) {
                open(currentPlaylist.get(--current));
            }
        }
    }

    @Override
    public void toggleRepeat() {
        log.d("toggleRepeat");
        repeat = !repeat;
    }

    @Override
    public void setPositon(double positon) {
        log.d("setPositon");
    }

    @Override
    public void setMediaStoppedListener(MediaEventListener listener) {
        log.d("setMediaStoppedListener");
        player.setOnStopped(listener::mediaEvent);
    }

    @Override
    public void setMediaEndListener(MediaEventListener listener) {
        log.d("setMediaEndListener");
        player.setOnEndOfMedia(listener::mediaEvent);
    }

    @Override
    public void setMediaPausedListener(MediaEventListener listener) {
        log.d("setMediaPausedListener");
        player.setOnPaused(listener::mediaEvent);
    }

    @Override
    public void toggleShuffle() {
        log.d("toggleShuffle");
        shuffle = !shuffle;
    }

    public void setProgressTimeListener(ChangeListener<Duration> listener) {
        player.currentTimeProperty().addListener(listener);
    }

    public void setNextHandler(Runnable handler) {
        nextHandler = handler;
    }

    public void setPreviousHandler(Runnable handler) {
        previousHandler = handler;
    }

    public void seek(Duration seekTime) {
        player.seek(seekTime);
    }


}

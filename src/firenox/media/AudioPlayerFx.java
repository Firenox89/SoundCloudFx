package firenox.media;

import firenox.logger.Logger;
import firenox.model.PagedList;
import firenox.model.Track;
import firenox.ui.UIManager;
import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.util.ArrayList;

/**
 * Created by firenox on 10/6/15.
 */
public class AudioPlayerFx implements IAudioPlayer {

    private static Logger log = Logger.getLogger(AudioPlayerFx.class.getName());
    Media currentMedia;
    MediaPlayer player;
    private boolean isPlaying = false;
    private double defaultVolume = 0.2;
    private Property<Number> volumeSliderProb;
    private Runnable nextHandler;
    private Runnable previousHandler;
    private PagedList<Track> currentPlaylist;
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

    public void open(Track track) {
        currentTrack = track;
        String url = track.getStreamURL();

        log.d("open: " + url);
        currentMedia = new Media(url);
        stop();

        player = new MediaPlayer(currentMedia);

        player.volumeProperty().bindBidirectional(volumeSliderProb);
        volumeSliderProb.setValue(defaultVolume);
        player.setOnEndOfMedia(this::next);
        //TODO: append listener to new player

        player.play();
        isPlaying = true;

        UIManager.setTrackForPlayerUI(currentTrack, currentPlaylist);
    }

    public void open(PagedList<Track> playlist, int startIndex) {
        currentPlaylist = playlist;
        open(currentPlaylist.get(startIndex));
    }

    @Override
    public void setVolume(double volume) {
        log.d("volume = " + volume);
        volumeSliderProb.setValue(volume);
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
                } else {
                    open(currentPlaylist.get(++current));
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

    public void setProgressTimeListener(ChangeListener<Duration> listener) {
        player.currentTimeProperty().addListener(listener);
    }

    public void setNextHandler(Runnable handler) {
        nextHandler = handler;
    }

    public void setPreviousHandler(Runnable handler) {
        previousHandler = handler;
    }

    public void seek() {
    }


}

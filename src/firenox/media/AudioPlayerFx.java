package firenox.media;

import firenox.logger.Logger;
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

    AudioPlayerFx() {
    }

    AudioPlayerFx(String url) {
        currentMedia = new Media(url);
        player = new MediaPlayer(currentMedia);

        player.setVolume(new Double(1));
        player.play();
        isPlaying = true;
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

    @Override
    public void open(String url) {
        log.d("open: " + url);
        currentMedia = new Media(url);
        stop();

        player = new MediaPlayer(currentMedia);
        player.play();
        isPlaying = true;
    }

    @Override
    public void setVolume(double volume) {
        log.d("volume = " + volume);
        if (player != null) {
            player.setVolume(volume);
        }
    }

    @Override
    public void next() {
        log.d("next");
        if (player != null) {

        }
    }

    @Override
    public void previous() {
        log.d("previous");

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

}

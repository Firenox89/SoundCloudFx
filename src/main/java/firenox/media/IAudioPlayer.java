package firenox.media;

import firenox.model.PagedListEntry;
import javafx.util.Duration;

/**
 * Created by firenox on 10/7/15.
 */
interface IAudioPlayer {
    void play();

    void pause();

    void stop();

    void open(PagedListEntry track);

    void setVolume(double volume);

    void next();

    void previous();

    void toggleRepeat();

    void seek(Duration positon);

    void setMediaStoppedListener(MediaEventListener listener);

    void setMediaEndListener(MediaEventListener listener);

    void setMediaPausedListener(MediaEventListener listener);

    void toggleShuffle();
}

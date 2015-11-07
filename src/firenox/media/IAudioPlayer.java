package firenox.media;

import firenox.model.Track;

/**
 * Created by firenox on 10/7/15.
 */
interface IAudioPlayer {
    void play();

    void pause();

    void stop();

    void open(Track track);

    void setVolume(double volume);

    void next();

    void previous();

    void toggleRepeat();

    void setPositon(double positon);

    void setMediaStoppedListener(MediaEventListener listener);

    void setMediaEndListener(MediaEventListener listener);

    void setMediaPausedListener(MediaEventListener listener);

    void toggleShuffle();
}

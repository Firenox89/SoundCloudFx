package firenox.media;

/**
 * Created by firenox on 10/11/15.
 */
public class AudioManager {
    private static IAudioPlayer player = new AudioPlayerFx();
    private static AudioPlayerFx playerFx = new AudioPlayerFx();

    public static IAudioPlayer getPlayer() {
        return player;
    }

    public static AudioPlayerFx getPlayerFx() {
        return playerFx;
    }
}

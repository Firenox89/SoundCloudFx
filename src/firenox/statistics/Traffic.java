package firenox.statistics;

import firenox.logger.Logger;

/**
 * Created by firenox on 10/10/15.
 */
public class Traffic {

    private static int imageTraffic;
    private static int imageConnects = 0;

    private static Logger log = Logger.getLogger(Traffic.class.getName());
    private static int mp3Traffic;
    private static int mp3Connects;
    private static int jsonTraffic;
    private static int jsonConnects;

    public static void addImageTraffic(int numberOfBytes) {
        imageTraffic += numberOfBytes;
        imageConnects++;
        log.d("imageTraffic = " + imageTraffic);
    }

    public static void addMP3Traffic(int numberOfBytes) {
        mp3Traffic += numberOfBytes;
        mp3Connects++;
        log.d("mp3Traffic = " + mp3Traffic);
    }

    public static void addJSONTraffic(int numberOfBytes) {
        jsonTraffic += numberOfBytes;
        jsonConnects++;
        log.d("jsonTraffic = " + jsonTraffic);
    }
}

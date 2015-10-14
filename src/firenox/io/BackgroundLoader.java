package firenox.io;

import firenox.logger.Logger;

import java.util.ArrayList;

/**
 * Created by firenox on 10/12/15.
 */
public class BackgroundLoader extends Thread {
    private static Logger log = new Logger(BackgroundLoader.class.getName());
    private static ArrayList<Runnable> taskQueue = new ArrayList<>();
    private static int SLEEP_TIME = 300;
    private static BackgroundLoader instance = new BackgroundLoader();

    private BackgroundLoader() {
    }

    public static void addTask(Runnable task) {
        taskQueue.add(task);
    }

    public static void init() {
        if (!instance.isAlive()) {
            instance.setDaemon(true);
            instance.start();
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                while (!taskQueue.isEmpty()) {
                    taskQueue.get(0).run();
                    taskQueue.remove(0);
                }
            } catch (Exception e) {
                log.e(e);
            }
            try {
                Thread.sleep(SLEEP_TIME);
            } catch (InterruptedException e) {
                log.e(e);
            }
        }
    }
}

package firenox.io;

import firenox.logger.Logger;

import java.util.ArrayList;
import java.util.concurrent.*;

/**
 * Created by firenox on 10/12/15.
 */
public class BackgroundLoader extends Thread {
    private static Logger log = new Logger(BackgroundLoader.class.getName());
    private static ArrayList<Runnable> taskQueue = new ArrayList<>();
    private static int SLEEP_TIME = 300;
    private static BackgroundLoader instance = new BackgroundLoader();
    private static SingleExecutor singleExecuter = new SingleExecutor();
    static ExecutorService executor = Executors.newSingleThreadExecutor();

    private BackgroundLoader() {
    }

    public static void init() {
        if (!instance.isAlive()) {
            instance.setDaemon(true);
            //lets see if that is a wise thing to do
            instance.setPriority(Thread.NORM_PRIORITY - 1);
            instance.start();
        }
        if (!singleExecuter.isAlive()) {
            singleExecuter.setDaemon(true);
            singleExecuter.start();
        }
    }

    public static void addTask(Runnable task) {
        taskQueue.add(task);
    }

    public static void addTaskLimitQueue(Runnable task) {
        if (taskQueue.size() < 3)
            taskQueue.add(task);
    }

    public static void addTaskWithNoQueue(Runnable task) {
        if (!singleExecuter.isRunning()) {
            synchronized (singleExecuter) {
                singleExecuter.setTask(task);
                singleExecuter.notify();
            }
        }
    }

    public static void addTaskWithTimeout(Runnable task, int timeout) {
        Future future = executor.submit(() ->
        {
            try {
                task.run();
                //don't kill the executor
            } catch (Throwable t) {
                t.printStackTrace();
            }
        });
        try {
            future.get(timeout, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            future.cancel(true);
            //apache client hangs up, reinit wrapper
            //should be done in the api wrapper
            LogInHandler.init();
            addTaskWithTimeout(task, timeout);
            log.e(e);
        } catch (Exception e) {
            log.e(e);
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

    static class SingleExecutor extends Thread {
        private Runnable runnable;
        private boolean running = false;

        public boolean isRunning() {
            return running;
        }

        public void setTask(Runnable runnable) {
            this.runnable = runnable;
        }

        @Override
        public void run() {
            while (true) {
                if (runnable != null) {
                    running = true;
                    runnable.run();
                    running = false;
                    runnable = null;
                }
                synchronized (this) {
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}

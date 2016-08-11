package firenox.io;

import firenox.logger.LogType;
import firenox.logger.Logger;
import javafx.concurrent.Task;

import java.util.ArrayList;
import java.util.concurrent.*;

/**
 * Created by firenox on 10/12/15.
 */
public class BackgroundLoader extends Thread {
  private static ExecutorService executor = Executors.newSingleThreadExecutor();
  private static ExecutorService executorService;
  private static Logger log = new Logger(BackgroundLoader.class.getName());
  private static ArrayList<Runnable> taskQueue = new ArrayList<>();
  private static int SLEEP_TIME = 50;
  private static BackgroundLoader instance = new BackgroundLoader();

  public static void init() {
    if (!instance.isAlive()) {
      instance.setDaemon(true);
      //lets see if that is a wise thing to do
      instance.setPriority(Thread.MIN_PRIORITY);
      instance.setName("Network Thread");
      instance.start();
    }
    executor.submit(() -> Thread.currentThread().setName("Network Thread 2"));
    executorService = Executors.newCachedThreadPool(r -> {
      Thread t = new Thread(r);
      t.setDaemon(true);
      t.setPriority(Thread.NORM_PRIORITY - 1);
      return t;
    });
  }

  public static void createTask(Runnable task) {
    executorService.submit(
        new Task<Void>() {
          @Override
          protected Void call() throws Exception {
            task.run();
            return null;
          }
        });
  }

  public static void addTaskLimitQueue(Runnable task) {
    if (taskQueue.size() < 2)
      taskQueue.add(task);
  }

  public static void addTaskWithTimeout(Runnable task, int timeout) {
    Future future = executor.submit(() ->
    {
      try {
        task.run();
        //don't kill the executor
      } catch (Throwable t) {
        log.log(LogType.ERROR, t);
      }
    });
    try {
      future.get(timeout, TimeUnit.MILLISECONDS);
    } catch (TimeoutException e) {
      future.cancel(true);
      //apache client hangs up, reinit wrapper
      //should be done in the api wrapper
      SessionHandler.init();
      addTaskWithTimeout(task, timeout);
      log.log(LogType.NETWORK, e);
    } catch (Exception e) {
      log.log(LogType.ERROR, e);
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
        log.log(LogType.ERROR, e);
      }
      try {
        Thread.sleep(SLEEP_TIME);
      } catch (InterruptedException e) {
        log.log(LogType.ERROR, e);
      }
    }
  }
}

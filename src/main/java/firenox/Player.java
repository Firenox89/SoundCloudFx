package firenox;

import com.soundcloud.api.Endpoints;
import com.soundcloud.api.EndpointsV2;
import com.soundcloud.api.Request;
import firenox.io.BackgroundLoader;
import firenox.io.Http;
import firenox.io.SessionHandler;
import firenox.io.RequestManager;
import firenox.model.ModelManager;
import firenox.ui.UIManager;
import firenox.ui.WaveRenderer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.*;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

//import jxgrabkey.HotkeyConflictException;
//import jxgrabkey.HotkeyListener;
//import jxgrabkey.JXGrabKey;
//import javax.swing.*;
//import java.awt.event.KeyEvent;

public class Player extends Application {

  boolean hotkeyEventReceived;

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) {
//        testLogic();
//        testScene(primaryStage);
//        testLib();

//        testJson();
//        testSoundcloudApi();
    BackgroundLoader.init();
    BackgroundLoader.createTask(() -> SessionHandler.init());

    UIManager.init(primaryStage);
  }

  private void testJson() {
    int track_id = 231999572;
    int me_id = ModelManager.getMe().getId();
//        String url = "/users/"+me_id+"/favorites/"+track_id;
//        String url = "/"+me_id+"/favorites/"+track_id;
    String repostApi = "https://api-v2.soundcloud.com/stream/users/5505586/reposts?client_id=02gUJC0hH2ct1EGOcYXQIzRFU91c72Ea&limit=10&offset=0&linked_partitioning=1&app_version=be9afa9";
    String url = "/me/favorites/" + track_id;
    try {
      String resp = Http.formatJSON(Http.getString(RequestManager.request(
          Request.to(url)
      )));
      System.out.println("resp = " + resp);
    } catch (IOException e) {
      e.printStackTrace();
    }
    try {
      String resp = Http.formatJSON(Http.getString(RequestManager.unlikeTrack(track_id)));
      System.out.println("resp = " + resp);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void testSoundcloudApi() {
    int me = 5505586;
//client_id=02gUJC0hH2ct1EGOcYXQIzRFU91c72Ea&app_version=cc53575
    String client_id = "&client_id=0397513cf92bf19c7b9e3eb8c2587f0f";
//        String client_id = "client_id=02gUJC0hH2ct1EGOcYXQIzRFU91c72Ea&app_version=cc53575";
    String toTest = EndpointsV2.getStreamUsers(me) + client_id;
    System.out.println("toTest = " + toTest);

    try {

      HttpGet httpget = new HttpGet(toTest);
      System.out.println("httpget = " + httpget);
      DefaultHttpClient httpclient = new DefaultHttpClient();
      HttpResponse res = httpclient.execute(httpget);
      System.out.println("res = " + res);
      System.out.println("res.getStatusLine() = " + res.getStatusLine());
      String s = Http.getString(res);
      System.out.println("s = " + Http.formatJSON(s));
    } catch (IOException e) {
      e.printStackTrace();
    }


//        System.out.println(result);
  }

  private void testLogic() {
    File testDir = new File("/home/firenox/IdeaProjects/SoundCloudPlayer/src/firenox/ui");
    Map<String, File> list =
        Arrays.stream(testDir.listFiles())
            .filter(File::isFile)
            .collect(Collectors.toMap(File::getName, Function.<File>identity()));
    list.forEach((s, f) -> System.out.println(s));
    System.exit(0);
  }

  private void testLink() {
    System.out.println(Http.formatJSON(RequestManager.getString(Endpoints.MY_FAVORITES)));
  }

//    private void testLib() {
//        int index = 15;
//        //Load JXGrabKey lib
//        try {
//            System.load(new File("/media/firenox/X/git/SoundCloudFx/lib/jxgrabkey-0.3.2/lib/libJXGrabKey.so").getCanonicalPath());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        //Enable Debug Output
//        JXGrabKey.setDebugOutput(true);
//
//        //Register some Hotkey
//        try {
//
//            int key = KeyEvent.VK_K, mask = KeyEvent.CTRL_MASK | KeyEvent.ALT_MASK | KeyEvent.SHIFT_MASK;
////            int key = KeyEvent.VK_F2, mask = KeyEvent.ALT_MASK; //Conflicts on GNOME
//
//            JXGrabKey.getInstance().registerAwtHotkey(index, mask, key);
//        } catch (HotkeyConflictException e) {
//            JOptionPane.showMessageDialog(null, e.getMessage(), e.getClass().getName(), JOptionPane.ERROR_MESSAGE);
//
//            JXGrabKey.getInstance().cleanUp(); //Automatically unregisters Hotkeys and Listeners
//            //Alternatively, just unregister the key causing this or leave it as it is
//            //the key may not be grabbed at all or may not respond when numlock, capslock or scrollock is on
//            return;
//        }
//
//        //Implement HotkeyListener
//        HotkeyListener hotkeyListener = hotkey_idx -> {
//            System.out.println("hotkey_idx = " + hotkey_idx);
//            hotkeyEventReceived = true;
//        };
//
//        //Add HotkeyListener
//        JXGrabKey.getInstance().addHotkeyListener(hotkeyListener);
//
//        //Wait for Hotkey Event
//        while (!hotkeyEventReceived) {
//            try {
//                Thread.sleep(1000);
//                System.out.println("wait");
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//
//        // Shutdown JXGrabKey
//        JXGrabKey.getInstance().unregisterHotKey(1); //Optional
//        JXGrabKey.getInstance().removeHotkeyListener(hotkeyListener); //Optional
//        JXGrabKey.getInstance().cleanUp();
//    }

  private void testScene(Stage stage) {

    InputStream is;
    try {
      is = new FileInputStream(new File("wave.png"));
      Image image = WaveRenderer.init(is, 100, 800).renderWaveFx();
      StackPane root = new StackPane();
      ImageView imageView = new ImageView(image);
      root.getChildren().add(imageView);
      Scene scene = new Scene(root, image.getWidth(), image.getHeight());
      stage.setScene(scene);
      stage.show();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

}
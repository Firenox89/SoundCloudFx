package firenox;

import com.soundcloud.api.Endpoints;
import firenox.io.BackgroundLoader;
import firenox.io.Http;
import firenox.io.LogInHandler;
import firenox.io.RequestManager;
import firenox.statistics.Traffic;
import firenox.ui.UIManager;
import firenox.ui.WaveRenderer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.*;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Player extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
//        testLogic();
//        testScene(primaryStage);

        //should return true in the version I desire
        //MediaManager.canPlayProtocol("https");

        UIManager.init(primaryStage);
        LogInHandler.init();
        BackgroundLoader.init();
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

    private void saveStream(InputStream inputStream, String filePath) {
        try {
            FileOutputStream fos = new FileOutputStream(new File(filePath));
            int read = 0;
            byte[] buffer = new byte[1024 * 4];
            while ((read = inputStream.read(buffer)) != -1) {
                fos.write(buffer);
                Traffic.addMP3Traffic(read);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
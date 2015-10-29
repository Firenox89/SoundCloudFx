package firenox.ui;

import firenox.io.RequestManager;
import firenox.model.User;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

import java.io.IOException;

/**
 * Created by firenox on 10/8/15.
 */
public class UserScene extends Scene {

    private final int artwork_width = 100;
    private final int artwork_height = 100;
    StackPane root;
    HBox userInfo = new HBox();
    private User currentUser;
    private Image avatar;
    private String name;

    public UserScene(StackPane root, double width, double height) {
        super(root, width, height);
        this.root = root;
    }

    private void createDetails() {
        root.getChildren().clear();
        root.getChildren().add(new Label(name));
        root.getChildren().add(new ImageView(avatar));
    }

    public void updateUser(User currentUser) {
        this.currentUser = currentUser;
        try {
            avatar = new Image(RequestManager.getResource(currentUser.getAvatar_url()));
            name = currentUser.getUsername();
        } catch (IOException e) {
            e.printStackTrace();
        }
        createDetails();
    }
}

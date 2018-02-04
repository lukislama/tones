package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Author of main icon: https://www.flaticon.com/authors/gregor-cresnar
 * Author of confirmation dialog icon: https://www.flaticon.com/authors/roundicons-freebies
 * Author of error dialog icon: http://www.flaticon.com/authors/madebyoliver
 */

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Tones");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.getIcons().add(new Image(this.getClass().getResource("musical-note.png").toString()));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}

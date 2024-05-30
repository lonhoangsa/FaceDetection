package org.source.facedetection;

import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.opencv.core.Core;

import java.io.IOException;

public class Main extends javafx.application.Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("hello-view.fxml"));
        BorderPane root = (BorderPane) fxmlLoader.load();
//        root.setStyle("-fx-background-color: whitesmoke;");
        Scene scene = new Scene(root, 800, 800);
        stage.setTitle("FaceDetect");

        stage.setScene(scene);
        stage.show();

        CameraController controller = fxmlLoader.getController();
        controller.init();

        stage.setOnCloseRequest((new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we)
            {
                controller.setClosed();
            }
        }));
    }
    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        launch();
    }
}
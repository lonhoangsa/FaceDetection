package org.source.facedetection;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;
import org.opencv.videoio.VideoCapture;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class CameraController extends Detector {

    @FXML
    private Button cameraButton;
    @FXML
    private ImageView originalFrame;
    private ScheduledExecutorService timer;
    private VideoCapture capture;
    private boolean cameraActive;
    private String haarSource = "src/main/resources/org/source/facedetection/haarcascades/haarcascade_frontalface_alt.xml";
    private String lbpSource = "src/main/resources/org/source/facedetection/lbpcascades/lbpcascade_frontalface_improved.xml";

    protected void init()
    {
        this.capture = new VideoCapture();
        originalFrame.setFitWidth(800);
        originalFrame.setPreserveRatio(true);
        this.cameraButton.setDisable(false);
    }

    @FXML
    protected void startCamera()
    {
        if (!this.cameraActive)
        {
            this.capture.open(0);

            if (this.capture.isOpened())
            {
                this.cameraActive = true;
//                System.out.println("bat cam");
                // 30fps
                Runnable frameGrabber = new Runnable() {

                    @Override
                    public void run()
                    {
//                        System.out.println("grab frame");
                        Mat frame = grabFrame();
                        Image imageToShow = Utils.mat2Image(frame);
                        updateImageView(originalFrame, imageToShow);
                    }
                };

                this.timer = Executors.newSingleThreadScheduledExecutor();
                this.timer.scheduleAtFixedRate(frameGrabber, 0, 16, TimeUnit.MILLISECONDS);

                // update the button content
                this.cameraButton.setText("Stop Camera");
            }
            else
            {
                // log the error
                System.err.println("Failed to open the camera connection...");
            }
        }
        else
        {
            this.cameraActive = false;
            this.cameraButton.setText("Start Camera");

            this.stopAcquisition();
            updateImageView(originalFrame, null);
        }
    }
    private Mat grabFrame()
    {
        Mat frame = new Mat();
        this.faceCascade = new CascadeClassifier(lbpSource);
//        System.out.println("chon thu vien");
        if (this.capture.isOpened())
        {
            try
            {
                this.capture.read(frame);
                if (!frame.empty())
                {
                    super.detectAndDisplay(frame);
                }

            }
            catch (Exception e)
            {
                System.err.println("Exception during the image elaboration: " + e);
            }
        }

        return frame;
    }
    private void stopAcquisition()
    {
        if (this.timer!=null && !this.timer.isShutdown())
        {
            try
            {
                // stop the timer
                this.timer.shutdown();
                this.timer.awaitTermination(33, TimeUnit.MILLISECONDS);
            }
            catch (InterruptedException e)
            {
                // log any exception
                System.err.println("Exception in stopping the frame capture, trying to release the camera now... " + e);
            }
        }

        if (this.capture.isOpened())
        {
            // release the camera
            this.capture.release();
        }
    }
    private void updateImageView(ImageView view, Image image)
    {
        Utils.onFXThread(view.imageProperty(), image);
    }
    protected void setClosed()
    {
        this.stopAcquisition();
    }

    @FXML
    private Button chooseImage;
    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }
    @FXML
    private void changeScene1(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ChooseImage.fxml"));
            Parent root = loader.load();

            // Lấy controller của scene2
            ChooseImageController chooseImageController = loader.getController();
            chooseImageController.setStage(stage);

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

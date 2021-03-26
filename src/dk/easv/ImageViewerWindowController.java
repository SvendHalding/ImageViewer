package dk.easv;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class ImageViewerWindowController
{
    private boolean slideshowRunning = false;
    private boolean threadStarted = false;
    private int milliseconds = 1000;

    private final List<Image> images = new ArrayList<>();
    private int currentImageIndex = 0;

    @FXML
    Parent root;

    @FXML
    private ImageView imageView;

    @FXML
    private Slider slideShowTimer;

    @FXML
    private Button btnStartStop;

    @FXML
    private Label pathToImage;

    private Thread th;

    public void initialize() {
        slideShowTimer.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                milliseconds = t1.intValue();
            }
        });
    }

    @FXML
    private void handleBtnLoadAction()
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select image files");
        fileChooser.getExtensionFilters().add(new ExtensionFilter("Images",
                "*.png", "*.jpg", "*.gif", "*.tif", "*.bmp"));
        List<File> files = fileChooser.showOpenMultipleDialog(new Stage());

        if (!files.isEmpty())
        {
            files.forEach((File f) ->
            {
                images.add(new Image(f.toURI().toString()));
            });
            displayImage();
        }
    }

    @FXML
    private void handleBtnPreviousAction()
    {
        if (!images.isEmpty())
        {
            currentImageIndex =
                    (currentImageIndex - 1 + images.size()) % images.size();
            displayImage();
        }
    }

    @FXML
    private void handleBtnNextAction()
    {
        if (!images.isEmpty())
        {
            currentImageIndex = (currentImageIndex + 1) % images.size();
            displayImage();
        }
    }

    @FXML
    private void handleStartStopSlideshow() {
        if(!slideshowRunning) {
            slideshowRunning = true;
            btnStartStop.setText("Stop Slide");
            Task task = new Task<Void>() {
                @Override
                public Void call() throws Exception {
                    while(slideshowRunning) {
                        System.out.println("test");
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                displayImage();
                                currentImageIndex = (currentImageIndex + 1) % images.size();
                            }
                        });

                        Thread.sleep(milliseconds);
                    }
                    return null;
                }
            };
            th = new Thread(task);
            th.setDaemon(true);
            th.start();

        } else {
            btnStartStop.setText("Start Slide");
            slideshowRunning = false;
        }
    }

    private void displayImage()
    {
        if (!images.isEmpty())
        {
            Image newImage = images.get(currentImageIndex);
            imageView.setImage(newImage);
            pathToImage.setText(newImage.getUrl());
        }
    }
}
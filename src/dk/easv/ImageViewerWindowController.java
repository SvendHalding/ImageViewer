package dk.easv;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.io.File;
import javafx.application.Platform;
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
    private final List<Image> images = new ArrayList<>();
    public Button btnStartStopSlide;
    public Slider slider;
    public Label imageName;
    private int currentImageIndex = 0;
    private int sliderTimer = 5000;
    private boolean sliderRunning = false;

    @FXML
    Parent root;

    @FXML
    private ImageView imageView;

    public void initialize() {
    slider.valueProperty().addListener((observable, oldvalue, newvalue)-> {sliderTimer = newvalue.intValue();} );
    }


    @FXML
    private  void handleBtnStartStop() {

        if (!sliderRunning) {
            sliderRunning = true;
            btnStartStopSlide.setText("Stop Slider");

            Task task = new Task<Void>() {
                @Override
                public Void call() throws Exception {
                while(sliderRunning) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            displayImage();
                            currentImageIndex = (currentImageIndex + 1) % images.size();
                        }
                    });

                    Thread.sleep(sliderTimer);
                }
                return null;
                }

            };
            Thread th = new Thread(task);
            th.setDaemon(true);
            th.start();
        }
        else {
            sliderRunning = false;
            btnStartStopSlide.setText("Start Slider");
        }

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

    private void displayImage()
    {
        if (!images.isEmpty())
        {
            Image image = images.get(currentImageIndex);
            imageView.setImage(image);
            String url = image.getUrl();
            String[] parts = url.split("/");
            String fileName = parts[parts.length - 1];
            imageName.setText(fileName);

        }
    }
}
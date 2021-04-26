package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private File directory;

    @FXML
    private File[] files;

    @FXML
    private ArrayList<File> songs;

    @FXML
    private Button playBtn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        songs = new ArrayList<File>();
        directory = new File("music");
        files = directory.listFiles();
        if (files != null){
            for (File file : files) {
                songs.add(file);
            }
        }
    }

    public void playMedia(){

    }
}

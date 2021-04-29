package sample;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Controller implements Initializable {

    @FXML
    private File directory;

    @FXML
    private File[] files;

    @FXML
    private ArrayList<File> songs;

    @FXML
    private Button playBtn, nextBtn, prevBtn, nameBtn;

    @FXML
    private Label songLbl;

    @FXML
    private HBox songName;

    @FXML
    private Pane songPane;

    @FXML
    private TableView songTbl;

    private int songNumber;

    private Media media;
    private MediaPlayer mediaPlayer;
    private int status;
    private ObservableList<ObservableList> data;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        songs = new ArrayList<File>();
        directory = new File("music");
        files = directory.listFiles();
        data = FXCollections.observableArrayList();

        ObservableList<String> row = FXCollections.observableArrayList();
        if (files != null){
            for (File file : files) {
                songs.add(file);
                row.add(file.getName());
                HBox hb = new HBox();
                JFXButton b = new JFXButton(file.getName());
                hb.getChildren().add(b);
                songPane.getChildren().add(hb);

            }
            data.add(row);
            songTbl.getItems().add(data);
        }

        media = new Media(songs.get(songNumber).toURI().toString());
        mediaPlayer = new MediaPlayer(media);

        songLbl.setText(songs.get(songNumber).getName());
    }


    public void playMedia(){
        if (status == 0){
            mediaPlayer.play();
            status=1;
        }else if(status == 1){
            mediaPlayer.pause();
            status=0;
        }

    }

    public void nextMedia(){
        if(songNumber < songs.size()-1){
            songNumber++;
        }else {
            songNumber = 0;
        }
        mediaPlayer.stop();
        status=0;
        media = new Media(songs.get(songNumber).toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        songLbl.setText(songs.get(songNumber).getName());
        playMedia();
    }

    public void prevMedia(){
        if(songNumber > 0){
            songNumber--;
        }else {
            songNumber = songs.size() - 1;
        }
        mediaPlayer.stop();
        status=0;
        media = new Media(songs.get(songNumber).toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        songLbl.setText(songs.get(songNumber).getName());
        playMedia();
    }
}

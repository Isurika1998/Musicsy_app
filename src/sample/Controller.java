package sample;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.collections.MapChangeListener;

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
    private Label songLbl, artistLbl;

    @FXML
    private HBox songName;

    @FXML
    private Pane songPane;

    @FXML
    private ListView songList;

    private int songNumber;

    private Media media;
    private MediaPlayer mediaPlayer;
    private String title;
    private String artist;
    private String album;

    private int status;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        songs = new ArrayList<File>();
        directory = new File("music");
        files = directory.listFiles();

        if (files != null){
            for (File file : files) {
                songs.add(file);
                songList.getItems().add(file.getName());
            }
        }

        media = new Media(songs.get(songNumber).toURI().toString());
        songLbl.setText("");
        artistLbl.setText("");
        media.getMetadata().addListener((MapChangeListener.Change<? extends String, ? extends Object> c) -> {
            if (c.wasAdded()) {
                if ("artist".equals(c.getKey())) {
                    artist = c.getValueAdded().toString();
                    artistLbl.setText(artist);
                } else if ("title".equals(c.getKey())) {
                    title = c.getValueAdded().toString();
                    songLbl.setText(title);
                } else if ("album".equals(c.getKey())) {
                    album = c.getValueAdded().toString();
                }
            }
        });

        mediaPlayer = new MediaPlayer(media);
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
        songLbl.setText("");
        artistLbl.setText("");
        media = new Media(songs.get(songNumber).toURI().toString());
        media.getMetadata().addListener((MapChangeListener.Change<? extends String, ? extends Object> c) -> {
            if (c.wasAdded()) {
                if ("artist".equals(c.getKey())) {
                    artist = c.getValueAdded().toString();
                    artistLbl.setText(artist);
                } else if ("title".equals(c.getKey())) {
                    title = c.getValueAdded().toString();
                    songLbl.setText(title);
                } else if ("album".equals(c.getKey())) {
                    album = c.getValueAdded().toString();
                }
            }
        });

        mediaPlayer = new MediaPlayer(media);
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
        songLbl.setText("");
        artistLbl.setText("");

        media = new Media(songs.get(songNumber).toURI().toString());
        media.getMetadata().addListener((MapChangeListener.Change<? extends String, ? extends Object> c) -> {
            if (c.wasAdded()) {
                if ("artist".equals(c.getKey())) {
                    artist = c.getValueAdded().toString();
                    artistLbl.setText(artist);
                } else if ("title".equals(c.getKey())) {
                    title = c.getValueAdded().toString();
                    songLbl.setText(title);
                } else if ("album".equals(c.getKey())) {
                    album = c.getValueAdded().toString();
                }
            }
        });
        
        mediaPlayer = new MediaPlayer(media);
        playMedia();
    }
}

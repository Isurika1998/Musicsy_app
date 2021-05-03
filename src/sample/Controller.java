package sample;

import com.jfoenix.controls.JFXButton;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import javafx.collections.MapChangeListener;
import javafx.scene.paint.Color;

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
    private Label songLbl, artistLbl, time1Lbl, time2Lbl;

    @FXML
    private Pane pane;

    @FXML
    private VBox pnItems = null;

    @FXML
    private GridPane playList;

    @FXML
    private HBox songName;

    @FXML
    private Pane songPane;

    @FXML
    private ListView songList;

    @FXML
    private Slider volumeSlider;

    @FXML
    private ProgressBar progressBar;

    private int songNumber;

    private Media media,playListMedia;
    private MediaPlayer mediaPlayer;
    private String title;
    private String artist;
    private String album;

    private Timer timer;
    private TimerTask task;
    private boolean running;

    private int status;
    private int imgFlag;



    PreparedStatement pstmt=null;
    Connection con=null;
    ResultSet rs=null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {


        songs = new ArrayList<File>();
        directory = new File("D:\\Songs");
        files = directory.listFiles();
        int count=0;
        if (files != null){

            for (File file : files) {
                songs.add(file);
                count++;
            }
        }
        Node[] nodes = new Node[count];
        int[] playlistImgFlag = new int[count];
        int playListSongNo = 0;

        for (File file : files) {
            try {
                final int j = playListSongNo;

                //add playlist songs
                nodes[playListSongNo] = FXMLLoader.load(getClass().getResource("../view/item.fxml"));

                playListMedia = new Media(songs.get(playListSongNo).toURI().toString());
                int[] finalPlaylistImgFlag = playlistImgFlag;

                // get metadata
                playListMedia.getMetadata().addListener((MapChangeListener.Change<? extends String, ? extends Object> c) -> {
                    if (c.wasAdded()) {
                        if ("artist".equals(c.getKey())) {
                            Label playlistArtist = (Label) nodes[j].lookup("#artist");
                            playlistArtist.setText(c.getValueAdded().toString());
                        } else if ("title".equals(c.getKey())) {
                            Label playlistSong = (Label) nodes[j].lookup("#song");
                            playlistSong.setText(j+". "+c.getValueAdded().toString());
                        } else if ("album".equals(c.getKey())) {
                            Label playlistAlbum = (Label) nodes[j].lookup("#album");
                            playlistAlbum.setText(c.getValueAdded().toString());
                        }else if ("image".equals(c.getKey())) {
                            finalPlaylistImgFlag[j] = 1;
                            Image i = (Image)c.getValueAdded();
                            ImageView iv = new ImageView(i);
                            iv.setFitWidth(60);
                            iv.setFitHeight(60);
                            Pane p = (Pane) nodes[j].lookup("#cover");
                            p.getChildren().add(iv);
                        }
                        if(finalPlaylistImgFlag[j] == 0){
                            Image i = new Image("assets/default-song-cover.png");
                            ImageView iv = new ImageView(i);
                            iv.setFitWidth(60);
                            iv.setFitHeight(60);
                            Pane p = (Pane) nodes[j].lookup("#cover");
                            p.getChildren().add(iv);
                        }

                    }
                });

                //give the items some effect
                nodes[playListSongNo].setOnMouseEntered(event -> {
                    nodes[j].setStyle("-fx-background-color : #4d004d");
                });
                nodes[playListSongNo].setOnMouseExited(event -> {
                    if(j%2==0){
                        nodes[j].setStyle("-fx-background-color : #111");
                    }else {
                        nodes[j].setStyle("-fx-background-color : black");
                    }
                });
                if(j%2==0){
                    nodes[j].setStyle("-fx-background-color : #111");
                }
                pnItems.getChildren().add(nodes[playListSongNo]);
                playListSongNo++;

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        /*try {
            con=DBUtil.getConnection();
            String sql = "select * from music";
            Statement st = con.createStatement();
            rs = st.executeQuery(sql);
            if(rs.next()) {
                songList.getItems().add(rs.getString("song_description"));
            }else{
                System.out.println("the end");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }*/

        // show now playing media
        media = new Media(songs.get(songNumber).toURI().toString());
        songLbl.setText("");
        artistLbl.setText("");
        //Image image = new Image("assets/song-cover.jpeg", 100, 100, false, false);

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
                }else if ("image".equals(c.getKey())) {
                    imgFlag = 1;
                    Image i = (Image)c.getValueAdded();
                    ImageView iv = new ImageView(i);
                    iv.setFitWidth(135);
                    iv.setFitHeight(135);
                    pane.getChildren().add(iv);
                }
                if(imgFlag == 0){
                    Image i = new Image("assets/default-song-cover.png");
                    ImageView iv = new ImageView(i);
                    iv.setFitWidth(135);
                    iv.setFitHeight(135);
                    pane.getChildren().add(iv);
                }

            }
        });

        mediaPlayer = new MediaPlayer(media);

        //code for volume changing
        volumeSlider.valueProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {

                mediaPlayer.setVolume(volumeSlider.getValue() * 0.01);
            }
        });
    }


    public void playMedia(){
        if (status == 0){
            mediaPlayer.play();
            status=1;
            beginTimer();
        }else if(status == 1){
            mediaPlayer.pause();
            status=0;
            cancelTimer();
        }

    }

    public void nextMedia(){
        imgFlag = 0;
        if(songNumber < songs.size()-1){
            songNumber++;
        }else {
            songNumber = 0;
        }
        mediaPlayer.stop();
        if(running) {
            cancelTimer();
        }
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
                }else if ("image".equals(c.getKey())) {
                    imgFlag = 1;
                    Image i = (Image)c.getValueAdded();
                    ImageView iv = new ImageView(i);
                    iv.setFitWidth(135);
                    iv.setFitHeight(135);
                    pane.getChildren().add(iv);
                }
                if(imgFlag == 0){
                    Image i = new Image("assets/default-song-cover.png");
                    ImageView iv = new ImageView(i);
                    iv.setFitWidth(135);
                    iv.setFitHeight(135);
                    pane.getChildren().add(iv);
                }
            }
        });

        mediaPlayer = new MediaPlayer(media);
        playMedia();
    }

    public void prevMedia(){
        imgFlag = 0;
        if(songNumber > 0){
            songNumber--;
        }else {
            songNumber = songs.size() - 1;
        }
        mediaPlayer.stop();
        if(running) {
            cancelTimer();
        }
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
                }else if ("image".equals(c.getKey())) {
                    imgFlag = 1;
                    Image i = (Image)c.getValueAdded();
                    ImageView iv = new ImageView(i);
                    iv.setFitWidth(135);
                    iv.setFitHeight(135);
                    pane.getChildren().add(iv);
                }
                if(imgFlag == 0){
                    Image i = new Image("assets/default-song-cover.png");
                    ImageView iv = new ImageView(i);
                    iv.setFitWidth(135);
                    iv.setFitHeight(135);
                    pane.getChildren().add(iv);
                }

            }
        });

        mediaPlayer = new MediaPlayer(media);
        playMedia();
    }

    public void beginTimer() {

        timer = new Timer();

        task = new TimerTask() {

            public void run() {

                running = true;
                double current = mediaPlayer.getCurrentTime().toSeconds();
                double end = media.getDuration().toSeconds();
                progressBar.setProgress(current/end);

                if(current/end == 1) {

                    cancelTimer();
                }
            }
        };

        timer.scheduleAtFixedRate(task, 0, 1000);
    }

    public void cancelTimer() {

        running = false;
        timer.cancel();
    }
}

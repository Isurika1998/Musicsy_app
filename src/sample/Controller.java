package sample;


import com.jfoenix.controls.JFXButton;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import javafx.collections.MapChangeListener;
import javafx.util.Duration;

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
    private JFXButton allmusicBtn, playlistBtn, favouritesBtn;

    @FXML
    private Label songLbl, artistLbl, time1Lbl, time2Lbl, headingLbl;
    @FXML
    private Pane pane, pnl_allmusic, pnl_playlists, pnl_favourites;
    @FXML
    private VBox pnItems = null;
    @FXML
    private HBox itemC;
    @FXML
    private Slider volumeSlider;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private FontAwesomeIcon favIcon;

    private int songNumber;

    private Media media,playListMedia;
    private MediaPlayer mediaPlayer;
    private String title;
    private String artist;
    private String album;

    private Timer timer;
    private TimerTask task;
    private boolean running;

    private int status, imgFlag, nextMediaIndicator;
    private String[][] playlist;
    private final DecimalFormat formatter = new DecimalFormat("00.00");


    //for db connection
    PreparedStatement pstmt=null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        getSongList("D:\\Songs");

        getMetadata(songNumber);

        //code for volume changing
        volumeSlider.valueProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {

                mediaPlayer.setVolume(volumeSlider.getValue() * 0.01);
            }
        });
    }

    public void getSongList(String path){
        songs = new ArrayList<File>();
        directory = new File(path);
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
        playlist = new String[count][4];
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
                            playlist[j][0]=c.getValueAdded().toString();
                        } else if ("title".equals(c.getKey())) {
                            Label playlistSong = (Label) nodes[j].lookup("#song");
                            playlistSong.setText(j+". "+c.getValueAdded().toString());
                            playlist[j][1]=c.getValueAdded().toString();
                        } else if ("album".equals(c.getKey())) {
                            Label playlistAlbum = (Label) nodes[j].lookup("#album");
                            playlistAlbum.setText(c.getValueAdded().toString());
                            playlist[j][2]=c.getValueAdded().toString();
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
    }


    public void getMetadata(int num){
        media = new Media(songs.get(num).toURI().toString());
        media.getMetadata().addListener((MapChangeListener.Change<? extends String, ? extends Object> c) -> {
            songLbl.setText(playlist[num][1]);
            artistLbl.setText(playlist[num][0]);
            if (c.wasAdded()) {
                if ("image".equals(c.getKey())) {
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

    }


    public void playMedia() {
        if (status == 0){
                mediaPlayer.play();
                mediaPlayer.currentTimeProperty().addListener(new ChangeListener<Duration>() {
                    @Override
                    public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {
                        time1Lbl.setText(String.format("%02d:%02d", (int) newValue.toMinutes() % 60, (int)newValue.toSeconds() % 60));
                        Duration totalTime = media.getDuration();
                        time2Lbl.setText(String.format("%02d:%02d", (int) totalTime.toMinutes() % 60, (int)totalTime.toSeconds() % 60));
                    }
                });
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
        getMetadata(songNumber);
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
        getMetadata(songNumber);
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
                    nextMediaIndicator=1;
                    cancelTimer();
                }
            }
        };

        timer.scheduleAtFixedRate(task, 0, 1000);
    }

    public void cancelTimer() {
        running = false;
        timer.cancel();
        if(nextMediaIndicator==1){
            nextMediaIndicator=0;
            task.cancel();
            nextMedia();
        }
    }

    public void addFavourite(){
        try {
            Connection con=DBUtil.getConnection();
            String query = "INSERT INTO favourites values(?, ?)";

            // create the mysql insert preparedstatement
            PreparedStatement preparedStmt = con.prepareStatement(query);
            preparedStmt.setInt(1, 7);
            preparedStmt.setString (2, songs.get(songNumber).toURI().toString());
            favIcon.setStyle("color : red");

            // execute the preparedstatement
            preparedStmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void getFavouriteSongList(String path){
        songs = new ArrayList<File>();
        directory = new File(path);
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
        playlist = new String[count][4];
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
                            playlist[j][0]=c.getValueAdded().toString();
                        } else if ("title".equals(c.getKey())) {
                            Label playlistSong = (Label) nodes[j].lookup("#song");
                            playlistSong.setText(j+". "+c.getValueAdded().toString());
                            playlist[j][1]=c.getValueAdded().toString();
                        } else if ("album".equals(c.getKey())) {
                            Label playlistAlbum = (Label) nodes[j].lookup("#album");
                            playlistAlbum.setText(c.getValueAdded().toString());
                            playlist[j][2]=c.getValueAdded().toString();
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
    }

    public  void getFavourites(){
        try {
            Connection con=DBUtil.getConnection();
            String query = "SELECT * FROM favourites";
            Statement statement = con.createStatement();
            ResultSet results = statement.executeQuery("SELECT * FROM favourites");
            int i=0;
            while (results.next()) {
                String data = results.getString(2);
                File file = new File("D:\\Songs\\"+data);
               // System.out.println(file.toURI().toString());
                files = null;
                files[i]=file;
                i++;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void onTabClick(ActionEvent evt){
        if(evt.getSource() == allmusicBtn){
            pnItems.getChildren().clear();
            getSongList("D:\\Songs");
            pnl_allmusic.toFront();
            headingLbl.setText("All Songs");
        }else if(evt.getSource() == playlistBtn){
            pnl_playlists.toFront();
            headingLbl.setText("Playlists");
        }else if(evt.getSource() == favouritesBtn){
           // FavouritesController favController = new FavouritesController();
            pnItems.getChildren().clear();
            getFavourites();
            getSongList("D:\\Favourites");
            pnl_allmusic.toFront();
            headingLbl.setText("Favourites");
        }
    }



}

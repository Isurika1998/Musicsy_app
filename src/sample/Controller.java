package sample;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
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
    private Label songLbl, artistLbl;

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

    private int songNumber;

    private Media media;
    private MediaPlayer mediaPlayer;
    private String title;
    private String artist;
    private String album;

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
                /*Label lbl = new Label(file.getName());
                lbl.setTextFill(Color.color(1, 1, 1));
                lbl.setStyle("-fx-font-family: Calibri; -fx-font-size: 18");
               playList.addRow(k,lbl);
               k++;*/
            }
        }
        Node[] nodes = new Node[count];
        int playListSongNo = 0;
        for (File file : files) {
            try {

                final int j = playListSongNo;
                nodes[playListSongNo] = FXMLLoader.load(getClass().getResource("../view/item.fxml"));
                Label playlistSong = (Label) nodes[playListSongNo].lookup("#song");
                playlistSong.setText(file.getName());

                //give the items some effect

                nodes[playListSongNo].setOnMouseEntered(event -> {
                    nodes[j].setStyle("-fx-background-color : #0A0E3F");
                });
                nodes[playListSongNo].setOnMouseExited(event -> {
                    nodes[j].setStyle("-fx-background-color : #02030A");
                });
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
        imgFlag = 0;
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
}

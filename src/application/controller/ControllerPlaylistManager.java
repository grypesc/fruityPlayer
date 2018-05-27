package application.controller;

import application.model.TrackData;
import application.model.Playlist;
import application.model.radams.gracenote.webapi.GracenoteException;
import java.io.IOException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.input.DragEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class ControllerPlaylistManager {
    @FXML
    public ListView listView;
    public Stage infoStage, playlistManagerStage;
    public Playlist playlist;
    private ControllerMain mainController;

    public void initialize(Stage infoStageArg, ControllerMain mainControllerArg) {
        mainController = mainControllerArg;
        playlistManagerStage = infoStageArg;
        playlistManagerStage.getScene().getStylesheets().add(mainController.getStyleSheetURL());
        playlistManagerStage.setX(mainController.getStage().getX() - 198);
        playlistManagerStage.setY(mainController.getStage().getY() + 26);
        playlist = mainController.model.getPlaylist();

        ObservableList<String> observableList = FXCollections.observableArrayList(playlist.getTracksNames());
        listView.setItems(observableList);

        mainController.getStage().xProperty().addListener((observable, oldValue, newValue) -> {
            playlistManagerStage.setX(mainController.getStage().getX() - 198);
            playlistManagerStage.setY(mainController.getStage().getY() + 26);

        });

        mainController.getStage().yProperty().addListener((observable, oldValue, newValue) -> {
            playlistManagerStage.setX(mainController.getStage().getX() - 198);
            playlistManagerStage.setY(mainController.getStage().getY() + 26);

        });

        mainController.getModel().getCurrentTrackIndex().addListener((observable, oldValue, newValue) -> {

            listView.getFocusModel().focus(newValue.intValue());
        });
        if (mainController.getModel().isSongLoaded())
            listView.getFocusModel().focus(mainController.getModel().getCurrentTrackIndex().intValue());
    }

   public void select()
   {
       listView.getFocusModel().focus(mainController.getModel().getCurrentTrackIndex().intValue());
   }

    public void refreshListView() {
        ObservableList<String> observableList = FXCollections.observableArrayList(playlist.getTracksNames());
        listView.setItems(observableList);
    }

    public void addTracks() {
        mainController.addTracksWithFileChooser();
        refreshListView();
        listView.getFocusModel().focus(mainController.getModel().getCurrentTrackIndex().intValue());
    }
    

    public void removeTrack() {
        if (playlist.isEmpty() || listView.getSelectionModel().getSelectedIndex()== -1) {
            return;
        }
        int oldIndex = listView.getSelectionModel().getSelectedIndex();
        mainController.getModel().removeTrack(listView.getSelectionModel().getSelectedIndex());
        refreshListView();
 
        listView.getSelectionModel().select(oldIndex);      
        listView.getFocusModel().focus(mainController.getModel().getCurrentTrackIndex().intValue());
    }

    @FXML
    private void trackInfo() {

        if (mainController.getModel().isSongLoaded()==false){
            mainController.displayErrorWindow("Please load a song first");
            return;
        }
        try {
            ControllerInfo controllerInfo;
            TrackData data = (mainController.getModel().getTrackData(mainController.getModel().getMedia()));
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/view/Info.fxml"));
            AnchorPane infoLayout = (AnchorPane) loader.load();
            Scene infoScene = new Scene(infoLayout);
            infoScene.getStylesheets().add(mainController.getStyleSheetURL());
            infoStage = new Stage();
            infoStage.setScene(infoScene);
            controllerInfo = (ControllerInfo) loader.getController();
            infoStage.getIcons().add(new Image("application/view/images/icons/grapes.png"));
            controllerInfo.initialize(data);

            infoStage.show();
        } catch (IOException e) {
            mainController.displayErrorWindow("Critical error.");
        }
        catch (Exception e){
            mainController.displayErrorWindow("Couldn't connect to database, check your Internet connection.");
        }
            
    }
    
    @FXML    
    private void handleDragOver(DragEvent event) {   
        mainController.handleDragOver(event);
  
    }

    @FXML    
    private void handleDragDropped(DragEvent event) {  
        mainController.handleDragDropped(event);
    }

}

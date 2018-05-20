package application.controller;

import application.model.TrackData;
import application.model.Playlist;
import java.io.IOException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class ControllerPlaylistManager {

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
        listView.setMouseTransparent( false );
        listView.setFocusTraversable( false );

        mainController.getStage().xProperty().addListener((observable, oldValue, newValue) -> {
            playlistManagerStage.setX(mainController.getStage().getX() - 198);
            playlistManagerStage.setY(mainController.getStage().getY() + 26);

        });

        mainController.getStage().yProperty().addListener((observable, oldValue, newValue) -> {
            playlistManagerStage.setX(mainController.getStage().getX() - 198);
            playlistManagerStage.setY(mainController.getStage().getY() + 26);

        });

        mainController.getModel().getCurrentTrackIndex().addListener((observable, oldValue, newValue) -> {

           // listView.getFocusModel().focus(newValue.intValue());
                listView.getSelectionModel().select(newValue.intValue());
        });

    }

    public void refreshListView() {
        ObservableList<String> observableList = FXCollections.observableArrayList(playlist.getTracksNames());
        listView.setItems(observableList);
    }

    public void addTracks() {
        mainController.addTracksWithFileChooser();
        refreshListView();
    }

    public void removeTrack() {
        if (playlist.isEmpty()) {
            return;
        }
        ObservableList<String> oList;
        oList = listView.getSelectionModel().getSelectedItems();
        mainController.getModel().removeTrack(oList.get(0));
        refreshListView();
    }

    public void trackInfo() {
        if (mainController.getModel().isSongLoaded()==false){
            mainController.displayErrorWindow("Please load a song first");
            return;
        }
        if(mainController.isConnectedToDatabase==false){
            mainController.displayErrorWindow("No internet connection");
            return;
        }
        ControllerInfo controllerInfo;
        TrackData data = (mainController.getModel().getTrackData(mainController.getModel().getMedia()));
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/view/Info.fxml"));
            AnchorPane infoLayout = (AnchorPane) loader.load();
            Scene infoScene = new Scene(infoLayout);
            infoScene.getStylesheets().add(mainController.getStyleSheetURL());
            infoStage = new Stage();
            infoStage.setScene(infoScene);
            controllerInfo = (ControllerInfo) loader.getController();
            controllerInfo.initialize(data);

            infoStage.show();
        } catch (IOException e) {
        }
    }

}

package application.controller;

import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Slider;
import javafx.scene.text.Text;
import application.model.Model;
import application.model.DurationExtended;
import application.model.radams.gracenote.webapi.GracenoteException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.DragEvent;
import javafx.stage.Stage;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.StageStyle;

public class ControllerMain {
    @FXML
    private Slider volumeSlider;
    @FXML
    private ProgressIndicator trackProgress;
    @FXML
    private Text labelProgressCounter;
    public Model model;
    public Stage mainStage, playlistManagerStage, equalizerStage, settingsStage;
    private ControllerEqualizer controller;
    private ControllerPlaylistManager controllerPM;
    private ControllerSettings controllerSettings;
    private String styleSheetName, pathToStyleSheet;
    private String settingsFileName;

    public void initialize(Stage stage) throws IOException {
        model = new Model();
        labelProgressCounter.setText("Hey");
        mainStage = stage;
        pathToStyleSheet = "application/view/css/";
        settingsFileName="settings.fruity";
        readSettings();
        mainStage.getScene().getStylesheets().add(pathToStyleSheet + styleSheetName);
    }

    public void addTracksWithFileChooser() {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().addAll(new ExtensionFilter("Audio Files", "*.wav", "*.mp3", "*.aac", "*aiff", "aif"),
                new ExtensionFilter("All Files", "*.*"));
        model.addPlaylist(fc.showOpenMultipleDialog(null));

    }

    private void startTrackProgressListener() {
        if (model.isSongLoaded()) {
            model.getCurrentTimeProperty().addListener((observable, oldValue, newValue) -> {
                trackProgress.setProgress((newValue.toMillis()) / (model.getTrackDuration().toMillis()));
                labelProgressCounter.setText(DurationExtended.toMinutesAndSeconds(newValue));
                if (newValue.toSeconds() >= getModel().getTrackDuration().toSeconds() - 0.2) {
                    nextTrack();
                }

            });
        }
    }

    private void startVolumeListener() {
        if (model.isSongLoaded()) {
            volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
                model.setVolume((double) volumeSlider.getValue());

            });
        }
    }
    @FXML
    public void play() {
        if (!model.isSongLoaded()) {
            if (model.isPlaylistEmpty()) {
                addTracksWithFileChooser();
                if (playlistManagerStage != null) {
                    controllerPM.refreshListView();
                }
            }
            model.play();
            model.setVolume(volumeSlider.getValue());
        } else {
            model.play();
        }
        startTrackProgressListener();
        startVolumeListener();
        if (controllerPM!=null)
            controllerPM.listView.getFocusModel().focus(model.getCurrentTrackIndex().intValue());
    }
    @FXML
    public void pause() {
        model.pause();
    }
    @FXML
    public void stop() {
        model.stop();
    }
    @FXML
    public void prevTrack() {
        model.loadPrevTrack();
        play();
    }
    @FXML
    public void nextTrack() {
        model.loadNextTrack();
        play();
    }
    @FXML
    public void setCurrentDuration(MouseEvent mouse) {
        if (!model.isSongLoaded()) {
            return;
        }
        double radius = Math.sqrt(Math.pow(mouse.getX() - 47, 2) + Math.pow(mouse.getY() - 47, 2));
        if (mouse.getY() <= 47) {
            model.setCurrentTime((Math.PI / 2 - Math.acos((mouse.getX() - 47) / radius)) / (2 * Math.PI));
        } else {
            model.setCurrentTime(0.5 - (Math.PI / 2 - Math.acos((mouse.getX() - 47) / radius)) / (2 * Math.PI));
        }
    }

    public Model getModel() {
        return model;
    }

    public Stage getStage() {
        return mainStage;
    }

    public String getStyleSheetName() {
        return styleSheetName;
    }

    public void setStyleSheetName(String s) {
        styleSheetName = s;
    }

    public String getPathToStyleSheet() {
        return pathToStyleSheet;
    }

    public String getStyleSheetURL() {
        return pathToStyleSheet + styleSheetName;
    }
    @FXML
    public void applyStyleSheet() {
        mainStage.getScene().getStylesheets().clear();
        mainStage.getScene().getStylesheets().add(pathToStyleSheet + styleSheetName);
        settingsStage.getScene().getStylesheets().clear();
        settingsStage.getScene().getStylesheets().add(pathToStyleSheet + styleSheetName);
        if (equalizerStage != null) {
            equalizerStage.getScene().getStylesheets().clear();
            equalizerStage.getScene().getStylesheets().add(pathToStyleSheet + styleSheetName);
        }
        if (playlistManagerStage != null) {
            playlistManagerStage.getScene().getStylesheets().clear();
            playlistManagerStage.getScene().getStylesheets().add(pathToStyleSheet + styleSheetName);
        }

    }
    @FXML
    public void openPlaylistManager() {
        try {
            if (controllerPM != null && !playlistManagerStage.isShowing()) {
                playlistManagerStage.show();
                return;
            }
            if (playlistManagerStage != null && playlistManagerStage.isShowing()) {
                playlistManagerStage.close();
                return;
            }
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/view/PlaylistManager.fxml"));
            AnchorPane playlistManagerLayout = (AnchorPane) loader.load();
            Scene playlistManagerScene = new Scene(playlistManagerLayout);

            playlistManagerStage = new Stage();
            playlistManagerStage.setScene(playlistManagerScene);
            playlistManagerStage.initStyle(StageStyle.UNDECORATED);
            controllerPM = (ControllerPlaylistManager) loader.getController();
            controllerPM.initialize(playlistManagerStage, this);
            playlistManagerStage.show();
            model.registerGraceNote();
        } catch (IOException e) {
            displayErrorWindow("Critical error");
        } catch (Exception e) {
            displayErrorWindow("Could not connect to database. Check you Internet connection.");
        }

    }
    @FXML
    public void openEqualizer() {
        try {
            if (controller != null && !equalizerStage.isShowing()) {
                equalizerStage.show();
                return;
            }
            if (equalizerStage != null && equalizerStage.isShowing()) {
                equalizerStage.close();
                return;
            }
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/view/Equalizer.fxml"));
            AnchorPane equalizerLayout = (AnchorPane) loader.load();
            Scene equalizerScene = new Scene(equalizerLayout);

            equalizerStage = new Stage();
            equalizerStage.setScene(equalizerScene);
            equalizerStage.initStyle(StageStyle.UNDECORATED);
            controller = (ControllerEqualizer) loader.getController();
            controller.initialize(this, equalizerStage);

            equalizerStage.show();
        } catch (IOException e) {
            displayErrorWindow("Critical error");
        }
    }
    @FXML
    public void openSettings() {
        try {
            if (controllerSettings != null && !settingsStage.isShowing()) {
                settingsStage.show();
                return;
            }
            if (settingsStage != null && settingsStage.isShowing()) {
                settingsStage.close();
                return;
            }
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/view/Settings.fxml"));
            AnchorPane settingsLayout = (AnchorPane) loader.load();
            Scene settingsScene = new Scene(settingsLayout);

            settingsStage = new Stage();
            settingsStage.setScene(settingsScene);
            settingsStage.initStyle(StageStyle.UNDECORATED);
            controllerSettings = (ControllerSettings) loader.getController();
            controllerSettings.initialize(this, settingsStage);
            settingsStage.show();
        } catch (IOException e) {
            displayErrorWindow("Critical error");
        }
    }
    /**
     * Fills userID and currrent style sheet from settings file. If file is not found it will create one and reset settings to default.
     * @throws FileNotFoundException
     * @throws IOException 
     */
    public void readSettings() throws FileNotFoundException, IOException {
        String everything = "";
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(settingsFileName));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                everything += (line + "\n");
            }
            bufferedReader.close();
            styleSheetName = everything.substring((everything.indexOf("defaultSkin = ") + 14), everything.indexOf("\n", everything.indexOf("defaultSkin = ")));
            model.setUserID(everything.substring((everything.indexOf("userID = ") + 9), everything.indexOf("\n", everything.indexOf("userID = "))));
        
        } catch (FileNotFoundException ex) {
            displayErrorWindow("Unable to open settings file, settings were set to default. ");
            writeSettings(settingsFileName, "BlueBerry.css", "");
            styleSheetName = "BlueBerry.css";
            model.setUserID("");
        } catch (IOException ex) {
            displayErrorWindow("A fruity exception occured while reading settings file. ");
            Platform.exit();
        }

    }

    public void writeSettings(String fileName, String styleSheet, String usrID) {
        try {
            // Assume default encoding.
            FileWriter fileWriter = new FileWriter(fileName, false);

            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            bufferedWriter.write("defaultSkin = ");
            bufferedWriter.write(styleSheet);
            bufferedWriter.newLine();
            bufferedWriter.write("userID = ");
            bufferedWriter.write(usrID);

            bufferedWriter.close();
        } catch (IOException ex) {
            displayErrorWindow("Unable to create settings file.");
            Platform.exit();
            
        }
    }
    
    public void writeSettings() {
        writeSettings(settingsFileName, styleSheetName, model.getUserID());   
    }

    public void displayErrorWindow(String error) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(error);
        alert.showAndWait();
    }
    
    @FXML    
    public void handleDragOver(DragEvent event) {   
    if (event.getDragboard().hasFiles())
        event.acceptTransferModes(TransferMode.ANY);
                
    }

    @FXML    
    public void handleDragDropped(DragEvent event) {  
        List <File> list = event.getDragboard().getFiles();
        model.addPlaylist(list);
        if (controllerPM!=null)
            controllerPM.refreshListView();
    }
}






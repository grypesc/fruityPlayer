/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package application.controller;

import application.model.TrackData;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 *
 * This Stage displays info about the current track
 */
public class ControllerInfo {
    private Image imageAlbum, imageArtist;
    @FXML
    private ImageView imageView;
    @FXML
    private Label label;
    @FXML
    private ChoiceBox choiceBox;
    ObservableList<String> obsList;
    
    public void initialize(TrackData data) {
        
        imageAlbum = new Image ("/application/view/images/notFound.png");
        imageArtist = new Image ("/application/view/images/notFound.png");
                            
        if(!data.getAlbumCoverURL().equals(""))
            imageAlbum = new Image(data.getAlbumCoverURL());
        if(!data.getArtistImage().equals(""))
            imageArtist = new Image(data.getArtistImage());
        
        imageView.setImage(imageAlbum);
        label.setText(data.getInfo());
        List<String> list = new ArrayList<>();
        list.add("Album cover");
        list.add("Artist photo");
        obsList = FXCollections.observableList(list);
        choiceBox.setItems(obsList);
        choiceBox.getSelectionModel().select("Album cover");
        startChoiceBoxListener();
    }
    
    private void startChoiceBoxListener() {
        choiceBox.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals(0))
                imageView.setImage(imageAlbum);
            if (newValue.equals(1))
                imageView.setImage(imageArtist);
        });
    }
}

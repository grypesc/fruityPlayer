/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package application.controller;

import application.model.TrackData;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 *
 * This Stage displays info about the current track
 */
public class ControllerInfo {
    private Image image;
    @FXML
    private ImageView imageView;
    @FXML
    private Label label;

    public void initialize(TrackData data) {
        if(!data.getUrl().equals(""))
            image = new Image(data.getUrl());
        else 
            image = new Image ("/application/view/images/notFound.png");
        imageView.setImage(image);
        label.setText(data.getInfo());
        
    }
    
}

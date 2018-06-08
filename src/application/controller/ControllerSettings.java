/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package application.controller;

import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Stage;

/**
 *
 * @author Grzegorz Rypeść
 */
public class ControllerSettings {

    private Stage settingsStage;
    private ControllerMain mainController;
    @FXML
    private ChoiceBox choiceBox;
    private ObservableList<String> styleSheetsURL;

    public void initialize(ControllerMain mainControllerArg, Stage settingsStageArg) {
        mainController = mainControllerArg;
        settingsStage = settingsStageArg;
        settingsStage.getScene().getStylesheets().add(mainController.getStyleSheetURL());
        settingsStage.setX(mainController.getStage().getX() - 180);
        settingsStage.setY(mainController.getStage().getY() + 150);

        //setting position listeners
        mainController.getStage().xProperty().addListener((observable, oldValue, newValue) -> {
            settingsStage.setX(mainController.getStage().getX() - 180);
            settingsStage.setY(mainController.getStage().getY() + 150);

        });

        mainController.getStage().yProperty().addListener((observable, oldValue, newValue) -> {
            settingsStage.setX(mainController.getStage().getX() - 180);
            settingsStage.setY(mainController.getStage().getY() + 150);

        });

        List<String> list = new ArrayList<>();
        list.add("BlackBerry");
        list.add("BlueBerry");
        list.add("Cherry");
        list.add("Coconut");
        list.add("Lime");
        list.add("Orange");
        list.add("Raspberry");
        
        styleSheetsURL = FXCollections.observableList(list);
        choiceBox.setItems(styleSheetsURL);
        choiceBox.getSelectionModel().select(mainController.getStyleSheetName().replace(".css", ""));
        startChoiceBoxListener();
    }

    private void startChoiceBoxListener() {
        choiceBox.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            mainController.setStyleSheetName(choiceBox.getItems().get(newValue.intValue()) + ".css");
            mainController.applyStyleSheet();

        });
    }
}

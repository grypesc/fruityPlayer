/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package application.controller;

import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.scene.media.EqualizerBand;
import javafx.stage.Stage;

/**
 * 
 * @author Grzepa
 */
public class ControllerEqualizer {

    private Stage equalizerStage;
    private ControllerMain mainController;
    private final Slider[] sliders = new Slider[10];
    @FXML
    private Slider slider0, slider1, slider2, slider3, slider4, slider5, slider6, slider7, slider8, slider9, sliderBalance;
    @FXML
    private CheckBox checkBoxApply;
    private boolean areListenersOn;

    /**
    * 
    * Creates position listeners that keep equalizer window next to main window
    */
    public void initialize(ControllerMain mainControllerArg, Stage equalizerStageArg) {
        mainController = mainControllerArg;
        equalizerStage = equalizerStageArg;
        equalizerStage.setX(mainController.getStage().getX() - 498);
        equalizerStage.setY(mainController.getStage().getY() + 26);
        equalizerStage.getScene().getStylesheets().add(mainController.getStyleSheetURL());
        sliders[0] = slider0;
        sliders[1] = slider1;
        sliders[2] = slider2;
        sliders[3] = slider3;
        sliders[4] = slider4;
        sliders[5] = slider5;
        sliders[6] = slider6;
        sliders[7] = slider7;
        sliders[8] = slider8;
        sliders[9] = slider9;

        areListenersOn = false;
        checkBoxApply.selectedProperty().addListener((ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) -> {
            if (mainController.getModel().getMediaPlayer() == null)
                return;
            if (new_val) {
                for (int i = 0; i < 10; i++) {
                    double gain;
                    gain = sliders[i].getValue();
                    gain = gain / 100;
                    gain = EqualizerBand.MIN_GAIN + gain * (EqualizerBand.MAX_GAIN - EqualizerBand.MIN_GAIN) + 6;
                    gain*=0.67;
                    mainController.getModel().getMediaPlayer().getAudioEqualizer().getBands().get(i).setGain(gain);
                }
                mainController.getModel().getMediaPlayer().setBalance((sliderBalance.getValue() - 50) / 100);
                if (areListenersOn == false) {
                    startSlidersListeners();
                    startBalanceListener();
                    areListenersOn = true;
                }
            } else {
                for (int i = 0; i < 10; i++) 
                    mainController.getModel().getMediaPlayer().getAudioEqualizer().getBands().get(i).setGain(0);
                mainController.getModel().getMediaPlayer().setBalance(0);
            }
        });
        
        //setting position listeners
        mainController.getStage().xProperty().addListener((observable, oldValue, newValue) -> {
            equalizerStage.setX(mainController.getStage().getX() - 498);
            equalizerStage.setY(mainController.getStage().getY() + 26);

        });

        mainController.getStage().yProperty().addListener((observable, oldValue, newValue) -> {
            equalizerStage.setX(mainController.getStage().getX() - 498);
            equalizerStage.setY(mainController.getStage().getY() + 26);

        });

    }
    
    private void startSlidersListeners() {
        for (int i = 0; i < 10; i++) {
            final int fi = i;
            sliders[i].valueProperty().addListener((ObservableValue<? extends Number> arg0, Number oldValue, Number newValue) -> {
                if (!checkBoxApply.isSelected()) {
                    return;
                }
                double gain;
                gain = newValue.doubleValue() / 100;
                gain = EqualizerBand.MIN_GAIN + gain * (EqualizerBand.MAX_GAIN - EqualizerBand.MIN_GAIN) + 6;
                gain*=0.67;
                if (mainController.getModel().getMediaPlayer() != null) {
                    mainController.getModel().getMediaPlayer().getAudioEqualizer().getBands().get(fi).setGain(gain);

                }
            });
        }
    }

    private void startBalanceListener() {
        sliderBalance.valueProperty().addListener((observable, oldValue, newValue) -> {
            if(checkBoxApply.isSelected())
                mainController.getModel().getMediaPlayer().setBalance((newValue.doubleValue() - 50) / 100);
        });
    }
    @FXML
    public void reset() {
        for (int i = 0; i < 10; i++) {
            sliders[i].setValue(50);
        }
        sliderBalance.setValue(50);
    }

}

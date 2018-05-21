package application;
import application.controller.ControllerMain;
import java.io.IOException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.stage.WindowEvent;

public class Main extends Application {
    
    ControllerMain controllerMain;
	@Override
	public void start(Stage mainStage) {
		try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("view/MainStage.fxml"));
                        SplitPane root = (SplitPane)loader.load();
			Scene scene = new Scene(root);
			mainStage.setScene(scene);
			mainStage.setResizable(false);
                        mainStage.setTitle("FP");
                      //  mainStage.resizableProperty().setValue(Boolean.FALSE);
			mainStage.getIcons().add(new Image("application/view/images/icons/grapes.png"));
                        controllerMain = (ControllerMain)loader.getController();
                        controllerMain.initialize(mainStage);    
			mainStage.show();
                        
                        mainStage.setOnCloseRequest((WindowEvent we) -> {
                            Platform.exit();
                        });
                        
		} catch(IOException e) {}
	}
        
        /** Stops the application and saves settings */
        @Override
        public void stop() throws IOException{
            controllerMain.writeSettings(); 
       }
	
	public static void main(String[] args) {
		launch(args);
	}
}

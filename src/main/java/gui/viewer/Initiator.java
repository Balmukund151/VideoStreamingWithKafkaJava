package gui.viewer;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import show.video.VideoController;

public class Initiator extends Application {

	@Override
	public void start(Stage stage) throws Exception {
		FXMLLoader loader = new FXMLLoader();
		Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("generator.fxml"));
		Scene scene = new Scene(root, 1000, 600);
		stage.setTitle("1st Screen");
		stage.setScene(scene);
		stage.setOnCloseRequest(e->{VideoController.cameraActive=false;Platform.exit();});
		stage.show();
	}
	
	

	public static void main(String[] args) {
		Application.launch(args);

	}

}

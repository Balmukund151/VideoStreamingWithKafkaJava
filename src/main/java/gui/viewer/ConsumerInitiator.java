package gui.viewer;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import show.video.VideoReceiverController;

public class ConsumerInitiator extends Application {

	@Override
	public void start(Stage stage) throws Exception {
		FXMLLoader loader = new FXMLLoader();
		Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("receiver.fxml"));
		Scene scene = new Scene(root, 900, 600);
//		Scene scene = new Scene(root);
		stage.setTitle("2nd screen");
		stage.setScene(scene);
		stage.setOnCloseRequest(e->{VideoReceiverController.isDisplayActive = false ;Platform.exit();});
		stage.show();
	}

	public static void main(String[] args) {
		Application.launch(args);

	}

}

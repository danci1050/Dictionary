package sample;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
	private static Stage pStage;

	@FXML
	private Pane sidebar;


	public static Stage getpStage() {
		return pStage;
	}

	public static void setpStage(Stage pStage) {
		Main.pStage = pStage;
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		pStage=primaryStage;
		Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
		primaryStage.setTitle("Dictionary");
		primaryStage.setScene(new Scene(root, 1280, 720));
		primaryStage.sizeToScene();
		primaryStage.show();
	}

	public static void main(String[] args) throws IOException, ClassNotFoundException {
		launch(args);
	}
}

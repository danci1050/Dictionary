package sample;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;

public class Main extends Application {
	private static Stage pStage;
	private static Controller controller;

	public static Stage getpStage() {
		return pStage;
	}

	public static void setpStage(Stage pStage) {
		Main.pStage = pStage;
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		pStage=primaryStage;
		Controller.setAddNewWord(true);
		FXMLLoader loader = new FXMLLoader(getClass().getResource("sample.fxml"));
		Parent root = loader.load();
		controller = loader.getController();
		primaryStage.setTitle("Dictionary");
		primaryStage.setOnCloseRequest((WindowEvent ignored) -> controller.getTranslator().saveDictionaries());
		primaryStage.setScene(new Scene(root, 1280, 720));
		primaryStage.sizeToScene();
		primaryStage.show();
	}

	public static void main(String[] args) throws IOException, ClassNotFoundException {
		launch(args);
	}
}

package sample;


import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.util.Pair;
import netscape.javascript.JSObject;

import java.io.File;
import java.util.*;

public class Controller {

	private static boolean addNewWord;

	@FXML
	private Pane sidebar;

	@FXML
	private Pane dictionaryTab ;

	@FXML
	private Pane settingsTab;

	@FXML
	private Pane translatorTab;

	@FXML
	private Pane viewDictionaryTab;

	@FXML
	private ToggleButton englishDutch;

	@FXML
	private ToggleButton dutchEnglish;

	@FXML
	private TextField searchField;

	@FXML
	private Label searchedWord;

	@FXML
	private Label result;

	@FXML
	private Button popup;

	@FXML
	private TableView<Entry> dictionaryTable;

	@FXML
	private TableColumn<Entry,String> s1;

	@FXML
	private TableColumn<Entry,String> s2;

	@FXML
	private Label popupLabel;

	@FXML
	private WebView webviewtest;

	public  WebView getWebviewtest() {
		return webviewtest;
	}

	@FXML
	private WebView settingsWebview;

	private static Integration javaSettingsIntegration = new Integration();

	public void setWebviewtest(WebView webviewtest) {
		this.webviewtest = webviewtest;
	}

	public static boolean isAddNewWord() {
		return addNewWord;
	}

	public static void setAddNewWord(boolean addNewWord) {
		Controller.addNewWord = addNewWord;
	}

	@FXML
	public void dictionaryPane(ActionEvent dictionaryPane){
		dictionaryTab.setVisible(true);
		translatorTab.setVisible(false);
		viewDictionaryTab.setVisible(false);
		settingsTab.setVisible(false);

	}
	@FXML
	public void translatorPane(ActionEvent translatorPane){
		dictionaryTab.setVisible(false);
		translatorTab.setVisible(true);
		viewDictionaryTab.setVisible(false);
		settingsTab.setVisible(false);
		webviewtest.prefWidthProperty().bind(translatorTab.widthProperty());
		webviewtest.prefHeightProperty().bind(translatorTab.heightProperty());
		WebEngine webEngine = webviewtest.getEngine();

		//shitty solution
		Integration.setWebviewtest(webviewtest);

		File f = new File(System.getProperty("user.dir")+"\\src\\sample\\translator.html");
		webEngine.setUserStyleSheetLocation(getClass().getResource("translator.css").toString());
		webEngine.load(f.toURI().toString());

		webEngine.getLoadWorker().stateProperty().addListener(new ChangeListener<Worker.State>() {
			@Override
			public void changed(ObservableValue<? extends Worker.State> observableValue, Worker.State state, Worker.State t1) {
				if(t1== Worker.State.SUCCEEDED){
					JSObject window = (JSObject) webEngine.executeScript("window");
						window.setMember("javaIntegration", new Integration());

				}

			}
		});

		//webEngine.load("http://google.com");



	}
	@FXML
	public void viewDictionaryPane(ActionEvent viewDictionaryPane){
		/*dictionaryTab.setVisible(false);
		translatorTab.setVisible(false);
		viewDictionaryTab.setVisible(true);
		settingsTab.setVisible(false);
		String param;
		ObservableList<Entry> t = FXCollections.observableArrayList();

		t.add(new Entry("english word","dutch word"));
		t.add(new Entry("english word1","dutch word2"));
		t.add(new Entry("english word2","dutch word3"));
		s1.setCellValueFactory(new PropertyValueFactory<Entry,String>("s1"));
		s2.setCellValueFactory(new PropertyValueFactory<Entry,String>("s2"));
		dictionaryTable.getItems().addAll(t);*/
	}
	@FXML
	public void settingsPane(ActionEvent settingsPane){
		WebEngine webEngine = settingsWebview.getEngine();
		File f = new File(System.getProperty("user.dir")+"\\src\\sample\\settings.html");
		webEngine.setUserStyleSheetLocation(getClass().getResource("translator.css").toString());
		webEngine.load(f.toURI().toString());

		webEngine.getLoadWorker().stateProperty().addListener(new ChangeListener<Worker.State>() {
			@Override
			public void changed(ObservableValue<? extends Worker.State> observableValue, Worker.State state, Worker.State t1) {
				if(t1== Worker.State.SUCCEEDED){
					System.out.println("js");
					JSObject window = (JSObject) webEngine.executeScript("window");
					window.setMember("javaSettingsIntegration", javaSettingsIntegration);

				}

			}
		});
		dictionaryTab.setVisible(false);
		translatorTab.setVisible(false);
		viewDictionaryTab.setVisible(false);
		settingsTab.setVisible(true);
	}




}



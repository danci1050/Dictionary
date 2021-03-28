package sample;


import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;

public class Controller {

	@FXML
	private Pane sidebar;

	@FXML
	private Pane dictionaryTab;

	@FXML
	private Pane settingsTab;

	@FXML
	private Pane translatorTab;

	@FXML
	private BorderPane viewDictionaryTab;

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
	private TableColumn<Entry,String> original;

	@FXML
	private TableColumn<Entry,String> translations;

	@FXML
	private Label popupLabel;

	@FXML
	private WebView webviewtest;

	public  WebView getWebviewtest() {
		return webviewtest;
	}

	private JSObject javaIntegration;

	private final Translator translator = new Translator();

	public void setWebviewtest(WebView webviewtest) {
		this.webviewtest = webviewtest;
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
	}

	@FXML
	public void viewDictionaryPane(ActionEvent viewDictionaryPane){
		dictionaryTab.setVisible(false);
		translatorTab.setVisible(false);
		viewDictionaryTab.setVisible(true);
		settingsTab.setVisible(false);

		// Setup the top Pane
		FlowPane selectionPane = new FlowPane();
		viewDictionaryTab.setTop(selectionPane);
		BorderPane.setMargin(selectionPane, new Insets(10));

		// Setup Choices for languages
		ChoiceBox<Dictionary> languagesChoiceBox = new ChoiceBox<>();
		languagesChoiceBox.getItems().addAll(translator.getDictionaries().values());
		selectionPane.getChildren().add(new Label("Select the dictionary: "));
		selectionPane.getChildren().add(languagesChoiceBox);
		languagesChoiceBox.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> selectDictionary(newValue));

		// Setup the table columns
		original = new TableColumn<>("Original");
		original.setPrefWidth(200);
		original.setCellValueFactory(new PropertyValueFactory<>("word"));
		translations = new TableColumn<>("Translations");
		translations.setPrefWidth(890);
		translations.setCellValueFactory(new PropertyValueFactory<>("stringTranslation"));

		// Setup the table
		dictionaryTable = new TableView<>();
		dictionaryTable.getColumns().add(original);
		dictionaryTable.getColumns().add(translations);
		dictionaryTable.setPlaceholder(new Label("Please select the dictionary"));

		// Add table items
		ObservableList<Entry> obsList = FXCollections.observableArrayList();
		dictionaryTable.setItems(obsList);
		dictionaryTable.getSortOrder().add(original);
		dictionaryTable.sort();
		viewDictionaryTab.setCenter(dictionaryTable);
	}

	@FXML
	public void settingsPane(ActionEvent settingsPane){
		dictionaryTab.setVisible(false);
		translatorTab.setVisible(false);
		viewDictionaryTab.setVisible(false);
		settingsTab.setVisible(true);
	}

	private void selectDictionary(Dictionary dictionary) {
		dictionaryTable.getItems().setAll(dictionary.getDictValues());
		dictionaryTable.sort();
	}
}

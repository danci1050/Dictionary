package sample;


import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.web.WebView;
import javafx.util.Pair;
import netscape.javascript.JSObject;

import java.util.Optional;

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
		selectionPane.setHgap(20);
		selectionPane.setPrefWrapLength(1090);
		viewDictionaryTab.setTop(selectionPane);
		BorderPane.setMargin(selectionPane, new Insets(10));

		// Setup Choices for languages
		ChoiceBox<Dictionary> languagesChoiceBox = new ChoiceBox<>();
		languagesChoiceBox.getItems().addAll(translator.getDictionaries().values());
		selectionPane.getChildren().add(new Label("Select the dictionary:"));
		selectionPane.getChildren().add(languagesChoiceBox);
		languagesChoiceBox.getSelectionModel().selectedItemProperty().addListener(
				(ObservableValue<? extends Dictionary> observableValue, Dictionary oldValue, Dictionary newValue) -> {
			dictionaryTable.getItems().setAll(newValue.getDictValues());
			dictionaryTable.sort();
		});

		// TODO: Update the list after the button fires
		// Setup the Add translation button
		Button addButton = new Button("Add Translation");
		addButton.setOnAction(
				(ActionEvent actionEvent) -> addTranslationDialog(actionEvent, languagesChoiceBox.getValue())
		);
		HBox spacer = new HBox();
		spacer.setPrefWidth(550);
		selectionPane.getChildren().addAll(spacer, addButton);

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

	public void addTranslationDialog(ActionEvent actionEvent, Dictionary dictionary) {
		this.addTranslationDialog(actionEvent, dictionary, "Enter the original word or phrase");
	}

	public void addTranslationDialog(ActionEvent actionEvent, Dictionary dictionary, String original) {
		Dialog<Pair<String, String[]>> dialog = new Dialog<>();
		dialog.setTitle("Add a Translation");

		if (dictionary == null) {
			dialog.setContentText("Please select dictionary first");
			dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
			dialog.show();
			return;
		}

		// Setup the text elements
		Label originalLabel	= new Label(dictionary.getFromLanguage());
		TextField originalField = new TextField(original);
		Label translationLabel = new Label(dictionary.getToLanguage());
		TextArea translationField = new TextArea("Enter the translation");

		// Setup dialog pane
		GridPane dialogPane = new GridPane();
		dialogPane.setHgap(20);
		dialogPane.setVgap(20);
		dialogPane.addRow(0, originalLabel, originalField);
		dialogPane.addRow(1, translationLabel, translationField);
		dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.FINISH);
		dialog.getDialogPane().setContent(dialogPane);
		dialog.setHeaderText("You can separate translations by semicolon");

		// Setup dialog result converter
		dialog.setResultConverter(buttonType -> {
			if (buttonType == ButtonType.FINISH && !originalField.getText().isEmpty() && !translationField.getText().isEmpty()) {
				return new Pair<>(originalField.getText(), translationField.getText().split(";"));
			}
			return null;
		});

		// Show dialog and add the result into dictionary
		Optional<Pair<String, String[]>> result = dialog.showAndWait();
		if (result.isPresent()) {
			dictionary.add(result.get().getKey(), result.get().getValue(), new String[result.get().getValue().length]);
		}
		// Show errors if text fields were empty
		else if (originalField.getText().isEmpty()) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setContentText("The original word cannot be empty");
			alert.showAndWait();
		} else if (translationField.getText().isEmpty()) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setContentText("The translation cannot be empty");
			alert.showAndWait();
		}
	}
}

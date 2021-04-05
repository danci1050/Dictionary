package sample;


import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.util.Pair;
import netscape.javascript.JSObject;

import java.net.URISyntaxException;
import java.util.Optional;

//TODO: Add comments to methods
/**
 * Controls the GUI
 */
public class Controller {

	private static boolean addNewWord;

	@FXML
	private Pane settingsTab;

	@FXML
	private Pane translatorTab;

	@FXML
	private BorderPane viewDictionaryTab;

	@FXML
	private TableView<Entry> dictionaryTable;

	@FXML
	private TableColumn<Entry,String> original;

	@FXML
	private TableColumn<Entry,String> translations;

	@FXML
	private WebView webviewtest;

	public  WebView getWebviewtest() {
		return webviewtest;
	}

	@FXML
	private WebView settingsWebview;

	/**
	 * The instance of Translator the program uses
	 */
	private final Translator translator = new Translator();
	private static Integration javaSettingsIntegration = new Integration();

	public Translator getTranslator() {
		return translator;
	}

	public void setWebviewtest(WebView webviewtest) {
		this.webviewtest = webviewtest;
	}

	public static boolean isAddNewWord() {
		return addNewWord;
	}

	public static void setAddNewWord(boolean addNewWord) {
		Controller.addNewWord = addNewWord;
	}

	public Pane getTranslatorTab() {
		return translatorTab;
	}

	@FXML
	public void initialize(){
		translatorPane(new ActionEvent());
	}

	/**
	 * Set's up and displays the Pane which shows the entire Dictionary
	 *
	 * @param translatorPane An action event
	 */
	@FXML
	public void translatorPane(ActionEvent translatorPane){
		translatorTab.setVisible(true);
		viewDictionaryTab.setVisible(false);
		settingsTab.setVisible(false);
		webviewtest.prefWidthProperty().bind(translatorTab.widthProperty());
		webviewtest.prefHeightProperty().bind(translatorTab.heightProperty());
		WebEngine webEngine = webviewtest.getEngine();

		//shitty solution
		Integration.setWebviewtest(webviewtest);

		webEngine.setUserStyleSheetLocation(getClass().getResource("translator.css").toString());
		try {
			webEngine.load(getClass().getResource("translator.html").toURI().toString());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		webEngine.getLoadWorker().stateProperty().addListener(new ChangeListener<Worker.State>() {
			@Override
			public void changed(ObservableValue<? extends Worker.State> observableValue, Worker.State state, Worker.State t1) {
				if (t1 == Worker.State.SUCCEEDED) {
					JSObject window = (JSObject) webEngine.executeScript("window");
					window.setMember("javaIntegration", new Integration());

				}
			}
		});
	}

	/**
	 * Set's up and displays the Pane which shows the entire Dictionary
	 *
	 * @param viewDictionaryPane An action event
	 */
	@FXML
	public void viewDictionaryPane(ActionEvent viewDictionaryPane){
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

		// Setup the Add translation button
		Button addButton = new Button("Add Translation");
		addButton.setOnAction(
				(ActionEvent actionEvent) -> {
					Dictionary dictionary = languagesChoiceBox.getValue();
					Optional<Pair<String, String[]>> result = addTranslationDialog(actionEvent, dictionary);
					if (result.isPresent()) {
						try {
							dictionaryTable.getItems().add(dictionary.searchAWord(result.get().getKey()));
						} catch (NoTranslationException e) {
							e.printStackTrace();
							System.exit(1);
						}
						dictionaryTable.sort();
					}
				}
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

	/**
	 * Set's up and displays the Pane which shows the setting
	 *
	 * @param settingsPane An action event
	 */
	@FXML
	public void settingsPane(ActionEvent settingsPane){
		WebEngine webEngine = settingsWebview.getEngine();
		webEngine.setUserStyleSheetLocation(getClass().getResource("translator.css").toString());
		try {
			webEngine.load(getClass().getResource("settings.html").toURI().toString());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

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
		translatorTab.setVisible(false);
		viewDictionaryTab.setVisible(false);
		settingsTab.setVisible(true);
	}

	/**
	 * Opens a dialog window to add a translation to a word
	 *
	 * @param actionEvent an action event
	 * @param dictionary The Dictionary to add the translation to
	 * @return The result of the dialog
	 */
	public Optional<Pair<String, String[]>> addTranslationDialog(ActionEvent actionEvent, Dictionary dictionary) {
		return this.addTranslationDialog(actionEvent, dictionary, "Enter the original word or phrase");
	}

	/**
	 * Opens a dialog window to add a translation to a word and initializes the word textfield to a specific word
	 *
	 * @param actionEvent an action event
	 * @param dictionary The Dictionary to add the translation to
	 * @param original The word to initialize the text field to
	 * @return The result of the dialog
	 */
	public Optional<Pair<String, String[]>> addTranslationDialog(ActionEvent actionEvent, Dictionary dictionary, String original) {
		Dialog<Pair<String, String[]>> dialog = new Dialog<>();
		dialog.setTitle("Add a Translation");

		if (dictionary == null) {
			dialog.setContentText("Please select dictionary first");
			dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
			dialog.show();
			return Optional.empty();
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

		return result;
	}
}

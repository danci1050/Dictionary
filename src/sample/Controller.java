package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.Pane;

public class Controller {

	@FXML
	private Pane dictionaryTab;

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
	public void dictionaryPane(ActionEvent dictionaryPane) {
		dictionaryTab.setVisible(true);
		translatorTab.setVisible(false);
		viewDictionaryTab.setVisible(false);
		settingsTab.setVisible(false);
	}

	@FXML
	public void translatorPane(ActionEvent translatorPane) {
		dictionaryTab.setVisible(false);
		translatorTab.setVisible(true);
		viewDictionaryTab.setVisible(false);
		settingsTab.setVisible(false);
	}

	@FXML
	public void viewDictionaryPane(ActionEvent viewDictionaryPane) {
		dictionaryTab.setVisible(false);
		translatorTab.setVisible(false);
		viewDictionaryTab.setVisible(true);
		settingsTab.setVisible(false);
	}

	@FXML
	public void settingsPane(ActionEvent settingsPane) {
		dictionaryTab.setVisible(false);
		translatorTab.setVisible(false);
		viewDictionaryTab.setVisible(false);
		settingsTab.setVisible(true);
	}

	// TODO: must be removed after the translator class is merged in
	Dictionary dict = new Dictionary();

	@FXML
	public void search(ActionEvent search) {
		try {
			if (dutchEnglish.isSelected()) {
				searchedWord.setText(
						searchField.getText().substring(0, 1).toUpperCase() + searchField.getText().substring(1));
				result.setText(dict.searchAWord(searchField.getText().toLowerCase()).getTranslation().toString());
			} else {
				searchedWord.setText(
						searchField.getText().substring(0, 1).toUpperCase() + searchField.getText().substring(1));
				result.setText(dict.searchAWord(searchField.getText().toLowerCase()).getTranslation().toString());
			}
		} catch (NoTranslationException e) {
			result.setText(e.getMessage());
		}
	}

}

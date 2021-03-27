package sample;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

import javafx.util.Pair;

public class Translator {
	private String dictionariesFolder;
	private HashMap<String, Dictionary> dictionaries;

	public Translator() {
		this("dictionaries");
	}

	public Translator(String folder) {
		dictionaries = new HashMap<>();
		dictionariesFolder = folder;
		String[] files = null;
		if (folder != null) {
			files = new File(folder).list();
		}
		if (files != null && files.length > 0) {
			for (String filename : files) {
				String[] languages = filename.replace(".ser", "").split("_");
				Dictionary dict_object = new Dictionary(languages[0], languages[1]);
				try {
					dict_object.loadDict(folder + "/" + filename);
					dictionaries.put(String.join("", languages), dict_object);
				} catch (ClassNotFoundException | IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void addEntry(String fromLanguage, String toLanguage, String wordPhrase, String[] translations,
			String[] explanations) {
		dictionaries.get(fromLanguage + toLanguage).add(wordPhrase, translations, explanations);
	}

	public void removeEntry(String fromLanguage, String toLanguage, String wordPhrase, String[] translations)
			throws NoTranslationException {
		dictionaries.get(fromLanguage + toLanguage).remove(wordPhrase, translations);
	}

	public Entry searchAWord(String fromLanguage, String toLanguage, String word) throws NoTranslationException {
		return dictionaries.get(fromLanguage + toLanguage).searchAWord(word);
	}

	public void addDictionary(String fromLanguage, String toLanguage, Dictionary dict) {
		dictionaries.put(fromLanguage + toLanguage, dict);
	}

	public void saveDictionaries() {
		for (Dictionary dictionary : dictionaries.values()) {
			dictionary.writeDictionary(dictionariesFolder);
		}
	}

	public Set<Pair<String, String>> getLanguages() {
		Set<Pair<String, String>> languages = new HashSet<>();
		for (Dictionary dictionary : dictionaries.values()) {
			languages.add(new Pair<>(dictionary.getFromLanguage(), dictionary.getToLanguage()));
		}
		return languages;
	}

	// TODO: verify that this method is correct
	private Pair<String, List<Pair<String, String>>> processPhrase(String[] inputTextArray, int i, Entry wordEntry) {
		Pair<String, List<Pair<String, String>>> result;
		Entry nextEntry;

		if (wordEntry.getPhrase() != null && (nextEntry = wordEntry.getPhrase().get(inputTextArray[i + 1])) != null) {
			result = processPhrase(inputTextArray, i + 1, nextEntry);
			return new Pair<>(wordEntry.getWord() + " " + result.getKey(), result.getValue());
		} else {
			return new Pair<>(wordEntry.getWord(), wordEntry.getTranslation());
		}
	}

	// TODO: verify that this method is correct
	public List<Pair<String, List<Pair<String, String>>>> translate(String fromLanguage, String toLanguage,
			String inputText) {
		// Regex pattern matches any Unicode punctuation or symbol
		String punctuation = "([\\p{P}\\p{S}])";

		inputText = inputText.replaceAll(punctuation, " $1 ");
		// Regex pattern matches any whitespace character(s)
		String[] inputTextArray = inputText.split("[\\s]+");

		List<Pair<String, List<Pair<String, String>>>> translation = new LinkedList<>();
		for (int i = 0; i < inputTextArray.length;) {
			String word = inputTextArray[i];

			if (!word.matches(punctuation)) {
				try {
					Entry wordEntry = searchAWord(fromLanguage, toLanguage, word);
					translation.add(processPhrase(inputTextArray, i, wordEntry));
				} catch (NoTranslationException e) {
					translation.add(new Pair<>(word, null));
				}
			} else {
				translation.add(new Pair<>(word, null));
			}

			// Move by the number of processed words
			i += translation.get(translation.size() - 1).getKey().split(" ").length;
		}

		return translation;
	}

	/**
	 *
	 * @param fromLanguage
	 * @param toLanguage
	 * @param inputText
	 * @return A list, where the first Pair contains the total translation time
	 */
	// TODO implement
	public List<Pair<String, Set<String>>> timedTranslate(String fromLanguage, String toLanguage, String inputText) {
		return null;
	}

	public File loadFile() {

		JFrame f = new JFrame();
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle("Open");
		int userSelection = chooser.showOpenDialog(f);
		if (userSelection == JFileChooser.APPROVE_OPTION) {
			return chooser.getSelectedFile();
		}
		return null;
	}

	public String readFile(File file) {
		List<String> text = new ArrayList<>();
		try {
			Files.lines(Path.of(file.getPath()), StandardCharsets.ISO_8859_1).forEachOrdered(text::add);
			return text.stream().collect(Collectors.joining(", "));
		} catch (IOException e) {
			System.out.println("io exception");
		}

		return null;
	}

	public File saveFile() {

		JFrame f = new JFrame();
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle("Save");
		int userSelection = chooser.showSaveDialog(f);
		if (userSelection == JFileChooser.APPROVE_OPTION) {
			return chooser.getSelectedFile();
		}
		return null;

	}

	public void save(File file, String txt) {

		try {

			Files.writeString(Path.of(file.getPath()), txt);

		} catch (IOException e) {
			System.out.println("error");
		}

	}

	public void saveTime() throws InterruptedException {
		long startTime = System.nanoTime();
		TimeUnit.SECONDS.sleep(5);

		long endTime = System.nanoTime();
		long timeElapsed = endTime - startTime;
		System.out.println(timeElapsed / 1000);
	}
}

package sample;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
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
		//TODO: Remove next line block after dictionaries are serialized
		if (files != null && files.length == 0) {
			Dictionary dummyDict = new Dictionary("Dutch", "English");
			Dictionary dummyDict2 = new Dictionary("English", "Dutch");
			try {
				dummyDict.generateDictionaryFromCSVFile("dictionaries_csv/dutWordList_cleaned.csv");
				dummyDict2.generateDictionaryFromCSVFile("dictionaries_csv/engWordList_cleaned.csv");
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
			addDictionary(dummyDict);
			addDictionary(dummyDict2);
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

	public void addDictionary(Dictionary dict) {
		dictionaries.put(dict.getFromLanguage() + dict.getToLanguage(), dict);
	}

	public HashMap<String, Dictionary> getDictionaries() {
		return dictionaries;
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
			if (result.getValue().size() != 0) {
				return new Pair<>(wordEntry.getWord() + " " + result.getKey(), result.getValue());
			}
		}
		return new Pair<>(wordEntry.getWord(), wordEntry.getTranslation());
	}

	// TODO: verify that this method is correct
	public List<Pair<String, List<Pair<String, String>>>> translate(String fromLanguage, String toLanguage,
			String inputText) {
		// Regex pattern matches any Unicode punctuation or symbol
		String punctuation = "([\\p{P}\\p{S}\\n])";

		inputText = inputText.replaceAll(punctuation, " $1 ");
		// Regex pattern matches any whitespace character(s)
		String[] inputTextArray = inputText.split("[ ]+");

		List<Pair<String, List<Pair<String, String>>>> translation = new LinkedList<>();
		for (int i = 0; i < inputTextArray.length;) {
			String word = inputTextArray[i];

			if (!word.matches(punctuation)) {
				try {
					Entry wordEntry = searchAWord(fromLanguage, toLanguage, word);
					translation.add(processPhrase(inputTextArray, i, wordEntry));
				} catch (NoTranslationException e) {
					translation.add(new Pair<>(word, new ArrayList<>(0)));
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
	public List<Pair<String, List<Pair<String, String>>>> timedTranslate(String fromLanguage, String toLanguage, String inputText) {
		List<Pair<String, List<Pair<String, String>>>> translation;
		long startTime = System.nanoTime();

		translation = translate(fromLanguage, toLanguage, inputText);

		long endTime = System.nanoTime();
		long timeElapsed = endTime - startTime;
		translation.add(0, new Pair<>(String.format("translationTime=%.2f ms", timeElapsed / 1e6), null));

		return translation;
	}

	//TODO: potentially implement method to save the exact translation displayed by the GUI, taking into account
	// the translations selected by the "other translations" dialog
	public String getStringTranslation (List<Pair<String, List<Pair<String, String>>>> translation) {
		StringBuilder translationString = new StringBuilder();
		String time;
		if ((time = translation.get(0).getKey()).contains("translationTime=")) {
			translationString.append(String.format("The translation was done in %s\n", time.split("=")[1]));
			translation.remove(0);
		}

		for (Pair<String, List<Pair<String, String>>> pair : translation) {
			if (pair.getValue() == null) {
				translationString.append(pair.getKey());
				continue;
			}
			if (translationString.length() >= 1 && !translationString.substring(translationString.length() - 1).matches("\\n")) {
				translationString.append(" ");
			}
			if (pair.getValue().size() == 0) {
				translationString.append("<").append(pair.getKey()).append(">");
			} else {
				translationString.append(pair.getValue().get(0).getKey());
			}
		}

		return translationString.toString();
	}

	public File loadFileDialog() {

		JFrame f = new JFrame();
		JFileChooser chooser = new JFileChooser();
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
			System.out.println("IO Exception");
		}

		return null;
	}

	public File saveFileDialog() {

		JFrame f = new JFrame();
		JFileChooser chooser = new JFileChooser();
		chooser.setSelectedFile(new File("translation.txt"));
		int userSelection = chooser.showSaveDialog(f);
		if (userSelection == JFileChooser.APPROVE_OPTION) {
			return chooser.getSelectedFile();
		}

		return null;

	}
	//TODO: potentially implement method to save the exact translation displayed by the GUI, taking into account
	// the translations selected by the "other translations" dialog
	public void saveTranslation(File file, List<Pair<String, List<Pair<String, String>>>> translation) {
		try {
			Files.writeString(Path.of(file.getPath()), getStringTranslation(translation));
		} catch (IOException e) {
			System.out.println("IO Exception");
		}

	}
}

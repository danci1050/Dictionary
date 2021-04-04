package sample;

import javafx.util.Pair;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class Translator {
	private String dictionariesFolder;
	private HashMap<String, Dictionary> dictionaries;

	/**
	 * Initializes the class, loading serialized Dictionaries from "dictionaries" folder.
	 */
	public Translator() {
		this("dictionaries");
	}

	/**
	 * Initializes the class, loading serialized Dictionaries from the specified folder. No Dictionaries will be
	 * initialized if null is passed as the argument.
	 * @param folder Path to the folder with serialized Dictionaries.
	 */
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
				dummyDict.generateDictionaryFromCSVFile(Path.of("dictionaries_csv","dutWordList_cleaned.csv"));
				dummyDict2.generateDictionaryFromCSVFile(Path.of("dictionaries_csv", "engWordList_cleaned.csv"));
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
			addDictionary(dummyDict);
			addDictionary(dummyDict2);
		}
	}

	/**
	 * Adds the translations to the Dictionary.
	 * @param fromLanguage The language of the original expression.
	 * @param toLanguage The language of the translation.
	 * @param expression The original expression.
	 * @param translations The translations of the expression.
	 * @param explanations The explanations of the translations. This array should be the same length as the
	 *                        translations array.
	 */
	public void addTranslation(String fromLanguage, String toLanguage, String expression, String[] translations,
							   String[] explanations) {
		dictionaries.get(fromLanguage + toLanguage).add(expression, translations, explanations);
	}

	/**
	 * Removes the specified translations from the Dictionary.
	 * @param fromLanguage The language of the original expression.
	 * @param toLanguage The language of the translation.
	 * @param expression The original expression.
	 * @param translations The translations of the expression.
	 * @throws NoTranslationException
	 */
	public void removeEntry(String fromLanguage, String toLanguage, String expression, String[] translations)
			throws NoTranslationException {
		dictionaries.get(fromLanguage + toLanguage).remove(expression, translations);
	}

	/**
	 * Searches for a single word in the Dictionary.
	 * @param fromLanguage The language of the original word.
	 * @param toLanguage The language to translate to.
	 * @param word The sought word.
	 * @return An Entry from the Dictionary, corresponding to the the word.
	 * @throws NoTranslationException Thrown if the Dictionary does not contain the translation for the word.
	 */
	public Entry searchAWord(String fromLanguage, String toLanguage, String word) throws NoTranslationException {
		return dictionaries.get(fromLanguage + toLanguage).searchAWord(word);
	}

	/**
	 * Adds the specified Dictionary to the Translator's dictionaries.
	 * @param dict The Dictionary to add.
	 */
	public void addDictionary(Dictionary dict) {
		dictionaries.put(dict.getFromLanguage() + dict.getToLanguage(), dict);
	}

	/**
	 * Returns all dictionaries the Translator has access to.
	 * @return All dictionaries the Translator has access to.
	 */
	public HashMap<String, Dictionary> getDictionaries() {
		return dictionaries;
	}

	/**
	 * Serializes all Dictionaries the Translator has access to and saves them to the folder from which the Translator
	 * loaded the Dictionaries at initialization. Does nothing if the Translator was initialized from "null" directory.
	 */
	public void saveDictionaries() {
		if (dictionariesFolder != null) {
			for (Dictionary dictionary : dictionaries.values()) {
				dictionary.writeDictionary(dictionariesFolder);
			}
		}
	}

	/**
	 * Returns the set of pairs of languages the Dictionary can translate from/to.
	 * @return Set of pairs of languages the Dictionary can translate from/to. The key of the pair is the source
	 *         language and the value is the destination language.
	 */
	public Set<Pair<String, String>> getLanguages() {
		Set<Pair<String, String>> languages = new HashSet<>();
		for (Dictionary dictionary : dictionaries.values()) {
			languages.add(new Pair<>(dictionary.getFromLanguage(), dictionary.getToLanguage()));
		}
		return languages;
	}

	/**
	 * Matches phrases from a particular Entry to following words of the input text array.
	 * @param inputTextArray The array to match the phrases to.
	 * @param i Index of the inputTextArray which corresponds to the position the wordEntry is located on.
	 * @param wordEntry The Entry containing phrases to match
	 * @return A Pair, where the Key is the matched phrase and the Value is a List of all known translations paired
	 *         with their corresponding explanations.
	 */
	// TODO: verify that this method is correct
	private Pair<String, List<Pair<String, String>>> processPhrase(String[] inputTextArray, int i, Entry wordEntry) {
		Pair<String, List<Pair<String, String>>> result;
		Entry nextEntry;
		String skipWhenProcessing = "[\\n]+";

		if (i == inputTextArray.length - 1) {
			return new Pair<>(wordEntry.getWord(), wordEntry.getTranslation());
		}

		// If the next word is a character which should be skipped when processing phrases, skip it
		if (inputTextArray[i + 1].matches(skipWhenProcessing)) {
			return processPhrase(inputTextArray, i + 1, wordEntry);
		}
		// If this Entry does contain a phrase with the next word of the input text array
		if (wordEntry.getPhrase() != null && (nextEntry = wordEntry.getPhrase().get(inputTextArray[i + 1])) != null) {
			// Call this method recursively on the phrases' Entry
			result = processPhrase(inputTextArray, i + 1, nextEntry);
			// If there is at least one translation of the matched phrase
			if (result.getValue().size() > 0) {
				// Return the matched phrase together with the translation
				return new Pair<>(wordEntry.getWord() + " " + result.getKey(), result.getValue());
			}
		}
		// If the Entry does contain a phrase with the next word of the input text array or the phrase has no
		// translation, return the translation of the phrase that was matched so far.
		return new Pair<>(wordEntry.getWord(), wordEntry.getTranslation());
	}

	/**
	 * <p>
	 *     Translates the specified text from fromLanguage to toLanguage. A composite data structure is returned, containing
	 * 	   the translation.
	 * </p>
	 * <p>
	 *     Each Pair in the returned List is representing a word or a phrase that was translated. The Key
	 * 	   of the Pair is the word/phrase in the original language, and the Value corresponds to the translation.
	 * </p>
	 * <p>
	 *     The translation for each word/phrase is a List of Pairs, where the Key is the translation and the Value is
	 *     the explanation. If the length of this list is zero, then there is no translation for this word in the
	 *     dictionary. If this list is null, the word is considered to be some type of punctuation or a number, and was
	 *     not translated.
	 * </p>
	 * @param fromLanguage The language of the original text.
	 * @param toLanguage The language of the sought translation.
	 * @param inputText The text to translate.
	 * @return The translation in the format described in the method description.
	 */
	// TODO: verify that this method is correct
	public List<Pair<String, List<Pair<String, String>>>> translate(String fromLanguage, String toLanguage,
			String inputText) {

		List<Pair<String, List<Pair<String, String>>>> translation = new LinkedList<>();

		if (inputText.matches("^[\\s]*$")) {
			return translation;
		}

		// Regex pattern matches any Unicode punctuation, symbol, newline character or number
		String doNotTranslate = "([\\p{P}\\p{S}\\n0-9]+)";

		// Separate characters/words which are not supposed to be translated by spaces
		inputText = inputText.replaceAll(doNotTranslate, " $1 ");
		String[] inputTextArray = inputText.split("[ ]+");

		for (int i = 0; i < inputTextArray.length;) {
			String word = inputTextArray[i];

			// If the word is supposed to be translated
			if (!word.matches(doNotTranslate)) {
				try {
					// If the Dictionary contains an Entry corresponding to the word, try to match all its phrases
					Entry wordEntry = searchAWord(fromLanguage, toLanguage, word);
					translation.add(processPhrase(inputTextArray, i, wordEntry));
				} catch (NoTranslationException e) {
					// If the Dictionary does not contain a corresponding Entry, add a Pair with empty translation List
					translation.add(new Pair<>(word, new ArrayList<>(0)));
				}
			} else {
				// If the word is not supposed to be translated, do not translate, instead add a Pair with null
				// translation
				translation.add(new Pair<>(word, null));
			}

			// Move by the number of processed words
			// A regex pattern for whitespace is not wanted here, as it would incorrectly count the newline characters
			i += translation.get(translation.size() - 1).getKey().split(" ").length;
		}

		return translation;
	}

	/**
	 *	Translates the specified text from fromLanguage to toLanguage. A composite data structure is returned, containing
	 * 	the translation. For the translation format, see {@link #translate the translate() method.}. The translation
	 * 	time is returned as the first entry of the translation list.
	 * @param fromLanguage The language of the original text.
	 * @param toLanguage The language of the sought translation.
	 * @param inputText The text to translate.
	 * @return A translation List, where the first Pair contains the total translation time
	 * @see Translator#translate(java.lang.String, java.lang.String, java.lang.String)
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

	/**
	 * A method which joins the translation returned by the {@link #translate translate()} or
	 * {@link #timedTranslate timedTranslate()} method into a string. Words, which were not translated are put in
	 * closed brackets <>. Translation time is also handled correctly.
	 * @param translation The translation returned by translate() or timedTranslate() methods.
	 * @return
	 */
	//TODO: potentially implement method to save the exact translation displayed by the GUI, taking into account
	// the translations selected by the "other translations" dialog
	public String getStringTranslation (List<Pair<String, List<Pair<String, String>>>> translation) {
		StringBuilder translationString = new StringBuilder();

		// Process translation time
		String time;
		if ((time = translation.get(0).getKey()).contains("translationTime=")) {
			translationString.append(String.format("The translation was done in %s\n", time.split("=")[1]));
			translation.remove(0);
		}

		// Process translation
		for (Pair<String, List<Pair<String, String>>> pair : translation) {
			// The translation is null -> the was not meant to be translated
			if (pair.getValue() == null) {
				translationString.append(pair.getKey());
				continue;
			}
			// If the last character was not a newline, append space
			if (translationString.length() >= 1 &&
					!translationString.substring(translationString.length() - 1).matches("\\n")) {
				translationString.append(" ");
			}
			// If the word has no translation
			if (pair.getValue().size() == 0) {
				translationString.append("<").append(pair.getKey()).append(">");
			} else {
				// If the word has translation
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
	public void saveTranslation(File file, List<Pair<String, List<Pair<String, String>>>> translation) {
		try {
			Files.writeString(Path.of(file.getPath()), getStringTranslation(translation));
		} catch (IOException e) {
			System.out.println("IO Exception");
		}
	}
}

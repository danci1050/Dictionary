package sample;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;

public class Dictionary {
	// TODO: make the class language-independent to support non-named language pairs
	private Map<String, Entry> dictToEnglish;
	private Map<String, Entry> dictFromEnglish;

	public Dictionary() {
		// TODO: a dictionary must be loaded by the Translator class
		dictToEnglish = new HashMap<String, Entry>();
		dictFromEnglish = new HashMap<String, Entry>();

		// TODO: call these functions from Translator
		/*
		 * try { loadDict("dictionaries/dutchEnglish.ser", true);
		 * loadDict("dictionaries/englishDutch.ser", true); } catch
		 * (ClassNotFoundException | IOException e) { e.printStackTrace(); }
		 */
		try {
			// TODO: load actual dictionaries, not the test file.
			// TODO: implement inverse dictionary construction
			generateDictionaryFromCSVFile("test/wordsample.csv", true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Add an entry to the dictionary
	 * 
	 * @param original
	 * @param translations
	 */
	public void add(String original, String[] translations, boolean toEnglish) {
		original = original.strip();

		Entry newEntry = new Entry(original);
		// add all possible translations of the word
		// TODO: handle phrases correctly
		for (String translation : translations) {
			newEntry.addTranslation(translation.strip());
		}

		(toEnglish ? dictToEnglish : dictFromEnglish).put(original, newEntry);
	}

	/**
	 * Generate a Map with words as keys and Entry as values
	 * 
	 * @param path path to the CSV file
	 * @throws FileNotFoundException CSV file not found or is not a file
	 * @throws IOException           general error
	 */
	public void generateDictionaryFromCSVFile(String path, boolean toEnglish)
			throws FileNotFoundException, IOException {
		// Delimiter used for CSV parsing
		final String delimiter = ",";

		FileReader fr = new FileReader(path);
		BufferedReader br = new BufferedReader(fr);

		// TODO: verify that the CSV file is valid. Should check that each word has at
		// least one translation
		String line;
		while ((line = br.readLine()) != null) {
			System.out.println(line);
			line = line.toLowerCase();
			// Split the input line in 2 parts
			String[] splitLine = line.split(delimiter, 2);
			add(splitLine[0], splitLine[1].split(delimiter), toEnglish);
		}

		fr.close();
	}

	public Map<String, Entry> getDictFromEnglish() {
		return dictFromEnglish;
	}

	public Map<String, Entry> getDictToEnglish() {
		return dictToEnglish;
	}

	/**
	 * Loads a serialized dictionary from disk
	 * 
	 * @param path      path to the dictionary
	 * @param toEnglish the dictionary is to English language
	 * @throws IOException            failed to read the file
	 * @throws ClassNotFoundException
	 */
	// TODO: verify that the loaded dictionary is valid by checking types of values
	// of the map
	public void loadDict(String path, boolean toEnglish) throws IOException, ClassNotFoundException {
		FileInputStream fis = new FileInputStream(path);
		ObjectInputStream ois = new ObjectInputStream(fis);
		if (toEnglish) {
			dictToEnglish = (Map<String, Entry>) ois.readObject();
		} else {
			dictFromEnglish = (Map<String, Entry>) ois.readObject();
		}
		ois.close();

	}

	/**
	 * Removes an entry from the dictionary
	 * 
	 * @param word      word to remove
	 * @param toEnglish use toEnglish dictionary
	 */
	public void remove(String word, boolean toEnglish) {
		(toEnglish ? dictToEnglish : dictFromEnglish).remove(word);
	}

	/**
	 * Searches a word is the dictionary and returns the dictionary entry
	 * 
	 * @param searchWord word to search
	 * @param toEnglish  whether the translation is being done to English
	 * @return corresponding entry in the dictionary
	 */
	// TODO: rename(?) getTranslation would be more descriptive.
	public Entry search(String searchWord, Boolean toEnglish) throws NoTranslationException {

		Entry entry = (toEnglish ? dictToEnglish : dictFromEnglish).get(searchWord);
		if (entry == null) {
			throw new NoTranslationException();
		} else {
			return entry;
		}
	}

	public void setDictFromEnglish(Map<String, Entry> dictFromEnglish) {
		this.dictFromEnglish = dictFromEnglish;
	}

	public void setDictToEnglish(Map<String, Entry> dictToEnglish) {
		this.dictToEnglish = dictToEnglish;
	}

	/**
	 * Serializes and writes a dictionary to a file
	 * 
	 * @param path      path to the file to write to
	 * @param toEnglish write the toEnglsh dictionary
	 */
	public void writeDictionary(String path, boolean toEnglish) {
		// TODO: implement
		return;
	}
}

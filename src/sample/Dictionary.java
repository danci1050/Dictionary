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
	private Map<String, Entry> dict;
	// TODO: add a dictionary name field/other metadata(?)

	public Dictionary() {
		// TODO: a dictionary must be loaded by the Translator class
		dict = new HashMap<String, Entry>();

		// TODO: call these functions from Translator
		/*
		 * try { loadDict("dictionaries/dutchEnglish.ser", true);
		 * loadDict("dictionaries/englishDutch.ser", true); } catch
		 * (ClassNotFoundException | IOException e) { e.printStackTrace(); }
		 */
		try {
			// TODO: load actual dictionaries, not the test file.
			generateDictionaryFromCSVFile("test/wordsample.csv");
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
	public void add(String original, String[] translations) {
		original = original.strip();

		Entry newEntry = new Entry(original);
		// add all possible translations of the word
		// TODO: handle phrases correctly
		for (String translation : translations) {
			newEntry.addTranslation(translation.strip());
		}

		dict.put(original, newEntry);
	}

	/**
	 * Generate a Map with words as keys and Entry as values
	 * 
	 * @param path path to the CSV file
	 * @throws FileNotFoundException CSV file not found or is not a file
	 * @throws IOException           general error
	 */
	public void generateDictionaryFromCSVFile(String path) throws FileNotFoundException, IOException {
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
			add(splitLine[0], splitLine[1].split(delimiter));
		}

		fr.close();
	}

	public Map<String, Entry> getDict() {
		return dict;
	}

	/**
	 * Loads a serialized dictionary from disk
	 * 
	 * @param path path to the dictionary
	 * @throws IOException            failed to read the file
	 * @throws ClassNotFoundException
	 */
	// TODO: verify that the loaded dictionary is valid by checking types of values
	// of the map. Also do other safety checks/validation here.
	public void loadDict(String path) throws IOException, ClassNotFoundException {
		FileInputStream fis = new FileInputStream(path);
		ObjectInputStream ois = new ObjectInputStream(fis);
		dict = (Map<String, Entry>) ois.readObject();
		ois.close();

	}

	/**
	 * Removes an entry from the dictionary
	 * 
	 * @param word word to remove
	 */
	public void remove(String word) {
		dict.remove(word);
	}

	/**
	 * Searches a word is the dictionary and returns the dictionary entry
	 * 
	 * @param searchWord word to search
	 * @return corresponding entry in the dictionary
	 */
	// TODO: rename(?) getTranslation would be more descriptive.
	public Entry search(String searchWord) throws NoTranslationException {

		Entry entry = dict.get(searchWord);
		if (entry == null) {
			throw new NoTranslationException();
		} else {
			return entry;
		}
	}

	public void setDict(Map<String, Entry> dict) {
		this.dict = dict;
	}

	/**
	 * Serializes and writes a dictionary to a file
	 * 
	 * @param path path to the file to write to
	 */
	public void writeDictionary(String path) {
		// TODO: implement
		return;
	}
}

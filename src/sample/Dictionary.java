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
	private final String fromLanguage;
	private final String toLanguage;

	public Dictionary(String fromLanguage, String toLanguage) {
		dict = new HashMap<>();
		this.fromLanguage = fromLanguage;
		this.toLanguage = toLanguage;
	}

	public String getFromLanguage() {
		return fromLanguage;
	}

	public String getToLanguage() {
		return toLanguage;
	}

	/**
	 * Add an entry to the dictionary
	 *
	 * @param original
	 * @param translations
	 */
	public void add(String original, String[] translations) {
		original = original.strip().toLowerCase();
		String[] originalWords = original.split(" ");

		Entry entry;
		int i = 1;
		// progress down the first word's entry phrases as far as the corresponding entries exist
		try {
			entry = searchAWord(originalWords[0]);
			while (i < originalWords.length) {
				if (entry.getPhrase().get(originalWords[i]) != null) {
					entry = entry.getPhrase().get(originalWords[i]);
					i++;
				} else {
					break;
				}
			}
		} catch (NoTranslationException e) {
			entry = new Entry(originalWords[0]);
			dict.put(originalWords[0], entry);
		}

		// create entries which are missing
		while (i < originalWords.length) {
			Entry newEntry = new Entry(originalWords[i]);
			entry.getPhrase().put(originalWords[i], newEntry);
			entry = newEntry;
			i++;
		}

		// add all possible translations of the word/phrase
		for (String translation : translations) {
			entry.addTranslation(translation.strip());
		}
	}

	/**
	 * Generate a Map with words as keys and Entry as values
	 * 
	 * @param path path to the CSV file
	 * @throws FileNotFoundException CSV file not found or is not a file
	 * @throws IOException           general error
	 */
	//TODO make sure that the csv files are in the same form and that phrases are loaded correctly
	public void generateDictionaryFromCSVFile(String path) throws FileNotFoundException, IOException {
		// Delimiter used for CSV parsing
		final String delimiter = ",";

		FileReader fr = new FileReader(path);
		BufferedReader br = new BufferedReader(fr);

		//TODO: verify that the CSV file is valid. Should check that each word has at
		// least one translation
		String line;
		while ((line = br.readLine()) != null) {
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
	//TODO: verify that the loaded dictionary is valid by checking types of values
	// of the map. Also do other safety checks/validation here.
	public void loadDict(String path) throws IOException, ClassNotFoundException {
		FileInputStream fis = new FileInputStream(path);
		ObjectInputStream ois = new ObjectInputStream(fis);
		dict = (Map<String, Entry>) ois.readObject();
		ois.close();

	}

	/**
	 * Removes translations from the dictionary
	 *
	 * @param original Word/phrase to remove the meaning from
	 * @param translations The translations to remove
	 */
	public void remove(String original, String[] translations) throws NoTranslationException {
		original = original.strip().toLowerCase();
		String[] originalWords = original.split(" ");
		Entry[] entries = new Entry[originalWords.length];

		Entry entry = searchAWord(originalWords[0]);
		entries[0] = entry;
		for (int i = 1; i < originalWords.length; i++) {
			if (entry.getPhrase().get(originalWords[i]) != null) {
				entry = entry.getPhrase().get(originalWords[i]);
				entries[i] = entry;
			} else {
				throw new NoTranslationException();
			}
		}
		for (String translation : translations) {
			if (!entry.getTranslation().remove(translation)) {
				throw new NoTranslationException();
			}
		}

		// cleanup empty dictionary entries
		for (int i = originalWords.length - 1; i > 0; i--) {
			if (entries[i].getPhrase().size() == 0 && entries[i].getTranslation().size() == 0) {
				entries[i-1].getPhrase().remove(originalWords[i]);
			} else {
				return;
			}
		}
	}

	/**
	 * Searches a word (NOT a phrase!) is the dictionary and returns the dictionary entry
	 * 
	 * @param searchWord word to search
	 * @return corresponding entry in the dictionary
	 */
	public Entry searchAWord(String searchWord) throws NoTranslationException {

		Entry entry = dict.get(searchWord.toLowerCase());
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
		//TODO: dictionaries should be stored in "dictionaries" folder and have filename in the form
		// fromLanguage_toLanguage.ser
		return;
	}
}

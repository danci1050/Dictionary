package sample;

import java.io.*;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
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
	 * Add an entry to the dictionary. "translations" length should be the same as
	 * "explanations".
	 *
	 * @param original     word in the original language
	 * @param translations translations of the word
	 * @param explanations explanations/comments of the translation
	 */
	public void add(String original, String[] translations, String[] explanations) {
		original = original.strip().toLowerCase();
		String[] originalWords = original.split("[\\s]+");

		Entry entry;
		int i = 1;
		// progress down the first word's entry phrases as far as the corresponding
		// entries exist
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
		for (int idx = 0; idx < translations.length; idx++) {
			String explanation;
			if (idx >= explanations.length) {
				explanation = "";
			} else {
				if (explanations[idx] == null) {
					explanations[idx] = "";
				}
				explanation = explanations[idx];
			}

			entry.addTranslation(translations[idx].replaceAll("[\\s,]+", " ").strip(),
					explanation.replaceAll("[\\s,]+", " ").strip());
		}
	}

	/**
	 * Generate a Map with words as keys and Entry as values
	 * 
	 * @param path The path to the CSV file
	 * @throws FileNotFoundException CSV file not found or is not a file
	 * @throws IOException           general error
	 */
	// TODO: make sure that the csv files are in the same form and that phrases are
	// loaded correctly
	public void generateDictionaryFromCSVFile(Path path) throws FileNotFoundException, IOException {
		// Delimiter used for CSV parsing
		// Arbitrary UTF-8 special character
		final String CSVDelimiter = new String(new byte[] { 0x17 });
		// Delimiter used to split data in columns
		// The current implementation is basically a nested CSV
		final String CSVSecondaryDelimiter = new String(new byte[] { 0x1b });

		FileReader fr = new FileReader(path.toFile());
		BufferedReader br = new BufferedReader(fr);

		// TODO: verify that the CSV file is valid. Should check that each word has at
		// least one translation
		String line;
		// Skip the first line (headers)
		line = br.readLine();
		int errorCount = 0;
		while ((line = br.readLine()) != null) {
			line = line.toLowerCase();
			// Split the input line in 3 parts
			// [Original, [Translations;...], [Explanations;...]]
			String[] splitLine = line.split(CSVDelimiter, -1);
			// Only add the translation if the CSV is valid (has 3 columns)
			// TODO: display a message or throw an exception(?). Could also try to ignore
			// the invalid line.
			if (splitLine.length == 3) {
				add(splitLine[0], splitLine[1].split(CSVSecondaryDelimiter), splitLine[2].split(CSVSecondaryDelimiter));
			} else {
				errorCount++;
			}
		}

		if (errorCount > 0) {
			System.err.println("Number of Errors while parsing CSV: " + errorCount);
		}
		fr.close();
	}

	public Collection<Entry> getDictValues() {
		Collection<Entry> values = new LinkedList<>();
		for (Entry entry : dict.values()) {
			values.addAll(entry.getAllPhrases());
		}
		return values;
	}

	/**
	 * Loads a serialized dictionary from disk
	 * 
	 * @param file The serialized dictionary file
	 * @throws IOException            failed to read the file
	 * @throws ClassNotFoundException
	 */
	// TODO: verify that the loaded dictionary is valid by checking types of values
	// of the map. Also do other safety checks/validation here.
	public void loadDict(File file) throws IOException, ClassNotFoundException {
		FileInputStream fis = new FileInputStream(file);
		ObjectInputStream ois = new ObjectInputStream(fis);
		dict = (Map<String, Entry>) ois.readObject();
		ois.close();
	}

	/**
	 * Removes translations from the dictionary
	 *
	 * @param original     Word/phrase to remove the meaning from
	 * @param translations The translations to remove
	 */
	// TODO: add a method to remove all translations without specifying them
	public void remove(String original, String[] translations) throws NoTranslationException {
		original = original.strip().toLowerCase();
		String[] originalWords = original.split("[\\s]+");
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
			if (!entry.getTranslation()
					.removeIf(pair -> (pair.getKey().equals(translation.replaceAll("[\\s]+", " "))))) {
				throw new NoTranslationException("The translation could not be found in the dictionary");
			}
		}

		// cleanup empty dictionary entries
		for (int i = originalWords.length - 1; i > 0; i--) {
			if (entries[i].getPhrase().size() == 0 && entries[i].getTranslation().size() == 0) {
				entries[i - 1].getPhrase().remove(originalWords[i]);
			} else {
				return;
			}
		}
	}

	/**
	 * Searches a word (NOT a phrase!) is the dictionary and returns the dictionary
	 * entry
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
	 * Serializes and writes a dictionary to a file. Filenames are in format
	 * "fromLanguage_toLanguage.ser"
	 * 
	 * @param path path to the directory to write to
	 */

	public void writeDictionary(Path path) {
		File directory = path.toFile();
		if (!directory.exists()) {
			directory.mkdir();
		}

		if (!directory.isDirectory()) {
			System.err.println("The path given is not a directory!");
			return;
		}

		FileOutputStream fos;
		try {
			fos = new FileOutputStream(new File(directory, String.format("%s_%s.ser", fromLanguage, toLanguage)));
		} catch (FileNotFoundException e) {
			System.err.println("Could not open the file for writing: ");
			System.err.println(e);
			return;
		}

		try {
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(dict);
			oos.close();
		} catch (IOException e) {
			System.err.println("An error has occured while writing the file: ");
			System.err.println(e);
		}
	}

	@Override
	public String toString() {
		return fromLanguage + " -> " + toLanguage;
	}
}

package sample;

import javafx.util.Pair;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class Test {
	public static void main(String[] args) {
		new Test();
	}

	private Test() {
		initialize();
		process();
	}

	private void initialize() {

	}

	private void process() {
		testDictionary();
		testTranslator();
	}

	/**
	 * These tests are already described in the test plan. Update the test plan
	 * accordingly if making changes.
	 */
	private void testDictionary() {
		Dictionary dictionary = new Dictionary("SourceLanguage", "DestinationLanguage");

		// Adding new word
		dictionary.add("ATest", new String[] { "BTest1", "BTest2" }, new String[] { "BExpl1", "BExpl2" });
		System.out.println(dictionary.getDictValues());

		// Adding new phrase
		dictionary.add("A Multi word test here", new String[] { "BSingleWord" }, new String[] { "BSingleExpl" });
		dictionary.add("A Multi word test here", new String[] { "B Translation words" },
				new String[] { "B Explanation words" });
		System.out.println(dictionary.getDictValues());

		// Removing a phrase translation
		try {
			dictionary.remove("A Multi word test here", new String[] { "BSingleWord" });
		} catch (NoTranslationException e) {
			e.printStackTrace();
			System.exit(1);
		}
		System.out.println(dictionary.getDictValues());

		// Removing non-existent phrase translation (Handling exceptions)
		try {
			dictionary.remove("A Multi word test here", new String[] { "BSingleWord" });
		} catch (NoTranslationException e) {
			e.printStackTrace(System.out);
		}

		// Cleaning the dictionary after removing all translations and phrases from an
		// Entry
		try {
			dictionary.remove("A Multi word test here", new String[] { "B Translation words" });
		} catch (NoTranslationException e) {
			e.printStackTrace();
			System.exit(1);
		}
		System.out.println(dictionary.getDictValues());

		// Serialization tests
		try {
			Dictionary testDict = new Dictionary("Dutch", "English");
			testDict.generateDictionaryFromCSVFile(Path.of("test", "dutchEnglishSmall.csv"));
			testDict.writeDictionary(Path.of("test"));

			Dictionary testDict2 = new Dictionary("Dutch", "English");
			testDict2.loadDict(new File("test/Dutch_English.ser"));
			System.out.println("Size of serialized dictionary is the same as the original: "
					+ (testDict.getDictValues().size() == testDict2.getDictValues().size()));
		} catch (Exception e) {
		}
	}

	private void testTranslator() {
		// Test generating Dictionary from CSV
		Dictionary testDict = new Dictionary("Dutch", "English");
		try {
			testDict.generateDictionaryFromCSVFile(Path.of("test", "dutchEnglishSmall.csv"));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		// Test adding Dictionary to empty Translator
		Translator translator = new Translator(null);
		translator.addDictionary(testDict);
		System.out.println(translator.getLanguages());

		// Test translating text
		String text = null;
		try {
			text = Files.readString(Path.of("test/dutchSmall.txt"));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		List<Pair<String, List<Pair<String, String>>>> translation = translator.timedTranslate("Dutch", "English",
				text);
		System.out.println(translator.getStringTranslation(translation));

		// Load the serialized dictionaries
		translator = new Translator();

		// Test translating a news article with the big dictionary
		translation = translator.timedTranslate("Dutch", "English",
				translator.readFile(Path.of("test", "dutNewsArticle.txt").toFile()));
		System.out.println(translator.getStringTranslation(translation));
		System.out.println(Arrays.deepToString(translation.toArray()));
	}
}

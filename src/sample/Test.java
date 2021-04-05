package sample;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import javafx.util.Pair;

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

		System.out.println("Testing Dictionary functionality...");
		System.out.println("========");
		// Adding new word
		System.out.print("Can add a new word: ");
		dictionary.add("ATest", new String[] { "BTest1", "BTest2" }, new String[] { "BExpl1", "BExpl2" });
		System.out.println(dictionary.getDictValues());

		// Adding new phrase
		dictionary.add("A Multi word test here", new String[] { "BSingleWord" }, new String[] { "BSingleExpl" });
		dictionary.add("A Multi word test here", new String[] { "B Translation words" },
				new String[] { "B Explanation words" });
		System.out.println(dictionary.getDictValues());

		// Checking if a word exists
		try {
			dictionary.searchAWord("Btest");
			System.err.println("The word is in the dictionary");
		} catch (NoTranslationException e) {
			System.out.println(e.getMessage());
		}

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

		// Test generating Dictionary from CSV
		dictionary = new Dictionary("Dutch", "English");
		try {
			dictionary.generateDictionaryFromCSVFile(Path.of("test", "dutchEnglishSmall.csv"));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		// Serialization tests
		try {
			dictionary.writeDictionary(Path.of("test"));

			Dictionary testDict2 = new Dictionary("Dutch", "English");
			testDict2.loadDict(Path.of("test", "Dutch_English.ser").toFile());
			System.out.println("Size of serialized dictionary is the same as the original: "
					+ (dictionary.getDictValues().size() == testDict2.getDictValues().size()));
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	private void testTranslator() {
		Dictionary testDict = new Dictionary("Dutch", "English");
		System.out.println("Testing csv generation...");
		System.out.println("========");
		try {
			testDict.generateDictionaryFromCSVFile(Path.of("test", "dutchEnglishSmall.csv"));
			System.out.println("Testing csv generation...");

			System.out.print("The languages are set correctly: ");
			System.out
					.println(testDict.getFromLanguage().equals("Dutch") && testDict.getToLanguage().equals("English"));

			System.out.print("The dictionary is of valid size: ");
			System.out.println(testDict.getDictValues().size() == 41); // 40 Entry objects, 1 Entry has a translation
																		// and a phrase
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		System.out.println("========");

		System.out.println("Testing Translator functionality...");
		System.out.println("========");
		// Test adding Dictionary to empty Translator
		System.out.print("Translator returns correct languages of it's dictionaries: ");
		Translator translator = new Translator(null);
		translator.addDictionary(testDict);
		System.out.println(translator.getLanguages().toString().equals("[Dutch=English]"));

		// Create translator with full dictionaries
		translator = new Translator();

		// Search with an input of a single word
		try {
			System.out.println(translator.searchAWord("English", "Dutch", "dog"));
		} catch (NoTranslationException e) {
			e.printStackTrace();
			System.exit(1);
		}

		// Search with an input of a single English phrase
		System.out.println("\nThe translation of \"dog\":");
		System.out.println(translator.translate("English", "Dutch", "dog eared book"));
		System.out.println("=========");

		// Translation with an input of a line of String in English divided by white space (only words no phrases)
		String text = "Traffic is set to resume in both directions through the canal at 20:00 local time (18:00 GMT), officials say.";
		System.out.println("\nTranslating: " + text);
		List<Pair<String, List<Pair<String, String>>>> translation = translator.translate("English", "Dutch",  text);
		System.out.println(translation);
		System.out.println(translator.getStringTranslation(translation));
		System.out.println("========");

		// Translation with an input of a line of String in English (phrases included)
		text = "Everytime I go to the local library, I get a dog eared book.";
		System.out.println("\nTranslating: " + text);
		translation = translator.translate("English", "Dutch",  text);
		System.out.println(translation);
		System.out.println(translator.getStringTranslation(translation));
		System.out.println("========");

		// Handling inputs with numbers and characters included
		text = "Tug boats honked their horns in celebration as the 400m-long (1,300ft) Ever Given was dislodged on Monday.";
		System.out.println("\nTranslating: " + text);
		translation = translator.translate("English", "Dutch",  text);
		System.out.println(translation);
		System.out.println(translator.getStringTranslation(translation));
		System.out.println("========");

		// Input empty String
		text = "";
		System.out.println("\nTranslating: " + text);
		translation = translator.translate("English", "Dutch",  text);
		System.out.println(translation);
		System.out.println(translator.getStringTranslation(translation));
		System.out.println("========");

		// Translate from file
		Path path = Path.of("test", "EnglishNewsArticle.txt");
		System.out.println("\nTranslating: file from " + path.toAbsolutePath());
		translation = translator.translate("English", "Dutch", translator.readFile(path));
		System.out.println(translation);
		System.out.println(translator.getStringTranslation(translation));
		System.out.println("========");

		// Translate a large file and record time
		path = Path.of("test", "englishLong.txt");
		System.out.println("\nTranslating: file from " + path.toAbsolutePath());
		translation = translator.timedTranslate("English", "Dutch", translator.readFile(path));
		System.out.println(translation);
		System.out.println(translator.getStringTranslation(translation));
		System.out.println("========");

		// Save translation
		path = Path.of("test", "englishLongTranslated.txt");
		System.out.println("Saving the translation to " + path);
		translator.saveTranslation(path, translation);

		System.exit(10);
		// Test translating text
		text = null;
		try {
			text = Files.readString(Path.of("test/dutchSmall.txt"));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		translation = translator.timedTranslate("Dutch", "English",
				text);
		System.out.println(translator.getStringTranslation(translation));
		if (!"""
				<Wat> <leuk> <om> <jou> <eindelijk> <weer> <eens> <te> <schrijven>. i have <je> <lang> <niet> <gezien>. i living now in amsterdam. i
				living in a small house together with my friend. next my house is a football-field. there footbal i
				each saturday together with yet a couple friends. i have made new friends on there football-club.
				she name aman and john. john is eighteen year and lives also in amsterdam. aman is there <oudere> brother from
				john.
				<Nederland> <bevalt> <mij> <erg> <goed>. <Het> <enige> <nadeel> is <dat> <het> <nogal> <vaak> <regent>. there <mensen> <zijn> <wel> <erg>
				<aardig> and <altijd> <behulpzaam>. <Het> <leren> from there <Nederlandse> <taal> <gaat> <goed>. <Alleen> there <grammatica> <vind> i
				<lastig>. i have <al> <veel> new <woorden> <geleerd>. <Hoe> <gaat> <het> <bij> <jou> in <Duitsland>? <Hoe> <gaat> <het> on <je>
				new <school>? <Laat> <mij> <weten> <hoe> <het> there is in <Duitsland>! i <ben> <benieuwd> <naar> <je> <reactie>."""
				.equals(translator.getStringTranslation(translation))) System.err.println("The translation is different");
		else System.out.println("The translation is correct");

		// Load the serialized dictionaries
		translator = new Translator();

		// Test translating a news article with the big dictionary
		translation = translator.timedTranslate("Dutch", "English",
				translator.readFile(Path.of("test", "dutNewsArticle.txt")));
		System.out.println(translator.getStringTranslation(translation));
		System.out.println(Arrays.deepToString(translation.toArray()));
	}
}

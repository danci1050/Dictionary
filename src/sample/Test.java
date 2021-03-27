package sample;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
		// TODO: update the method
//		testTranslator();
	}

	private void testDictionary() {
		Dictionary dictionary = new Dictionary("Anglish", "Bnglish");
		dictionary.add("ATest", new String[] { "BTest1", "BTest2" }, new String[] { "BExpl1", "BExpl2" });
		System.out.println(dictionary.getDict());

		dictionary.add("A Multi word test here", new String[] { "B Translation words", "BSingleWord" },
				new String[] { "B Explanation words", "BSingleExpl" });
		System.out.println(dictionary.getDict());

		try {
			dictionary.remove("A Multi word test here", new String[] { "BSingleWord" });
		} catch (NoTranslationException e) {
			e.printStackTrace();
			System.exit(1);
		}
		System.out.println(dictionary.getDict());
	}

	private void testTranslator() {
		// Test generating Dictionary from CSV
		Dictionary testDict = new Dictionary("Dutch", "English");
		try {
			testDict.generateDictionaryFromCSVFile("test/dutchEnglishSmall.csv");
//            testDict.generateDictionaryFromCSVFile("dictionaries_csv/dutWordList.csv");
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		// Test adding Dictionary to empty Translator
		Translator translator = new Translator(null);
		translator.addDictionary("Dutch", "English", testDict);
		System.out.println(translator.getLanguages());

		// Test translating text
		String text = null;
		try {
			text = Files.readString(Path.of("test/dutchSmall.txt"));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		List<Pair<String, List<Pair<String, String>>>> translation = translator.translate("Dutch", "English", text);
		printTranslation(translation);

		// Test
		System.out.println(translator.readFile(translator.loadFile()));
	}

	private void printTranslation(List<Pair<String, List<Pair<String, String>>>> translation) {
		StringBuilder out = new StringBuilder();
		for (Pair<String, List<Pair<String, String>>> pair : translation) {
			if (pair.getValue() == null) {
				out.append("<").append(pair.getKey()).append("> ");
			} else {
				out.append(pair.getValue().iterator().next()).append(" ");
			}
		}
		System.out.println(out);
	}
}

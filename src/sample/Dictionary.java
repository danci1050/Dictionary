package sample;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;

public class Dictionary {
	private static Map<String, Entry> dutchEnglish;
	private static Map<String, Entry> englishDutch;

	public Dictionary() {
		dutchEnglish = new HashMap<String, Entry>();
		englishDutch = new HashMap<String, Entry>();
	}
	/*
	 * Some of these functions are maybe redundant
	 */

	public static Map<String, Entry> getDutchEnglish() {
		return dutchEnglish;
	}

	public static void setDutchEnglish(Map<String, Entry> dutchEnglish) {
		Dictionary.dutchEnglish = dutchEnglish;
	}

	public static Map<String, Entry> getEnglishDutch() {
		return englishDutch;
	}

	public static void setEnglishDutch(Map<String, Entry> englishDutch) {
		Dictionary.englishDutch = englishDutch;
	}

	// TODO: verify that the loaded dictionary is valid by checking types of values
	// of the map
	public static void loadDutchEnglish() throws IOException, ClassNotFoundException {

		FileInputStream fis = new FileInputStream("dictionaries/dutchEnglish.ser");
		ObjectInputStream ois = new ObjectInputStream(fis);
		dutchEnglish = (Map<String, Entry>) ois.readObject();
		ois.close();
	}

	// TODO: verify that the loaded dictionary is valid by checking types of values
	// of the map
	public static void loadEnglishDutch() throws IOException, ClassNotFoundException {

		FileInputStream fis = new FileInputStream("dictionaries/englishDutch.ser");
		ObjectInputStream ois = new ObjectInputStream(fis);
		englishDutch = (Map<String, Entry>) ois.readObject();
		ois.close();
	}

	// TODO: use local dictionary. GUI logic must be updated to match
	public static String search(Map<String, Entry> temp, String searchWord) {
		// TODO: update pre-generated dictionaries to match the hashmap types
//		return (String) temp.get(searchWord).getWord();
		return "Not implemented.";
	}

	// TODO: use local dictionary. GUI logic must be updated to match
	public static void add(String english, String dutch) {
		return;

//		dutchEnglish.put(dutch, english);
//		englishDutch.put(english, dutch);
	}

	public static void remove(Map<String, Entry> temp, String word) {
		temp.remove(word);
	}
}

package sample;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Entry {

	private final String word;
	private final Set<String> translation = new HashSet<>();
	private final HashMap<String, Entry> phrase = new HashMap<>();

	public Entry(String word) {
		this.word = word;
	}

	public Entry(String word, String translation) {
		this.word = word;
		this.translation.add(translation);
	}

	public String getWord() {
		return word;
	}

	public Set<String> getTranslation() {
		return translation;
	}

	public HashMap<String, Entry> getPhrase() {
		return phrase;
	}
}

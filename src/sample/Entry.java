package sample;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.util.Pair;

public class Entry {
	private final String word;
	private final List<Pair<String, String>> translation = new ArrayList<>();
	private final Map<String, Entry> phrase = new HashMap<>();

	public Entry(String word) {
		this.word = word;
	}

	public Entry(String word, String translation) {
		this(word, translation, "");
	}

	public Entry(String word, String translation, String explanation) {
		this.word = word;
		this.addTranslation(translation, explanation);
	}

	public String getWord() {
		return word;
	}

	public List<Pair<String, String>> getTranslation() {
		return translation;
	}

	/**
	 * Add a translation-explanation pair to the entry
	 * 
	 * @param translation translation of the original word
	 * @param explanation explanation/comments to the translation
	 */
	public void addTranslation(String translation, String explanation) {
		this.translation.add(new Pair<String, String>(translation, explanation));
	}

	public void addTranslation(String translation) {
		this.addTranslation(translation, "");
	}

	public Map<String, Entry> getPhrase() {
		return phrase;
	}

	@Override
	public String toString() {
		return "Entry{" + "translation=" + translation + ", phrase=" + phrase + '}';
	}
}

package sample;

import java.util.*;

import javafx.util.Pair;

public class Entry {
	private final String word;
	private List<Pair<String, String>> translation = new ArrayList<>();
	private final Map<String, Entry> phrase = new HashMap<>();

	public Entry(String word) {
		this.word = word;
	}

	public Entry(String word, String translation) {
		this(word, translation, "");
	}

	public Entry(String word, List<Pair<String, String>> translation) {
		this.word = word;
		this.translation = translation;
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

	public String getStringTranslation() {
		List<String> stringTranslations = new ArrayList<>(translation.size());
		for (Pair<String, String> aTranslation : translation) {
			String stringTranslation = aTranslation.getKey();
			if (!aTranslation.getValue().equals("")) {
				stringTranslation += "(" + aTranslation.getValue() + ")";
			}
			stringTranslations.add(stringTranslation);
		}
		return String.join(", ", stringTranslations);
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

	public Collection<Entry> getAllPhrases() {
		Collection<Entry> values = new LinkedList<>();
		if (translation.size() != 0) {
			values.add(this);
		}
		if (phrase.size() != 0) {
			for (Entry phrase : phrase.values()) {
				for (Entry phraseValue : phrase.getAllPhrases()) {
					values.add(new Entry(word + " " + phraseValue.getWord(), phraseValue.getTranslation()));
				}
			}
		}
		return values;
	}

	@Override
	public String toString() {
		return "Entry{word=" + word + ", translation=" + translation + ", phrase=" + phrase + '}';
	}
}

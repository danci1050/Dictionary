package sample;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javafx.util.Pair;

public class Entry implements Serializable {
	private static final long serialVersionUID = 1L;
	private final String word;
	private Set<String> translations = new HashSet<String>();
	private Map<String, String> explanations = new HashMap<>();
	private final Map<String, Entry> phrase = new HashMap<>();

	public Entry(String word) {
		this.word = word;
	}

	public Entry(String word, String translation) {
		this.word = word;
		this.addTranslation(translation);
	}

	public Entry(String word, List<Pair<String, String>> translations) {
		this.word = word;
		for (Pair<String, String> pair : translations) {
			this.addTranslation(pair.getKey(), pair.getValue());
		}
	}

	public Entry(String word, String translation, String explanation) {
		this.word = word;
		this.addTranslation(translation, explanation);
	}

	/**
	 * Get the word associated with this entry
	 * 
	 * @return the word
	 */
	public String getWord() {
		return word;
	}

	/**
	 * Translations object getter
	 * 
	 * @return translations object
	 */
	public Set<String> getTranslations() {
		return translations;
	}

	/**
	 * Explanations object getter
	 * 
	 * @return explanations object
	 */
	public Map<String, String> getExplanations() {
		return explanations;
	}

	/**
	 * Get an explanation for a translation
	 * 
	 * @param translation translation to get explanation for
	 * @return explanation
	 */
	public String getExplanation(String translation) {
		return explanations.get(translation);
	}

	/**
	 * Generates and returns a list of translations with associated explanations (or
	 * null).
	 * 
	 * @return generated list of translations with explanations
	 */
	public List<Pair<String, String>> getTranslationsWithExplanations() {
		List<Pair<String, String>> translationsWithExplanations = new ArrayList<>(translations.size());

		for (String translation : translations) {
			String explanation = explanations.get(translation);
			translationsWithExplanations.add(new Pair<>(translation, explanation));
		}

		return translationsWithExplanations;
	}

	// TODO: comment
	public String getStringTranslation() {
		List<String> stringTranslations = new ArrayList<>(translations.size());
		for (String translation : translations) {
			// Add explanation if it is present
			String explanation = explanations.get(translation);
			if (explanation != null) {
				translation += " (" + explanation + ")";
			}
			stringTranslations.add(translation);
		}
		return String.join(", ", stringTranslations);
	}

	/**
	 * Add a translation to the entry
	 *
	 * @param translation translation of the original word
	 */
	public void addTranslation(String translation) {
		this.translations.add(translation);
	}

	/**
	 * Add a translation-explanation pair to the entry
	 *
	 * @param translation translation of the original word
	 * @param explanation explanation/comments to the translation
	 */
	public void addTranslation(String translation, String explanation) {
		this.translations.add(translation);
		this.explanations.put(translation, explanation);
	}

	// TODO: comment
	public Map<String, Entry> getPhrase() {
		return phrase;
	}

	// TODO: comment
	public List<Entry> getAllPhrases() {
		List<Entry> values = new ArrayList<>();
		if (translations.size() != 0) {
			values.add(this);
		}

		if (phrase.size() != 0) {
			for (Entry phrase : phrase.values()) {
				for (Entry phraseValue : phrase.getAllPhrases()) {
					values.add(new Entry(word + " " + phraseValue.getWord(),
							phraseValue.getTranslationsWithExplanations()));
				}
			}
		}

		return values;
	}

	/**
	 * Removes a translation and it's explanation (if present) from the entry.
	 * 
	 * @param translation translation to remove
	 */
	public void removeTranslation(String translation) {
		this.translations.remove(translation);
		this.explanations.remove(translation);
	}

	/**
	 * Remove an explanation associated with translation
	 * 
	 * @param translation translation to remove explanation for
	 */
	public void removeExplanation(String translation) {
		this.explanations.remove(translation);
	}

	/**
	 * Set or replace explanation associated with translation
	 * 
	 * @param translation translation to set explanation for
	 * @param explanation explanation to set
	 */
	public void setExplanation(String translation, String explanation) {
		this.explanations.put(translation, explanation);
	}

	@Override
	public String toString() {
		return "Entry{\n\tword=" + word + ",\n\ttranslations=" + translations + ",\n\texplanations=" + explanations
				+ ",\n\tphrase=" + phrase + "\n}";
	}
}

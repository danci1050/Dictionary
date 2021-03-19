package sample;

import javafx.util.Pair;

import java.io.File;
import java.util.*;

public class Translator {
    private HashMap<String, Dictionary> dictionaries;

    public Translator() {
        dictionaries = new HashMap<>();
        String[] files = new File("src/sample/dictionaries").list();
        if (files != null) {
        for (String dict : files) {
            // TODO create dictionaries for each file in folder
            dictionaries.put(dict.replace(".ser",""), new Dictionary());
            }
        }
    }

    public void addEntry(String fromLanguage, String toLanguage, String word, String translation) {
        Entry entry = findEntry(fromLanguage, toLanguage, word);
        if (entry == null) {
            // TODO instantiate Entry and add it to proper dictionary
        } else {
            entry.getTranslation().add(translation);
        }
    }

    public void removeEntry(String fromLanguage, String toLanguage, String word, String translation) throws NoSuchElementException {
        Entry entry = findEntry(fromLanguage, toLanguage, word);
        if (entry == null) {
            throw new NoSuchElementException("The word does not exist in the dictionary");
        }
        if (!entry.getTranslation().contains(translation)) {
            throw new NoSuchElementException("The meaning for this word is not in the dictionary");
        }
        else {
            entry.getTranslation().remove(translation);
        }
    }


    //TODO search for entry(word/phrase) in a correct dictionary
    public Entry findEntry(String fromLanguage, String toLanguage, String word) {
        return new Entry(word, "baguette");
    }

    private Pair<String, Set<String>> processPhrase(String[] inputTextArray, int i, Entry wordEntry) {
        Pair<String, Set<String>> result = null;

        Entry nextEntry = wordEntry.getPhrase() == null ? null : wordEntry.getPhrase().get(inputTextArray[i+1]);
        if (nextEntry != null) {
            result = processPhrase(inputTextArray, i + 1, nextEntry);
        }

        if (result != null) {
            return new Pair<>(wordEntry.getWord() + " " + result.getKey(), result.getValue());
        } else {
            return new Pair<>(wordEntry.getWord(), wordEntry.getTranslation());
        }
    }

    public List<Pair<String, Set<String>>> translate(String fromLanguage, String toLanguage, String inputText) {
        // Regex pattern matches any Unicode punctuation or symbol
        String punctuation = "([\\p{P}\\p{S}])";

        inputText = inputText.replaceAll(punctuation, " $1 ");
        // Regex pattern matches any whitespace character(s)
        String[] inputTextArray = inputText.split("[\\s]+");

        List<Pair<String, Set<String>>> translation = new LinkedList<>();
        for (int i = 0; i < inputTextArray.length; i++) {
            String word = inputTextArray[i];

            if (!word.matches(punctuation)) {
                System.out.println(word);
                Entry wordEntry = findEntry(fromLanguage, toLanguage, word);
                if (wordEntry != null) {
                    translation.add(processPhrase(inputTextArray, i, wordEntry));
                } else {
                    translation.add(new Pair<>(word, null));
                }
            } else {
                translation.add(new Pair<>(word, new HashSet<>(Collections.singletonList(word))));
            }
        }

        return translation;
    }
}

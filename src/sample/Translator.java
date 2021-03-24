package sample;

import javafx.util.Pair;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Translator {
    private String dictionariesFolder;
    private HashMap<String, Dictionary> dictionaries;

    public Translator() {
        this("dictionaries");
    }

    public Translator(String folder) {
        dictionaries = new HashMap<>();
        dictionariesFolder = folder;
        String[] files = null;
        if (folder != null) {
            files = new File(folder).list();
        }
        if (files != null && files.length > 0) {
            for (String filename : files) {
                Dictionary dict_object = new Dictionary();
                try {
                    dict_object.loadDict(folder + "/" + filename);
                    dictionaries.put(filename.replace(".ser",""), dict_object);
                } catch (ClassNotFoundException | IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void addEntry(String fromLanguage, String toLanguage, String wordPhrase, String[] translations) {
        dictionaries.get(fromLanguage+toLanguage).add(wordPhrase, translations);
    }

    public void removeEntry(String fromLanguage, String toLanguage, String wordPhrase, String[] translations) throws NoTranslationException {
        dictionaries.get(fromLanguage+toLanguage).remove(wordPhrase, translations);
    }

    public Entry searchAWord(String fromLanguage, String toLanguage, String word) throws NoTranslationException {
        return dictionaries.get(fromLanguage+toLanguage).searchAWord(word);
    }

    public void addDictionary(String fromLanguage, String toLanguage, Dictionary dict) {
        dictionaries.put(fromLanguage+toLanguage, dict);
    }

    public void saveDictionaries() {
        for (Dictionary dictionary : dictionaries.values()) {
            dictionary.writeDictionary(dictionariesFolder);
        }
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
        for (int i = 0; i < inputTextArray.length;) {
            String word = inputTextArray[i];

            if (!word.matches(punctuation)) {
                try {
                    Entry wordEntry = searchAWord(fromLanguage, toLanguage, word);
                    translation.add(processPhrase(inputTextArray, i, wordEntry));
                } catch (NoTranslationException e) {
                    translation.add(new Pair<>(word, null));
                }
            } else {
                translation.add(new Pair<>(word, new HashSet<>(Collections.singletonList(word))));
            }

            // Move by the number of processed words
            i += translation.get(translation.size() - 1).getKey().split(" ").length;
        }

        return translation;
    }
}

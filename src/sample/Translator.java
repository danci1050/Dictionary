package sample;

import javafx.util.Pair;

import java.io.File;
import java.util.*;

public class Translator {
    private HashMap<String, Dictionary> dictionaries;
    // Regex pattern matches any Unicode punctuation or symbol
    private final String punctuation = "([\\p{P}\\p{S}])";

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

    private Pair<String, Set<String>> processPhrase(String[] inputTextArray, int i, Entry wordEntry) {
        //TODO temporary faking entries
        wordEntry = new Entry();
        Pair<String, Set<String>> result = null;

        Entry nextEntry = wordEntry.getPhrase().get(inputTextArray[i+1]);
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

        inputText = inputText.replaceAll(punctuation, " $1 ");
        // Regex pattern matches any whitespace character(s)
        String[] inputTextArray = inputText.split("[\\s]+");

        List<Pair<String, Set<String>>> translation = new LinkedList<>();
        for (int i = 0; i < inputTextArray.length; i++) {
            String word = inputTextArray[i];

            if (!word.matches(punctuation)) {
                System.out.println(word);
                //TODO search for entry in a correct dictionary
                Entry wordEntry = null;
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

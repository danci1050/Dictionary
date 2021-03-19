package sample;

import java.util.HashMap;
import java.util.Set;

public class Entry {


    private String word;
    private Set<String> translation;
    private HashMap<String, Entry> phrase;

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public Set<String> getTranslation() {
        return translation;
    }

    public void setTranslation(Set<String> translation) {
        this.translation = translation;
    }

    public HashMap<String, Entry> getPhrase() {
        return phrase;
    }

    public void setPhrase(HashMap<String, Entry> phrase) {
        this.phrase = phrase;
    }
}

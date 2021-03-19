package sample;

import javafx.util.Pair;

import java.util.List;
import java.util.Set;

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
        testTranslator();
    }

    private void testTranslator() {
        Translator translator = new Translator();
        List<Pair<String, Set<String>>> translation = translator.translate("english", "dutch", "The aim of this project is to create a program that functions as a translator from English to Dutch and Dutch to English. This program will translate words by taking an input (a line of text in English or Dutch) and output the translation for it.  It will also be possible to search for a word by outputting its translation, remove a word if it already exists in the dictionary and add it if it does not.  The user will also be able to process phrases that include more than one word and to upload a file containing a sizeable text and receive the output, a valid translation, as efficiently as possible. The program will have a Menu for the user the select relevant options they want to use.");
        System.out.println(translation);
    }
}

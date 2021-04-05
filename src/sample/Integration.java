package sample;

import javafx.event.ActionEvent;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.util.Pair;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


// TODO: temporary class needs to be merged with controller - necessary to avoid deserialization of dictionaries at
// each translation

public class Integration{

    private static WebView webviewtest;

    public static WebView getWebviewtest() {
        return webviewtest;
    }

    public static void setWebviewtest(WebView webviewtest) {
        Integration.webviewtest = webviewtest;
    }

    private Translator t;

    private Controller controller;

    public Integration(){
        t=new Translator();
        controller=new Controller();
    }
    public void translate(String fromLanguage, String toLanguage, String inputText){
        List<Pair<String, List<Pair<String, String>>>> translation = t.translate(fromLanguage,toLanguage,inputText);
        processTranslation(translation);

    }
    private void processTranslation(List<Pair<String, List<Pair<String, String>>>> translation){

        WebEngine webEngine = webviewtest.getEngine();
        webEngine.executeScript("clearTranslation()");
        System.err.println(Arrays.deepToString(translation.toArray()));
        for(int i=0; i<translation.size();i++) {
            String translatedWord;
            String[] otherTranslations;
            if (translation.get(i).getValue() != null) {
                if (translation.get(i).getValue().size() == 0) {
                    // This is a case where no translation was found
                    //TODO Open the add translation dialog
                    translatedWord = translation.get(i).getKey();
                    otherTranslations = new String[] {"\"" + translation.get(i).getKey() + "\""};
                } else {
                    // This is a case with at least one translation
                    translatedWord = translation.get(i).getValue().get(0).getKey();
                    otherTranslations = new String[translation.get(i).getValue().size()];
                    for (int j = 0; j < otherTranslations.length; j++) {
                        otherTranslations[j] = "\"" + translation.get(i).getValue().get(j).getKey().replaceAll("\"", "") + " \"";
                    }
                }
            } else {
                // This is a case where the key is a punctuation
                translatedWord = translation.get(i).getKey();
                otherTranslations = new String[] {"\"" + translation.get(i).getKey() + "\""};
            }

            System.err.println("addTranslation("+i+",\""+translatedWord+"\","+Arrays.deepToString(otherTranslations)+")");
            webEngine.executeScript("addTranslation("+i+",\""+translatedWord+"\","+Arrays.deepToString(otherTranslations)+")");
        }
    }
    // not an ideal solution but I cant get the clipboard API to work on javafx webview
    // does not work on linux
    public void copy(String text){
        text=text.replaceAll("&nbsp;"," ");
        System.out.println("copy");
        StringSelection selection = new StringSelection(text);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);

    }

    public void download(String text) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);
        File selectedFile = fileChooser.showSaveDialog(Main.getpStage());
        if (selectedFile != null) {
            Files.writeString(Path.of(selectedFile.getPath()), text);
        }
    }

    public void toggleAddNewWord(boolean ToggleValue){
        System.out.println(ToggleValue);
        Controller.setAddNewWord(ToggleValue);
    }
    public boolean getAddNewWord(){
        return Controller.isAddNewWord();
    }

    public void readFile() throws IOException {
        WebEngine webEngine = webviewtest.getEngine();
        String[] r  = new String[3];
        DecimalFormat df = new DecimalFormat( "#.##" );
        List<String> text = new ArrayList<>();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);
        File selectedFile = fileChooser.showOpenDialog(Main.getpStage());
        if (selectedFile != null) {
            Files.lines(Path.of(selectedFile.getPath()), StandardCharsets.ISO_8859_1).forEachOrdered(text::add);
            r[0]=text.stream().collect(Collectors.joining(", "));
            r[2]=selectedFile.getName();
            String[] aMultiples = {"KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB"};
            double nBytes  = Double.valueOf(selectedFile.length());
            r[1]=String.valueOf(nBytes+" bytes");
            for (int i = 0; nBytes > 1000; i++) {
                nBytes /= Double.valueOf(1000);
                r[1] = String.valueOf(df.format(nBytes)+ " " + aMultiples[i]);
            }
            System.out.println(r[1]);
            webEngine.executeScript("fileupload(\""+r[0]+"\",\""+r[1]+"\",\""+r[2]+"\")");
        }
    }
    public void addWord(String from, String to,String word){
        System.out.println(word);
        controller.addTranslationDialog(new ActionEvent(),t.getDictionaries().get(from+to),word);
    }
    public boolean getAddAWord(){
        return Controller.isAddNewWord();
    }
}
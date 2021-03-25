package sample;

import javafx.fxml.FXML;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.util.Pair;

import java.util.Arrays;
import java.util.List;
import java.util.Set;


// temporary class needs to be merged with controller
public class Integration{
    @FXML
    private WebView webviewtest;
    public Integration(){

    }
    public void translate(String fromLanguage, String toLanguage, String inputText){

        Translator t = new Translator();
        System.out.println(inputText);
        List<Pair<String, Set<String>>> translation =t.translate(fromLanguage,toLanguage,inputText);
        processTranslation(translation);

    }
    private void processTranslation(List<Pair<String, Set<String>>> translation){
        for(int i=0; i<translation.size();i++) {
            WebEngine webEngine = webviewtest.getEngine();
            webEngine.executeScript("addTranslation("+i+","+translation.get(i).getKey()+","+ Arrays.deepToString(translation.get(i).getValue().toArray()));
        }
    }
}
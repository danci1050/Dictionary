package sample;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;

public class Dictionary {
    private static  HashMap<String, String> dutchEnglish;
    private  static HashMap<String, String> englishDutch;

    public Dictionary(){
        dutchEnglish=new HashMap<String, String>();
        englishDutch=new HashMap<String, String>();
    }
    /*
    Some of these functions are maybe redundant
     */

    public static HashMap<String, String> getDutchEnglish() {
        return dutchEnglish;
    }

    public static void setDutchEnglish(HashMap<String, String> dutchEnglish) {
        Dictionary.dutchEnglish = dutchEnglish;
    }

    public static HashMap<String, String> getEnglishDutch() {
        return englishDutch;
    }

    public static void setEnglishDutch(HashMap<String, String> englishDutch) {
        Dictionary.englishDutch = englishDutch;
    }

    // TODO: 11/03/2021 Solve relative pathing
    public static void loadDutchEnglish() throws IOException, ClassNotFoundException {

        FileInputStream fis = new FileInputStream("C:\\Users\\danci\\IdeaProjects\\Dictionary\\src\\sample\\dutchEnglish.ser");
        ObjectInputStream ois = new ObjectInputStream(fis);
        dutchEnglish= (HashMap)ois.readObject();
    }

    public static void loadEnglishDutch() throws IOException, ClassNotFoundException {

        FileInputStream fis = new FileInputStream("C:\\Users\\danci\\IdeaProjects\\Dictionary\\src\\sample\\englishDutch.ser");
        ObjectInputStream ois = new ObjectInputStream(fis);
        englishDutch= (HashMap)ois.readObject();
    }

    public static String search(HashMap<String, String> temp, String searchWord){
        return (String) temp.get(searchWord);
    }
    public static void add(String english, String dutch){
        dutchEnglish.put(dutch,english);
        englishDutch.put(english,dutch);
    }
    public static  void remove(HashMap<String, String> temp, String word){
        temp.remove(word);
    }
}

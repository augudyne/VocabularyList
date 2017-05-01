package vocabularylist.projects.austin.vocabularylist.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Austin on 2016-12-20.
 */
public class Word implements Iterable<WordVariant>, Comparable<Word>{
    private String topLevelName;
    private List<WordVariant> wordVariants;

    public void setWordVariants(List<WordVariant> wordVariants) {
        this.wordVariants = wordVariants;
    }

    public Word(String topLevelName) {
        this.topLevelName = topLevelName;
        wordVariants = new ArrayList<>();
    }

    public String getTopLevelName() {
        return topLevelName;
    }

    @Override
    public int hashCode(){
        return topLevelName.hashCode();
    }

    @Override
    public boolean equals(Object o){
        if (o == null) return false;
        else if (o instanceof Word){
            Word w = (Word) o;
            return w.getTopLevelName().equals(topLevelName);
        }
        return false;
    }
    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(topLevelName);
        sb.append('\n');
        for(WordVariant w: wordVariants){
            sb.append(w.getValue());
            sb.append("  :  ");
        }
        sb.delete(sb.length() - 5, sb.length());
        sb.append("\n");
        for(WordVariant w: wordVariants){
            sb.append(w.toString());
            sb.append("\n");
        }
        return sb.toString();
    }

    public List<WordVariant> getWordVariants() {
        return wordVariants;
    }

    /**
     * String wordString = o.getString("topLevelName");
     String wordClass = o.getString("class");

     JSONArray defs = o.getJSONArray("definitions");
     * @return
     */
    public JSONObject toJSON(){

        JSONObject thisObjectAsJSON = new JSONObject();
        JSONArray wordVariantsAsArray = new JSONArray();

        for(WordVariant wv : wordVariants){
            wordVariantsAsArray.put(wv.toJSONObject());
        }
        try {
            thisObjectAsJSON.put("word", topLevelName);
            thisObjectAsJSON.put("variations", wordVariantsAsArray);
        } catch (JSONException e) {
            System.out.println("Error converting Word to JSONObject");
        }
        return thisObjectAsJSON;
    }

    @Override
    public Iterator<WordVariant> iterator() {
        return wordVariants.iterator();
    }

    @Override
    public int compareTo(Word another) {
        return topLevelName.compareTo(another.getTopLevelName());
    }
}

package vocabularylist.projects.austin.vocabularylist.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Austin on 2016-12-23.
 */
public class WordVariant implements Cloneable, Serializable{
    private String value;
    private String partOfSpeech;
    private List<String> definitions;

    public void setPartOfSpeech(String partOfSpeech) {
        this.partOfSpeech = partOfSpeech;
    }

    public WordVariant(WordVariant wv) {
        this.value = wv.getValue();
        this.partOfSpeech = wv.getPartOfSpeech();
        this.definitions = wv.getDefinitions();

    }

    public void setDefinitions(List<String> definitions) {
        this.definitions = definitions;
    }

    public WordVariant(String value) {
        this.value = value;
    }

    public WordVariant(String value, String partOfSpeech, List<String> definitions) {
        this.value = value;
        this.partOfSpeech = partOfSpeech;
        this.definitions = definitions;
    }

    public String getValue() {
        return value.replaceAll("[\\d\\[\\]]", "");
    }

    public String getPartOfSpeech() {
        return partOfSpeech;
    }

    public List<String> getDefinitions() {
        return definitions;
    }

    public JSONObject toJSONObject() {
        JSONObject thisAsJSONObject = new JSONObject();
        JSONArray definitionsAsJSONArray = new JSONArray();

        for(String s: definitions) {
            definitionsAsJSONArray.put(s);
        }
        try{
            thisAsJSONObject.put("value", value);
            thisAsJSONObject.put("part", partOfSpeech);
            thisAsJSONObject.put("definitions", definitionsAsJSONArray);
        } catch (JSONException e) {
            System.out.println("Error converting WordVariant to JSONObject");
        }

        return thisAsJSONObject;
    }

    @Override
    public Object clone() throws CloneNotSupportedException{
        return super.clone();
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder(value.replaceAll("[\\[\\]\\d]", ""));
        sb.append(" : ");
        sb.append(partOfSpeech);
        sb.append("\n\n");
        int x = 1;
        for(String s: definitions){
            sb.append(x);
            sb.append(". ");
            sb.append(s);
            sb.append('\n');
            x++;
        }

        return sb.toString();
    }

    public String definitionsToString(){
        StringBuilder sb = new StringBuilder();
        int x = 1;
        for(String s: definitions){
            sb.append("[" + x + "]. " + s + "\n\n");
            x++;
        }
        return sb.toString();
    }
}


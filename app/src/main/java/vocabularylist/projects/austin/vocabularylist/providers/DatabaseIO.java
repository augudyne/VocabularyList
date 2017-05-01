package vocabularylist.projects.austin.vocabularylist.providers;

import android.app.Activity;
import android.content.Context;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import java.util.ArrayList;
import java.util.List;


import vocabularylist.projects.austin.vocabularylist.model.Word;
import vocabularylist.projects.austin.vocabularylist.model.WordManager;
import vocabularylist.projects.austin.vocabularylist.model.WordVariant;

/**
 * Created by Austin on 2016-12-23.
 * Does input and output from the JSONDatabase file of words
 * - singleton design pattern
 */
public class DatabaseIO {
    private static DatabaseIO instance;
    private static final String FILE_NAME ="database.json";
    private static Context activity;


    public DatabaseIO(Context activity) {
        this.activity = activity;
    }

    public static DatabaseIO getInstance(Context activity) {
        if (instance == null) {
            instance = new DatabaseIO(activity);
        }
        return instance;
    }

    public JSONObject getDatabaseFromFile() {
        JSONObject wordDatabase = null;
        StringBuilder respStringBuilder = new StringBuilder();
        try{
            InputStream is = activity.openFileInput(FILE_NAME);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line = "";
            while((line = br.readLine()) != null) {
                respStringBuilder.append(line);
            }
            System.out.println(respStringBuilder.toString());
            is.close();
            wordDatabase = new JSONObject(respStringBuilder.toString());

        }catch (FileNotFoundException e1) {
            System.out.println("File Not Found");
        } catch (IOException e2) {
            System.out.println("IO Exception");
        } catch (JSONException e3) {
            System.out.println(respStringBuilder.toString());
            System.out.println("Malformed JSON Array");
        }
        return wordDatabase;
    }

    public void loadDatabase(){
        WordManager wm = WordManager.getInstance();
        JSONObject stateObject = getDatabaseFromFile();
        if(stateObject == null) return;
        try{
            JSONArray offlineWords = stateObject.getJSONArray("offlineWords");
            if(offlineWords != null){
                for (int i = 0; i < offlineWords.length(); i++){
                    String s = offlineWords.getString(i);
                    wm.addToOfflineWords(s);
                }
            }
        } catch (JSONException e){
            System.out.println("No Offline Words Found");
        }

        try {
            JSONArray wordDatabase = stateObject.getJSONArray("vocabListWords");
            //populate vocabList in wordManager
            if(wordDatabase != null){
                for(int x = 0; x < wordDatabase.length(); x++) {
                    JSONObject o = wordDatabase.getJSONObject(x);
                    String wordString = o.getString("word");
                    JSONArray variations = o.getJSONArray("variations");
                    List<WordVariant> variants = new ArrayList<>();
                    for(int y = 0; y < variations.length(); y++){
                        JSONObject var = variations.getJSONObject(y);
                        String value = var.getString("value");
                        String part = var.getString("part");
                        List<String> defs = new ArrayList<>();
                        JSONArray rawDefs = var.getJSONArray("definitions");
                        for(int z = 0; z < rawDefs.length(); z++) {
                            String def = rawDefs.getString(z);
                            defs.add(def);
                        }
                        WordVariant wv = new WordVariant(value, part, defs);
                        variants.add(wv);
                    }
                    Word w = new Word(wordString);
                    w.setWordVariants(variants);
                    wm.addWord(w);
                }
            }
        } catch (JSONException e) {
            System.out.println("Cannot obtain JSONObject");
        }
    }

    /**
     * TODO: Should check the differences and only add new
     * @return
     */
    public JSONObject stateToJSONObject(){
        WordManager wm = WordManager.getInstance();
        JSONObject currentDatabase = new JSONObject();

        JSONArray offlineWords = new JSONArray();
        for(String s: wm.getOfflineWords()){
            offlineWords.put(s);
        }

        JSONArray vocabListWords = new JSONArray();
        for(Word w: wm) {
            vocabListWords.put(w.toJSON());
        }
        try {
            currentDatabase.put("offlineWords", offlineWords);
            currentDatabase.put("vocabListWords", vocabListWords);
        } catch (JSONException e){
            System.out.println("Error trying to create JSONObject in DatabaseIO");
        }
        return currentDatabase;
    }

    public void writeDatabaseToFile() {
        JSONObject stateToWrite = stateToJSONObject();
        System.out.println("Writing to file: " + stateToWrite.toString());
        try {
            OutputStream os = activity.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
            OutputStreamWriter osw = new OutputStreamWriter(os);
            osw.write(stateToWrite.toString());
            osw.close();
        } catch (FileNotFoundException e) {
            System.out.println("File Not Found for Output");
        } catch (IOException e1) {
            System.out.println("IO Exception in writing database to file");
        }

    }
}

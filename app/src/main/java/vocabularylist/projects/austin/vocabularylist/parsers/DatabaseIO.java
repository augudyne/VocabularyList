package vocabularylist.projects.austin.vocabularylist.parsers;

import android.app.Activity;
import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import vocabularylist.projects.austin.vocabularylist.R;
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
    private static Activity activity;


    public DatabaseIO(Activity activity) {
        this.activity = activity;
    }

    public static DatabaseIO getInstance(Activity activity) {
        if (instance == null) {
            instance = new DatabaseIO(activity);
        }
        return instance;
    }

    public JSONArray getDatabaseFromFile() {
        JSONArray wordDatabase = null;
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
            wordDatabase = new JSONArray(respStringBuilder.toString());

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
        JSONArray wordDatabase = getDatabaseFromFile();
        if(wordDatabase != null){
        for(int x = 0; x < wordDatabase.length(); x++) {
            try {
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
                WordManager.getInstance().addWord(w);

            } catch (JSONException e) {
                System.out.println("Cannot obtain JSONObject");
            }
        }}
    }

    /**
     * TODO: Should check the differences and only add new
     * @return
     */
    public JSONArray databaseToJSONArray(){
        WordManager wm = WordManager.getInstance();
        JSONArray currentDatabase = new JSONArray();

        for(Word w: wm) {
            currentDatabase.put(w.toJSON());
        }
        return currentDatabase;
    }

    public void writeDatabaseToFile() {

        JSONArray arrayToWrite = databaseToJSONArray();
        System.out.println("Writing to file: " + arrayToWrite.toString());
        System.out.println(arrayToWrite.toString());
        try {
            OutputStream os = activity.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
            OutputStreamWriter osw = new OutputStreamWriter(os);
            osw.write(arrayToWrite.toString());
            osw.close();
        } catch (FileNotFoundException e) {
            System.out.println("File Not Found for Output");
        } catch (IOException e1) {
            System.out.println("IO Exception in writing database to file");
        }

    }
}

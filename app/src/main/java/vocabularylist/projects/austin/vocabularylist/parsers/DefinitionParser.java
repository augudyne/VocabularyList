package vocabularylist.projects.austin.vocabularylist.parsers;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import android.util.Log;
import android.widget.Toast;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import vocabularylist.projects.austin.vocabularylist.R;
import vocabularylist.projects.austin.vocabularylist.WordSuggestionsFragment;
import vocabularylist.projects.austin.vocabularylist.exceptions.SAXTerminationException;
import vocabularylist.projects.austin.vocabularylist.exceptions.URLConnectionException;
import vocabularylist.projects.austin.vocabularylist.exceptions.WordNotFoundException;
import vocabularylist.projects.austin.vocabularylist.model.Word;
import vocabularylist.projects.austin.vocabularylist.model.WordManager;

/**
 * Created by Austin on 2016-12-20.
 * // the SAX way:
 XMLReader myReader = XMLReaderFactory.createXMLReader();
 myReader.setContentHandler(handler);
 myReader.parse(new InputSource(new URL(url).openStream()));

 // or if you prefer DOM:
 DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
 DocumentBuilder db = dbf.newDocumentBuilder();
 Document doc = db.parse(new URL(url).openStream());
 */
public class DefinitionParser extends AsyncTask<ArrayAdapter, Void, Void> {
    private static final String BASE_PATH = "http://www.dictionaryapi.com/api/v1/references/collegiate/xml/";
    private static final String BASE_POST = "?key=9915092f-5fee-4eba-ae03-2075c417c624";
    private static final String TAG = "DefinitionParser";
    private String word;
    private FragmentActivity activity;
    private ArrayAdapter<Word> arrayAdapter;
    private boolean wordFound = true;
    private boolean isNonConnect = false;
    private ArrayList<String> suggestions;

    public DefinitionParser(FragmentActivity cxt, String word) {
        this.word = word;
        this.activity = cxt;
    }

    private URL wordToURL(String word) throws MalformedURLException {
        Log.i(TAG, BASE_PATH + word + BASE_POST);
        return new URL(BASE_PATH + word + BASE_POST);
    }

    @Override
    protected Void doInBackground(ArrayAdapter... params) {
        try {
            arrayAdapter = params[0];
            SAXParserFactory factory = SAXParserFactory.newInstance();
            URLConnection conn = wordToURL(word).openConnection();
            conn.setConnectTimeout(10000);
            factory.setNamespaceAware(true);
            XMLReader xmlReader = saxParser.getXMLReader();
            xmlReader.setContentHandler(new WebsterHandler(word));
            if (conn.getContent() == null) {
                throw new URLConnectionException();
            }
            xmlReader.parse(new InputSource(conn.getInputStream()));
        }catch (URLConnectionException e0){
            Toast.makeText(activity, "Unable to connect to Webster API", Toast.LENGTH_SHORT).show();

            //update the offlineWordsSet if offline and is new word
            if(!WordManager.getInstance().hasWord(word)) {
                Set<String> offlineWordsSet = new HashSet<>();
                SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                sharedPref.getStringSet("offlineWords", offlineWordsSet);
                editor.remove("offlineWords");
                offlineWordsSet.add(word);
                editor.putStringSet("offlineWords", offlineWordsSet);
                isNonConnect = true;
            }
        } catch (WordNotFoundException e) {
            //TODO: Word Not Found functionality
            suggestions = e.getSuggestions();
            wordFound = false;
        }
        catch(ParserConfigurationException e) {
            System.out.println("Unable to properly configure " + e.getMessage());
        } catch (SAXTerminationException ste){
            System.out.print("SAX Terminated by Custom Exception");
        } catch (SAXException se){
            System.out.println("SAX parsing error " + se.getMessage() );
        } catch(IOException ioException) {
            System.out.println("Java IOException " + ioException.getMessage());
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if(isNonConnect){
            Toast.makeText(activity, "Unable to connect to Webster API", Toast.LENGTH_SHORT).show();
        } else if(wordFound) {
            Toast.makeText(activity, " Definition successfully loaded", Toast.LENGTH_SHORT).show();
            arrayAdapter.clear();
            arrayAdapter.addAll(WordManager.getInstance().getWords());
            arrayAdapter.notifyDataSetChanged();
            FragmentManager fm = activity.getSupportFragmentManager();
            fm.popBackStack();
            //TODO: Uncomment if it doesnt save anymore
            //DatabaseIO.getInstance(activity).writeDatabaseToFile();
        } else {
            FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment, WordSuggestionsFragment.newInstance(suggestions, word));
            ft.addToBackStack(null);
            ft.commit();
        }
    }


    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }
}


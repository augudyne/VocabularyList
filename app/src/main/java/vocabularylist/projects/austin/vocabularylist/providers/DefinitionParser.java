package vocabularylist.projects.austin.vocabularylist.providers;

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

import android.util.Log;
import android.widget.Toast;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import vocabularylist.projects.austin.vocabularylist.R;
import vocabularylist.projects.austin.vocabularylist.WordInfoFragment;
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
    private ArrayList<String> suggestions;
    private ArrayAdapter arrayAdapter;
    private TaskState currentState = TaskState.EXECUTING;


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
            if(params[0] != null) {
                arrayAdapter = params[0];
            } else {
                System.out.println("No array adapter given...terminating without trying to get word");
                return null;
            }
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            URLConnection conn = wordToURL(word).openConnection();
            conn.setConnectTimeout(10000);
            factory.setNamespaceAware(true);
            XMLReader xmlReader = saxParser.getXMLReader();
            xmlReader.setContentHandler(new WebsterHandler(word));
            if (conn.getContent() == null) {
                throw new URLConnectionException();
            }
            xmlReader.parse(new InputSource(conn.getInputStream()));
            currentState = TaskState.WORD_FOUND;
        }catch (IOException e0){
            System.out.println("IOException: " + e0.toString());
            currentState = TaskState.CONNECTION_FAILED;
        } catch (WordNotFoundException e){
            System.out.println("Word Not Found, setting suggestions for new fragment");
            suggestions = e.getSuggestions();
            currentState = TaskState.WORD_NOT_FOUND;
        }
        catch(ParserConfigurationException e) {
            System.out.println("Unable to properly configure " + e.getMessage());
        } catch (SAXTerminationException ste){
            System.out.print("SAX Terminated by Custom Exception");
        } catch (SAXException se){
            System.out.println("SAX parsing error " + se.getMessage() );
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        System.out.println("In: onPostExecute");
        switch (currentState) {
            case WORD_FOUND:
                whenWordFound();
                break;
            case CONNECTION_FAILED:
                connectionFailed();
                break;
            case WORD_NOT_FOUND:
                offerSuggestions();
                break;
            default:
                System.out.println("Current State: " + currentState.toString());
        }
    }


    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    private enum TaskState{
        CONNECTION_FAILED("Connection Failed"), WORD_FOUND("Word Found"), WORD_NOT_FOUND("Word Not Found"), EXECUTING("Still Executing...");
        String stateAsString;
        TaskState(String string){
            stateAsString = string;
        }

        public String toString(){
            return stateAsString;
        }
    };

    private void whenWordFound() {
        Toast.makeText(activity, " Definition successfully loaded", Toast.LENGTH_SHORT).show();
        DatabaseIO.getInstance(activity).writeDatabaseToFile();
        arrayAdapter.notifyDataSetChanged();
        if(WordManager.getInstance().getOfflineWords().size() == 0) {
            //should return to main screen
            System.out.println("No more offline words, going to main screen");
            activity.getSupportFragmentManager().popBackStack();
        }
        FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
        ft.addToBackStack(null);
        ft.replace(R.id.fragment_container, WordInfoFragment.newInstance(word));
        ft.commit();

        //fm.popBackStack();
    }

    private void connectionFailed() {
        Toast.makeText(activity, "Adding " + word + " to offline list", Toast.LENGTH_SHORT).show();
        //update the offlineWordsSet if offline and is new word
        if(!WordManager.getInstance().hasWord(word)) {
            System.out.println("Adding " + word + " to offline list");
            WordManager wm = WordManager.getInstance();
            wm.addToOfflineWords(word);
            DatabaseIO.getInstance(activity).writeDatabaseToFile();
        }

        arrayAdapter.notifyDataSetChanged();
    }

    private void offerSuggestions() {

        WordManager.getInstance().removeOfflineWord(word);
        DatabaseIO.getInstance(activity).writeDatabaseToFile();
        System.out.println("Removing: " + word + " from manager, Result: " + WordManager.getInstance().getOfflineWords().toString());
        FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
        if(WordManager.getInstance().getOfflineWords().size() == 0) {
            //should return to main screen
            System.out.println("No more offline words, going to main screen");
            activity.getSupportFragmentManager().popBackStack();
        }
        ft.replace(R.id.fragment_container, WordSuggestionsFragment.newInstance(suggestions, word));
        ft.addToBackStack(null);
        ft.commit();
    }



}


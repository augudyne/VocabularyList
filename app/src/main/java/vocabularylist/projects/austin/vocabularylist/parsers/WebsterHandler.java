package vocabularylist.projects.austin.vocabularylist.parsers;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import vocabularylist.projects.austin.vocabularylist.exceptions.SAXTerminationException;
import vocabularylist.projects.austin.vocabularylist.exceptions.WordNotFoundException;
import vocabularylist.projects.austin.vocabularylist.model.Word;
import vocabularylist.projects.austin.vocabularylist.model.WordManager;
import vocabularylist.projects.austin.vocabularylist.model.WordVariant;

/**
 * Created by Austin on 2016-12-20.
 */
public class WebsterHandler extends DefaultHandler {
    private static final String TAG = "WebsterHandler";
    private List<WordVariant> wordVariants;
    private String word;
    private String wordClass;
    private List<String> definitions;
    private StringBuffer sb;
    private StringBuilder defsb;
    private WordVariant wv;
    private ArrayList<String> suggestions;

    private boolean isWordClass = false;
    private boolean isDefinition = false;
    private boolean isSuggestion = false;
    private boolean isAWord = false;

    public WebsterHandler(String word) {
        this.word = word;
    }


    @Override
    public void startDocument() throws SAXException {
        definitions = new ArrayList<>();
        wordVariants = new ArrayList<>();
        sb = new StringBuffer();
        defsb = new StringBuilder();
        super.startDocument();
        suggestions = new ArrayList<>();
    }

    @Override
    public void endDocument() throws SAXException{
        super.endDocument();
        if(!isAWord) {
            throw new WordNotFoundException(suggestions);
        }
        WordManager wm = WordManager.getInstance();
        Word w = new Word(word);
        w.setWordVariants(wordVariants);
        wm.addWord(w);

    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        switch(localName){
            case "entry":
                wv = new WordVariant(attributes.getValue("id"));
                isAWord = true;
            case "fl":
                isWordClass = true;
                break;
            case "dt":
                isDefinition = true;
                break;
            case "suggestion":
                isSuggestion = true;
                isAWord = false;
                break;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);
        switch(localName){
            case "entry":
                wv.setPartOfSpeech(wordClass);
                List<String> definitionsBuffer = new ArrayList<>();
                definitionsBuffer.addAll(definitions);
                wv.setDefinitions(definitionsBuffer);
                wordVariants.add(new WordVariant(wv));
                clearBuffers();
                break;
            case "dt":
                isDefinition = false;
                definitions.add(defsb.toString().replace(":", ""));
                defsb.setLength(0);
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        super.characters(ch, start, length);
        sb.append(ch, start, length);
        if(isDefinition) {
            defsb.append(ch, start, length);
        } else if(isWordClass){
            this.wordClass = sb.toString().trim();
            isWordClass = false;
        } else if (isSuggestion) {
            this.suggestions.add(sb.toString());
            isSuggestion = false;
        }
        sb.setLength(0);
    }

    private void clearBuffers() {
        wordClass = "";
        definitions.clear();
    }

}

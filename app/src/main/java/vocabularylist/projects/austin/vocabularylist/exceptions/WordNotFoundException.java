package vocabularylist.projects.austin.vocabularylist.exceptions;

import org.xml.sax.SAXException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Austin on 2016-12-25.
 */
public class WordNotFoundException extends SAXException {
    private ArrayList<String> suggestions;
    public WordNotFoundException() {
    }

    public WordNotFoundException(ArrayList<String> list) {
        this.suggestions = list;
    }

    public ArrayList<String> getSuggestions() {
        return suggestions;
    }
}

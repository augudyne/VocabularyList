package vocabularylist.projects.austin.vocabularylist.exceptions;

import org.xml.sax.SAXException;

/**
 * Created by Austin on 2016-12-23.
 */
public class SAXTerminationException extends SAXException{
    public SAXTerminationException() {
    }

    public SAXTerminationException(String message) {
        super(message);
    }
}

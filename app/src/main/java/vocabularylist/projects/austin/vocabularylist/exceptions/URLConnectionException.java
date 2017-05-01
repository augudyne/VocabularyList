package vocabularylist.projects.austin.vocabularylist.exceptions;

/**
 * Created by Austin on 2017-01-11.
 */
public class URLConnectionException extends RuntimeException {
    public URLConnectionException() {
    }

    public URLConnectionException(String detailMessage) {
        super(detailMessage);
    }
}

package frontend.Error;

import java.util.ArrayList;

public class ParserErrors {
    private static final ParserErrors instance = new ParserErrors();
    private final ArrayList<Error> errors;

    private ParserErrors() {
        errors = new ArrayList<>();
    }

    public static ParserErrors getInstance() {
        return instance;
    }

    public void addError(int lineNum, String message) {
        errors.add(new Error(lineNum, message));
    }

    public ArrayList<Error> getErrors() {
        return errors;
    }
}

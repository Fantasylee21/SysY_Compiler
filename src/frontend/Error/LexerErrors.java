package frontend.Error;

import java.util.ArrayList;

public class LexerErrors {
    private static final LexerErrors instance = new LexerErrors();
    private final ArrayList<Error> errors;

    private LexerErrors() {
        errors = new ArrayList<>();
    }

    public static LexerErrors getInstance() {
        return instance;
    }

    public void addError(int lineNum, String message) {
        errors.add(new Error(lineNum, message));
    }

    public ArrayList<Error> getErrors() {
        return errors;
    }
}

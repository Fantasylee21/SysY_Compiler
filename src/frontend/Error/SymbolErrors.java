package frontend.Error;

import java.util.ArrayList;

public class SymbolErrors {
    private static final SymbolErrors instance = new SymbolErrors();
    ArrayList<Error> errors;

    private SymbolErrors() {
        errors = new ArrayList<>();
    }

    public static SymbolErrors getInstance() {
        return instance;
    }

    public void addError(int lineNum, String message) {
        errors.add(new frontend.Error.Error(lineNum, message));
    }

    public ArrayList<Error> getErrors() {
        return errors;
    }
}

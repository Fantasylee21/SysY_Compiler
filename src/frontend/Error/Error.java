package frontend.Error;

public class Error {
    private final int lineNum;
    private final String message;

    public Error(int lineNum, String message) {
        this.lineNum = lineNum;
        this.message = message;
    }

    public int getLineNum() {
        return lineNum;
    }

    public String getMessage() {
        return message;
    }
}

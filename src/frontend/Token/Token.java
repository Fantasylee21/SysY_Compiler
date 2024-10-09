package frontend.Token;

public class Token {
    private final int lineNum;
    private final TokenType type;
    private final String value;

    public Token(int lineNum, TokenType type, String value) {
        this.lineNum = lineNum;
        this.type = type;
        this.value = value;
    }

    public int getLineNum() {
        return lineNum;
    }

    public TokenType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }
}

package frontend.AST.SyntaxComponent;

import frontend.AST.Node;
import frontend.AST.SyntaxType;
import frontend.Token.Token;

import java.util.ArrayList;

public class TokenNode extends Node {
    private final Token token;

    public TokenNode(int startLine, int endLine, SyntaxType type, ArrayList<Node> children, Token token) {
        super(startLine, endLine, type, children);
        this.token = token;
    }

    public Token getToken() {
        return token;
    }

    @Override
    public void print() {
        System.out.println(token.getType() + " " + token.getValue());
    }

    @Override
    public Integer calcValue() {
        return Integer.parseInt(token.getValue());
    }
}

package frontend.AST.SyntaxComponent;

import frontend.AST.Node;
import frontend.AST.SyntaxType;
import frontend.Token.Token;

import java.util.ArrayList;

public class TokenNode extends Node {
    private Token token;

    public TokenNode(int startLine, int endLine, SyntaxType type, ArrayList<Node> children, Token token) {
        super(startLine, endLine, type, children);
        this.token = token;
    }

    @Override
    public void print() {
        System.out.println(token.getType() + " " + token.getValue());
    }
}

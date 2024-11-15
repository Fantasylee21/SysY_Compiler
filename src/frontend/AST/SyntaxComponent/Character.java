package frontend.AST.SyntaxComponent;

import frontend.AST.ExpValueType;
import frontend.AST.Node;
import frontend.AST.SyntaxType;
import llvm.Constant;
import llvm.Value;
import llvm.type.Int8Type;

import java.util.ArrayList;
// Character ==> CharConst
public class Character extends Node {
    public Character(int startLine, int endLine, SyntaxType type, ArrayList<Node> children) {
        super(startLine, endLine, type, children);
    }

    @Override
    public Value generateIR() {
        TokenNode tokenNode = (TokenNode) children.get(0);
        char c = tokenNode.getToken().getValue().charAt(1);
        if (c == '\\') {
            char c2 = tokenNode.getToken().getValue().charAt(2);
            if (c2 == 'n') {
                c = '\n';
            } else if (c2 == 't') {
                c = '\t';
            } else if (c2 == '0') {
                c = '\0';
            } else if (c2 == '\'') {
                c = '\'';
            } else if (c2 == '\"') {
                c = '\"';
            }
        }
        Constant constant = new Constant(c);
        constant.setType(Int8Type.getInstance());
        return constant;
    }

    @Override
    public Integer calcValue() {
        TokenNode tokenNode = (TokenNode) children.get(0);
        char c = tokenNode.getToken().getValue().charAt(1);
        if (c == '\\') {
            char c2 = tokenNode.getToken().getValue().charAt(2);
            if (c2 == 'n') {
                c = '\n';
            } else if (c2 == 't') {
                c = '\t';
            } else if (c2 == '0') {
                c = '\0';
            } else if (c2 == '\'') {
                c = '\'';
            } else if (c2 == '\"') {
                c = '\"';
            }
        }
        return (int) c;
    }
}

package frontend.AST.SyntaxComponent;

import frontend.AST.Node;
import frontend.AST.SyntaxType;

import java.util.ArrayList;

// LVal ==> Ident ['[' Exp ']']
public class LVal extends Node {
    public LVal(int startLine, int endLine, SyntaxType type, ArrayList<Node> children) {
        super(startLine, endLine, type, children);
    }
}

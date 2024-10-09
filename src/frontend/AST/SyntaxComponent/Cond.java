package frontend.AST.SyntaxComponent;

import frontend.AST.Node;
import frontend.AST.SyntaxType;

import java.util.ArrayList;

// CondExp ==> LorExp
public class Cond extends Node {
    public Cond(int startLine, int endLine, SyntaxType type, ArrayList<Node> children) {
        super(startLine, endLine, type, children);
    }
}

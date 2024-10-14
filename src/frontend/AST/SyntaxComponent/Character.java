package frontend.AST.SyntaxComponent;

import frontend.AST.ExpValueType;
import frontend.AST.Node;
import frontend.AST.SyntaxType;

import java.util.ArrayList;
// Character ==> CharConst
public class Character extends Node {
    public Character(int startLine, int endLine, SyntaxType type, ArrayList<Node> children) {
        super(startLine, endLine, type, children);
    }
}

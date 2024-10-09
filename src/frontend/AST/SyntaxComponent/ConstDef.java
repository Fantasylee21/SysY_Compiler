package frontend.AST.SyntaxComponent;

import frontend.AST.Node;
import frontend.AST.SyntaxType;

import java.util.ArrayList;
// ConstDef ==> Ident [ '[' ConstExp ']' ] '=' ConstInitVal
public class ConstDef extends Node {
    public ConstDef(int startLine, int endLine, SyntaxType type, ArrayList<Node> child) {
        super(startLine, endLine, type, child);
    }
}

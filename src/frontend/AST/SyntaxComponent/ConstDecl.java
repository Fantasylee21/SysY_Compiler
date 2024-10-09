package frontend.AST.SyntaxComponent;

import frontend.AST.Node;
import frontend.AST.SyntaxType;

import java.util.ArrayList;
// ConstDecl ==> 'const' 'int'|'char' ConstDef { ',' ConstDef } ';'
public class ConstDecl extends Node {
    public ConstDecl(int startLine, int endLine, SyntaxType type, ArrayList<Node> child) {
        super(startLine, endLine, type, child);
    }
}

package frontend.AST.SyntaxComponent;

import frontend.AST.Node;
import frontend.AST.SyntaxType;

import java.util.ArrayList;
// VarDecl ==> 'int'|'char' VarDef { ',' VarDef } ';'
public class VarDecl extends Node {
    public VarDecl(int startLine, int endLine, SyntaxType type, ArrayList<Node> child) {
        super(startLine, endLine, type, child);
    }
}

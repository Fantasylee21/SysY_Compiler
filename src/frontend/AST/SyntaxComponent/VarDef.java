package frontend.AST.SyntaxComponent;

import frontend.AST.Node;
import frontend.AST.SyntaxType;

import java.util.ArrayList;
//VarDef  ==> Ident [ '[' ConstExp ']' ] [ '=' InitVal]
public class VarDef extends Node {
    public VarDef(int startLine, int endLine, SyntaxType type, ArrayList<Node> child) {
        super(startLine, endLine, type, child);
    }
}

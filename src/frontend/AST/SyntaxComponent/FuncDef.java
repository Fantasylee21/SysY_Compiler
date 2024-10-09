package frontend.AST.SyntaxComponent;

import frontend.AST.Node;
import frontend.AST.SyntaxType;

import java.util.ArrayList;

//FuncDef ==> FuncType Ident '(' [FuncFParams] ')' Block
public class FuncDef extends Node {
    public FuncDef(int startLine, int endLine, SyntaxType type, ArrayList<Node> children) {
        super(startLine, endLine, type, children);
    }
}

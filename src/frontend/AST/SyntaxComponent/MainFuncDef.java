package frontend.AST.SyntaxComponent;

import frontend.AST.Node;
import frontend.AST.SyntaxType;

import java.util.ArrayList;
// MainFuncDef ==> 'int' 'main' '(' ')' Block
public class MainFuncDef extends Node {
    public MainFuncDef(int startLine, int endLine, SyntaxType type, ArrayList<Node> child) {
        super(startLine, endLine, type, child);
    }
}

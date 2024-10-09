package frontend.AST.SyntaxComponent;

import frontend.AST.Node;
import frontend.AST.SyntaxType;

import java.util.ArrayList;
// FuncFParams ==> FuncFParam { ',' FuncFParam }
public class FuncFParams extends Node {
    public FuncFParams(int startLine, int endLine, SyntaxType type, ArrayList<Node> child) {
        super(startLine, endLine, type, child);
    }
}

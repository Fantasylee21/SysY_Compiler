package frontend.AST.SyntaxComponent;

import frontend.AST.Node;
import frontend.AST.SyntaxType;

import java.util.ArrayList;

// FuncRParams ==> Exp { ',' Exp }
public class FuncRParams extends Node {
    public FuncRParams(int startLine, int endLine, SyntaxType type, ArrayList<Node> child) {
        super(startLine, endLine, type, child);
    }

}
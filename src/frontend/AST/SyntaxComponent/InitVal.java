package frontend.AST.SyntaxComponent;

import frontend.AST.Node;
import frontend.AST.SyntaxType;

import java.util.ArrayList;
// InitVal ==> Exp | '{' [ Exp { ',' Exp } ] '}' | StringConst
public class InitVal extends Node {
    public InitVal(int startLine, int endLine, SyntaxType type, ArrayList<Node> children) {
        super(startLine, endLine, type, children);
    }
}

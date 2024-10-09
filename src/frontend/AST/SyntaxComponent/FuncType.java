package frontend.AST.SyntaxComponent;
import frontend.AST.Node;
import frontend.AST.SyntaxType;

import java.util.ArrayList;

// FuncType ==> 'void' | 'int' | 'char'
public class FuncType extends Node {
    public FuncType(int startLine, int endLine, SyntaxType type, ArrayList<Node> child) {
        super(startLine, endLine, type, child);
    }
}

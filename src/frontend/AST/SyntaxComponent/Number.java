package frontend.AST.SyntaxComponent;

import frontend.AST.ExpValueType;
import frontend.AST.Node;
import frontend.AST.SyntaxType;

import java.util.ArrayList;

// Number ==> IntConst
public class Number extends Node {
    public Number(int startLine, int endLine, SyntaxType type, ArrayList<Node> children) {
        super(startLine, endLine, type, children);
    }

    @Override
    public int calcValue() {
        return children.get(0).calcValue();
    }

}

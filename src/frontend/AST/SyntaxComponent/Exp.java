package frontend.AST.SyntaxComponent;

import frontend.AST.ExpValueType;
import frontend.AST.Node;
import frontend.AST.SyntaxType;
import llvm.Value;

import java.util.ArrayList;
// Exp ==> AddExp
public class Exp extends Node {
    public Exp(int startLine, int endLine, SyntaxType type, ArrayList<Node> child) {
        super(startLine, endLine, type, child);
    }

    @Override
    public Integer calcValue() {
        return children.get(0).calcValue();
    }

    @Override
    public ExpValueType getExpValueType() {
        return children.get(0).getExpValueType();
    }

    @Override
    public Value generateIR() {
        return children.get(0).generateIR();
    }
}

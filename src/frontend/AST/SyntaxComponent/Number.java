package frontend.AST.SyntaxComponent;

import frontend.AST.Node;
import frontend.AST.SyntaxType;
import llvm.Constant;
import llvm.Value;

import java.util.ArrayList;

// Number ==> IntConst
public class Number extends Node {
    public Number(int startLine, int endLine, SyntaxType type, ArrayList<Node> children) {
        super(startLine, endLine, type, children);
    }

    @Override
    public Integer calcValue() {
        return children.get(0).calcValue();
    }

    @Override
    public Value generateIR() {
        TokenNode tokenNode = (TokenNode) children.get(0);
        return new Constant(Integer.parseInt(tokenNode.getToken().getValue()));
    }
}

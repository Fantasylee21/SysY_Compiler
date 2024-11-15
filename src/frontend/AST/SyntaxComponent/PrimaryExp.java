package frontend.AST.SyntaxComponent;

import frontend.AST.ExpValueType;
import frontend.AST.Node;
import frontend.AST.SyntaxType;
import llvm.Value;

import java.util.ArrayList;

// PrimaryExp ==> '(' Exp ')' | LVal | Number | Character
public class PrimaryExp extends Node {
    public PrimaryExp(int startLine, int endLine, SyntaxType type, ArrayList<Node> children) {
        super(startLine, endLine, type, children);
    }

    @Override
    public Integer calcValue() {
        if (children.size() == 1) {
            return children.get(0).calcValue();
        } else {
            return children.get(1).calcValue();
        }
    }

    @Override
    public ExpValueType getExpValueType() {
        if (children.size() == 1) {
            return children.get(0).getExpValueType();
        } else {
            return children.get(1).getExpValueType();
        }
    }

    @Override
    public Value generateIR() {
        if (children.size() > 1) {
            return children.get(1).generateIR();
        } else {
            if (children.get(0) instanceof LVal lVal) {
                return lVal.generateIRForLVal(false);
            }
            return children.get(0).generateIR();
        }
    }
}

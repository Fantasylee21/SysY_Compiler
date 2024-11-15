package frontend.AST.SyntaxComponent;

import frontend.AST.Node;
import frontend.AST.SyntaxType;
import llvm.BasicBlock;

import java.util.ArrayList;

// CondExp ==> LorExp
public class Cond extends Node {
    public Cond(int startLine, int endLine, SyntaxType type, ArrayList<Node> children) {
        super(startLine, endLine, type, children);
    }

    public void generateIRForCond(BasicBlock thenBlock, BasicBlock elseBlock) {
        LOrExp lOrExp = (LOrExp) children.get(0);
        lOrExp.generateIRForLOr(thenBlock, elseBlock);
    }
}

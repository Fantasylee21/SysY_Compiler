package frontend.AST.SyntaxComponent;

import frontend.AST.Node;
import frontend.AST.SyntaxType;
import llvm.LLVMBuilder;
import llvm.BasicBlock;
import llvm.Value;
import llvm.midInstr.BrInstr;

import java.util.ArrayList;

// LAndExp ==> EqExp {'&&' EqExp}
public class LAndExp extends Node {
    public LAndExp(int startLine, int endLine, SyntaxType type, ArrayList<Node> children) {
        super(startLine, endLine, type, children);
    }

    @Override
    public void print() {
        int cnt = children.size();
        for (Node child : children) {
            child.print();
            if (cnt > 2 && child.getType() == SyntaxType.EqExp) {
                System.out.println("<" + type.toString() + ">");
            }
            cnt--;
        }
        System.out.println("<" + type.toString() + ">");
    }

    public void generateIRForAnd(BasicBlock thenBlock, BasicBlock elseBlock) {
        int cnt = children.size();
        for (int i = 0; i < cnt; i += 2) {
            EqExp eqExp = (EqExp) children.get(i);
            if (i + 1 < cnt) {
                BasicBlock nextBlock = new BasicBlock(LLVMBuilder.getLlvmBuilder().getBranchName());
                Value eqValue = eqExp.generateIR();
                new BrInstr(null, eqValue, nextBlock, elseBlock);

                LLVMBuilder.getLlvmBuilder().setCurBlock(nextBlock);
            } else {
                Value eqValue = eqExp.generateIR();
                new BrInstr(null, eqValue, thenBlock, elseBlock);
            }
        }
    }
}

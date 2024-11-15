package frontend.AST.SyntaxComponent;

import frontend.AST.Node;
import frontend.AST.SyntaxType;
import llvm.BasicBlock;
import llvm.LLVMBuilder;

import java.util.ArrayList;
// LOrExp ==>  LAndExp {'||' LAndExp}
public class LOrExp extends Node {
    public LOrExp(int startLine, int endLine, SyntaxType type, ArrayList<Node> children) {
        super(startLine, endLine, type, children);
    }

    @Override
    public void print() {
        int cnt = children.size();
        for (Node child : children) {
            child.print();
            if (cnt > 2 && child.getType() == SyntaxType.LAndExp) {
                System.out.println("<" + type.toString() + ">");
            }
            cnt--;
        }
        System.out.println("<" + type.toString() + ">");
    }

    public void generateIRForLOr(BasicBlock thenBlock, BasicBlock elseBlock) {
        int cnt = children.size();
        for (int i = 0; i < cnt; i += 2) {
            LAndExp lAndExp = (LAndExp) children.get(i);
            if (i + 1 < cnt) {
                BasicBlock nextBlock = new BasicBlock(LLVMBuilder.getLlvmBuilder().getBranchName());
                LLVMBuilder.getLlvmBuilder().removeBasicBlock(nextBlock.getName());
                lAndExp.generateIRForAnd(thenBlock, nextBlock);
                LLVMBuilder.getLlvmBuilder().addBasicBlock(nextBlock);
                LLVMBuilder.getLlvmBuilder().setCurBlock(nextBlock);
            } else {
                lAndExp.generateIRForAnd(thenBlock, elseBlock);
            }
        }
    }
}

package llvm.midOptimize;
import llvm.*;
import llvm.Module;
import llvm.midInstr.*;

import java.util.ArrayList;
import java.util.Iterator;

public class RemovePhi {
    private Module module;

    public RemovePhi(Module module) {
        this.module = module;
    }

    public void run() {
        for (Function function : module.getFunctions()) {
            Phi2PCopy(function);
            RemovePhi2Move(function);
        }
    }

    public void Phi2PCopy(Function function) {
        ArrayList<BasicBlock> basicBlocks = new ArrayList<>(function.getBasicBlocks());
        for (BasicBlock basicBlock : basicBlocks) {
            if (!(basicBlock.getInstructions().get(0) instanceof PhiInstr)) {
                continue;
            }
            ArrayList<BasicBlock> predecessors = basicBlock.getPredecessors();
            ArrayList<PCopyInstr> pCopyInstrs = new ArrayList<>();
            for (int i = 0; i < predecessors.size(); i++) {
                pCopyInstrs.add(new PCopyInstr());
            }
            for (int i = 0; i < predecessors.size(); i++) {
                BasicBlock predecessor = predecessors.get(i);
                if (predecessor.getSuccessors().size() == 1) {
                    predecessor.getInstructions().add(predecessor.getInstructions().size() - 1, pCopyInstrs.get(i));
                    pCopyInstrs.get(i).setParentBasicBlock(predecessor);
                } else {
                    Function parentFunction = predecessor.getParentFunction();
                    BasicBlock newBlock = new BasicBlock( "pCopy" + LLVMBuilder.getLlvmBuilder().getBranchName(), true);
                    newBlock.setParentFunction(parentFunction);
                    function.getBasicBlocks().add(function.getBasicBlocks().indexOf(basicBlock), newBlock);
                    newBlock.getInstructions().add(pCopyInstrs.get(i));
                    pCopyInstrs.get(i).setParentBasicBlock(newBlock);
                    BrInstr brInstr = (BrInstr) predecessor.getInstructions().get(predecessor.getInstructions().size() - 1);
                    if (brInstr.getOperands().get(1).equals(basicBlock)) {
                        brInstr.getOperands().set(1, newBlock);
                    } else {
                        brInstr.getOperands().set(2, newBlock);
                    }
                    JumpInstr jumpInstr = new JumpInstr(null, basicBlock);
                    newBlock.getInstructions().add(jumpInstr);
                    jumpInstr.setParentBasicBlock(newBlock);

                    predecessor.getSuccessors().set(predecessor.getSuccessors().indexOf(basicBlock), newBlock);
                    basicBlock.getPredecessors().set(basicBlock.getPredecessors().indexOf(predecessor), newBlock);
                    newBlock.getSuccessors().add(basicBlock);
                    newBlock.getPredecessors().add(predecessor);
                }
            }

            Iterator<MidInstr> iterator = basicBlock.getInstructions().iterator();
            while (iterator.hasNext()) {
                MidInstr midInstr = iterator.next();
                if (midInstr instanceof PhiInstr phiInstr) {
                    for (int i = 0; i < phiInstr.getOperands().size(); i++) {
                        Value operand = phiInstr.getOperands().get(i);
                        pCopyInstrs.get(i).addCopy(phiInstr, operand);
                    }
                    iterator.remove();
                }
            }
        }
    }

    public void RemovePhi2Move(Function function) {
        for (BasicBlock basicBlock : function.getBasicBlocks()) {
            if (basicBlock.getInstructions().size() <= 1) {
                continue;
            }
            if (basicBlock.getInstructions().get(basicBlock.getInstructions().size() - 2) instanceof PCopyInstr pCopyInstr) {
                ArrayList<Value> dsts = pCopyInstr.getDsts();
                ArrayList<Value> srcs = pCopyInstr.getSrcs();
                ArrayList<MoveInstr> moveInstrs = new ArrayList<>();
                for (int i = 0; i < dsts.size(); i++) {
                    moveInstrs.add(new MoveInstr(dsts.get(i), srcs.get(i)));
                }
                for (MoveInstr moveInstr : moveInstrs) {
                    basicBlock.getInstructions().add(basicBlock.getInstructions().size() - 2, moveInstr);
                    moveInstr.setParentBasicBlock(basicBlock);
                }
                basicBlock.getInstructions().remove(basicBlock.getInstructions().size() - 2);
            }
        }
    }
}

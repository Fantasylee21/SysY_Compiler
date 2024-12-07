package llvm.midOptimize;

import llvm.BasicBlock;
import llvm.Function;
import llvm.Module;
import llvm.midInstr.BrInstr;
import llvm.midInstr.JumpInstr;
import llvm.midInstr.MidInstr;

import java.util.HashSet;
import java.util.Iterator;

public class DelDeadBlock {
    private Module module;

    public DelDeadBlock(Module module) {
        this.module = module;
    }

    public void run() {
        Function Main = module.getFunctions().get(module.getFunctions().size() - 1);
        HashSet<Function> visited2 = new HashSet<>();
        dfsFunction(Main, visited2);
        Iterator<Function> functionIterator = module.getFunctions().iterator();
        while (functionIterator.hasNext()) {
            Function function = functionIterator.next();
            if (!visited2.contains(function)) {
                functionIterator.remove();
            }
        }

        for (Function function : module.getFunctions()) {
            BasicBlock entryBlock = function.getBasicBlocks().get(0);
            HashSet<BasicBlock> visited = new HashSet<>();
            dfsBlock(entryBlock, visited);
            Iterator<BasicBlock> blockIterator = function.getBasicBlocks().iterator();
            while (blockIterator.hasNext()) {
                BasicBlock block = blockIterator.next();
                if (!visited.contains(block)) {
                    blockIterator.remove();
                }
            }
        }
    }

    public void dfsBlock(BasicBlock block, HashSet<BasicBlock> visited) {
        visited.add(block);
        MidInstr lastInstr = block.getInstructions().get(block.getInstructions().size() - 1);
        if (lastInstr instanceof BrInstr) {
            BasicBlock thenBlock = (BasicBlock) ((BrInstr) lastInstr).getOperands().get(1);
            BasicBlock elseBlock = (BasicBlock) ((BrInstr) lastInstr).getOperands().get(2);
            if (!visited.contains(thenBlock)) {
                dfsBlock(thenBlock, visited);
            }
            if (!visited.contains(elseBlock)) {
                dfsBlock(elseBlock, visited);
            }
        } else if (lastInstr instanceof JumpInstr jumpInstr) {
            BasicBlock targetBlock = (BasicBlock) jumpInstr.getOperands().get(0);
            if (!visited.contains(targetBlock)) {
                dfsBlock(targetBlock, visited);
            }
        }
    }

    public void dfsFunction(Function function, HashSet<Function> visited) {
        visited.add(function);
        for (Function callFunc : function.getCallFunc()) {
            if (!visited.contains(callFunc)) {
                dfsFunction(callFunc, visited);
            }
        }
    }
}

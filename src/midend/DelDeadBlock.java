package midend;

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
        for (Function function : module.getFunctions()) {
            BasicBlock entryBlock = function.getBasicBlocks().get(0);
            HashSet<BasicBlock> visited = new HashSet<>();
            dfs(entryBlock, visited);
            Iterator<BasicBlock> blockIterator = function.getBasicBlocks().iterator();
            while (blockIterator.hasNext()) {
                BasicBlock block = blockIterator.next();
                if (!visited.contains(block)) {
                    blockIterator.remove();
                }
            }
        }
    }

    private void dfs(BasicBlock block, HashSet<BasicBlock> visited) {
        visited.add(block);
        MidInstr lastInstr = block.getInstructions().get(block.getInstructions().size() - 1);
        if (lastInstr instanceof BrInstr) {
            BasicBlock thenBlock = (BasicBlock) ((BrInstr) lastInstr).getOperands().get(1);
            BasicBlock elseBlock = (BasicBlock) ((BrInstr) lastInstr).getOperands().get(2);
            if (!visited.contains(thenBlock)) {
                dfs(thenBlock, visited);
            }
            if (!visited.contains(elseBlock)) {
                dfs(elseBlock, visited);
            }
        } else if (lastInstr instanceof JumpInstr jumpInstr) {
            BasicBlock targetBlock = (BasicBlock) jumpInstr.getOperands().get(0);
            if (!visited.contains(targetBlock)) {
                dfs(targetBlock, visited);
            }
        }
    }
}

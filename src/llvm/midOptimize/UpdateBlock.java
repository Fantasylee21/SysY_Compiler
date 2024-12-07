package llvm.midOptimize;

import llvm.BasicBlock;
import llvm.Function;
import llvm.Module;
public class UpdateBlock {
    private Module module;

    public UpdateBlock(Module module) {
        this.module = module;
    }

    public void run() {
        for (Function function : module.getFunctions()) {
            for (BasicBlock basicBlock : function.getBasicBlocks()) {
                function.getPreMap().put(basicBlock, basicBlock.getPredecessors());
                function.getSucMap().put(basicBlock, basicBlock.getSuccessors());
            }
        }
    }
}

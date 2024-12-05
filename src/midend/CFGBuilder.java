package midend;

import llvm.BasicBlock;
import llvm.Function;
import llvm.midInstr.BrInstr;
import llvm.midInstr.JumpInstr;
import llvm.midInstr.MidInstr;

import llvm.Module;
import java.util.ArrayList;
import java.util.HashMap;

public class CFGBuilder {
    private Module module;

    private HashMap<BasicBlock, ArrayList<BasicBlock>> preMap;
    private HashMap<BasicBlock, ArrayList<BasicBlock>> sucMap;

    public CFGBuilder(Module module) {
        this.module = module;
        this.preMap = new HashMap<>();
        this.sucMap = new HashMap<>();
    }

    public void init(Function function) {
        ArrayList<BasicBlock> basicBlocks = function.getBasicBlocks();
        preMap = new HashMap<>();
        sucMap = new HashMap<>();
        for (BasicBlock basicBlock : basicBlocks) {
            preMap.put(basicBlock, new ArrayList<>());
            sucMap.put(basicBlock, new ArrayList<>());
        }
    }

    public void buildCFG(Function function) {
        for (BasicBlock basicBlock : function.getBasicBlocks()) {
            MidInstr lastInstr = basicBlock.getInstructions().get(basicBlock.getInstructions().size() - 1);
            if (lastInstr instanceof BrInstr brInstr) {
                BasicBlock thenBlock = (BasicBlock) brInstr.getOperands().get(1);
                BasicBlock elseBlock = (BasicBlock) brInstr.getOperands().get(2);
                sucMap.get(basicBlock).add(thenBlock);
                sucMap.get(basicBlock).add(elseBlock);
                preMap.get(thenBlock).add(basicBlock);
                preMap.get(elseBlock).add(basicBlock);
            } else if (lastInstr instanceof JumpInstr jumpInstr) {
                BasicBlock targetBlock = (BasicBlock) jumpInstr.getOperands().get(0);
                sucMap.get(basicBlock).add(targetBlock);
                preMap.get(targetBlock).add(basicBlock);
            }
        }
        function.setPreMap(preMap);
        function.setSucMap(sucMap);
        for (BasicBlock basicBlock : function.getBasicBlocks()) {
            basicBlock.setPredecessors(preMap.get(basicBlock));
            basicBlock.setSuccessors(sucMap.get(basicBlock));
        }
    }

    public void run() {
        for (Function function : module.getFunctions()) {
            init(function);
            buildCFG(function);
        }
    }
}

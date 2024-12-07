package llvm.midOptimize;

import llvm.BasicBlock;
import llvm.Function;
import llvm.midInstr.BrInstr;
import llvm.midInstr.JumpInstr;
import llvm.midInstr.MidInstr;

import llvm.Module;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class BlockRelationBuilder {
    private Module module;

    private HashMap<BasicBlock, ArrayList<BasicBlock>> preMap;
    private HashMap<BasicBlock, ArrayList<BasicBlock>> sucMap;
    private HashMap<BasicBlock, ArrayList<BasicBlock>> domMap;
    private HashMap<BasicBlock, BasicBlock> parentMap;
    private HashMap<BasicBlock, ArrayList<BasicBlock>> childrenMap;
    private HashMap<BasicBlock, ArrayList<BasicBlock>> dfMap;

    public BlockRelationBuilder(Module module) {
        this.module = module;
        this.preMap = new HashMap<>();
        this.sucMap = new HashMap<>();
        this.domMap = new HashMap<>();
        this.parentMap = new HashMap<>();
        this.childrenMap = new HashMap<>();
        this.dfMap = new HashMap<>();
    }

    public void init(Function function) {
        ArrayList<BasicBlock> basicBlocks = function.getBasicBlocks();
        domMap = new HashMap<>();
        parentMap = new HashMap<>();
        childrenMap = new HashMap<>();
        dfMap = new HashMap<>();
        preMap = new HashMap<>();
        sucMap = new HashMap<>();
        for (BasicBlock basicBlock : basicBlocks) {
            domMap.put(basicBlock, new ArrayList<>());
            parentMap.put(basicBlock, null);
            childrenMap.put(basicBlock, new ArrayList<>());
            dfMap.put(basicBlock, new ArrayList<>());
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


    public void buildDominator(Function function) {
        BasicBlock entryBlock = function.getBasicBlocks().get(0);
        for (BasicBlock basicBlock : function.getBasicBlocks()) {
            HashSet<BasicBlock> visited = new HashSet<>();
            dfsDominator(entryBlock, basicBlock, visited);
            ArrayList<BasicBlock> doms = new ArrayList<>();
            for (BasicBlock block : function.getBasicBlocks()) {
                if (!visited.contains(block)) {
                    doms.add(block);
                }
            }
            basicBlock.setDominators(doms);
            domMap.put(basicBlock, doms);
        }
    }

    public void dfsDominator(BasicBlock curBlock, BasicBlock targetBlock, HashSet<BasicBlock> visited) {
        if (curBlock.equals(targetBlock)) {
            return;
        }
        visited.add(curBlock);
        for (BasicBlock child : curBlock.getSuccessors()) {
            if (!visited.contains(child)) {
                dfsDominator(child, targetBlock, visited);
            }
        }
    }

    public void buildImmDominator(Function function) {
        for (BasicBlock basicBlock : function.getBasicBlocks()) {
            for (BasicBlock domedBlock : basicBlock.getDominators()) {
                if (!basicBlock.getDominators().contains(domedBlock) || domedBlock.equals(basicBlock)) {
                    continue;
                }
                boolean isImmDom = true;
                for (BasicBlock midBlock : basicBlock.getDominators()) {
                    if (!midBlock.equals(basicBlock) && !midBlock.equals(domedBlock) && midBlock.getDominators().contains(domedBlock)) {
                        isImmDom = false;
                        break;
                    }
                }
                if (isImmDom) {
                    parentMap.put(domedBlock, basicBlock);
                    childrenMap.get(basicBlock).add(domedBlock);
                }
            }
        }
        for (BasicBlock basicBlock : function.getBasicBlocks()) {
            basicBlock.setParent(parentMap.get(basicBlock));
            basicBlock.setChildren(childrenMap.get(basicBlock));
        }
    }

    public void buildDF(Function function) {
        for (Map.Entry<BasicBlock, ArrayList<BasicBlock>> entry : sucMap.entrySet()) {
            BasicBlock basicBlock = entry.getKey();
            ArrayList<BasicBlock> children = entry.getValue();
            for (BasicBlock child : children) {
                BasicBlock runner = basicBlock;
                while (true) {
                    if (!runner.getDominators().contains(child) || runner.equals(child)) {
                        dfMap.get(runner).add(child);
                        runner = parentMap.get(runner);
                    } else {
                        break;
                    }
                }
            }
        }
        for (BasicBlock basicBlock : function.getBasicBlocks()) {
            basicBlock.setDominanceFrontiers(dfMap.get(basicBlock));
        }
    }

    public void run() {
        for (Function function : module.getFunctions()) {
            init(function);
            buildCFG(function);
            buildDominator(function);
            buildImmDominator(function);
            buildDF(function);
            for (BasicBlock basicBlock : function.getBasicBlocks()) {
                basicBlock.setDefUse();
            }
        }
    }
}

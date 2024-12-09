package backend.optimize;

import backend.ObjBlock;
import backend.ObjFunction;
import backend.ObjModule;
import backend.objInstr.ObjInstr;
import backend.objInstr.branch.ObjBranchInstr;
import backend.objInstr.jump.JumpType;
import backend.objInstr.jump.ObjJumpInstr;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class RemoveBlockByJ {
    private ObjModule objModule;

    public RemoveBlockByJ(ObjModule objModule) {
        this.objModule = objModule;
    }

    public void run() {
        for (ObjFunction objFunction : objModule.getFunctions()) {
            removeBlockByJ(objFunction);
        }
    }

    public void removeBlockByJ(ObjFunction objFunction) {
        HashMap<String, String> labelMap = new HashMap<>();
        Iterator<ObjBlock> iterator = objFunction.getBlocks().iterator();
        while (iterator.hasNext()) {
            ObjBlock objBlock = iterator.next();
            if (objBlock.getInstructions().size() == 1 && objBlock.getInstructions().get(0) instanceof ObjJumpInstr objJumpInstr && objJumpInstr.getJumpType() == JumpType.J) {
                String endLabel= objJumpInstr.getLabel();
                String startLabel = objBlock.getName();
                labelMap.put(startLabel, endLabel);
                for (String label : labelMap.keySet()) {
                    if (labelMap.get(label).equals(startLabel)) {
                        labelMap.put(label, endLabel);
                    }
                }
                for (String label : labelMap.keySet()) {
                    if (label.equals(endLabel)) {
                        labelMap.put(startLabel, labelMap.get(label));
                    }
                }
                iterator.remove();
            }
        }
        for (ObjBlock objBlock : objFunction.getBlocks()) {
            for (ObjInstr objInstr : objBlock.getInstructions()) {
                if (objInstr instanceof ObjJumpInstr objJumpInstr && objJumpInstr.getJumpType() == JumpType.J) {
                    String label = objJumpInstr.getLabel();
                    if (labelMap.containsKey(label)) {
                        objJumpInstr.setLabel(labelMap.get(label));
                    }
                } else if (objInstr instanceof ObjBranchInstr objBranchInstr) {
                    String label = objBranchInstr.getLabel();
                    if (labelMap.containsKey(label)) {
                        objBranchInstr.setLabel(labelMap.get(label));
                    }
                }
            }
        }
    }
}

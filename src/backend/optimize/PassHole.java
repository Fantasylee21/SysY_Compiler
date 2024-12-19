package backend.optimize;

import backend.ObjBlock;
import backend.ObjFunction;
import backend.ObjModule;
import backend.objInstr.ObjInstr;
import backend.objInstr.ObjMoveInstr;
import llvm.midInstr.MoveInstr;

import java.util.HashSet;
import java.util.Iterator;

public class PassHole {
    private ObjModule objModule;

    public PassHole(ObjModule objModule) {
        this.objModule = objModule;
    }

    public void run() {
        for (ObjFunction objFunction : objModule.getFunctions()) {
            passHole(objFunction);
        }
    }

    public void passHole(ObjFunction objFunction) {
        /* 无用代码消除
        move a, r0
        move a, r0
        */
        for (ObjBlock objBlock : objFunction.getBlocks()) {
            HashSet<ObjInstr> removeIndex = new HashSet<>();
            for (int i = 0; i < objBlock.getInstructions().size() - 1; i++) {
                ObjInstr objInstr = objBlock.getInstructions().get(i);
                ObjInstr nextObjInstr = objBlock.getInstructions().get(i + 1);
                if (objInstr instanceof ObjMoveInstr objMoveInstr && nextObjInstr instanceof ObjMoveInstr nextObjMoveInstr) {
                    if (objMoveInstr.getDst().toString().equals(nextObjMoveInstr.getDst().toString()) && objMoveInstr.getSrc().toString().equals(nextObjMoveInstr.getSrc().toString())) {
                        removeIndex.add(nextObjMoveInstr);
                    } else if (objMoveInstr.getDst().toString().equals(nextObjMoveInstr.getSrc().toString()) && objMoveInstr.getSrc().toString().equals(nextObjMoveInstr.getDst().toString())) {
                        removeIndex.add(nextObjMoveInstr);
                    }
                }
            }
            if (objFunction.getLlvmFunction().getArguments().isEmpty()) {
//                找到使用$t9的进行删除
                for (ObjInstr objInstr : objBlock.getInstructions()) {
                    if (objInstr instanceof ObjMoveInstr objMoveInstr && objMoveInstr.getDst().toString().equals("$t9")) {
                        removeIndex.add(objInstr);
                    }
                    if (objInstr instanceof ObjMoveInstr objMoveInstr && objMoveInstr.getSrc().toString().equals("$t9")) {
                        removeIndex.add(objInstr);
                    }
                }
            }
            for (ObjInstr objInstr : removeIndex) {
                objBlock.getInstructions().remove(objInstr);
            }
        }
    }
}

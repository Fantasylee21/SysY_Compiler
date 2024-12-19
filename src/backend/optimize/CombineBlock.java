package backend.optimize;

import backend.ObjBlock;
import backend.ObjFunction;
import backend.ObjModule;

import java.util.Iterator;

public class CombineBlock {
    private ObjModule objModule;

    public CombineBlock(ObjModule objModule) {
        this.objModule = objModule;
    }

    public void run() {
        for (ObjFunction objFunction : objModule.getFunctions()) {
            combineBlock(objFunction);
        }
    }

    public void combineBlock(ObjFunction objFunction) {
        Iterator<ObjBlock> iterator = objFunction.getBlocks().iterator();
        while (iterator.hasNext()) {
            ObjBlock objBlock = iterator.next();
            if (objBlock.getSuccessors().size() == 1 && objBlock.getSuccessors().get(0).getPredecessors().size() == 1 && iterator.next() != null && objBlock.getSuccessors().get(0).equals(iterator.next())) {
                ObjBlock successor = objBlock.getSuccessors().get(0);
                objBlock.getInstructions().remove(objBlock.getInstructions().size() - 1);
                objBlock.getInstructions().addAll(successor.getInstructions());
                objBlock.getSuccessors().clear();
                objBlock.getSuccessors().addAll(successor.getSuccessors());
                iterator.remove();
            }
        }
    }
}

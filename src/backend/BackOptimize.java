package backend;

import backend.optimize.CombineBlock;
import backend.optimize.PassHole;
import backend.optimize.RegAllocator;
import backend.optimize.RemoveBlockByJ;

public class BackOptimize {
    private ObjModule objModule;

    public BackOptimize(ObjModule objModule) {
        this.objModule = objModule;
    }

    public void run() {
        RegAllocator regAllocator = new RegAllocator(objModule);
        RemoveBlockByJ removeBlockByJ = new RemoveBlockByJ(objModule);
        CombineBlock combineBlock = new CombineBlock(objModule);
        PassHole passHole = new PassHole(objModule);

        regAllocator.allocReg();
//        removeBlockByJ.run();
//        combineBlock.run();
        passHole.run();
    }
}

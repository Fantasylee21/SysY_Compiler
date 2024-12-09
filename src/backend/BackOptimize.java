package backend;

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

        regAllocator.allocReg();
        removeBlockByJ.run();
    }
}

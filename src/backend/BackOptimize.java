package backend;

import backend.optimize.RemoveBlockByJ;

public class BackOptimize {
    private ObjModule objModule;

    public BackOptimize(ObjModule objModule) {
        this.objModule = objModule;
    }

    public void run() {
        RemoveBlockByJ removeBlockByJ = new RemoveBlockByJ(objModule);
        removeBlockByJ.run();
    }
}

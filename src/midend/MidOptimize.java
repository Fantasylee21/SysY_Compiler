package midend;
import llvm.Module;
public class MidOptimize {
    private Module module;

    public MidOptimize(Module module) {
        this.module = module;
    }

    public void run() {
        DelDeadBlock delDeadBlock = new DelDeadBlock(module);
        CFGBuilder cfgBuilder = new CFGBuilder(module);
        DelDeadCode delDeadCode = new DelDeadCode(module);
        delDeadBlock.run();
        delDeadCode.run();
        cfgBuilder.run();
    }
}

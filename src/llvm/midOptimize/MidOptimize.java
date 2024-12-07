package llvm.midOptimize;
import llvm.Module;
public class MidOptimize {
    private Module module;

    public MidOptimize(Module module) {
        this.module = module;
    }

    public void run() {
        DelDeadBlock delDeadBlock = new DelDeadBlock(module);
        BlockRelationBuilder blockRelationBuilder = new BlockRelationBuilder(module);
        DelDeadCode delDeadCode = new DelDeadCode(module);
        Mem2Reg mem2Reg = new Mem2Reg(module);
        RemovePhi removePhi = new RemovePhi(module);
        delDeadBlock.run();
        delDeadCode.run();
        blockRelationBuilder.run();
        mem2Reg.run();
        removePhi.run();
    }
}

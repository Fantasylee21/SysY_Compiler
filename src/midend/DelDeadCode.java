package midend;
import llvm.BasicBlock;
import llvm.Function;
import llvm.Module;
import llvm.midInstr.AllocaInstr;
import llvm.midInstr.GetElementPtrInstr;
import llvm.midInstr.LoadInstr;
import llvm.midInstr.MidInstr;
import llvm.midInstr.binaryOperatorTy.BinaryOperatorTyInstr;
import llvm.midInstr.icmp.IcmpInstr;

import java.util.Iterator;

public class DelDeadCode {
    private Module module;

    public DelDeadCode(Module module) {
        this.module = module;
    }

    public boolean canBeUse(MidInstr instr) {
        if (instr instanceof AllocaInstr) {
            return true;
        }
        if (instr instanceof BinaryOperatorTyInstr) {
            return true;
        }
        if (instr instanceof IcmpInstr) {
            return true;
        }
        if (instr instanceof LoadInstr) {
            return true;
        }
        if (instr instanceof GetElementPtrInstr) {
            return true;
        }
        return false;
    }

    public void run() {
        for (Function function : module.getFunctions()) {
            for (BasicBlock basicBlock : function.getBasicBlocks()) {
                Iterator<MidInstr> instrIterator = basicBlock.getInstructions().iterator();
                while (instrIterator.hasNext()) {
                    MidInstr instr = instrIterator.next();
                    if (canBeUse(instr) && instr.getUseList().isEmpty()) {
                        instrIterator.remove();
                    }
                }
            }
        }
    }
}

package llvm.instr;

import llvm.BasicBlock;
import llvm.Value;
import llvm.type.VoidType;

public class BrInstr extends Instr {
    public BrInstr(String name, Value cond, BasicBlock thenBlock, BasicBlock elseBlock) {
        super(VoidType.getInstance(), name, InstrType.BR);
        addOperand(cond);
        addOperand(thenBlock);
        addOperand(elseBlock);
    }

    @Override
    public String toString() {
        return "br i1 " + operands.get(0).getName() + ", label %" + operands.get(1).getName() + ", label %" + operands.get(2).getName();
    }
}

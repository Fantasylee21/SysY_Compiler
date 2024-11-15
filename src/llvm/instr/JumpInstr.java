package llvm.instr;

import llvm.BasicBlock;
import llvm.type.VoidType;

public class JumpInstr extends Instr {
    public JumpInstr(String name, BasicBlock target) {
        super(VoidType.getInstance(), name, InstrType.JUMP);
        addOperand(target);
    }

    @Override
    public String toString() {
        return "br label %" + operands.get(0).getName();
    }

}

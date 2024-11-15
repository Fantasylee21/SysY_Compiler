package llvm.instr.io;

import llvm.Value;
import llvm.instr.Instr;
import llvm.instr.InstrType;
import llvm.type.VoidType;

public class PutIntInstr extends Instr {
    public PutIntInstr(String name, Value value) {
        super(VoidType.getInstance(), name, InstrType.PUTINT);
        addOperand(value);
    }

    @Override
    public String toString() {
        return "call void @putint(i32 " + operands.get(0).getName() + ")";
    }
}

package llvm.instr.io;

import llvm.Value;
import llvm.instr.Instr;
import llvm.instr.InstrType;

public class PutChInstr extends Instr {
    public PutChInstr(String name, Value value) {
        super(null, name, InstrType.PUTCH);
        addOperand(value);
    }

    @Override
    public String toString() {
        return "call void @putch(i32 " + operands.get(0).getName() + ")";
    }
}

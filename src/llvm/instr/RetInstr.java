package llvm.instr;

import llvm.Value;
import llvm.type.LLVMType;
import llvm.type.VoidType;

public class RetInstr extends Instr {
    public RetInstr(String name, Value retValue) {
        super(VoidType.getInstance(), name, InstrType.RET);
        addOperand(retValue);
    }

    @Override
    public String toString() {
        if (operands.get(0) == null) {
            return "ret void";
        }
        return "ret " + operands.get(0).getType().toString() + " " + operands.get(0).getName();
    }
}

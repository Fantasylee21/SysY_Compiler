package llvm.instr;

import llvm.Value;
import llvm.type.LLVMType;

public class TruncInstr extends Instr {
    private final LLVMType targetType;

    public TruncInstr(String name, Value value, LLVMType targetType) {
        super(targetType, name, InstrType.TRUNC);
        this.targetType = targetType;
        addOperand(value);
    }

    @Override
    public String toString() {
        return name + " = trunc " + operands.get(0).getType().toString() + " " + operands.get(0).getName() + " to " + targetType.toString();
    }
}

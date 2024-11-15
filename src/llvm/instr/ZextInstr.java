package llvm.instr;

import llvm.Value;
import llvm.type.LLVMType;
public class ZextInstr extends Instr {
    private final LLVMType targetType;

    public ZextInstr(String name, Value value, LLVMType targetType) {
        super(targetType, name, InstrType.ZEXT);
        this.targetType = targetType;
        addOperand(value);
    }

    @Override
    public String toString() {
        return name + " = zext " + operands.get(0).getType().toString() + " " + operands.get(0).getName() + " to " + targetType.toString();
    }
}

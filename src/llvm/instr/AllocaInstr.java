package llvm.instr;

import llvm.type.LLVMType;
import llvm.type.PointerType;

public class AllocaInstr extends Instr {
    private final LLVMType targetType;

    public AllocaInstr(String name, LLVMType targetType) {
        super(new PointerType(targetType), name, InstrType.ALLOCA);
        this.targetType = targetType;
    }

    @Override
    public String toString() {
        return name + " = alloca " + targetType.toString();
    }
}

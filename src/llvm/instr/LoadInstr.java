package llvm.instr;

import llvm.Value;
import llvm.type.LLVMEnumType;
import llvm.type.PointerType;

public class LoadInstr extends Instr {
    public LoadInstr(String name, Value pointer) {
        super(((PointerType) pointer.getType()).getTargetType(), name, InstrType.LOAD);
        addOperand(pointer);
    }

    @Override
    public String toString() {
        return name + " = load " + getType().toString() + ", " + operands.get(0).getType().toString() + " " + operands.get(0).getName();
    }
}

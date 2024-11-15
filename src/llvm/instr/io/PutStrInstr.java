package llvm.instr.io;

import llvm.PrintString;
import llvm.instr.Instr;
import llvm.instr.InstrType;
import llvm.type.PointerType;

public class PutStrInstr extends Instr {
    private final PrintString value;

    public PutStrInstr(String name, PrintString value) {
        super(null, name, InstrType.PUTSTR);
        this.value = value;
    }

    @Override
    public String toString() {
        PointerType pointerType = (PointerType) value.getType();
        return "call void @putstr(i8* getelementptr inbounds (" + pointerType.getTargetType().toString() + ", " + pointerType.toString() + " " + value.getName() + ", i64 0, i64 0))";
    }
}

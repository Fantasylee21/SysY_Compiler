package llvm.instr.io;

import llvm.instr.Instr;
import llvm.instr.InstrType;
import llvm.type.Int32Type;

public class GetIntInstr extends Instr {
    public GetIntInstr(String name) {
        super(Int32Type.getInstance(), name, InstrType.GETINT);
    }

    @Override
    public String toString() {
        return name + " = call i32 @getint()";
    }
}

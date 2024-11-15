package llvm.instr.io;

import llvm.instr.Instr;
import llvm.instr.InstrType;
import llvm.type.Int32Type;
import llvm.type.Int8Type;

public class GetCharInstr extends Instr {
    public GetCharInstr(String name) {
        super(Int32Type.getInstance(), name, InstrType.GETCHAR);
    }

    @Override
    public String toString() {
        return name + " = call i32 @getchar()";
    }
}

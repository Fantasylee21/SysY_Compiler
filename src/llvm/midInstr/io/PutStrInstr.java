package llvm.midInstr.io;

import backend.objInstr.ObjLaInstr;
import backend.objInstr.ObjLiInstr;
import backend.objInstr.ObjSyscallInstr;
import backend.register.RealRegister;
import backend.register.Register;
import llvm.PrintString;
import llvm.midInstr.MidInstr;
import llvm.midInstr.MidInstrType;
import llvm.type.PointerType;

public class PutStrInstr extends MidInstr {
    private final PrintString value;

    public PutStrInstr(String name, PrintString value) {
        super(null, name, MidInstrType.PUTSTR);
        this.value = value;
    }

    @Override
    public String toString() {
        PointerType pointerType = (PointerType) value.getType();
        return "call void @putstr(i8* getelementptr inbounds (" + pointerType.getTargetType().toString() + ", " + pointerType.toString() + " " + value.getName() + ", i64 0, i64 0))";
    }

    @Override
    public void generateMips() {
        new ObjLaInstr(Register.get$a0(), value.getName().substring(1));
        new ObjLiInstr(Register.get$v0(), 4);
        new ObjSyscallInstr();
    }
}

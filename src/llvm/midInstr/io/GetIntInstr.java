package llvm.midInstr.io;

import backend.MipsBuilder;
import backend.objInstr.ObjLiInstr;
import backend.objInstr.ObjMoveInstr;
import backend.objInstr.ObjSyscallInstr;
import backend.register.Register;
import backend.register.VirtualRegister;
import llvm.midInstr.MidInstr;
import llvm.midInstr.MidInstrType;
import llvm.type.Int32Type;

public class GetIntInstr extends MidInstr {
    public GetIntInstr(String name) {
        super(Int32Type.getInstance(), name, MidInstrType.GETINT);
    }

    @Override
    public String toString() {
        return name + " = call i32 @getint()";
    }

    @Override
    public void generateMips() {
        new ObjLiInstr(Register.get$v0(), 5);
        new ObjSyscallInstr();

        Register register = new Register(VirtualRegister.getVirtualRegister().getRegister());
        MipsBuilder.getMipsBuilder().addRegisterAllocation(this, register);

        new ObjMoveInstr(register, Register.get$v0());
    }
}

package llvm.midInstr.io;

import backend.MipsBuilder;
import backend.objInstr.ObjLiInstr;
import backend.objInstr.ObjMoveInstr;
import backend.objInstr.ObjSyscallInstr;
import backend.objInstr.store.ObjStoreInstr;
import backend.objInstr.store.StoreType;
import backend.register.Register;
import backend.register.VirtualRegister;
import llvm.midInstr.MidInstr;
import llvm.midInstr.MidInstrType;
import llvm.type.Int32Type;

public class GetCharInstr extends MidInstr {
    public GetCharInstr(String name) {
        super(Int32Type.getInstance(), name, MidInstrType.GETCHAR);
    }

    @Override
    public String toString() {
        return name + " = call i32 @getchar()";
    }

    @Override
    public void generateMips() {
        new ObjLiInstr(Register.get$v0(), 12);
        new ObjSyscallInstr();

        Register register = new Register(VirtualRegister.getVirtualRegister().getRegister());
        MipsBuilder.getMipsBuilder().addRegisterAllocation(this, register);

        new ObjMoveInstr(register, Register.get$v0());
    }
}

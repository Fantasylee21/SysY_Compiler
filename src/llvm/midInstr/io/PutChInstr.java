package llvm.midInstr.io;

import backend.MipsBuilder;
import backend.objInstr.ObjLiInstr;
import backend.objInstr.ObjMoveInstr;
import backend.objInstr.ObjSyscallInstr;
import backend.register.Register;
import llvm.Constant;
import llvm.Value;
import llvm.midInstr.MidInstr;
import llvm.midInstr.MidInstrType;

public class PutChInstr extends MidInstr {
    public PutChInstr(String name, Value value) {
        super(null, name, MidInstrType.PUTCH);
        addOperand(value);
    }

    @Override
    public String toString() {
        return "call void @putch(i32 " + operands.get(0).getName() + ")";
    }

    @Override
    public void generateMips() {
        Value value = operands.get(0);
        new ObjMoveInstr(Register.get$t9(), Register.get$a0());
        if (value instanceof Constant) {
            new ObjLiInstr(Register.get$a0(), ((Constant) value).getValue());
        } else {
            Register register = MipsBuilder.getMipsBuilder().getRegister(value);
            new ObjMoveInstr(Register.get$a0(), register);
        }
        new ObjLiInstr(Register.get$v0(), 11);
        new ObjSyscallInstr();
        new ObjMoveInstr(Register.get$a0(), Register.get$t9());
    }
}

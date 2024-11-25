package llvm.midInstr;

import backend.MipsBuilder;
import backend.objInstr.*;
import backend.objInstr.jump.JumpType;
import backend.objInstr.jump.ObjJumpInstr;
import backend.objInstr.load.LoadType;
import backend.objInstr.load.ObjLoadInstr;
import backend.objInstr.store.ObjStoreInstr;
import backend.objInstr.store.StoreType;
import backend.register.Register;
import backend.register.VirtualRegister;
import llvm.Constant;
import llvm.Function;
import llvm.GlobalVariable;
import llvm.Value;
import llvm.type.LLVMEnumType;
import llvm.type.VoidType;

import java.util.Objects;

public class RetInstr extends MidInstr {
    public RetInstr(String name, Value retValue) {
        super(VoidType.getInstance(), name, MidInstrType.RET);
        addOperand(retValue);
    }

    @Override
    public String toString() {
        if (operands.get(0) == null) {
            return "ret void";
        }
        return "ret " + operands.get(0).getType().toString() + " " + operands.get(0).getName();
    }

    @Override
    public void generateMips() {
        Function currentFunc = MipsBuilder.getMipsBuilder().getCurrentFunction();
        if (operands.get(0) != null && !Objects.equals(currentFunc.getName(), "@main")) {
            if (operands.get(0) instanceof Constant) {
                new ObjLiInstr(Register.get$v0(), ((Constant) operands.get(0)).getValue());
            } else {
                Register retValue = MipsBuilder.getMipsBuilder().getRegister(operands.get(0));
                new ObjMoveInstr(Register.get$v0(), retValue);
            }
        }
        if (!Objects.equals(currentFunc.getName(), "@main")) {
            new ObjCommentInstr("return function " + currentFunc.getName().substring(1));
            new ObjJRInstr(Register.get$ra());
        } else {
            new ObjJumpInstr(JumpType.J, "exit");
        }
    }
}

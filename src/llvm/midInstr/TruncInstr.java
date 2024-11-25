package llvm.midInstr;

import backend.MipsBuilder;
import backend.objInstr.ObjLiInstr;
import backend.objInstr.load.LoadType;
import backend.objInstr.load.ObjLoadInstr;
import backend.objInstr.store.ObjStoreInstr;
import backend.objInstr.store.StoreType;
import backend.register.Register;
import backend.register.VirtualRegister;
import llvm.Constant;
import llvm.GlobalVariable;
import llvm.Value;
import llvm.type.LLVMType;

public class TruncInstr extends MidInstr {
    private final LLVMType targetType;

    public TruncInstr(String name, Value value, LLVMType targetType) {
        super(targetType, name, MidInstrType.TRUNC);
        this.targetType = targetType;
        addOperand(value);
    }

    @Override
    public String toString() {
        return name + " = trunc " + operands.get(0).getType().toString() + " " + operands.get(0).getName() + " to " + targetType.toString();
    }

    @Override
    public void generateMips() {
        Register from = MipsBuilder.getMipsBuilder().getRegister(operands.get(0));
        if (from == null) {
            from = new Register(VirtualRegister.getVirtualRegister().getRegister());
            if (operands.get(0) instanceof Constant constant) {
                new ObjLiInstr(from, constant.getValue());
            }
        }
        MipsBuilder.getMipsBuilder().addRegisterAllocation(this, from);
    }
}

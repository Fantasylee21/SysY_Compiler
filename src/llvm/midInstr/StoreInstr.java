package llvm.midInstr;

import backend.MipsBuilder;
import backend.objInstr.ObjLaInstr;
import backend.objInstr.ObjLiInstr;
import backend.objInstr.load.LoadType;
import backend.objInstr.load.ObjLoadInstr;
import backend.objInstr.store.ObjStoreInstr;
import backend.objInstr.store.StoreType;
import backend.register.RealRegister;
import backend.register.Register;
import backend.register.VirtualRegister;
import llvm.Constant;
import llvm.GlobalVariable;
import llvm.Value;
import llvm.type.LLVMEnumType;
import llvm.type.LLVMType;
import llvm.type.PointerType;

public class StoreInstr extends MidInstr {
    public StoreInstr(String name, Value value, Value pointer_to) {
        super(null, name, MidInstrType.STORE);
        addOperand(value);
        addOperand(pointer_to);
    }

    @Override
    public String toString() {
        return "store " + operands.get(0).getType()+ " " + operands.get(0).getName() + ", " +  operands.get(1).getType() + " " + operands.get(1).getName();
    }

    @Override
    public void generateMips() {
        Register value = MipsBuilder.getMipsBuilder().getRegister(operands.get(0));
        Register pointer_to = MipsBuilder.getMipsBuilder().getRegister(operands.get(1));

        LLVMType targetType = ((PointerType) operands.get(1).getType()).getTargetType();


        if (operands.get(0) instanceof Constant) {
            value = new Register(VirtualRegister.getVirtualRegister().getRegister());
            new ObjLiInstr(value, ((Constant) operands.get(0)).getValue());
        }
        if (operands.get(1) instanceof GlobalVariable) {
            pointer_to = new Register(VirtualRegister.getVirtualRegister().getRegister());
            MipsBuilder.getMipsBuilder().addRegisterAllocation(operands.get(1), pointer_to);
            new ObjLaInstr(pointer_to, operands.get(1).getName().substring(1));
        }
        if (targetType.getType() == LLVMEnumType.Int8Type) {
            new ObjStoreInstr(StoreType.SB, value, pointer_to, 0);
        } else {
            new ObjStoreInstr(StoreType.SW, value, pointer_to, 0);
        }
    }
}

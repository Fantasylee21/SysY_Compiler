package llvm.midInstr;

import backend.MipsBuilder;
import backend.objInstr.ObjLaInstr;
import backend.objInstr.load.LoadType;
import backend.objInstr.load.ObjLoadInstr;
import backend.register.Register;
import backend.register.VirtualRegister;
import llvm.GlobalVariable;
import llvm.Value;
import llvm.type.LLVMEnumType;
import llvm.type.LLVMType;
import llvm.type.PointerType;

public class LoadInstr extends MidInstr {
    public LoadInstr(String name, Value pointer) {
        super(((PointerType) pointer.getType()).getTargetType(), name, MidInstrType.LOAD);
        addOperand(pointer);
    }

    @Override
    public String toString() {
        return name + " = load " + getType().toString() + ", " + operands.get(0).getType().toString() + " " + operands.get(0).getName();
    }

    @Override
    public void generateMips() {
        Register target = new Register(VirtualRegister.getVirtualRegister().getRegister());
        MipsBuilder.getMipsBuilder().addRegisterAllocation(this, target);
        Value pointer = operands.get(0);
        Register from = MipsBuilder.getMipsBuilder().getRegister(pointer);
        LLVMType targetType = getType();

        if (pointer instanceof GlobalVariable) {
            from = new Register(VirtualRegister.getVirtualRegister().getRegister());
            new ObjLaInstr(from , pointer.getName().substring(1));
            if (targetType.getType() == LLVMEnumType.Int8Type) {
                new ObjLoadInstr(LoadType.LB, target, from, 0);
            } else {
                new ObjLoadInstr(LoadType.LW, target, from, 0);
            }
        } else {
            if (targetType.getType() == LLVMEnumType.Int8Type) {
                new ObjLoadInstr(LoadType.LB, target, from, 0);
            } else {
                new ObjLoadInstr(LoadType.LW, target, from, 0);
            }
        }

//        if (targetType.getType() == LLVMEnumType.Int8Type) {
//            new ObjStoreInstr(StoreType.SB, target, Register.get$sp(), MipsBuilder.getMipsBuilder().getCurOffset());
//        } else {
//            new ObjStoreInstr(StoreType.SW, target, Register.get$sp(), MipsBuilder.getMipsBuilder().getCurOffset());
//        }
//        MipsBuilder.getMipsBuilder().addStackFrameValue(4, target);

    }
}

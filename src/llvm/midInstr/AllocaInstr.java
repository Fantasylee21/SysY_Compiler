package llvm.midInstr;

import backend.MipsBuilder;
import backend.objInstr.riCalculate.ObjRICalculate;
import backend.objInstr.riCalculate.RICalculateType;
import backend.register.Register;
import backend.register.VirtualRegister;
import llvm.type.ArrayType;
import llvm.type.LLVMEnumType;
import llvm.type.LLVMType;
import llvm.type.PointerType;

public class AllocaInstr extends MidInstr {
    private final LLVMType targetType;

    public AllocaInstr(String name, LLVMType targetType) {
        super(new PointerType(targetType), name, MidInstrType.ALLOCA);
        this.targetType = targetType;
    }

    @Override
    public String toString() {
        return name + " = alloca " + targetType.toString();
    }

    @Override
    public void generateMips() {
        LLVMType targetType = ((PointerType) getType()).getTargetType();
        Register register = new Register(VirtualRegister.getVirtualRegister().getRegister());
        MipsBuilder.getMipsBuilder().addRegisterAllocation(this, register);

        new ObjRICalculate(RICalculateType.ADDI, register, Register.get$sp(), MipsBuilder.getMipsBuilder().getCurOffset());
        if (targetType.getType() == LLVMEnumType.ArrayType) {
            ArrayType arrayType = (ArrayType) targetType;
            int size = arrayType .getSize();
            MipsBuilder.getMipsBuilder().curOffsetUp(size * 4);
        } else {
            MipsBuilder.getMipsBuilder().curOffsetUp(4);
        }

    }
}

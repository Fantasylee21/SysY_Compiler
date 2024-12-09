package llvm.midInstr;

import backend.MipsBuilder;
import backend.objInstr.ObjLiInstr;
import backend.objInstr.ObjMoveInstr;
import backend.register.Register;
import backend.register.VirtualRegister;
import llvm.Constant;
import llvm.Value;

public class MoveInstr extends MidInstr{
    public MoveInstr(Value dst, Value src) {
        super(null, null, MidInstrType.PCOPY, true);
        addOperand(src);
        addOperand(dst);
    }

    @Override
    public String toString() {
        return "move " + getOperands().get(1).getName() + ", " + getOperands().get(0).getName();
    }

    @Override
    public void generateMips() {
        Register srcReg = MipsBuilder.getMipsBuilder().getRegister(getOperands().get(0));
        Register dstReg = MipsBuilder.getMipsBuilder().getRegister(getOperands().get(1));
        if (srcReg == null) {
            srcReg = new Register(VirtualRegister.getVirtualRegister().getRegister());
            MipsBuilder.getMipsBuilder().addRegisterAllocation(getOperands().get(0), srcReg);
        }
        if (dstReg == null) {
            dstReg = new Register(VirtualRegister.getVirtualRegister().getRegister());
            MipsBuilder.getMipsBuilder().addRegisterAllocation(getOperands().get(1), dstReg);
        }
        if (getOperands().get(0) instanceof Constant constant) {
            new ObjLiInstr(dstReg, constant.getValue());
            return;
        }
        if (srcReg != dstReg) {
            new ObjMoveInstr(dstReg, srcReg);
        }

    }

}

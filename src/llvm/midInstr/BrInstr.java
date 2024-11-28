package llvm.midInstr;

import backend.MipsBuilder;
import backend.objInstr.branch.BranchType;
import backend.objInstr.branch.ObjBranchInstr;
import backend.objInstr.jump.JumpType;
import backend.objInstr.jump.ObjJumpInstr;
import backend.register.Register;
import llvm.BasicBlock;
import llvm.Value;
import llvm.type.VoidType;

public class BrInstr extends MidInstr {
    public BrInstr(String name, Value cond, BasicBlock thenBlock, BasicBlock elseBlock) {
        super(VoidType.getInstance(), name, MidInstrType.BR);
        addOperand(cond);
        addOperand(thenBlock);
        addOperand(elseBlock);
    }

    @Override
    public String toString() {
        return "br i1 " + operands.get(0).getName() + ", label %" + operands.get(1).getName() + ", label %" + operands.get(2).getName();
    }

    @Override
    public void generateMips() {
        Register rs = MipsBuilder.getMipsBuilder().getRegister(operands.get(0));
//        if (rs == null && operands.get(0) instanceof GlobalVariable) {
//            rs = new Register(VirtualRegister.getVirtualRegister().getRegister());
//            MipsBuilder.getMipsBuilder().addRegisterAllocation(operands.get(0), rs);
//
//            new ObjLaInstr(Register.get$k0(), operands.get(0).getName().substring(1));
//            new ObjLoadInstr(LoadType.LW, rs, Register.get$k0(), 0);
//        }
        new ObjBranchInstr(BranchType.BNE, operands.get(1).getName(), rs, Register.get$zero());
        new ObjJumpInstr(JumpType.J ,operands.get(2).getName());
    }
}

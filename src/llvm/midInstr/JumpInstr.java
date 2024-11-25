package llvm.midInstr;

import backend.objInstr.jump.JumpType;
import backend.objInstr.jump.ObjJumpInstr;
import llvm.BasicBlock;
import llvm.type.VoidType;

public class JumpInstr extends MidInstr {
    public JumpInstr(String name, BasicBlock target) {
        super(VoidType.getInstance(), name, MidInstrType.JUMP);
        addOperand(target);
    }

    @Override
    public String toString() {
        return "br label %" + operands.get(0).getName();
    }

    @Override
    public void generateMips() {
        new ObjJumpInstr(JumpType.J, operands.get(0).getName());
    }

}

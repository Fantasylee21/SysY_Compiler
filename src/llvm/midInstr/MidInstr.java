package llvm.midInstr;

import backend.register.Register;
import llvm.BasicBlock;
import llvm.LLVMBuilder;
import llvm.User;
import llvm.type.LLVMType;

public class MidInstr extends User {
    private final MidInstrType instrType;
    private BasicBlock parentBasicBlock;

    public MidInstr(LLVMType type, String name, MidInstrType instrType) {
        super(type, name);
        this.instrType = instrType;
        parentBasicBlock = null;
        LLVMBuilder.getLlvmBuilder().addInstruction(this);
    }

    public boolean isDef() {
        return false;
    }

    public MidInstrType getInstrType() {
        return instrType;
    }

    public void setParentBasicBlock(BasicBlock parentBasicBlock) {
        this.parentBasicBlock = parentBasicBlock;
    }

    public BasicBlock getParentBasicBlock() {
        return parentBasicBlock;
    }
}

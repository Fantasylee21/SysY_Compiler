package llvm.instr;

import llvm.BasicBlock;
import llvm.LLVMBuilder;
import llvm.User;
import llvm.type.LLVMType;

public class Instr extends User {
    private final InstrType instrType;
    private BasicBlock parentBasicBlock;

    public Instr(LLVMType type, String name, InstrType instrType) {
        super(type, name);
        this.instrType = instrType;
        parentBasicBlock = null;
        LLVMBuilder.getLlvmBuilder().addInstruction(this);
    }

    public InstrType getInstrType() {
        return instrType;
    }

    public void setParentBasicBlock(BasicBlock parentBasicBlock) {
        this.parentBasicBlock = parentBasicBlock;
    }

    public BasicBlock getParentBasicBlock() {
        return parentBasicBlock;
    }
}

package llvm.midInstr;

import llvm.BasicBlock;
import llvm.Value;
import llvm.type.Int32Type;

import java.util.ArrayList;

public class PhiInstr extends MidInstr {
    private ArrayList<BasicBlock> basicBlocks;

    public PhiInstr(String name, ArrayList<BasicBlock> basicBlocks) {
        super(Int32Type.getInstance(), name, MidInstrType.PHI, true);
        this.basicBlocks = basicBlocks;
        for (BasicBlock basicBlock : basicBlocks) {
            addOperand(basicBlock);
        }
    }

    public void addOption(BasicBlock basicBlock, Value value) {
        operands.set(basicBlocks.indexOf(basicBlock), value);
        value.addUse(this);
    }

    public ArrayList<BasicBlock> getBasicBlocks() {
        return basicBlocks;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append(" = phi ");
        sb.append(type.toString()).append(" ");
        for (int i = 0; i < basicBlocks.size(); i++) {
            sb.append("[ ").append(operands.get(i).getName()).append(", %").append(basicBlocks.get(i).getName()).append(" ]");
            if (i != basicBlocks.size() - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }
}

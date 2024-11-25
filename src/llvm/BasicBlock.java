package llvm;

import backend.objInstr.ObjLabelInstr;
import llvm.midInstr.MidInstr;
import llvm.midInstr.MidInstrType;
import llvm.type.OtherType;

import java.util.ArrayList;

public class BasicBlock extends Value {
    private ArrayList<MidInstr> instructions;
    private Function parentFunction;

    public BasicBlock(String name) {
        super(OtherType.getBasicBlock(), name);
        this.instructions = new ArrayList<>();
        this.parentFunction = null;

        LLVMBuilder.getLlvmBuilder().addBasicBlock(this);
    }

    public void setParentFunction(Function parentFunction) {
        this.parentFunction = parentFunction;
    }

    public Function getParentFunction() {
        return parentFunction;
    }

    public ArrayList<MidInstr> getInstructions() {
        return instructions;
    }

    public void addInstr(MidInstr instr) {
        instructions.add(instr);
    }

    public void removeExtraInstr() {
        for (int i = 0; i < instructions.size(); i++) {
            if (instructions.get(i).getInstrType() == MidInstrType.BR || instructions.get(i).getInstrType() == MidInstrType.RET || instructions.get(i).getInstrType() == MidInstrType.JUMP) {
                instructions = new ArrayList<>(instructions.subList(0, i + 1));
            }
        }
    }

    @Override
    public String toString() {
        removeExtraInstr();
        StringBuilder sb = new StringBuilder();
        sb.append(name).append(":\n\t");
        for (MidInstr instr : instructions) {
            sb.append(instr.toString()).append("\n\t");
        }
        sb.delete(sb.length() - 1, sb.length());
        return sb.toString();
    }

    public void generateMips() {
        new ObjLabelInstr(name);
        for (MidInstr instr : instructions) {
            instr.generateMips();
        }
    }

}

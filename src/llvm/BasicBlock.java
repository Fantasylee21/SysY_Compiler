package llvm;

import backend.MipsBuilder;
import backend.ObjBlock;
import backend.objInstr.ObjLabelInstr;
import llvm.midInstr.MidInstr;
import llvm.midInstr.MidInstrType;
import llvm.type.OtherType;

import java.util.ArrayList;
import java.util.HashSet;

public class BasicBlock extends Value {
    private ArrayList<MidInstr> instructions;
    private Function parentFunction;
    private ArrayList<BasicBlock> successors;
    private ArrayList<BasicBlock> predecessors;

    private HashSet<Value> def;
    private HashSet<Value> use;

    public BasicBlock(String name) {
        super(OtherType.getBasicBlock(), name);
        this.instructions = new ArrayList<>();
        this.parentFunction = null;

        LLVMBuilder.getLlvmBuilder().addBasicBlock(this);
    }

    public void setSuccessors(ArrayList<BasicBlock> successors) {
        this.successors = successors;
    }

    public void setPredecessors(ArrayList<BasicBlock> predecessors) {
        this.predecessors = predecessors;
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
        ObjBlock objBlock = new ObjBlock(name);
        MipsBuilder.getMipsBuilder().getCurrentFunction().enterBlock(objBlock);
        for (MidInstr instr : instructions) {
            instr.generateMips();
        }
        MipsBuilder.getMipsBuilder().getCurrentFunction().exitBlock();
    }

    public void setDefUse() {
        def = new HashSet<>();
        use = new HashSet<>();
        for (MidInstr instr : instructions) {
            for (Value operand : instr.getOperands()) {
                if (!def.contains(operand)) {
                    use.add(operand);
                }
            }
            if (!use.contains(instr) && instr.isDef()) {
                def.add(instr);
            }
        }
    }
}

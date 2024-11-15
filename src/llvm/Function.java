package llvm;

import llvm.instr.RetInstr;
import llvm.type.LLVMType;
import llvm.type.OtherType;

import java.util.ArrayList;

public class Function extends GlobalValue {
    private ArrayList<Value> arguments;
    private ArrayList<BasicBlock> basicBlocks;
    private LLVMType returnType;

    public Function(LLVMType returnType, String name) {
        super(OtherType.getFunction(), "@" + name);
        this.arguments = new ArrayList<>();
        this.basicBlocks = new ArrayList<>();
        this.returnType = returnType;
        LLVMBuilder.getLlvmBuilder().addFunction(this);
    }

    public ArrayList<Value> getArguments() {
        return arguments;
    }

    public ArrayList<BasicBlock> getBasicBlocks() {
        return basicBlocks;
    }

    public LLVMType getReturnType() {
        return returnType;
    }

    public void setArguments(ArrayList<Value> arguments) {
        this.arguments = arguments;
    }

    public void setBasicBlocks(ArrayList<BasicBlock> basicBlocks) {
        this.basicBlocks = basicBlocks;
    }

    public void removeBasicBlock(String name) {
        for (int i = 0; i < basicBlocks.size(); i++) {
            if (basicBlocks.get(i).getName().equals(name)) {
                basicBlocks.remove(i);
                break;
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("define dso_local ").append(returnType).append(" ").append(name).append("(");
        for (int i = 0; i < arguments.size(); i++) {
            sb.append(arguments.get(i).toString());
            if (i != arguments.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append(") {\n");
        for (BasicBlock basicBlock : basicBlocks) {
            sb.append(basicBlock.toString()).append("\n");
        }
        sb.delete(sb.length() - 1, sb.length());
        sb.append("}\n");
        return sb.toString();
    }

    public void checkReturn() {
        BasicBlock lastBlock = LLVMBuilder.getLlvmBuilder().getCurBasicBlock();
        if (lastBlock.getInstructions().isEmpty() || !(lastBlock.getInstructions().get(lastBlock.getInstructions().size() - 1) instanceof RetInstr)) {
            new RetInstr(null, null);
        }
    }
}

package backend;

import backend.objInstr.ObjInstr;
import llvm.Function;

import java.util.ArrayList;

public class ObjFunction {
    private ArrayList<ObjBlock> blocks;
    private String name;
    private int stackSize;
    private int paramSize;
    private int curOffset;
    private ObjBlock curBlock;
    private Function llvmFunction;


    public ObjFunction(String name) {
        this.name = name;
        this.blocks = new ArrayList<>();
        this.stackSize = 0;
        this.paramSize = 0;
        this.curOffset = 0;
    }

    public int getCurOffset() {
        return curOffset;
    }

    public Function getLlvmFunction() {
        return llvmFunction;
    }

    public void setParamSize(int paramSize) {
        this.paramSize = paramSize;
        this.curOffset = paramSize + 4 + 32;
    }

    public void setLlvmFunction(Function llvmFunction) {
        this.llvmFunction = llvmFunction;
    }

    public int getParamSize() {
        return paramSize;
    }

    public void addBlock(ObjBlock block) {
        blocks.add(block);
    }

    public ArrayList<ObjBlock> getBlocks() {
        return blocks;
    }

    public String getName() {
        return name;
    }

    public void setStackSize(int stackSize) {
        this.stackSize = stackSize;
    }

    public int getStackSize() {
        return stackSize;
    }

    public void curOffsetUp(int size) {
        curOffset += size;
    }

    public void enterBlock(ObjBlock block) {
        curBlock = block;
    }

    public void exitBlock() {
        blocks.add(curBlock);
        curBlock = null;
    }

    public void addInstr(ObjInstr instr) {
        curBlock.addInstr(instr);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("# enter function ").append(name).append("\n\n");
        sb.append(name).append(":\n");
        sb.append("\tsubu $sp, $sp, ").append(stackSize).append("\n");
        for (ObjBlock block : blocks) {
            sb.append(block.toString()).append("\n");
        }
        sb.append("\taddu $sp, $sp, ").append(stackSize).append("\n");
        sb.append("# exit function ").append(name).append("\n");
        return sb.toString();
    }

}

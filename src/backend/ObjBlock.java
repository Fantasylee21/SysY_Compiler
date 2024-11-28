package backend;

import backend.objInstr.ObjInstr;

import java.util.ArrayList;

public class ObjBlock {
    private ArrayList<ObjInstr> instructions;
    private String name;
    private ArrayList<ObjBlock> successors;
    private ArrayList<ObjBlock> predecessors;

    public ObjBlock(String name) {
        this.name = name;
        this.instructions = new ArrayList<>();
        this.successors = new ArrayList<>();
        this.predecessors = new ArrayList<>();
    }

    public void addInstr(ObjInstr instr) {
        instructions.add(instr);
    }

    public ArrayList<ObjInstr> getInstructions() {
        return instructions;
    }

    public String getName() {
        return name;
    }

    public void addSuccessor(ObjBlock block) {
        successors.add(block);
    }

    public void addPredecessor(ObjBlock block) {
        predecessors.add(block);
    }

    public ArrayList<ObjBlock> getSuccessors() {
        return successors;
    }

    public ArrayList<ObjBlock> getPredecessors() {
        return predecessors;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append(":\n\t");
        for (ObjInstr instr : instructions) {
            sb.append(instr.toString()).append("\n\t");
        }
        return sb.toString();
    }

}

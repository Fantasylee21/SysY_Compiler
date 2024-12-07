package llvm.midOptimize;
import llvm.*;
import llvm.Module;
import llvm.midInstr.*;
import llvm.type.LLVMEnumType;
import llvm.type.PointerType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Stack;

public class Mem2Reg {
    private Module module;
    private MidInstr instr;
    private ArrayList<MidInstr> useInstructions;
    private ArrayList<MidInstr> defInstructions;
    private ArrayList<BasicBlock> defBlocks;
    private ArrayList<BasicBlock> useBlocks;
    private Stack<Value> stack;

    public Mem2Reg(Module module) {
        this.module = module;
    }

    public void init(MidInstr instr) {
        this.instr = instr;
        this.useInstructions = new ArrayList<>();
        this.defInstructions = new ArrayList<>();
        this.defBlocks = new ArrayList<>();
        this.useBlocks = new ArrayList<>();
        this.stack = new Stack<>();

        for (Use use : instr.getUseList()) {
            if (use.getUser() instanceof MidInstr useInstr) {
                if (useInstr instanceof StoreInstr) {
                    defInstructions.add(useInstr);
                    if (!defBlocks.contains(useInstr.getParentBasicBlock())) {
                        defBlocks.add(useInstr.getParentBasicBlock());
                    }
                } else if (useInstr instanceof LoadInstr) {
                    useInstructions.add(useInstr);
                    if (!useBlocks.contains(useInstr.getParentBasicBlock())) {
                        useBlocks.add(useInstr.getParentBasicBlock());
                    }
                }
            }
        }
    }

    public void generatePhi(BasicBlock block) {
        ArrayList<MidInstr> instrs = block.getInstructions();
        MidInstr phiInstr = new PhiInstr(LLVMBuilder.getLlvmBuilder().getVarName(), block.getPredecessors());
        instrs.add(0, phiInstr);
        phiInstr.setParentBasicBlock(block);
        useInstructions.add(phiInstr);
        defInstructions.add(phiInstr);
    }

    public void insertPhi() {
        HashSet<BasicBlock> F = new HashSet<>();
        Stack<BasicBlock> W = new Stack<>();
        for (BasicBlock block : defBlocks) {
            W.push(block);
        }
        while (!W.isEmpty()) {
            BasicBlock X = W.pop();
            for (BasicBlock Y : X.getDominanceFrontiers()) {
                if (!F.contains(Y)) {
                    generatePhi(Y);
                    F.add(Y);
                    if (!defBlocks.contains(Y)) {
                        W.push(Y);
                    }
                }
            }
        }
    }

    public void rename(BasicBlock block) {
        int cnt = 0;
        Iterator<MidInstr> instrIterator = block.getInstructions().iterator();
        while (instrIterator.hasNext()) {
            MidInstr instr = instrIterator.next();
            if (instr instanceof LoadInstr && useInstructions.contains(instr)) {
                instr.replaceAllUsesWith(stack.peek());
                instrIterator.remove();
            } else if (instr instanceof StoreInstr && defInstructions.contains(instr)) {
                Value value = instr.getOperands().get(0);
                stack.push(value);
                instrIterator.remove();
                cnt++;
            } else if (instr instanceof PhiInstr && defInstructions.contains(instr)) {
                stack.push(instr);
                cnt++;
            } else if (instr.equals(this.instr)) {
                instrIterator.remove();
            }
        }

        for (BasicBlock succ : block.getSuccessors()) {
            MidInstr instr = succ.getInstructions().get(0);
            if (instr instanceof PhiInstr phiInstr) {
                if (!useInstructions.contains(instr)) {
                    continue;
                }
                if (stack.isEmpty() || stack.peek() == null) {
                    continue;
                }
                phiInstr.addOption(block, stack.peek());
            }
        }
        for (BasicBlock child : block.getChildren()) {
            rename(child);
        }
        for (int i = 0; i < cnt; i++) {
            stack.pop();
        }
    }

    public void run() {
        for (Function function : module.getFunctions()) {
            for (BasicBlock block : function.getBasicBlocks()) {
                ArrayList<MidInstr> instrs = new ArrayList<>(block.getInstructions());
                for (MidInstr instr : instrs) {
                    if (instr instanceof AllocaInstr) {
                        LLVMEnumType type = ((PointerType) instr.getType()).getTargetType().getType();
                        if (type != LLVMEnumType.Int8Type && type != LLVMEnumType.Int32Type) {
                            continue;
                        }
                        init(instr);
                        insertPhi();
                        rename(function.getBasicBlocks().get(0));
                    }
                }
            }
        }
    }

}

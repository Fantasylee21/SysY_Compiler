package backend;

import backend.objInstr.ObjInstr;
import backend.objInstr.ObjLaInstr;
import backend.objInstr.ObjLiInstr;
import backend.objInstr.ObjMoveInstr;
import backend.objInstr.branch.ObjBranchInstr;
import backend.objInstr.dm.ObjDmInstr;
import backend.objInstr.load.ObjLoadInstr;
import backend.objInstr.move.MoveType;
import backend.objInstr.move.ObjMoveHLInstr;
import backend.objInstr.riCalculate.ObjRICalculate;
import backend.objInstr.rrCalculate.ObjRRCalculateInstr;
import backend.objInstr.store.ObjStoreInstr;
import backend.register.Register;
import llvm.BasicBlock;
import llvm.Function;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class ObjFunction {
    private ArrayList<ObjBlock> blocks;
    private String name;
    private int paramSize;
    private int curOffset;
    private ObjBlock curBlock;
    private Function llvmFunction;
    private HashMap<ObjBlock, ArrayList<ObjBlock>> preMap = new HashMap<>();
    private HashMap<ObjBlock, ArrayList<ObjBlock>> sucMap = new HashMap<>();
    private HashMap<String, Integer> activeStart = new HashMap<>();
    private HashMap<String, Integer> activeEnd = new HashMap<>();

    public ObjFunction(String name) {
        this.name = name;
        this.blocks = new ArrayList<>();
        this.paramSize = 0;
        this.curOffset = 0;
    }

    public int getCurOffset() {
        return curOffset;
    }

    public Function getLlvmFunction() {
        return llvmFunction;
    }

    public void setPreMap(HashMap<ObjBlock, ArrayList<ObjBlock>> preMap) {
        this.preMap = preMap;
    }

    public void setSucMap(HashMap<ObjBlock, ArrayList<ObjBlock>> sucMap) {
        this.sucMap = sucMap;
    }

    public void setParamSize(int paramSize) {
        this.paramSize = paramSize;
        this.curOffset = paramSize + 4 + 88;
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

    public ObjBlock getObjBlock(String name) {
        for (ObjBlock block : blocks) {
            if (block.getName().equals(name)) {
                return block;
            }
        }
        return null;
    }

    public void BuildCFG() {
        HashMap<BasicBlock, ArrayList<BasicBlock>> preMap = llvmFunction.getPreMap();
        HashMap<BasicBlock, ArrayList<BasicBlock>> sucMap = llvmFunction.getSucMap();
        for (BasicBlock block : preMap.keySet()) {
            ArrayList<BasicBlock> pre = preMap.get(block);
            if (pre.isEmpty()) {
                continue;
            }
            ArrayList<ObjBlock> predecessors = new ArrayList<>();
            for (BasicBlock preBlock : pre) {
                predecessors.add(getObjBlock(preBlock.getName()));
            }
            getObjBlock(block.getName()).setPredecessors(predecessors);
            this.preMap.put(getObjBlock(block.getName()), predecessors);
        }
        for (BasicBlock block : sucMap.keySet()) {
            ArrayList<BasicBlock> suc = sucMap.get(block);
            if (suc.isEmpty()) {
                continue;
            }
            ArrayList<ObjBlock> successors = new ArrayList<>();
            for (BasicBlock sucBlock : suc) {
                successors.add(getObjBlock(sucBlock.getName()));
            }
            getObjBlock(block.getName()).setSuccessors(successors);
            this.sucMap.put(getObjBlock(block.getName()), successors);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("# enter function ").append(name).append("\n\n");
        sb.append(name).append(":\n");
        sb.append("\tsubu $sp, $sp, ").append(curOffset).append("\n");
        for (ObjBlock block : blocks) {
            sb.append(block.toString(curOffset)).append("\n");
        }
        sb.append("# exit function ").append(name).append("\n");
        return sb.toString();
    }

    private HashMap<ObjBlock, HashSet<String>> inMap;
    private HashMap<ObjBlock, HashSet<String>> outMap;
    private HashMap<String, HashSet<ObjBlock>> activeMap = new HashMap<>();

    public void activeAnalysis() {
        BuildCFG();
        inMap = new HashMap<>();
        outMap = new HashMap<>();
        for (ObjBlock bb : blocks) {
            outMap.put(bb, new HashSet<>());
            inMap.put(bb, new HashSet<>());
        }
        for (ObjBlock bb : blocks) {
            bb.buildUseDef();
        }
        boolean changed = true;
        while (changed) {
            changed = false;
            for (int i = blocks.size() - 1; i >= 0; i--) {
                ObjBlock bb = blocks.get(i);
                HashSet<String> newOut = new HashSet<>();
//                out = U后继(in)
                for (ObjBlock succ : bb.getSuccessors()) {
                    newOut.addAll(inMap.get(succ));
                    outMap.put(bb, newOut);
                }
//                in = (out - def) + use
                HashSet<String> newIn = new HashSet<>(newOut);
                newIn.removeAll(bb.getDef());
                newIn.addAll(bb.getUse());
                if (!newIn.equals(inMap.get(bb))) {
                    changed = true;
                    inMap.put(bb, newIn);
                }
            }
        }
        for (ObjBlock bb : blocks) {
            bb.setIn(inMap.get(bb));
            bb.setOut(outMap.get(bb));
        }
        for (ObjBlock bb : blocks) {
            for (String register : bb.getOut()) {
                if (!activeMap.containsKey(register)) {
                    activeMap.put(register, new HashSet<>());
                }
                activeMap.get(register).add(bb);
            }
            for (String register : bb.getIn()) {
                if (!activeMap.containsKey(register)) {
                    activeMap.put(register, new HashSet<>());
                }
                activeMap.get(register).add(bb);
            }
            for (String register : bb.getUse()) {
                if (!activeMap.containsKey(register)) {
                    activeMap.put(register, new HashSet<>());
                }
                activeMap.get(register).add(bb);
            }
            for (String register : bb.getDef()) {
                if (!activeMap.containsKey(register)) {
                    activeMap.put(register, new HashSet<>());
                }
                activeMap.get(register).add(bb);
            }
        }

//        for (String register : activeMap.keySet()) {
//            System.out.println(register);
//            for (ObjBlock block : activeMap.get(register)) {
//                System.out.println(block.getName());
//            }
//            System.out.println();
//        }
    }

    public void removeUnliveCode() {
        for (ObjBlock block : blocks) {
            Iterator<ObjInstr> iterator = block.getInstructions().iterator();
            while (iterator.hasNext()) {
                ObjInstr instr = iterator.next();
                if (instr instanceof ObjLoadInstr objLoadInstr) {
                    Register rt = objLoadInstr.getRt();
                    if (!activeMap.containsKey(rt.toString()) && !rt.isRealRegister()) {
                        iterator.remove();
                    }
                } else if (instr instanceof ObjRRCalculateInstr objRRCalculateInstr) {
                    Register rd = objRRCalculateInstr.getRd();
                    if (!activeMap.containsKey(rd.toString()) && !rd.isRealRegister()) {
                        iterator.remove();
                    }
                } else if (instr instanceof ObjRICalculate objRICalculate) {
                    Register rt = objRICalculate.getRt();
                    if (!activeMap.containsKey(rt.toString()) && !rt.isRealRegister()) {
                        iterator.remove();
                    }
                } else if (instr instanceof ObjMoveInstr objMoveInstr) {
                    Register dst = objMoveInstr.getDst();
                    if (!activeMap.containsKey(dst.toString()) && !dst.isRealRegister()) {
                        iterator.remove();
                    }
                } else if (instr instanceof ObjMoveHLInstr moveHLInstr) {
                    Register rd = moveHLInstr.getRd();
                    if (moveHLInstr.getMoveType() == MoveType.MFHI || moveHLInstr.getMoveType() == MoveType.MFLO) {
                        if (!activeMap.containsKey(rd.toString()) && !rd.isRealRegister()) {
                            iterator.remove();
                        }
                    }
                } else if (instr instanceof ObjLiInstr) {
                    Register target = ((ObjLiInstr) instr).getTarget();
                    if (!activeMap.containsKey(target.toString()) && !target.isRealRegister()) {
                        iterator.remove();
                    }
                } else if (instr instanceof ObjLaInstr) {
                    Register target = ((ObjLaInstr) instr).getTarget();
                    if (!activeMap.containsKey(target.toString()) && !target.isRealRegister()) {
                        iterator.remove();
                    }
                }
            }
        }
    }

    private HashSet<String> toAdd = new HashSet<>();

    public void analyseLine() {
        activeAnalysis();
//        TODO:中端优化
        removeUnliveCode();
        int line = 0;
        for (ObjBlock block : blocks) {
            if (!toAdd.isEmpty()) {
                Iterator<String> iterator = toAdd.iterator();
                while (iterator.hasNext()) {
                    String register = iterator.next();
                    if (!activeMap.get(register).contains(block)) {
                        HashSet<ObjBlock> set = activeMap.get(register);
                        boolean flag = false;
                        for (int j = blocks.indexOf(block); j < blocks.size(); j++) {
                            if (set.contains(blocks.get(j))) {
                                flag = true;
                                break;
                            }
                        }
                        if (!flag) {
                            activeEnd.put(register, line);
                            iterator.remove();
                        }
                    }
                }
            }
            for (ObjInstr instr : block.getInstructions()) {
                if (instr instanceof ObjBranchInstr) {
                    Register rs = ((ObjBranchInstr) instr).getRs();
                    Register rt = ((ObjBranchInstr) instr).getRt();
                    activePut(rs, line, block);
                    activePut(rt, line, block);
                } else if (instr instanceof ObjDmInstr) {
                    Register rs = ((ObjDmInstr) instr).getRs();
                    Register rt = ((ObjDmInstr) instr).getRt();
                    activePut(rs, line, block);
                    activePut(rt, line, block);
                } else if (instr instanceof ObjLoadInstr) {
                    Register rt = ((ObjLoadInstr) instr).getRt();
                    Register base = ((ObjLoadInstr) instr).getBase();
                    activePut(rt, line, block);
                    activePut(base, line, block);
                } else if (instr instanceof ObjMoveHLInstr) {
                    Register rd = ((ObjMoveHLInstr) instr).getRd();
                    activePut(rd, line, block);
                } else if (instr instanceof ObjRICalculate) {
                    Register rs = ((ObjRICalculate) instr).getRs();
                    Register rt = ((ObjRICalculate) instr).getRt();
                    activePut(rs, line, block);
                    activePut(rt, line, block);
                } else if (instr instanceof ObjRRCalculateInstr) {
                    Register rs = ((ObjRRCalculateInstr) instr).getRs();
                    Register rt = ((ObjRRCalculateInstr) instr).getRt();
                    Register rd = ((ObjRRCalculateInstr) instr).getRd();
                    activePut(rs, line, block);
                    activePut(rt, line, block);
                    activePut(rd, line, block);
                } else if (instr instanceof ObjMoveInstr) {
                    Register src = ((ObjMoveInstr) instr).getSrc();
                    Register dest = ((ObjMoveInstr) instr).getDst();
                    activePut(src, line, block);
                    activePut(dest, line, block);
                } else if (instr instanceof ObjStoreInstr) {
                    Register rt = ((ObjStoreInstr) instr).getRt();
                    Register base = ((ObjStoreInstr) instr).getBase();
                    activePut(rt, line, block);
                    activePut(base, line, block);
                } else if (instr instanceof ObjLaInstr) {
                    Register target = ((ObjLaInstr) instr).getTarget();
                    activePut(target, line, block);
                } else if (instr instanceof ObjLiInstr) {
                    Register target = ((ObjLiInstr) instr).getTarget();
                    activePut(target, line, block);
                }
                line++;
            }
        }
    }

    public void activePut(Register register, int line, ObjBlock block) {
        if (!register.isRealRegister()) {
            if (!activeStart.containsKey(register.toString())){
                activeStart.put(register.toString(), line);
            }
            if (activeMap.get(register.toString()).size() == 1) {
                activeEnd.put(register.toString(), line);
            } else {
                toAdd.add(register.toString());
            }
        }
    }

    public boolean sucHasLive(Register register, ObjBlock block) {
        for (ObjBlock suc : block.getSuccessors()) {
            if (activeMap.get(register.toString()).contains(suc)) {
                return true;
            }
        }
        return false;
    }

    public boolean NoConflict(String register, ObjBlock block) {
        //找到block的下一个
        ObjBlock next = null;
        int index = blocks.indexOf(block);
        if (index < blocks.size() - 1) {
            next = blocks.get(index + 1);
        } else {
            return true;
        }
        if (block.getOut().contains(register) && next.getIn().contains(register)) {
            return true;
        }
        return false;
    }

    public HashMap<String, Integer> getActiveEnd() {
        return activeEnd;
    }

    public HashMap<String, Integer> getActiveStart() {
        return activeStart;
    }

    public ObjBlock getBlockByLabel(String label) {
        for (ObjBlock block : blocks) {
            if (block.getName().equals(label)) {
                return block;
            }
        }
        return null;
    }
}

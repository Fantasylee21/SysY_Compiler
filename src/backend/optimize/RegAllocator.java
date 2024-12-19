package backend.optimize;

import backend.ObjBlock;
import backend.ObjFunction;
import backend.ObjModule;
import backend.objInstr.*;
import backend.objInstr.branch.ObjBranchInstr;
import backend.objInstr.dm.ObjDmInstr;
import backend.objInstr.jump.JumpType;
import backend.objInstr.jump.ObjJumpInstr;
import backend.objInstr.load.LoadType;
import backend.objInstr.load.ObjLoadInstr;
import backend.objInstr.move.ObjMoveHLInstr;
import backend.objInstr.riCalculate.ObjRICalculate;
import backend.objInstr.rrCalculate.ObjRRCalculateInstr;
import backend.objInstr.store.ObjStoreInstr;
import backend.objInstr.store.StoreType;
import backend.register.Register;
import llvm.Function;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.TreeMap;

public class RegAllocator {
    private ObjModule objModule;
    private HashMap<String, Boolean> regMap = new HashMap<>();
    HashMap<String, String> regAlloc = new HashMap<>();
    private TreeMap<Integer, String> frame = new TreeMap<>(new Comparator<Integer>() {
        @Override
        public int compare(Integer o1, Integer o2) {
            return o2 - o1;
        }
    });

    public RegAllocator(ObjModule objModule) {
        this.objModule = objModule;
        for (int i = 0; i < 8; i++) {
            regMap.put("$s" + i, false);
        }
        for (int i = 0; i < 9; i++) {
            regMap.put("$t" + i, false);
        }
        regMap.put("$fp", false);
    }

    public int getOffset(Register register) {
        for (Integer offset : frame.keySet()) {
            if (frame.get(offset).equals(register.getVirtualReg())) {
                return offset;
            }
        }
        return -1;
    }

    public Register getOneFreeReg() {
        for (String reg : regMap.keySet()) {
            if (!regMap.get(reg)) {
                regMap.put(reg, true);
                if (reg.charAt(1) == 's') {
                    return Register.get$s(Integer.parseInt(reg.substring(2)));
                } else if (reg.charAt(1) == 't') {
                    return Register.get$t(Integer.parseInt(reg.substring(2)));
                } else {
                    return Register.get$fp();
                }
            }
        }
        return null;
    }

    public Register getAllocReg(String reg) {
        if (reg.charAt(1) == 's') {
            return Register.get$s(Integer.parseInt(reg.substring(2)));
        } else if (reg.charAt(1) == 't') {
            return Register.get$t(Integer.parseInt(reg.substring(2)));
        } else {
            return Register.get$fp();
        }
    }

    public boolean hasFreeReg() {
        for (String reg : regMap.keySet()) {
            if (!regMap.get(reg)) {
                return true;
            }
        }
        return false;
    }

    public void freeReg(String reg) {
        regMap.put(reg, false);
    }

    public void freeAllReg() {
        regMap.replaceAll((r, v) -> false);
    }

    public void allocReg() {
        for (ObjFunction objFunction : objModule.getFunctions()) {
            objFunction.analyseLine();
            int line = 0;
            for (ObjBlock objBlock : objFunction.getBlocks()) {
                for (ObjInstr instr : objBlock.getInstructions()) {
                    if (instr instanceof ObjBranchInstr) {
                        Register rs = ((ObjBranchInstr) instr).getRs();
                        Register rt = ((ObjBranchInstr) instr).getRt();
                        doAlloc(rs, line, objFunction, objBlock, true, false, instr);
                        doAlloc(rt, line, objFunction, objBlock, false, false, instr);
                    } else if (instr instanceof ObjDmInstr) {
                        Register rs = ((ObjDmInstr) instr).getRs();
                        Register rt = ((ObjDmInstr) instr).getRt();
                        doAlloc(rs, line, objFunction, objBlock, true, false, instr);
                        doAlloc(rt, line, objFunction, objBlock, false, false, instr);
                    } else if (instr instanceof ObjLoadInstr) {
                        Register base = ((ObjLoadInstr) instr).getBase();
                        Register rt = ((ObjLoadInstr) instr).getRt();
                        doAlloc(base, line, objFunction, objBlock, true, false, instr);
                        doAlloc(rt, line, objFunction, objBlock, false, true, instr);
                    } else if (instr instanceof ObjMoveHLInstr) {
                        Register rd = ((ObjMoveHLInstr) instr).getRd();
                        doAlloc(rd, line, objFunction, objBlock, true, false, instr);
                    } else if (instr instanceof ObjRICalculate) {
                        Register rs = ((ObjRICalculate) instr).getRs();
                        Register rt = ((ObjRICalculate) instr).getRt();
                        doAlloc(rs, line, objFunction, objBlock, true, false, instr);
                        doAlloc(rt, line, objFunction, objBlock, true, true, instr);
                    } else if (instr instanceof ObjRRCalculateInstr) {
                        Register rs = ((ObjRRCalculateInstr) instr).getRs();
                        Register rt = ((ObjRRCalculateInstr) instr).getRt();
                        Register rd = ((ObjRRCalculateInstr) instr).getRd();
                        doAlloc(rs, line, objFunction, objBlock, false, false, instr);
                        doAlloc(rt, line, objFunction, objBlock, true, false, instr);
                        doAlloc(rd, line, objFunction, objBlock, true, true, instr);
                    } else if (instr instanceof ObjMoveInstr) {
                        Register src = ((ObjMoveInstr) instr).getSrc();
                        Register dest = ((ObjMoveInstr) instr).getDst();
                        doAlloc(src, line, objFunction, objBlock, false, false, instr);
                        doAlloc(dest, line, objFunction, objBlock, true, true, instr);
                    } else if (instr instanceof ObjStoreInstr) {
                        Register rt = ((ObjStoreInstr) instr).getRt();
                        Register base = ((ObjStoreInstr) instr).getBase();
                        doAlloc(rt, line, objFunction, objBlock, false, false, instr);
                        doAlloc(base, line, objFunction, objBlock, true, false, instr);
                    } else if (instr instanceof ObjLaInstr) {
                        Register target = ((ObjLaInstr) instr).getTarget();
                        doAlloc(target, line, objFunction, objBlock, true, true, instr);
                    } else if (instr instanceof ObjLiInstr) {
                        Register target = ((ObjLiInstr) instr).getTarget();
                        doAlloc(target, line, objFunction, objBlock, true, true, instr);
                    } else if (instr instanceof ObjJumpInstr) {
                        JumpType jumpType = ((ObjJumpInstr) instr).getJumpType();
                        if (jumpType == JumpType.JAL) {
                            int cnt = 0;
                            int callCommentLine = objBlock.getInstructions().indexOf(instr);
                            while (!(objBlock.getInstructions().get(callCommentLine) instanceof ObjCommentInstr && ((ObjCommentInstr) objBlock.getInstructions().get(callCommentLine)).getComment().startsWith("call start"))) {
                                callCommentLine--;
                                cnt++;
                            }
                            int offset = objFunction.getParamSize() + 4;
                            for (String reg : regMap.keySet()) {
                                if (regMap.get(reg)) {
                                    toAddPut(line - cnt + 1, new ObjStoreInstr(StoreType.SW, getAllocReg(reg), Register.get$sp(), offset, true));
                                    offset += 4;
                                }
                            }
                            int argSize = objFunction.getLlvmFunction().getArguments().size();
                            for (int i = 0; i < argSize; i++) {
                                if (i < 4) {
                                    toAddPut(line - cnt + 1, new ObjStoreInstr(StoreType.SW, Register.get$a(i), Register.get$sp(), offset, true));
                                    int j = callCommentLine;
                                    while (j < objBlock.getInstructions().indexOf(instr)) {
                                        ObjInstr objInstr = objBlock.getInstructions().get(j);
                                        if (objInstr instanceof ObjMoveInstr moveInstr && moveInstr.getSrc().toString().equals(Register.get$a(i).toString())) {
                                            int aNum1 = Integer.parseInt(moveInstr.getDst().toString().substring(2));
                                            if (aNum1 > i) {
                                                objBlock.getInstructions().set(j, new ObjLoadInstr(LoadType.LW, moveInstr.getDst(), Register.get$sp(), offset, true));
                                            }
                                        }
                                        j++;
                                    }
                                    offset += 4;
                                }
                            }
                            offset = objFunction.getParamSize() + 4;
                            for (String reg : regMap.keySet()) {
                                if (regMap.get(reg)) {
                                    toAddPut(line + 1, new ObjLoadInstr(LoadType.LW, getAllocReg(reg), Register.get$sp(), offset, true));
                                    offset += 4;
                                }
                            }
                            for (int i = 0; i < argSize; i++) {
                                if (i < 4) {
                                    toAddPut(line + 1, new ObjLoadInstr(LoadType.LW, Register.get$a(i), Register.get$sp(), offset, true));
                                    offset += 4;
                                }
                            }
                        }
                    }
                    HashMap<String, Integer> activeEnd = objFunction.getActiveEnd();
                    TreeMap<Integer, ArrayList<String>> activeEndLine = new TreeMap<>();
                    for (String reg : activeEnd.keySet()) {
                        Integer end = activeEnd.get(reg);
                        if (activeEndLine.containsKey(end)) {
                            activeEndLine.get(end).add(reg);
                        } else {
                            ArrayList<String> regs = new ArrayList<>();
                            regs.add(reg);
                            activeEndLine.put(end, regs);
                        }
                    }
                    ArrayList<String> regs = activeEndLine.get(line);
                    if (regs != null) {
                        for (String reg : regs) {
                            if (regAlloc.containsKey(reg)) {
                                freeReg(regAlloc.get(reg));
                                regAlloc.remove(reg);
                            }
                        }
                    }
                    line++;
                }
            }
            useToAddPut(objFunction);
            freeAllReg();
        }
    }

    public void useToAddPut(ObjFunction objFunction) {
        int line = 0;
        for (ObjBlock objBlock : objFunction.getBlocks()) {
            line += objBlock.getInstructions().size();
        }
        int cnt = objFunction.getBlocks().size();
        int blockSize = objFunction.getBlocks().get(cnt - 1).getInstructions().size();
        line -= blockSize;
        for (int i = toAdd.size() - 1; i >= 0; i--) {
            for (int lineNum : toAdd.get(i).keySet()) {
                while (lineNum < line) {
                    cnt--;
                    if (cnt == 0) {
                        line = 0;
                    } else {
                        blockSize = objFunction.getBlocks().get(cnt - 1).getInstructions().size();
                        line -= blockSize;
                    }
                }
                objFunction.getBlocks().get(cnt - 1).getInstructions().add(lineNum - line, toAdd.get(i).get(lineNum));
            }
        }
        toAdd.clear();
        boolean flag = false;
        for (ObjBlock objBlock : objFunction.getBlocks()) {
            for (ObjInstr instr : objBlock.getInstructions()) {
                if (instr instanceof ObjCommentInstr) {
                    String comment = ((ObjCommentInstr) instr).getComment();
                    if (comment.startsWith("load arguments")) {
                        flag = true;
                    } else if (comment.startsWith("end arguments")) {
                        flag = false;
                        break;
                    }
                } else if (instr instanceof ObjLoadInstr loadInstr && flag) {
                    loadInstr.setOffset(loadInstr.getOffset() + objFunction.getCurOffset());
                }

            }
        }
    }

    private ArrayList<HashMap<Integer, ObjInstr>> toAdd = new ArrayList<>();

    public void toAddPut(int line, ObjInstr instr) {
        HashMap<Integer, ObjInstr> last = new HashMap<>();
        last.put(line, instr);
        if (toAdd.isEmpty()) {
            toAdd.add(last);
        } else if (line >= toAdd.get(toAdd.size() - 1).keySet().iterator().next()) {
            toAdd.add(last);
        } else {
            for (int i = toAdd.size() - 1; i >= 0; i--) {
                if (line >= toAdd.get(i).keySet().iterator().next()) {
                    toAdd.add(i + 1, last);
                    break;
                }
            }
        }
    }

    public void doAlloc(Register register, int line, ObjFunction objFunction, ObjBlock objBlock, boolean first, boolean def, ObjInstr instr) {
        HashMap<String, Integer> activeStart = objFunction.getActiveStart();
        if (!register.isRealRegister()) {
            if (activeStart.get(register.toString()) == line && !regAlloc.containsKey(register.toString())) {
                if (hasFreeReg()) {
                    if (regAlloc.containsKey(register.toString())) {
                        freeReg(regAlloc.get(register.toString()));
                    }
                    Register freeReg = getOneFreeReg();
                    regAlloc.put(register.toString(), freeReg.toString());
                    register.setRealRegister(freeReg.getRealRegister());
                } else {
                    int curOffset = objFunction.getCurOffset();
                    frame.put(curOffset, register.getVirtualReg());
                    objFunction.curOffsetUp(4);
                    if (first) {
                        register.setRealRegister(Register.get$k0().getRealRegister());
                        toAddPut(line + 1, new ObjStoreInstr(StoreType.SW, Register.get$k0(), Register.get$sp(), curOffset, true));
                    } else {
                        register.setRealRegister(Register.get$k1().getRealRegister());
                        toAddPut(line + 1, new ObjStoreInstr(StoreType.SW, Register.get$k1(), Register.get$sp(), curOffset, true));
                    }
                }
            } else {
                if (regAlloc.containsKey(register.toString())) {
                    register.setRealRegister(getAllocReg(regAlloc.get(register.toString())).getRealRegister());
                } else {
                    if (first) {
                        if (!def) {
                            toAddPut(line, new ObjLoadInstr(LoadType.LW, Register.get$k0(), Register.get$sp(), getOffset(register), true));
                        } else {
                            int curOffset = objFunction.getCurOffset();
                            if (getOffset(register) == -1) {
                                frame.put(curOffset, register.getVirtualReg());
                                objFunction.curOffsetUp(4);
                            } else {
                                curOffset = getOffset(register);
                            }
                            toAddPut(line + 1, new ObjStoreInstr(StoreType.SW, Register.get$k0(), Register.get$sp(), curOffset, true));
                        }
                        register.setRealRegister(Register.get$k0().getRealRegister());
                    } else {
                        if (!def) {
                            toAddPut(line, new ObjLoadInstr(LoadType.LW, Register.get$k1(), Register.get$sp(), getOffset(register), true));
                        }
                        register.setRealRegister(Register.get$k1().getRealRegister());
                    }
                }
            }
        }
    }
}
